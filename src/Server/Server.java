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
        while(true){
            try {
                clientSocket = ss.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress() + " port: " + clientSocket.getPort());

                new ClientHandler(clientSocket).start();
            } catch (SocketException se) {
                System.out.println("Client Disconnected");
            } catch (EOFException se) {
                System.out.println("Client goes offline");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.run();
    }
}

class ClientHandler extends Thread {
    protected Socket clientSocket;

    ClientHandler(Socket _client) {
        this.clientSocket = _client;
    }

    public void run() {
        try {

            DataInputStream din = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String msgin = "", msgout = "";
            while (true) {
                if (clientSocket.getInetAddress().toString().equals("/10.10.1.2")) {
                    msgout = br.readLine();
                    if (msgout.startsWith("download")) {
                        String fileName = msgout.substring(9);
                        if (this.showFile().contains(fileName)) {
                            dout.writeUTF(fileName);
                            dout.writeLong((new File("./SharedFolder/" + fileName)).length());
                            dout.flush();
                            this.sendFile(fileName, dout);
                            System.out.println("Sent to Client 1");
                            return;
                        } else {
                            System.out.println("Wrong file name, File sending aborted");
                        }
                    } else if (msgout.equals("@logout")) {
                        clientSocket.close();
                    }
                }
            }
        } catch (SocketException se) {
            System.out.println("Client Disconnected");
        } catch (EOFException se) {
            System.out.println("Client goes offline");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String file, DataOutputStream dout) throws IOException {
        FileInputStream fis = new FileInputStream("./SharedFolder/" + file);
        byte[] buffer = new byte[32768];

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
}