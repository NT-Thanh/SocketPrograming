package Server;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
                if (msgin.equals("list")) {
                    dout.writeUTF(String.join(",", this.showFile()));
                    dout.flush();
                } else if (msgin.startsWith("download")) {
                    String fileName = msgin.substring(9);
                    if (this.showFile().contains(fileName)) {
                        dout.writeLong((new File("./src/Server/SharedFolder/" + fileName)).length());
                        dout.flush();
                        this.sendFile(fileName, dout);
                    } else {
                        dout.writeLong(0);
                        System.out.println("File sending aborted");
                    }
                } else if (msgin.startsWith("upload")) {
                    String fileName = msgin.substring(7);

                    long fileSize = din.readLong();
                    if (fileSize > 0) {
                        this.saveFile(din, fileName, (int) fileSize);
                    } else {
                        System.out.println("File receiving aborted");
                    }
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
        FileInputStream fis = new FileInputStream("./src/Server/SharedFolder/" + file);
        byte[] buffer = new byte[4096];

        int count;
        while ((count = fis.read(buffer)) > 0) {
            dout.write(buffer, 0, count);
        }

        fis.close();
    }

    private void saveFile(DataInputStream din, String filename, int fileSize) throws Exception {
        FileOutputStream fos = new FileOutputStream("./src/Server/SharedFolder/" + filename);
        byte[] buffer = new byte[4096];

        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;
        while ((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }
        fos.close();
    }

    public ArrayList<String> showFile() {
        File folder = new File("./src/Server/SharedFolder");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> files = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            files.add(listOfFiles[i].getName());
        }
        return files;
    }
}