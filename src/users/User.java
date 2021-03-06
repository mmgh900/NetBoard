package users;

import serlizables.Packet;

import java.io.IOException;

public abstract class User {

    final public static int PORT = 6060;
    public Connection connection;

    public abstract void receivePacket(Packet packet);

    public abstract void close() throws IOException;


}
