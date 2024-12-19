package Client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileClient {
    private static final String SERVER_IP = "localhost"; // IP of the server | default is localHost
    private static final int PORT = 5000; // Port of the server which we need to connect

    public static void main(String[] args) {

        File file = new File("test.txt");

        try{
            //connecting to the socket
            Socket socket = new Socket(SERVER_IP, PORT);

            // Getting the DataOutputStream to directly update the output stream
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // To read content from the file we use FileInputStream
            FileInputStream fileInputStream = new FileInputStream("/home/vasu-choudhari/Desktop/FirstApp/server/server.js");

            byte[] b = new byte[4096]; // buffer set to 4KB

            int bytesRead = fileInputStream.read(b);
            // IMP ** This will read the file data of length at-most b.length() and will save the bytes
            // in the byte array b and returns the number of bytes it has read
            // so bytesRead is at-most b.length() ie. bytesRead <= b.length()

            while(bytesRead != -1) {
                dataOutputStream.write(b,0,bytesRead);
                // writes the data from the buffer from 0 -> bytesRead

                // Reading next
                bytesRead = fileInputStream.read(b);
            }

            System.out.println("File sent successfully to the server");
            dataOutputStream.close();
            fileInputStream.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
