package users;

import Serlizables.Packet;
import Serlizables.UserInfo;

import java.io.IOException;

public abstract class User {

    final public static int PORT = 6060;
    public UserInfo userInfo = new UserInfo();
    public Connection connection;

    public abstract void receivePacket(Packet packet);

    public abstract void close() throws IOException;


}
