package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private Socket sock;
    private String host;

    public Client(String _host, int port) {
        this.host = _host;
        try {
            sock = new Socket(_host, port);
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
                if (!this.host.equals("192.168.1.2")) {
                    while (true) {
                        long fileSize = din.readLong();
                        if (fileSize > 0) {
                            this.saveFile(din, "TempFile.txt", (int) fileSize);
                            Client client1to2 = new Client("10.10.2.2", 9981);
                            client1to2.run();
                            Client client1to3 = new Client("10.10.3.2", 9981);
                            client1to3.run();
                        } else {
                            System.out.println("File name did not match");
                        }
                    }
                }else {
                    sendFile("TempFile.txt", dout);
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
        System.out.println("Transfer duration: " + (endTime - startTime));
        fos.close();
    }

    public void sendFile(String file, DataOutputStream dout) throws IOException {
        FileInputStream fis = new FileInputStream("./SharedFolder/" + file);
        byte[] buffer = new byte[32768];

        int count;
        while ((count = fis.read(buffer)) > 0) {
            dout.write(buffer, 0, count);
        }
        System.out.println("Sent");
        fis.close();
    }

    public static void main(String args[]) {
        Client client = new Client("192.168.1.2", 9981);
        client.run();
    }
}