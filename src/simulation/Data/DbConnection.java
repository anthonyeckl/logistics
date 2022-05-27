package simulation.Data;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class DbConnection {
    public static void main(String[] args) throws ClassNotFoundException {
        String jdbcUrl = "jdbc:sqlite:/E:\\Java\\logistics\\src\\simulation\\Data\\Logistics_Simulation_Database.db";
        Class.forName("org.sqlite.JDBC");
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            String sql = "SELECT * FROM Distribution WHERE LocationID=$1";
            sql = sql.replace("$1", Integer.toString(3));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            System.out.println(result.getInt(4));
            /*

            try {
                String sql = "INSERT INTO Distribution ('ItemID','BoxID') " + "VALUES ($1, $2)";
                sql = sql.replace("$1", "45");
                sql = sql.replace("$2", "87");
                Statement statement = connection.createStatement();
                statement.executeQuery(sql);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }



            Connection connection = DriverManager.getConnection(jdbcUrl);
            String sql = "SELECT * FROM Items WHERE ID=$1";
            sql = sql.replace("$1", Integer.toString(2));
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            String id = result.getString("ID");
            String name = result.getString("ItemName");
            String cat = result.getString("ItemCategory");
            String size = result.getString("ItemSize");
            System.out.println("ID: "+ id + ", ItemName: "+ name +", ItemCategory: "+ cat +", ItemSize: "+ size);

             */
            /*
            while(result.next()){
                String id = result.getString("ID");
                String name = result.getString("ItemName");
                String cat = result.getString("ItemCategory");
                String size = result.getString("ItemSize");
                System.out.println("ID: "+ id + ", ItemName: "+ name +", ItemCategory: "+ cat +", ItemSize: "+ size);
            }
            */
        } catch (SQLException e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
