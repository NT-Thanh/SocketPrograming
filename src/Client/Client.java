package Client;

import java.io.*;
import java.net.*;

public class Client {
    private Socket sock;
    public static boolean GOTFILE = false;

    public Client() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("use 'download' to get file from server");
            System.out.print("IP address: ");
            String host = br.readLine();
            System.out.print("Port: ");
            int port = Integer.parseInt(br.readLine());

            sock = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run() {
        while (true) {
            try {
                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                DataInputStream din = new DataInputStream(sock.getInputStream());

                String msgin = "", msgout = "";

                while (!msgout.equals("@logout")) {
                    msgout = br.readLine();

                    if (msgout.startsWith("download")) {
                        dout.writeUTF(msgout);
                        dout.flush();

                        long fileSize = din.readLong();
                        if (fileSize > 0) {
                            this.saveFile(din, "downloadedFile.txt", (int) fileSize);
                        }
                    } else if (msgout.equals("@logout")) {
                        return;
                    } else {
                        System.out.println("use 'download' to get file from server");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(DataInputStream din, String filename, int fileSize) throws Exception {
        FileOutputStream fos = new FileOutputStream("./" + filename);
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
        GOTFILE = true;
        System.out.println("Transfer duration: " + (endTime - startTime));
        fos.close();
    }
    public static void main(String args[]) {
        new LaunchSubServer().start();
        Client client = new Client();
        client.run();
    }
}
class LaunchSubServer extends Thread{
    public void run(){
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
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

            String msgin = "";
            while (true) {
                if (Client.GOTFILE) {
                    dout.writeLong((new File("./SharedFolder/TempFile.txt")).length());
                    dout.flush();
                    this.sendFile("TempFile.txt", dout);
                } else if (msgin.equals("@logout")) {
                    clientSocket.close();
                }
            }
        } catch (SocketException se) {
            System.out.println("Client Disconected");
        } catch (EOFException ex){
            System.out.println("Client Offline");
        }catch (Exception e) {
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