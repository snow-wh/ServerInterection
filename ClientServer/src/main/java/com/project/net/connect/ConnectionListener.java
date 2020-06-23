package com.project.net.connect;

public interface ConnectionListener {
    void onConnectionReady(Connection connection);
    void onReceiveString(Connection connection, String value);
   // void onReceive(Connection connection, byte[] value);
    void onDisconnect(Connection connection);
    void onException(Connection connection, Exception e);
}
