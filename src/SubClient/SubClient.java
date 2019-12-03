package SubClient;

import java.io.*;
import java.net.*;

public class SubClient {
    private Socket sock;

    public SubClient() {
        try {
            sock = new Socket("10.10.1.2", 9981);
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
                    if (fileSize > 0) {
                        this.saveFile(din, "downloadedFile.txt", (int) fileSize);
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
        System.out.println("Transfer duration: " + (endTime - startTime));
        fos.close();
    }

    public static void main(String args[]) {
        SubClient subClient = new SubClient();
        subClient.run();
    }
}