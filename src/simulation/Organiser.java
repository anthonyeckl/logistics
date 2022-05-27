package simulation;

import simulation.Data.DBHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class Organiser {
    private DBHandler dbHandler;
    // Robots
    ArrayList<Robot> inUse = new ArrayList<>();
    ArrayList<Robot> inIdle = new ArrayList<>();

    // jobs pending [[fromx, fromy, tox, toy, tarBox, orderID]]
    ArrayList<int[]> jobsPending = new ArrayList<>();

    // all orders [orderID, itemID, itemID, itemID, ...]
    // raw Order in Array format
    private ArrayList<int[]> orderList = new ArrayList<>();
    // order in Object format
    private ArrayList<Order> orderObjects = new ArrayList<>();
    // orderID availible number
    private int takenOrderID = 0;

    // orders, which are currently executed
    private ArrayList<int[]> orderListPending = new ArrayList<>();

    // inserter/ collector tiles
    private ArrayList<Integer> inserterTileIDArray = new ArrayList<>();
    private ArrayList<Integer> collectorTileIDArray = new ArrayList<>();

    public Organiser(DBHandler dbHandler){
        this.dbHandler = dbHandler;
        inserterTileIDArray.add(4879);
        collectorTileIDArray.add(4799);
    }

    // give tasks to idle robots
    public void parseIdleRobots(){
        if(inIdle.size() != 0 && jobsPending.size() != 0){
            int freeRobotsNum = Math.min(inIdle.size(), jobsPending.size());
            for (int i = 0; i < freeRobotsNum; i++) {
                inIdle.get(0).newJob(jobsPending.get(0));
                jobsPending.remove(0);
                inUse.add(inIdle.get(0));
                inIdle.remove(0);
            }
        }
    }
    // turn raw order Array int an Order Object
    public void processOrders(){
        for (int i = 0; i < orderList.size(); i++) {
            Order tmpOrder = new Order(orderList.get(0), dbHandler, collectorTileIDArray.get(0), takenOrderID, this);
            takenOrderID++;
            orderObjects.add(tmpOrder);
            orderList.remove(0);
            System.out.println("ORGANIZER: new Order processed orderID:" + tmpOrder.getOrderID());
        }
    }
    // when the box arrives at the collector the function processes the order by removing the needed item
    public void watchCollectorTile(int orderID){
        int collectorTile = collectorTileIDArray.get(0);
        int topBoxID = dbHandler.getTopBoxIDfromTilesbyTileID(collectorTile);
        ArrayList<int[]> allTopBoxLocations = dbHandler.getAllLocationsFromDistributionByBoxID(topBoxID);
        int[] tempItems = new int[0];
        Order tmpOrder = null;
        for (int i = 0; i < orderObjects.size(); i++) {
            if (orderObjects.get(i).getOrderID() == orderID){
                tmpOrder = orderObjects.get(i);
                tempItems = tmpOrder.getItems();
            }
        }

        for (int i = 0; i < allTopBoxLocations.size(); i++) {
            for (int j = 0; j < tempItems.length; j++) {
                if (allTopBoxLocations.get(i)[1] == tempItems[j]){
                    // del item from Order
                    tmpOrder.removeItem(tempItems[j]);
                    // del location
                    dbHandler.deleteLocationByLocID(allTopBoxLocations.get(i)[0]);
                    // update box utilization
                    dbHandler.updateBoxUtilizationByBoxID(topBoxID, dbHandler.getUtilizationByBoxID(topBoxID)-1);
                    // update box Usage
                    dbHandler.updateBoxToNotUsed(topBoxID);

                }
            }

        }
    }

    public void clearInserterAndCollectorTiles(){
        // Collector Tiles
        for (int i = 0; i < collectorTileIDArray.size(); i++) {
            int occ = dbHandler.getOccupationfromTilesByTileID(collectorTileIDArray.get(i));
            if( occ > 0){
                moveBoxAwayByTile(collectorTileIDArray.get(i));
            }
        }
        //Inserter Tiles
        for (int i = 0; i < inserterTileIDArray.size(); i++) {
            int occ = dbHandler.getOccupationfromTilesByTileID(inserterTileIDArray.get(i));
            if( occ > 0){
                moveBoxAwayByTile(inserterTileIDArray.get(i));
            }
        }
    }

    public void moveRobotAway(Robot robot, int fromTileID){
        // calculating the tiles around the stationary robot
        int tileIDRobot = Sim.calcTileNum(robot.getX(), robot.getY());
        int[] nearbyTiles = calcNearbyTileIDs(tileIDRobot, 1);
        // removing the tile thats on the path of the moving robot
        int[] finalArr = new int[nearbyTiles.length-1];
        int c = 0;
        for (int i = 0; i < nearbyTiles.length; i++) {
            if(nearbyTiles[i] != fromTileID){
                finalArr[c] = nearbyTiles[i];
                c++;
            }
        }
        // pucking one of the availiable tiles index 1 is randomly assigned no system to pick the tile the robot moves to
        int tarTile = finalArr[1];
        int[] tarTileCords = Sim.calcTileCords(tarTile);
        inIdle.remove(robot);
        inUse.add(robot);
        // boxID -1 stands for movement and no box shpuld be moved
        int[] jobArray = new int[]{robot.getX(),robot.getY(),tarTileCords[0], tarTileCords[1], -1};
        robot.newJob(jobArray);

    }

    // ITEM INSERTION

    public void newBox(int level, int tileID){
        int boxCount = dbHandler.getBoxCount();
        Box tempBox = new Box(boxCount+1, level, tileID);
        dbHandler.insertNewBox(boxCount+1, level, tileID);
    }

    public void moveBoxSpecificByTileIDs(int fromTileID, int toTileID){
        // add robot job
        int[] fromCords = Sim.calcTileCords(fromTileID);
        int[] toCords = Sim.calcTileCords(toTileID);
        this.addJobPending(new int[]{fromCords[0],fromCords[1],toCords[0],toCords[1], dbHandler.getTopBoxIDfromTilesbyTileID(fromTileID)});

    }

    public void moveBoxAwayByTile(int tileID){
        // find avalible space nearby
        int targetTileID = checkForAvailableSpace(tileID);
        // move the box
        moveBoxSpecificByTileIDs(tileID, targetTileID);

    }

    public void requestBoxByTileID(int tileID){
        // find empty box nearby
        int originTileID = checkForAvailableBox(tileID);
        // move the box
        moveBoxSpecificByTileIDs(originTileID,tileID);
    }

    public int checkForAvailableBox(int tileID){
        int availbleBoxTileID = -1;
        int rad = 0;
        boolean found = false;
        while(!found){
            rad++;
            int[] nearbyTileIDs = calcNearbyTileIDs(tileID, rad);
            for (int i = 0; i < nearbyTileIDs.length; i++) {
                int topBoxID = dbHandler.getTopBoxIDfromTilesbyTileID(tileID);
                if(dbHandler.getUtilizationByBoxID(topBoxID) <5){
                    found = true;
                    availbleBoxTileID = nearbyTileIDs[i];
                    break;
                }
            }

        }
        return availbleBoxTileID;
    }

    public int checkForAvailableSpace(int tileID){
        // find avalible space nearby
        int availbleSpaceTileID = -1;
        int rad = 0;
        boolean found = false;
        while(!found){
            rad++;
            int[] nearbyTileIDs = calcNearbyTileIDs(tileID, rad);

            // remove all tileID that are collector or inserters. Sets value to -1
            for (int i = 0; i < nearbyTileIDs.length; i++) {
                // colectors
                for (Integer value : collectorTileIDArray) {
                    if (nearbyTileIDs[i] == value) {
                        nearbyTileIDs[i] = -1;
                    }
                }
                // inserters
                for (Integer integer : inserterTileIDArray) {
                    if (nearbyTileIDs[i] == integer) {
                        nearbyTileIDs[i] = -1;
                    }
                }
            }

            for (int i = 0; i < nearbyTileIDs.length; i++) {
                if(nearbyTileIDs[i] != -1){
                    int occ = dbHandler.getOccupationfromTilesByTileID(nearbyTileIDs[i]);
                    if(occ <5){
                        found = true;
                        availbleSpaceTileID = nearbyTileIDs[i];
                        break;
                    }
                }
            }

        }
        return availbleSpaceTileID;
    }

    public boolean insertItem(int itemID){
        // get empty/ space in avalible box
        int inserterBox = dbHandler.getTopBoxIDfromTilesbyTileID(inserterTileIDArray.get(0));
        // validate that there is a box on the tile
        if(inserterBox != -1){
            // check database for utilization
            int insBoxUtil = dbHandler.getUtilizationByBoxID(inserterBox);
            if(insBoxUtil < 5){
                // insert item
                // new Distribution Entry
                dbHandler.addItemToBoxByItemID(itemID, inserterBox);
                // Box Utilization update
                dbHandler.updateBoxUtilizationByBoxID(inserterBox, insBoxUtil+1);
                return true;
            }else{
                moveBoxAwayByTile(inserterTileIDArray.get(0));
                return false;
            }
        }else{
            // job bring box to inserter
            requestBoxByTileID(inserterTileIDArray.get(0));
            return false;
        }
    }

    public int[] calcNearbyTileIDs(int tileID, int radius){
        ArrayList<int[]> pTiles = new ArrayList<>();
        int[] cTile = Sim.calcTileCords(tileID);
        // calculates the nearby tiles with radius
        for (int i = radius*-1; i < radius+1; i++) {
            for (int j = radius*-1; j < radius+1; j++) {
                int[] pTile = new int[2];
                pTile[0] = cTile[0]-(i*10);
                pTile[1] = cTile[1]-(j*10);
                pTiles.add(pTile);
            }
        }
        // removes all the tiles that are out of bounce
        for (int i = pTiles.size()-1; i >= 0; i--) {
            if(pTiles.get(i)[0] < 0 || pTiles.get(i)[1] < 0 || pTiles.get(i)[0] > 790 || pTiles.get(i)[1] > 790){
                //pTiles.set(i, new int[]{-1});
                pTiles.remove(i);
            }
        }
        // remove origin tile
        for (int i = 0; i < pTiles.size(); i++) {
            int[] tcords = Sim.calcTileCords(tileID);
            if(pTiles.get(i)[0] == tcords[0] && pTiles.get(i)[1]== tcords[1])
                pTiles.remove(i);
        }

        // ArrayList to int[] conversion
        int[] retTileIDsArray = new int[pTiles.size()];
        for (int i = 0; i < pTiles.size(); i++) {
            retTileIDsArray[i] = Sim.calcTileNum(pTiles.get(i)[0], pTiles.get(i)[1]);
        }
        return retTileIDsArray;
    }

    // ITEM INSERTION END

    // ITEM ORDERS
    // adds raw order array
    public void addOrder(int[] order){
        orderList.add(order);
    }

    // finished jobs from Order get updated in the corresponding order
    public void jobFromOrderFinished(int orderID, int boxID){
        for (int i = 0; i < orderObjects.size(); i++) {
            if (orderObjects.get(i).getOrderID() == orderID){
                orderObjects.get(i).jobDone(boxID);
            }
        }
    }

    // ITEM ORDERS END

    // GETTERS AND SETTERS

    public void removeOrderObject(Order removeOrder){
        orderObjects.remove(removeOrder);
    }

    public ArrayList<int[]> getOrderList() {
        return orderList;
    }

    public ArrayList<int[]> getOrderListPending() {
        return orderListPending;
    }

    public Robot getFirstInuse() {
        return inUse.get(0);
    }

    public ArrayList<Robot> getInUse() {
        return inUse;
    }

    public void addInUse(Robot robot){
        this.inUse.add(robot);
    }

    public void removeInUse(Robot robot){
        this.inUse.remove(robot);
    }

    public Robot getFirstInIdle() {
        return inIdle.get(0);
    }

    public ArrayList<Robot> getInIdle() {
        return inIdle;
    }

    public void addInIdle(Robot robot){
        this.inIdle.add(robot);
    }

    public void removeInIdle(Robot robot){
        this.inIdle.remove(robot);
    }

    public void addJobPending(int[] arr){
        this.jobsPending.add(arr);
    }

    public ArrayList<int[]> getJobsPending() {
        return jobsPending;
    }
}
