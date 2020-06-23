package com.project.net.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.debug.JOptionPaneTest;
import com.project.net.connect.*;
import com.project.net.packet.ToPacket;
import com.project.protocol.MHAP;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements ConnectionListener {

    private MHAP mhap ;
    private ToPacket packet = new ToPacket();

    private int id;

    public static void main(String[] args) {

        new Server();

    }


    private final ArrayList<Connection> connections = new ArrayList<Connection>();

    private Server() {
        System.out.println("Server started");
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("Connection exception " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

   @Override
   public synchronized void onConnectionReady(Connection connection) {
       connections.add(connection);
       System.out.println("Client connected " + connection);
   }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("Connection exception " + e);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        System.out.println("Client disconnected " + connection);
    }

    @Override
    public synchronized void onReceiveString(Connection connection, String value) {

        System.out.println(value);


            try {
                packet = new ObjectMapper()
                        .readerFor(ToPacket.class)
                        .readValue(value);

                mhap = new MHAP(packet.GetCode(),packet.GetID(),packet.GetLength(),packet.GetDate());
                if(id==0){
                    id=mhap.GetID();
                }

                mhap.GetIDConnection(id);
                String str = packet.Convert(mhap.CreatePacket());
                
                connection.sendString(str);

            } catch (JsonProcessingException e) {
                System.out.println("error to JsonFile");
            }



    }

   



}
