package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket ss;
    private Socket clientSocket;
    public static final int PORT = 9981;

    public Server() {
        try {
            ss = new ServerSocket(PORT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            clientSocket = ss.accept();
            System.out.println("Connection accepted from: " + clientSocket.getInetAddress() + " port: " + clientSocket.getPort());

            DataInputStream din = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String msgin = "", msgout = "";
            while (true) {
                msgout = br.readLine();
                if (msgout.startsWith("download")) {
                    String fileName = msgout.substring(9);
                    if (this.showFile().contains(fileName)) {
                        dout.writeLong((new File("./SharedFolder/" + fileName)).length());
                        dout.flush();
                        this.sendFile(fileName, dout);
                    } else {
                        dout.writeLong(0);
                        System.out.println("File sending aborted");
                    }
                } else if (msgout.equals("@logout")) {
                    clientSocket.close();
                }
            }
        } catch (SocketException se) {
            System.out.println("Client Disconected");
        } catch (EOFException se) {
            System.out.println("Client goes offline");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendFile(String file, DataOutputStream dout) throws IOException {
        FileInputStream fis = new FileInputStream("./SharedFolder/" + file);
        byte[] buffer = new byte[4096];

        int count;
        while ((count = fis.read(buffer)) > 0) {
            dout.write(buffer, 0, count);
        }
        fis.close();
    }

    public ArrayList<String> showFile() {
        File folder = new File("./SharedFolder");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> files = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            files.add(listOfFiles[i].getName());
        }
        return files;
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.run();
    }
}
