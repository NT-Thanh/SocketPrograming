package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private Socket sock;

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
    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }
}
