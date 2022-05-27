package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Helping {

    public static int[] generateJobArray(){
        int[] arr = new int[4];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (random.nextInt(80))*10;
        }
        return arr;
    }

    public static int[] calcNearbyTileIDs(int tileID, int radius){
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

    public static void main(String[] args) {
        //System.out.println(Sim.calcTileNum(20,0));

        int[] ret = calcNearbyTileIDs(Sim.calcTileNum(0,0),2);
        System.out.println(ret.length);
        for (int i = 0; i < ret.length; i++) {
            System.out.println(ret[i]);
        }
        /*
        ArrayList<int[]> tarraylist = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            tarraylist.add(generateJobArray());
        }
        for (int i = 0; i < tarraylist.size(); i++) {

            System.out.println(tarraylist.get(i)[0]+ ", " + tarraylist.get(i)[1]+ ", " + tarraylist.get(i)[2]+ ", " + tarraylist.get(i)[3]);

        }*/

    }
}
