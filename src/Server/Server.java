package Server;

import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket ss;
    private Socket clientSocket;
    public static final int PORT = 9981;

    public Server(int port) {
        try {
            ss = new ServerSocket(port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                clientSocket = ss.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress() + " port: " + clientSocket.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ClientHandler(clientSocket).start();
        }
    }

    public static void main(String args[]) {
        Server server = new Server(PORT);
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

            String msgin = "";
            while (true) {
                msgin = din.readUTF();
                if (msgin.startsWith("download")) {
                    dout.writeLong((new File("./SharedFolder/TempFile.txt")).length());
                    dout.flush();
                    this.sendFile("TempFile.txt", dout);
                } else if (msgin.equals("@logout")) {
                    clientSocket.close();
                }
            }
        } catch (SocketException se) {
            System.out.println("Client Disconected");
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
}