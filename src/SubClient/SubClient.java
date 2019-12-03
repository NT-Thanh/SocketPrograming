package SubClient;

import java.io.*;
import java.net.*;

public class SubClient {
    private Socket sock;

    public SubClient() {
        try {
            sock = new Socket("192.168.1.2", 9981);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                DataInputStream din = new DataInputStream(sock.getInputStream());

                while (true) {
                    long fileSize = din.readLong();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        new LaunchSubServer().start();
        SubClient subClient = new SubClient();
        subClient.run();
    }
}

class LaunchSubServer extends Thread {
    public void run() {
        Server server = new Server();
        server.run();
    }
}

class Server {
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
}

class ClientHandler extends Thread {
    protected Socket clientSocket;

    ClientHandler(Socket _client) {
        this.clientSocket = _client;
    }

    public void run() {
        try {
            DataInputStream din = new DataInputStream(clientSocket.getInputStream());

            while (true) {
                long fileSize = din.readLong();
                if (fileSize > 0) {
                    this.saveFile(din, "TempFile.txt", (int) fileSize);
                } else {
                    System.out.println("File receiving aborted");
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

    private void saveFile(DataInputStream din, String filename, int fileSize) throws Exception {
        FileOutputStream fos = new FileOutputStream("./SharedFolder/" + filename);
        byte[] buffer = new byte[32768];

        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;
        long startTime = System.nanoTime();
        while ((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }
        long endTime = System.nanoTime();
        System.out.println("Transfer duration: " + (endTime - startTime));

        fos.close();
    }
}