package com.project.net.connect;

import java.io.*;
import java.net.Socket;

public class Connection {



    private final Socket socket;
    private final ConnectionListener connectionListener;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;

    public Connection(ConnectionListener connectionListener, String ipAddress, int port) throws IOException {
        this(connectionListener, new Socket(ipAddress, port));
    }

    public Connection(ConnectionListener connectionListener, Socket socket) throws IOException {
        this.socket = socket;
        this.connectionListener = connectionListener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    connectionListener.onConnectionReady(Connection.this);
                    while (!thread.isInterrupted()){
                        connectionListener.onReceiveString(Connection.this, in.readLine());

                    }
                }catch (IOException e){
                    connectionListener.onException(Connection.this, e);
                }finally {
                    connectionListener.onDisconnect(Connection.this);
                }

            }
        });
        thread.start();
    }



    public synchronized void sendString (String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        }catch (IOException e){
            connectionListener.onException(Connection.this,e);
            disconnect();
        }
    }
    public synchronized void disconnect(){
        thread.interrupt();
        try {
            socket.close();
        }catch (IOException e){
            connectionListener.onException(Connection.this,e);
        }
    }

    @Override
    public String toString(){
        return "Connection: " + socket.getInetAddress() + " : " + socket.getPort();
    }


}
