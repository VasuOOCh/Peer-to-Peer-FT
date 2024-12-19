package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args) {
        final int PORT = 5000; // you may change this port
        try{
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server established ! Waiting for connections...");

            while(true) {
                // the .accept() method will block the next lines until it accepts a connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client PC connected");
                System.out.println("Address : " + clientSocket.getInetAddress());

                // now we will  create a thread to receive the file sent by the client
                // Constructor for thread used : new Thread (Runnable)
                Thread thread = new Thread(new FileReceive(clientSocket));
                thread.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
