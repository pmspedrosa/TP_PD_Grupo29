package Servidor.threads_servidor;

import java.io.*;
import java.net.Socket;

public class ThreadReceiveFiles extends Thread{
    private static final String localPath = "C://receivedFiles";

    public static final int MAX_SIZE = 400000;
    public static final int TIMEOUT = 5; //segundos

    private final Socket fromClient;
    private final String nomeFile;

    public ThreadReceiveFiles(Socket s, String nomeFile) {
        fromClient = s;
        this.nomeFile = nomeFile;
    }

    @Override
    public void run(){
        try /*(Socket socket = new Socket(ip, porto); ObjectOutputStream oout = new ObjectOutputStream(fromClient.getOutputStream());
            ObjectInputStream oin = new ObjectInputStream(fromClient.getInputStream());)*/ {
            System.out.println("ENTREI NA THREAD DOS FICHEIROS!!!");

            //fromClient.setSoTimeout(TIMEOUT * 1000);

            //PrintStream pout = new PrintStream(fromClient.getOutputStream(), true);
            //pout.println(nomeFile);

            //verificar se efetivamente está a receber algo xd

            File localDirectory;
            localDirectory = new File(localPath); // TODO: 31/12/2021 ALTERAR! Tem de ser definido no servidor com ajuda do GRDS (o local path)

            if(!localDirectory.exists()){
                System.out.println("A directoria " + localDirectory + " nao existe!");

                try{
                    System.out.println("A tentar criar a diretoria " + localDirectory);
                    if (localDirectory.mkdir()) {
                        System.out.println("Diretoria " + localDirectory + " criada!");
                    } else {
                        System.out.println("Erro ao criar diretoria!");
                    }
                }catch (Exception e){
                    System.out.println(e.toString() + " | " + e.getCause());
                }
                return;
            }

            if(localDirectory.isFile()){
                System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
                return;
            }
            if(!localDirectory.canWrite()){
                System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
                return;
            }

            String localFilePath = new File(localDirectory.getPath() + File.separator + nomeFile).getCanonicalPath();
            FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath);

            InputStream in = fromClient.getInputStream();

            System.out.println(fromClient.getReceiveBufferSize());

            int nBytes;
            byte[] buff = new byte[MAX_SIZE];

            do {
                nBytes = in.read(buff);

                if (nBytes > 0)
                    localFileOutputStream.write(buff, 0, nBytes);

            } while (nBytes > 0);

            System.out.println("Transferencia concluida.");


        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}


/*
 *

 //try {


 /*File localDirectory;
 String fileName, localFilePath = null;
 InetAddress serverAddr;
 int serverPort;
 Socket mysocket = null;
 PrintStream pout;
 InputStream in;
 FileOutputStream localFileOutputStream = null;
 //boolean isFromFileServer;
 int nBytes;
 byte[] buff = new byte[MAX_SIZE];


 //Message message = sendFile();


 fileName = message.getargumentos[1].trim();

 try {

 serverAddr = InetAddress.getByName(args[0]);
 serverPort = Integer.parseInt(args[1]);

 mysocket = new Socket(serverAddr,serverPort);       //estabelecer ligação
 mysocket.setSoTimeout(TIMEOUT * 1000);

 pout = new PrintStream(mysocket.getOutputStream(), true);
 in = mysocket.getInputStream();

 pout.println(fileName);

 System.out.println(mysocket.getReceiveBufferSize());

 do {

 nBytes = in.read(buff);

 if(nBytes>0)
 localFileOutputStream.write(buff, 0, nBytes);

 } while (nBytes > 0);

 System.out.println("Transferencia concluida.");

 } catch (UnknownHostException e) {
 System.out.println("Destino desconhecido:\n\t" + e);
 } catch (NumberFormatException e) {
 System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
 } catch (SocketTimeoutException e) {
 System.out.println("Nao foi recebida qualquer bloco adicional, podendo a transferencia estar incompleta:\n\t" + e);
 } catch (SocketException e) {
 System.out.println("Ocorreu um erro ao nivel do mysocket UDP:\n\t" + e);
 } catch (IOException e) {
 System.out.println("Ocorreu um erro no acesso ao mysocket ou ao ficheiro local " + localFilePath + ":\n\t" + e);
 }

 } finally {

 if (mysocket != null) {
 try {
 mysocket.close();
 } catch (IOException e) {
 e.printStackTrace();
 }
 }

 if (localFileOutputStream != null) {
 try {
 localFileOutputStream.close();
 } catch (IOException e) {
 e.printStackTrace();
 }

 }


 }

       /* } catch (Exception e) {
            e.printStackTrace();
        }
 **/



/*import java.io.*;
import java.net.*;

public class GetFileTcpClient {

    public static final int MAX_SIZE = 400000;
    public static final int TIMEOUT = 5; //segundos

    public static void main(String[] args) {
        File localDirectory;
        String fileName, localFilePath = null;
        InetAddress serverAddr;
        int serverPort;
        Socket mysocket = null;
        PrintStream pout;
        InputStream in;
        FileOutputStream localFileOutputStream = null;
        //boolean isFromFileServer;
        int nBytes;
        byte[] buff = new byte[MAX_SIZE];



        if (args.length != 4) {
            System.out.println("Sintaxe: java GetFileUdpClient serverAddress serverUdpPort fileToGet localDirectory");
            return;
        }

        fileName = args[2].trim();
        localDirectory = new File(args[3].trim());

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

            try {

                localFilePath = localDirectory.getCanonicalPath() + File.separator + fileName;
                localFileOutputStream = new FileOutputStream(localFilePath);

                System.out.println("Ficheiro " + localFilePath + " criado.");

            } catch (IOException e) {

                if (localFilePath == null) {
                    System.out.println("Ocorreu a excepcao {" + e + "} ao obter o caminho canonico para o ficheiro local!");
                } else {
                    System.out.println("Ocorreu a excepcao {" + e + "} ao tentar criar o ficheiro " + localFilePath + "!");
                }

                return;
            }

            try {

                serverAddr = InetAddress.getByName(args[0]);
                serverPort = Integer.parseInt(args[1]);

                mysocket = new Socket(serverAddr,serverPort);       //estabelecer ligação
                mysocket.setSoTimeout(TIMEOUT * 1000);

                pout = new PrintStream(mysocket.getOutputStream(), true);
                in = mysocket.getInputStream();

                pout.println(fileName);

                System.out.println(mysocket.getReceiveBufferSize());

                do {

                    nBytes = in.read(buff);

                    if(nBytes>0)
                        localFileOutputStream.write(buff, 0, nBytes);

                } while (nBytes > 0);

                System.out.println("Transferencia concluida.");

            } catch (UnknownHostException e) {
                System.out.println("Destino desconhecido:\n\t" + e);
            } catch (NumberFormatException e) {
                System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
            } catch (SocketTimeoutException e) {
                System.out.println("Nao foi recebida qualquer bloco adicional, podendo a transferencia estar incompleta:\n\t" + e);
            } catch (SocketException e) {
                System.out.println("Ocorreu um erro ao nivel do mysocket UDP:\n\t" + e);
            } catch (IOException e) {
                System.out.println("Ocorreu um erro no acesso ao mysocket ou ao ficheiro local " + localFilePath + ":\n\t" + e);
            }

        } finally {

            if (mysocket != null) {
                try {
                    mysocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (localFileOutputStream != null) {
                try {
                    localFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }

    }
}
*/