package simulation.Data;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.sql.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Random;

public class DBHandler {

    private Connection connection;

    public DBHandler() {
        // DB Adress
        String jdbcUrl = "jdbc:sqlite:/D:\\Java\\logistics\\src\\simulation\\Data\\Logistics_Simulation_Database.db";
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e){
            System.out.println("Error. Connection to DB failed");
            e.printStackTrace();
        }
    }

    public void getFromItemsByID(int id){
        try {
            String sql = "SELECT * FROM Items WHERE ID=$1";
            sql = sql.replace("$1", Integer.toString(id));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            String itemid = result.getString("ID");
            String name = result.getString("ItemName");
            String cat = result.getString("ItemCategory");
            String size = result.getString("ItemSize");
            System.out.println("ID: "+ itemid + ", ItemName: "+ name +", ItemCategory: "+ cat +", ItemSize: "+ size);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public int getTileIDfromBoxesByBoxID(int boxID){
        try {
            String sql = "SELECT * FROM Boxes WHERE BoxID=$1";
            sql = sql.replace("$1", Integer.toString(boxID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            return result.getInt("TileID");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public void setTileIDfromBoxesByBoxID(int newTileID, int boxID){
        try {
            String sql = "UPDATE Boxes set TileID=$1 " + "where BoxID=$2";
            sql = sql.replace("$1", Integer.toString(newTileID));
            sql = sql.replace("$2", Integer.toString(boxID));
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getBoxCount(){
        try {
            String sql = "SELECT Count(BoxID) FROM Boxes";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.getInt("count(BoxID)");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public void insertNewBox(int id, int level, int tileID){
        if(ifTileLevelIsEmpty(tileID, level)){
            try {
                // Add new Box entry in Boxes table
                String sql = "INSERT INTO Boxes " + "VALUES ($1, $2, 0)";
                sql = sql.replace("$1", Integer.toString(id));
                sql = sql.replace("$2", Integer.toString(tileID));
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                // change the corresponding Tiles entry. Insert BoxID into the Level column
                String sql2 = "UPDATE Tiles set $3=$4 "+ "where TileID=$5";
                sql2 = sql2.replace("$3", "Level"+level);
                sql2 = sql2.replace("$4", Integer.toString(id));
                sql2 = sql2.replace("$5", Integer.toString(tileID));
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public void addLevelReference(int id, int level, int tileID){
        if(ifTileLevelIsEmpty(tileID, level)){
            try {
                // change the corresponding Tiles entry. Insert BoxID into the Level column
                String sql2 = "UPDATE Tiles set $3=$4 "+ "where TileID=$5";
                sql2 = sql2.replace("$3", "Level"+level);
                sql2 = sql2.replace("$4", Integer.toString(id));
                sql2 = sql2.replace("$5", Integer.toString(tileID));
                Statement statement2 = connection.createStatement();
                statement2.executeQuery(sql2);


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void addTile(int tileID){
        try {
            String sql = "INSERT INTO Tiles " + "VALUES ($1, $2, $2, $2, $2, $2)";
            sql = sql.replace("$1", Integer.toString(tileID));
            sql = sql.replace("$2", "-1");
            Statement statement = connection.createStatement();
            statement.executeQuery(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int[] getBoxIDsfromTilesByTileID(int tileID){
        try {
            String sql = "SELECT * FROM Tiles WHERE TileID=$1";
            sql = sql.replace("$1", Integer.toString(tileID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            int l0 = result.getInt("Level0");
            int l1 = result.getInt("Level1");
            int l2 = result.getInt("Level2");
            int l3 = result.getInt("Level3");
            int l4 = result.getInt("Level4");
            int[] levels = {l0,l1,l2,l3,l4};
            return levels;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new int[0];
        }
    }

    public int getLevelfromTilesbyTileIDBoxID(int tileID, int boxID){
        int[] levels = getBoxIDsfromTilesByTileID(tileID);
        for (int i = 0; i < levels.length; i++) {
            if(levels[i] == boxID)
                return i;
        }
        return -1;
    }

    public int[] getOccupationfromTiles(){
        int[] retArray = new int[6400];
        try {
            String sql2 = "SELECT Occupation FROM Tiles";
            Statement statement2 = connection.createStatement();
            ResultSet result = statement2.executeQuery(sql2);

            ArrayList<String> stringArrayL = new ArrayList<>();
            while(result.next()){
                stringArrayL.add(result.getString("Occupation"));
            }
            for (int i = 0; i < stringArrayL.size(); i++) {
                retArray[i] = Integer.parseInt(stringArrayL.get(i));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new int[0];
        }

        return retArray;
    }

    public int getOccupationfromTilesByTileID(int tileID){
        try {
            String sql2 = "SELECT Occupation FROM Tiles WHERE TileID=$1";
            sql2 = sql2.replace("$1", Integer.toString(tileID));
            Statement statement2 = connection.createStatement();
            ResultSet result = statement2.executeQuery(sql2);
            return Integer.parseInt(result.getString("Occupation"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public int getLevelfromTilesbyTileID(int tileID){
        try {
            String sql = "SELECT * FROM Tiles WHERE TileID=$1";
            sql = sql.replace("$1", Integer.toString(tileID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            int l0 = result.getInt("Level0");
            int l1 = result.getInt("Level1");
            int l2 = result.getInt("Level2");
            int l3 = result.getInt("Level3");
            int l4 = result.getInt("Level4");
            int[] levels = {l0,l1,l2,l3,l4};
            for (int i = 0; i < levels.length; i++) {
                if(levels[i] == -1)
                    return i;
            }
            return -1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public int getTopBoxIDfromTilesbyTileID(int tileID){
        try {
            String sql = "SELECT * FROM Tiles WHERE TileID=$1";
            sql = sql.replace("$1", Integer.toString(tileID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            int l0 = result.getInt("Level0");
            int l1 = result.getInt("Level1");
            int l2 = result.getInt("Level2");
            int l3 = result.getInt("Level3");
            int l4 = result.getInt("Level4");
            int[] levels = {l0,l1,l2,l3,l4};
            if(levels[0] == -1)
                return -1;
            else{
                for (int i = 1; i < levels.length; i++) {
                    if(levels[i] == -1)
                        return levels[i-1];
                }
            }
            return -1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public boolean ifTileLevelIsEmpty(int tileID, int level){

        try {
            String sql = "SELECT * FROM Tiles WHERE TileID=$1";
            sql = sql.replace("$1", Integer.toString(tileID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.getString("Level" + level).equals("-1");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    public void addItemToBoxByItemID(int itemID, int boxID){
        try {
            String sql = "INSERT INTO Distribution ('ItemID','BoxID') " + "VALUES ($1, $2)";
            sql = sql.replace("$1", Integer.toString(itemID));
            sql = sql.replace("$2", Integer.toString(boxID));
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getUtilizationByBoxID(int boxID){
        try {
            String sql = "SELECT Utilization FROM Boxes WHERE BoxID=$1";
            sql = sql.replace("$1", Integer.toString(boxID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            int retInt = Integer.parseInt(result.getString("Utilization"));
            return retInt;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public void updateBoxUtilizationByBoxID(int boxID, int newUtil){
        if(newUtil <= 5 && 0 <= newUtil){
            try {
                // change the corresponding Tiles entry. Insert BoxID into the Level column
                String sql2 = "UPDATE Boxes set $3=$4 "+ "where BoxID=$5";
                sql2 = sql2.replace("$3", "Utilization");
                sql2 = sql2.replace("$4", Integer.toString(newUtil));
                sql2 = sql2.replace("$5", Integer.toString(boxID));
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            System.out.println("new Util Value ou of Bounce: "+ newUtil);
        }

    }


    public void updateTilebyBoxID(int tileID, int boxID, String instruction){
        // instruction Sting "remove"
        if(instruction.equals("remove")){
            try {
                String sql2 = "UPDATE Tiles set $3=$4, $5=$6 "+ "where TileID=$7";
                int level = getLevelfromTilesbyTileIDBoxID(tileID, boxID);
                sql2 = sql2.replace("$3", "Level"+level);
                sql2 = sql2.replace("$4", Integer.toString(-1));
                sql2 = sql2.replace("$5", "Occupation");
                sql2 = sql2.replace("$6", Integer.toString(getOccupationfromTilesByTileID(tileID)-1));
                sql2 = sql2.replace("$7", Integer.toString(tileID));
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        // instruction Sting "add"
        if(instruction.equals("add")){
            try {
                String sql2 = "UPDATE Tiles set $3 = $4, $5 = $6 "+ "where TileID = $7";
                int level = getLevelfromTilesbyTileID(tileID);
                sql2 = sql2.replace("$3", "Level"+level);
                sql2 = sql2.replace("$4", Integer.toString(boxID));
                sql2 = sql2.replace("$5", "Occupation");
                sql2 = sql2.replace("$6", Integer.toString(getOccupationfromTilesByTileID(tileID)+1));
                sql2 = sql2.replace("$7", Integer.toString(tileID));
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql2);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public void updateBoxbyTileID(int boxID, int tileID){
        try {
            String sql2 = "UPDATE Boxes set $3 = $4 "+ "where BoxID = $7";
            sql2 = sql2.replace("$3", "TileID");
            sql2 = sql2.replace("$4", Integer.toString(tileID));
            sql2 = sql2.replace("$7", Integer.toString(boxID));
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(sql2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<int[]> getAllLocationsFromDistributionByItemID(int itemID){
        try {
            String sql = "SELECT * FROM Distribution WHERE ItemID=$1";
            sql = sql.replace("$1", Integer.toString(itemID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            ArrayList<int[]> resultColumns = new ArrayList<>();
            while(result.next()){
                int[] tmpArr = {0,0,0,0};
                tmpArr[0] = result.getInt(1);
                tmpArr[1] = result.getInt(2);
                tmpArr[2] = result.getInt(3);
                tmpArr[3] = result.getInt(4);
                resultColumns.add(tmpArr);
            }

            return resultColumns;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<int[]> getAllLocationsFromDistributionByBoxID(int boxID){
        try {
            String sql = "SELECT * FROM Distribution WHERE BoxID=$1";
            sql = sql.replace("$1", Integer.toString(boxID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            ArrayList<int[]> resultColumns = new ArrayList<>();
            while(result.next()){
                int[] tmpArr = {0,0,0,0};
                tmpArr[0] = result.getInt(1);
                tmpArr[1] = result.getInt(2);
                tmpArr[2] = result.getInt(3);
                tmpArr[3] = result.getInt(4);
                resultColumns.add(tmpArr);
            }

            return resultColumns;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateBoxToUsed(int boxID){
        try {
            String sql2 = "UPDATE Boxes set $3=$4 "+ "where BoxID=$5";
            sql2 = sql2.replace("$3", "Used");
            sql2 = sql2.replace("$4", Integer.toString(1));
            sql2 = sql2.replace("$5", Integer.toString(boxID));
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(sql2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateBoxToNotUsed(int boxID){
        try {
            String sql2 = "UPDATE Boxes set $3=$4 "+ "where BoxID=$5";
            sql2 = sql2.replace("$3", "Used");
            sql2 = sql2.replace("$4", Integer.toString(0));
            sql2 = sql2.replace("$5", Integer.toString(boxID));
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(sql2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getUsageOfBoxByBoxID(int boxID){
        try {
            String sql = "SELECT * FROM Boxes WHERE BoxID=$1";
            sql = sql.replace("$1", Integer.toString(boxID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.getInt(4);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 1;
        }
    }

    public int getUsageOfLocationByLocID(int locID){
        try {
            String sql = "SELECT * FROM Distribution WHERE LocationID=$1";
            sql = sql.replace("$1", Integer.toString(locID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.getInt(4);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 1;
        }
    }

    public int getBoxIDFromLocationByLocID(int locID){
        try {
            String sql = "SELECT * FROM Distribution WHERE LocationID=$1";
            sql = sql.replace("$1", Integer.toString(locID));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result.getInt(3);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }

    public void updateLocationToUsed(int locID){
        try {
            String sql2 = "UPDATE Distribution set $3=$4 "+ "where LocationID=$5";
            sql2 = sql2.replace("$3", "Used");
            sql2 = sql2.replace("$4", Integer.toString(1));
            sql2 = sql2.replace("$5", Integer.toString(locID));
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(sql2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateLocationToNotUsed(int locID){
        try {
            String sql2 = "UPDATE Distribution set $3=$4 "+ "where LocationID=$5";
            sql2 = sql2.replace("$3", "Used");
            sql2 = sql2.replace("$4", Integer.toString(0));
            sql2 = sql2.replace("$5", Integer.toString(locID));
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(sql2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteLocationByLocID(int locID){
        try {
            String sql2 = "DELETE FROM Distribution WHERE LocationID=$1";
            sql2 = sql2.replace("$1", Integer.toString(locID));
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(sql2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
