package Servidor.threads_servidor;

import java.net.InetAddress;
import java.net.Socket;

public class ThreadTrocaFiles  extends Thread { //tcp

    public ThreadTrocaFiles() {
    }

    @Override
    public void run(){
        int contador = 1;
        do {
            try {
                System.out.println("EPAAH RECEBI UM FICHEIRO XD VOU TRATAR DELE LOL");
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            contador ++;
        }while(contador < 5);
    }
}


/*public class GetFileTcpServer {

    public static final int MAX_SIZE = 400000;
    public static final int TIMEOUT = 10;  //segundos

    public static void main(String[] args) {

        File localDirectory;
        String requestedFileName, requestedCanonicalFilePath = null;
        FileInputStream requestedFileInputStream = null;

        int listeningPort;
        ServerSocket serverSocket = null;
        Socket socket;
        BufferedReader bin;
        OutputStream out;

        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        if (args.length != 2) {
            System.out.println("Sintaxe: java GetFileUdpServer listeningPort localRootDirectory");
            return;
        }

        localDirectory = new File(args[1].trim());

        if (!localDirectory.exists()) {
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }

        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }

        if (!localDirectory.canRead()) {
            System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
            return;
        }

        try {

            listeningPort = Integer.parseInt(args[0]);
            if (listeningPort <= 0)
                throw new NumberFormatException("Porto UDP de escuta indicado <= 0 (" + listeningPort + ")");

            serverSocket = new ServerSocket(listeningPort);

            while (true) {

                socket = serverSocket.accept();
                socket.setSoTimeout(TIMEOUT);

                try {

                    bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = socket.getOutputStream();

                    requestedFileName = bin.readLine();

                    System.out.println("Recebido pedido para \"" + requestedFileName + "\" de " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                    requestedCanonicalFilePath = new File(localDirectory + File.separator + requestedFileName).getCanonicalPath();

                    if (!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                        System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                        System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath() + "!");
                        continue;
                    }

                    requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);
                    System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");

                    do {
                        nbytes = requestedFileInputStream.read(fileChunk);

                        if (nbytes == -1) {//EOF
                            out.write(fileChunk, 0, nbytes);
                            out.flush();
                        }

                    } while (nbytes > 0);

                    System.out.println("Transferencia concluida");

                    requestedFileInputStream.close();
                    requestedFileInputStream = null;

                } catch (SocketTimeoutException socketException) {
                    System.out.println("O cliente atual nao enviou qualquer nome de ficheiro(timeout)");
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException ioException) {
                    System.out.println("Problema de I/O no atendimenro ao cliente atual");
                }finally {
                        try{
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(requestedFileInputStream != null){
                            try{
                                requestedFileInputStream.close();
                                requestedFileInputStream = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                }
            }

        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu uma excepcao ao nivel do socket UDP:\n\t" + e);
        } catch (FileNotFoundException e) {   //Subclasse de IOException
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro " + requestedCanonicalFilePath + "!");
        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(requestedFileInputStream != null){
                    try {
                        requestedFileInputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } //try*/