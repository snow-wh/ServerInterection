package com.project.net.connect;

import java.security.MessageDigest;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;

public class ConnectionDB {

    private final String URL = "jdbc:mysql://localhost:3306/connection";
    private final String USERNAME = "root";
    private final String PASSWORD = "admin";

    private final String UPDATE = "update salt ";
    private final String Get_ID = "select id from connections where mac in ";
    private final String Get_IDConnection = "select id_connection from connections where mac in ";
    private final String Get_HASH = "select hash from connections where id= ";
    private final String Get_HASHSUM = "select salt from connections where id= ";



    private Connection connection;
    private PreparedStatement preparedStatement;

    public ConnectionDB(){
        try {

            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int GetIDConnection(String mac){
        int id = 0;
        try {

            String reqest = Get_IDConnection + "('" + mac + "')";

            preparedStatement = connection.prepareStatement(reqest);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id_connection");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void CreateTable(int idConnection){

        try {

            Statement preparedStatement = connection.createStatement();

            preparedStatement.executeUpdate("create table "+idConnection+" (id int primary key auto_increment," +
                    "device_name varchar(20), status int )");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void SetTable(int idConnection, String[] data,int[] status){

        try {

            preparedStatement = connection.prepareStatement("insert into"+idConnection+" values(?,?,?)");

            for (int i = 0; i < data.length; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2,data[i]);
                preparedStatement.setInt(3, status[i]);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetID(String mac){
        int id = 0;
        try {

            String reqest = Get_ID + "('" + mac + "')";

            preparedStatement = connection.prepareStatement(reqest);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }



    public void SetSalt (String date, int id) {


        try {

            Statement preparedStatement = connection.createStatement();

            preparedStatement.executeUpdate("update connections SET salt ="+"('"+date+"')"+ " WHERE id ="+ id);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String GetHas(int id){
      

        String hash="";

        try {

            String reqest = Get_HASH + id;

            preparedStatement = connection.prepareStatement(reqest);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                hash = resultSet.getString("hash");
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hash;
    }
   public String GetSalt(int id){

        String hash="";

        try {

            String reqest = Get_HASHSUM + id;

            preparedStatement = connection.prepareStatement(reqest);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                hash = resultSet.getString("salt");
            }



        } catch (SQLException e) {
            e.printStackTrace();
            hash="";
        }
        return hash;
    }


}
