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

            //*** The read methods of the InputStream blocks next code execution until
            // input data is available, end of file is detected, or an exception is thrown.

            String filename = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            System.out.println("Receiving file of size " + fileSize + " bytes");
            long totalBytesReadAndWrite = 0;


            // storing the last percentage
            int lastPerct = 0;

            File file = new File(filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] b = new byte[4096]; // (external) buffer of 4KB
            int bytesRead = dataInputStream.read(b,0, (int)Math.min(b.length, fileSize-totalBytesReadAndWrite));
            // the .read() function reads at-most b.length size data from the inputStream and stores it in external buffer
            // b

            // the above code worked for me instead of: int bytesRead = dataInputStream.read(b,0, b.length);
            // But note that both does the same thing

            while(totalBytesReadAndWrite < fileSize) { // bytesRead != 1 doesn't work for me, but it is correct logically
                fileOutputStream.write(b,0, bytesRead);
                // this (may) does not write directly to the file, instead it stores it in an internal buffer and
                // when the buffer is full it writes to the file
                totalBytesReadAndWrite += bytesRead;
                int perct = (int)(totalBytesReadAndWrite*100/fileSize);
                if(Math.abs(perct - lastPerct) >= 5) {
                    System.out.println("Receiving file... " + perct + "%");
                    lastPerct = perct;
                }

                bytesRead = dataInputStream.read(b,0, (int)Math.min(b.length, fileSize-totalBytesReadAndWrite));
            }

            fileOutputStream.close();
            // the above command automatically flushes the (leftover) data of the internal buffer and writes to the file

            System.out.println("File received successfully and stored as " + filename);
            System.out.println();


            // *IMP : If we close the dataInputStream, it will directly close the client socket's input Stream because
            // dataInputStream is referencing it.
            // It will not allow to read the next files sent by the client


        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
