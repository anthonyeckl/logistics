package simulation.Data;

import java.sql.*;

public class DbConnection {
    public static void main(String[] args) throws ClassNotFoundException {
        String jdbcUrl = "jdbc:sqlite:/E:\\Java\\logistics\\src\\simulation\\Data\\Logistics_Simulation_Database.db";
        Class.forName("org.sqlite.JDBC");
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            String sql = "SELECT * FROM Items";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                String id = result.getString("ID");
                String name = result.getString("ItemName");
                String cat = result.getString("ItemCategory");
                String size = result.getString("ItemSize");
                System.out.println("ID: "+ id + ", ItemName: "+ name +", ItemCategory: "+ cat +", ItemSize: "+ size);
            }
        } catch (SQLException e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
