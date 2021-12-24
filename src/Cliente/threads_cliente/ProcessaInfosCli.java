package Cliente.threads_cliente;

import Constantes.Constantes;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import static Constantes.Constantes.TIMEOUT;

public class ProcessaInfosCli extends Thread { //tcp
    private final Socket socket;

    public ProcessaInfosCli(Socket s) {
        socket = s;
    }

    public void enviaMsg(ObjectOutputStream oout, String msg){
        //Ligar ao cliente
        try{
            System.out.println("\nConectando com cliente...");
            socket.setSoTimeout(TIMEOUT * 1000);

            oout.writeUnshared("Não sou cliente");
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String recebemsg(ObjectInputStream oin) {
        //Ligar ao cliente
        String s = null;

        while(true){
            try {
                System.out.println("\nConectando com servidor...");
                socket.setSoTimeout(TIMEOUT * 1000);

                s = (String) oin.readObject();

                return s;
            } catch (SocketException e) {
                System.out.println("Socket: " + e + " - " + e.getCause());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Não recebi nada : " + e);
            }
        }
    }


    @Override
    public void run(){
        try {
            ObjectInputStream oin = null;
            ObjectOutputStream oout = null;

            Scanner sc = new Scanner(System.in);
            String msgFromServer;

            while (true) {
                //Socket SocketTcp = new Socket(serverAddr, serverPortTCP);
                //socketTcp = new Socket(serverAddr, serverPort);

                //Cria os objectos que permitem serializar e deserializar objectos em socket
                oin = new ObjectInputStream(socket.getInputStream());
                oout = new ObjectOutputStream(socket.getOutputStream());

                //lê do teclado
                System.out.print(" > ");
                String msgToServer = sc.nextLine();

                //Serializa a string para o OutputStream associado a socket
                oout.reset(); //para nao enviar a mensagem anterior
                oout.writeObject(msgToServer);
                oout.flush();

                //-----------------------------------------------------------

                //Deserializa a resposta recebida em socket
                msgFromServer = (String) oin.readObject();

                if (msgFromServer == null) {
                    System.out.println("O servidor nao enviou qualquer respota antes de"
                            + " fechar aligacao TCP!");
                } else {
                    System.out.println("Recebido \"" + msgFromServer.trim() + "\" de " +
                            socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }
    }
}
