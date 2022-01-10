package simulation;

import java.util.ArrayList;
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

    public static void main(String[] args) {
        ArrayList<int[]> tarraylist = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            tarraylist.add(generateJobArray());
        }
        for (int i = 0; i < tarraylist.size(); i++) {

            System.out.println(tarraylist.get(i)[0]+ ", " + tarraylist.get(i)[1]+ ", " + tarraylist.get(i)[2]+ ", " + tarraylist.get(i)[3]);

        }
    }
}
