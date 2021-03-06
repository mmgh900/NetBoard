package users;

import serlizables.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Connection implements Runnable {
    private final User sender;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Boolean isClosed;


    public Connection(Socket socket, User sender) {
        isClosed = false;
        this.sender = sender;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendPacket(Packet packet) {
        if (!isClosed) {
            try {
                objectOutputStream.reset();
                objectOutputStream.writeObject(packet);
                objectOutputStream.flush();
            } catch (EOFException | SocketException e) {
                isClosed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Boolean isRunning = true;
        while (isRunning) {
            try {
                Object object = objectInputStream.readObject();
                if (object instanceof Packet) {
                    sender.receivePacket((Packet) object);
                } else {
                    throw new Exception("Invalid data received");
                }
            } catch (EOFException | SocketException e) {
                isRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public void close() {
        try {
            isClosed = true;
            objectOutputStream.close();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
