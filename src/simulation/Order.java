package simulation;

import simulation.Data.DBHandler;

import java.util.ArrayList;
import java.util.Random;

public class Order {

    private int[] items;
    private DBHandler dbHandler;
    private Organiser organiser;
    private ArrayList<int[]> jobs = new ArrayList<>();
    private int collectorTileID;
    private int orderID;

    public Order(int[] items, DBHandler dbhandler, int collectorTileID, int orderID, Organiser organiser){
        this.items = items;
        this.dbHandler = dbhandler;
        this.collectorTileID = collectorTileID;
        this.orderID = orderID;
        this.organiser = organiser;
        calcJobs();
    }

    public void calcJobs(){
        for (int i = 0; i < items.length; i++) {
            int tempItemID = items[i];
            // [[fromx, fromy, tox, toy, tarBox, orderID]]
            int[] job = new int[6];

            ArrayList<int[]> returnedRows;
            returnedRows = dbHandler.getAllLocationsFromDistributionByItemID(tempItemID);

            // random picking one of all possible Locations
            Random random = new Random();
            // Array of all columns of the picked row from distribution table
            int[] pickedLocation;
            int locBoxID;
            // checking if the picked location is already used fo another order
            if(returnedRows.size()>0){
                do{
                    int pick = random.nextInt(returnedRows.size());
                    pickedLocation = returnedRows.get(pick);
                    locBoxID = dbHandler.getBoxIDFromLocationByLocID(pickedLocation[0]);
                }while (dbHandler.getUsageOfLocationByLocID(dbHandler.getUsageOfLocationByLocID(pickedLocation[0])) != 1 && dbHandler.getUsageOfBoxByBoxID(locBoxID) != 1);
                dbHandler.updateLocationToUsed(pickedLocation[0]);
                dbHandler.updateBoxToUsed(locBoxID);
                // add tarBox
                job[4] = pickedLocation[2];
                // add orderID
                job[5] = this.orderID;
                // to Cords
                int[] toCords = Sim.calcTileCords(collectorTileID);
                job[2] = toCords[0];
                job[3] = toCords[1];
                // from Cords
                int locationTileID = dbHandler.getTileIDfromBoxesByBoxID(pickedLocation[2]);
                int[] fromCords = Sim.calcTileCords(locationTileID);
                job[0] = fromCords[0];
                job[1] = fromCords[1];


                jobs.add(job);
                //System.out.println("Generated job for itemID: "+tempItemID+", orderID :"+ orderID);
                organiser.addLogMessage("500", "Generated job for itemID: "+tempItemID+", orderID :"+ orderID);
            }else{
                //System.out.println("NO ITEMS FOUND! itemID: "+tempItemID+", orderID :"+ orderID);
                organiser.addLogMessage("900", "NO ITEMS FOUND! itemID: "+tempItemID+", orderID :"+ orderID);
            }

        }
        pushJobsToOrganiser();
    }

    private void checkFinished(){
        boolean finished = true;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != -1) {
                finished = false;
                break;
            }
        }
        if (finished){
            //System.out.println("Order finished! orderID: "+orderID);
            organiser.addLogMessage("500", "Order finished! orderID: "+orderID);
            organiser.removeOrderObject(this);
        }
    }

    private void pushJobsToOrganiser(){
        for (int i = 0; i < jobs.size(); i++) {
            this.organiser.addJobPending(jobs.get(i));
        }
    }

    public void jobDone(int boxID){
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i)[4] == boxID){
                jobs.remove(i);
                break;
            }
        }
    }

    public int getOrderID() {
        return orderID;
    }

    public int[] getItems() {
        return items;
    }

    public void removeItem(int itemID){
        for (int i = 0; i < items.length; i++) {
            if(items[i] == itemID)
                items[i] = -1;
        }
        //System.out.println("Item delivered! itemID: "+itemID +", orderID: "+orderID);
        organiser.addLogMessage("500", "Item delivered! itemID: "+itemID +", orderID: "+orderID);
        checkFinished();
    }
}
