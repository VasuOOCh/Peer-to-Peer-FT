package Client;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class FileClient {
    private static Socket socket;
    private static DataOutputStream dataOutputStream;
    private static final String SERVER_IP = "localhost"; // IP of the server | default is localHost
    private static final int PORT = 5000; // Port of the server which we need to connect

    public static File selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }else {
            return null;
        }
    }

    public static void sendFile(File file) {
        try{

            // saving the file size and length
            dataOutputStream.writeLong(file.length());
            dataOutputStream.writeUTF(file.getName());

            // To read content from the file we use FileInputStream
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] b = new byte[4096]; // buffer set to 4KB

            int bytesRead = fileInputStream.read(b);
            // IMP ** This will read the file data of length at-most b.length() and will save the bytes
            // in the buffer byte array b and returns the number of bytes it has read
            // so bytesRead is at-most b.length() ie. bytesRead <= b.length()

            while(bytesRead != -1) {
                dataOutputStream.write(b,0,bytesRead);
                // writes the data from the buffer from 0 -> bytesRead

                // Reading next
                bytesRead = fileInputStream.read(b);
            }

            System.out.println("File sent successfully to the server");

            // close the streams
            dataOutputStream.flush();
            fileInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int action;

        try{
            //connecting to the socket and setting up the dataOutputStream for multiple uses
            socket = new Socket(SERVER_IP, PORT);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Information to display for user
            System.out.println("Connected to the server " + socket.getInetAddress());
            System.out.println("Instructions : ");
            System.out.println("Type 1 to send a file or 0 to close the connection and exit");

            action = sc.nextInt();
            while(action != 0) {
                if(action == 1) {
                    File file = selectFile();
                    if(file == null) {
                        System.out.println("No file selected !");
                    }else {
                        sendFile(file);
                    }

                }else {
                    System.out.println("Enter a valid command");
                }

                System.out.println();
                System.out.println("Enter a command (0 or 1)");
                action = sc.nextInt();
            }
            System.out.println("Closing connection...");
            dataOutputStream.close();
            socket.close();
            System.exit(1);
        }
        catch (IOException e) {
            if(Objects.equals(e.getMessage(), "Connection refused")) {
                System.out.println("Failed to establish a connection with a server");
                System.exit(1);
            }
            e.printStackTrace();
        }

    }
}
