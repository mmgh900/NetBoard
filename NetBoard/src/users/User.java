package users;

import serlizables.Packet;

import java.io.IOException;

public abstract class User {

    final public static int PORT = 6060;
    public SocketStreamManager socketStreamManager;

    public abstract void receivePacket(Packet packet);

    public abstract void close() throws IOException;


}
