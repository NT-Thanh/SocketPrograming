package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private Socket sock;

    public Client(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("use 'list' to see files on Server, 'download' to get file from server, 'upload' to send file to server, '@logout' to finish");
            System.out.print("IP address: ");
            String host = br.readLine();
            System.out.print("Port: ");
            int port = Integer.parseInt(br.readLine());

            sock = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> showFile() {
        File folder = new File("./src/Client/SharedFolder");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> files = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            files.add(listOfFiles[i].getName());
        }
        return files;
    }

    public void run() {
        while (true) {
            try {
                DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                DataInputStream din = new DataInputStream(sock.getInputStream());

                String msgin = "", msgout = "";

                while (!msgout.equals("@logout")){
                    msgout = br.readLine();

                    if(msgout.equals("list")){
                        dout.writeUTF(msgout);
                        dout.flush();

                        msgin = din.readUTF();
                        System.out.println(msgin);
                    }else if(msgout.startsWith("download")){
                        String fileName = msgout.substring(9);
                        dout.writeUTF(msgout);
                        dout.flush();

                        long fileSize = din.readLong();
                        if(fileSize>0){
                            this.saveFile(din, fileName, (int) fileSize);
                        }else{
                            System.out.println("File name did not match");
                        }
                    }else if(msgout.startsWith("upload")){
                        String fileName = msgout.substring(7);
                        dout.writeUTF(msgout);
                        dout.flush();

                        if (this.showFile().contains(fileName)) {
                            dout.writeLong((new File("./src/Client/SharedFolder/" + fileName)).length());
                            dout.flush();
                            this.sendFile(fileName, dout);
                        }else{
                            dout.writeLong(0);
                            System.out.println("File name did not match");
                        }
                    }else if(msgout.equals("@logout")){
                        return;
                    }else{
                        System.out.println("use 'list' to see file on Server, 'download' to get file from server, 'upload' to send file to server, '@logout' to finish");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(DataInputStream din, String filename, int fileSize) throws Exception {
        FileOutputStream fos = new FileOutputStream("./src/Client/" + filename);
        byte[] buffer = new byte[4096];

        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;
        while((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0 ) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }
        fos.close();
    }

    public void sendFile(String file, DataOutputStream dout) throws IOException {
        FileInputStream fis = new FileInputStream("./src/Client/SharedFolder/" + file);
        byte[] buffer = new byte[4096];

        int count;
        while ((count = fis.read(buffer)) > 0) {
            dout.write(buffer, 0, count);
        }
        System.out.println("Sent");
        fis.close();
    }
    public static void main(String args[]){
        Client client = new Client();
        client.run();
    }
}
