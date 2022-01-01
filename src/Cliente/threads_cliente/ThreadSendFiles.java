package Cliente.threads_cliente;

import java.io.*;
import java.net.*;

public class ThreadSendFiles extends Thread {
    private static final int MAX_SIZE = 4000;
    private String localFilePath;
    private String fileName;
    private Socket socket;

    public ThreadSendFiles(String localFilePath, String fileName, Socket socket){
        this.localFilePath = localFilePath;
        this.fileName = fileName;
        this.socket = socket;
    }

    @Override
    public void run() {
        FileInputStream requestedFileInputStream;
        String requestedCanonicalFilePath = null;
        File localDirectory;
        String fileName = null;

        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        PrintStream pout = null;

        fileName = this.fileName.trim();
        localFilePath = "C:/filesToSend";
        localDirectory = new File(localFilePath.trim());

        if (!localDirectory.exists()) {
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }

        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }

        if (!localDirectory.canWrite()) {
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }

        try {
            pout = new PrintStream(socket.getOutputStream(), true);

            requestedCanonicalFilePath = new File(localDirectory + File.separator + fileName).getCanonicalPath();

            if (!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                System.out.println("Forbidden access to the file " + requestedCanonicalFilePath + ".");
                System.out.println("Root directory isn't the same as " + localDirectory.getCanonicalPath() + ".");
                return;
            }

            requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);
            System.out.println("File " + requestedCanonicalFilePath + " open to read.");

            do {
                nbytes = requestedFileInputStream.read(fileChunk, 0, MAX_SIZE);

                if (nbytes == -1) { // EOF warning
                    nbytes = 0;
                }

                pout.write(fileChunk, 0, nbytes);
                try {
                    Thread.sleep(1); // on same computer so files dont get corrupted
                } catch (InterruptedException ex) {

                }

            } while (nbytes > 0);

            System.out.println("Upload completo");

            requestedFileInputStream.close();

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer bloco adicional, podendo a transferencia estar incompleta:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket TCP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket ou ao ficheiro local " + localFilePath + ":\n\t" + e);
        }
    }
}