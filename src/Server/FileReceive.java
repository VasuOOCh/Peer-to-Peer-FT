package Server;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileReceive implements Runnable{
    private Socket clientSocket;
    public FileReceive(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    // this method will run when the thread runs
    @Override
    public void run() {
        try {
            System.out.println("Receiving file from the client");

            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream("received");

            byte[] b = new byte[4096]; // buffer of 4KB
            int bytesRead = dataInputStream.read(b);
            // this will read some (no known exactly) number of bytes from the inputStream and stores in the
            // buffer b and returns the number of bytes read

            while(bytesRead != -1) {
                fileOutputStream.write(b,0, bytesRead);
                bytesRead = dataInputStream.read(b);
            }

            System.out.println("File received successfully and stored as received");
            System.out.println();
            System.out.println("Waiting for new connections...");
            fileOutputStream.close();
            dataInputStream.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
