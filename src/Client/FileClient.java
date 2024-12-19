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
    private static DataOutputStream dataOutputStream; // this acts as an outputStream of socket
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
            System.out.println("Sending the file");
            // saving the file size and length
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(file.length());

            // To read content from the file we use FileInputStream
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] b = new byte[4096]; // buffer(external) set to 4KB
            // this is not an internal buffer, we have created it so what we can send chunks of data at a time
            // We do not want to read the data byte-by-byte and sent it

            int bytesRead = fileInputStream.read(b);
            // IMP ** This will read the file data of length at-most b.length() and will save the bytes
            // in the buffer byte array b and returns the number of bytes it has actually read
            // so bytesRead is at-most b.length() ie. bytesRead <= b.length()

            while(bytesRead != -1) {
                dataOutputStream.write(b,0,bytesRead);
                // writes the data from the buffer from 0 -> bytesRead

                // Concept: when we are writing in the dataOutputStream we are actually writing inside the internal
                // buffer of the dataOutputStream, this action does not send data over overwork to other PC

                // IMP note: if the internal buffer is full , then data is automatically transferred over the network
                // and internal buffer is cleared

                // Reading next
                bytesRead = fileInputStream.read(b);
            }

            System.out.println("File sent successfully to the server");

            // close the streams
            dataOutputStream.flush(); // flush action: data of the internal buffer is immediately transferred over the
            // network and internal buffer is cleared and OutputStream can now be used to write and send other file
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
