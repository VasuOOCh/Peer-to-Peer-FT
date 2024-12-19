package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileServer {
    public static void main(String[] args) {
        final int PORT = 5000; // you may change this port
        int action;
        Scanner sc = new Scanner(System.in);

        try{
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server established ! Waiting for connections...");

            while(true) {

                // the .accept() method will block the next lines until it accepts a connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client PC connected " + clientSocket.getInetAddress());

                System.out.println("Press 1 to receive file or 0 to bypass this client");
                action = sc.nextInt();

                // if action is 0 we are bypassing the client connection i.e. ignoring it

                while(action != 0) {
                    if(action == 1) {

                        // now we will  create a thread to receive the file sent by the client
                        // Constructor for thread used : new Thread (Runnable)

                        Thread thread = new Thread(new FileReceive(clientSocket));
                        thread.start();

                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Press 1 to receive file or 0 to bypass this client");
                        action = sc.nextInt();

                    }else if(action != 1 && action !=0) {
                        System.out.println("Please enter a valid command");
                        action = sc.nextInt();
                    }
                }

                System.out.println("Waiting for connections...");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
