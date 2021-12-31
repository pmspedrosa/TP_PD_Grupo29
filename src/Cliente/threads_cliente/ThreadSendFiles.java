package Cliente.threads_cliente;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ThreadSendFiles extends Thread {
    private static final int MAX_SIZE = 4000;
    private File localDirectory;
    private String fileName;
    private InetAddress ip;
    private int porto;

    public ThreadSendFiles(File localDirectory, String fileName, InetAddress ip, int porto){
        this.localDirectory = localDirectory;
        this.fileName = fileName;
        this.ip = ip;
        this.porto = porto;
    }

    @Override
    public void run(){
//        File localDirectory = null;
        String canonicalFilePath = null;
        FileInputStream requestedFileInputStream;

        Socket socket = null;
        BufferedReader bin;
        PrintStream pOut;
        int listeningPort;

        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

//        if(args.length != 2){
//            System.out.println("Syntax: java GetFileTcpServer listeningPort localRootDirectory");
//            return;
//        }

//        localDirectory = new File(this.localDirectory.trim());


        if(!localDirectory.exists()){
            System.out.println("Directory: " + localDirectory + " doesn't exist.");
            return;
        }

        if(!localDirectory.isDirectory()){
            System.out.println("Path: " + localDirectory + "isn't a directory.");
            return;
        }

        if(!localDirectory.canRead()){
            System.out.println("No permissions to read from: " + localDirectory + ".");
            return;
        }

        try{
            socket = new Socket(ip, porto);

//            System.out.println("Transfer files server initialized at port " + mySocket.getLocalPort());

//            nextClient = null;

//            try {
//                nextClient = mySocket.accept();

                bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                pOut = new PrintStream(socket.getOutputStream(), true);

//                requestedFileName = bin.readLine();

//                System.out.println("Request received to \"" + requestedFileName + "\" from " + nextClient.getInetAddress().getHostAddress() + ":" + nextClient.getPort());

                canonicalFilePath = new File(localDirectory + File.separator + fileName).getCanonicalPath();

                if (!canonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                    System.out.println("Forbidden access to the file " + canonicalFilePath);
                    System.out.println("Root directory isn't the same as " + localDirectory.getCanonicalPath());
                    return;
                }

                requestedFileInputStream = new FileInputStream(canonicalFilePath);
                System.out.println("File " + canonicalFilePath + " open to read.");

                do {
                    nbytes = requestedFileInputStream.read(fileChunk, 0, MAX_SIZE);

                    if (nbytes == -1) { // EOF warning
                        nbytes = 0;
                    }

                    pOut.write(fileChunk, 0, nbytes);
                    try {
                        Thread.sleep(1); // on same computer so files dont get corrupted
                    } catch (InterruptedException ex) {
                        System.out.println("Erro: " + ex);
                    }

                } while (nbytes > 0);

                System.out.println("Upload completed");

                requestedFileInputStream.close();
//            }finally {
//                socket.close();
//            }
        } catch(NumberFormatException e){
            System.out.println("Port must be a positive integer.");
        } catch(SocketException e) {
            System.out.println("Error at UDP socket:\n\t" + e);
        } catch(FileNotFoundException e){
            System.out.println("Exception {\"" + e + "\"} occurred while opening the file " + canonicalFilePath + ".");
        } catch(IOException e) {
            System.out.println("Error accessing the socket\n\t" + e);
        } finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}