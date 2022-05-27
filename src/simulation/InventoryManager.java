package simulation;

import simulation.Data.DBHandler;

import java.util.ArrayList;

public class InventoryManager {
    private DBHandler dbHandler;
    private Organiser organiser;
    // all orders [orderID, itemID, itemID, itemID, ...]
    private ArrayList<int[]> orderList = new ArrayList<>();
    private ArrayList<Order> orderObjects = new ArrayList<>();

    private int takenOrderID = 0;

    // orders, which are currently executed
    private ArrayList<int[]> orderListPending = new ArrayList<>();


    private ArrayList<Integer> inserterTileIDArray = new ArrayList<>();
    private ArrayList<Integer> collectorTileIDArray = new ArrayList<>();

    public InventoryManager(DBHandler dbHandler, Organiser organiser){
        this.dbHandler = dbHandler;
        this.organiser = organiser;
        inserterTileIDArray.add(4879);
        collectorTileIDArray.add(4799);
    }

    public void processOrders(){
        for (int i = 0; i < orderList.size(); i++) {
            Order tmpOrder = new Order(orderList.get(0), dbHandler, collectorTileIDArray.get(0), takenOrderID, this.organiser);
            takenOrderID++;
            orderObjects.add(tmpOrder);
            orderList.remove(0);
        }
    }

    public void newBox(int level, int tileID){
        int boxCount = dbHandler.getBoxCount();
        Box tempBox = new Box(boxCount+1, level, tileID);
        dbHandler.insertNewBox(boxCount+1, level, tileID);
    }

    public void moveBoxSpecificByTileIDs(int fromTileID, int toTileID){
        // add robot job
        int[] fromCords = Sim.calcTileCords(fromTileID);
        int[] toCords = Sim.calcTileCords(toTileID);
        organiser.addJobPending(new int[]{fromCords[0],fromCords[1],toCords[0],toCords[1], dbHandler.getTopBoxIDfromTilesbyTileID(fromTileID)});

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
            for (int i = 0; i < nearbyTileIDs.length; i++) {
                int occ = dbHandler.getOccupationfromTilesByTileID(nearbyTileIDs[i]);
                if(occ <5){
                    found = true;
                    availbleSpaceTileID = nearbyTileIDs[i];
                    break;
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

        for (int i = radius*-1; i < radius+1; i++) {
            for (int j = radius*-1; j < radius+1; j++) {
                int[] pTile = new int[2];
                pTile[0] = cTile[0]-(i*10);
                pTile[1] = cTile[1]-(j*10);
                pTiles.add(pTile);
            }
        }
        for (int i = 0; i < pTiles.size(); i++) {
            if(pTiles.get(i)[0] < 0 || pTiles.get(i)[1] < 0)
                pTiles.remove(i);
        }
        int[] retTileIDsArray = new int[pTiles.size()];
        for (int i = 0; i < pTiles.size(); i++) {
            retTileIDsArray[0] = Sim.calcTileNum(pTiles.get(i)[0], pTiles.get(i)[1]);
        }
        return retTileIDsArray;
    }

    public void addOrder(int[] order){
        orderList.add(order);
    }

    public ArrayList<int[]> getOrderList() {
        return orderList;
    }

    public ArrayList<int[]> getOrderListPending() {
        return orderListPending;
    }
}
