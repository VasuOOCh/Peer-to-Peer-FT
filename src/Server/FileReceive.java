package Server;

import java.io.DataInputStream;
import java.io.File;
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
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

            long fileSize = dataInputStream.readLong();
            System.out.println("Receiving file of size " + fileSize + " bytes");
            long totalBytesReadAndWrite = 0;
            String filename = dataInputStream.readUTF();

            File file = new File(filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] b = new byte[4096]; // buffer of 4KB
            int bytesRead = dataInputStream.read(b,0, (int)Math.min(b.length, fileSize-totalBytesReadAndWrite));
            // this will read some (no known exactly) number of bytes from the inputStream and stores in the
            // buffer b and returns the number of bytes read

            while(totalBytesReadAndWrite < fileSize) {
                fileOutputStream.write(b,0, bytesRead);
                totalBytesReadAndWrite += bytesRead;
                bytesRead = dataInputStream.read(b,0, (int)Math.min(b.length, fileSize-totalBytesReadAndWrite));
            }

            System.out.println("File received successfully and stored as " + filename);
            System.out.println();
//            System.out.println("Waiting for new connections...");
            // closing the resources
            fileOutputStream.close();
//            dataInputStream.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
