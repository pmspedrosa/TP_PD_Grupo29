package Cliente;

import Cliente.threads_cliente.*;
import Constantes.Constantes;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static Constantes.Constantes.TIMEOUT;

public class Cliente {
    public static final String CONNECT_REQUEST = "CLIENTE";
    public static final int MAX_SIZE = 256;


    public static void main(String[] args) {
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;
        InetAddress serverAddr = null;
        int serverPortTCP = 0;
        String resposta;

        try (DatagramSocket mySocketUdp = new DatagramSocket()) {
            mySocketUdp.setSoTimeout(Constantes.TIMEOUT * 1000);

            if (args.length != 2) {
                System.out.println("Sintaxe: java Servidor.Servidor <IP do Grds.GRDS> <Porto de escuta do Grds.GRDS>");
                return;
            }

            grdsAddress = InetAddress.getByName(args[0]);
            grdsPort = Integer.parseInt(args[1]);

            mypacket = new DatagramPacket(CONNECT_REQUEST.getBytes(), CONNECT_REQUEST.length(), grdsAddress, grdsPort);
            mySocketUdp.send(mypacket);

            mypacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            mySocketUdp.receive(mypacket);

            resposta = new String(mypacket.getData(), 0, mypacket.getLength());
            //System.out.println("PACKET: " + resposta.spl

            String []aux = resposta.split("-");

            serverAddr = InetAddress.getByName(aux[0]);
            serverPortTCP = Integer.parseInt(aux[1]);


            System.out.println("vou ligar a " + serverAddr + ":" + serverPortTCP);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Iniciar comunicação com servidor
        try {
             //se ficar aqui vai dar erro após primeira leitura
            //Socket SocketTcp = new Socket(serverAddr, serverPortTCP);
            //SocketTcp.setSoTimeout(TIMEOUT * 1000);
            ObjectInputStream oin = null;
            ObjectOutputStream oout = null;

            Scanner sc = new Scanner(System.in);
            String msgFromServer;

            while (true) {
                Socket socketTcp = new Socket(serverAddr, serverPortTCP);

                //Cria os objectos que permitem serializar e deserializar objectos em socket
                oin = new ObjectInputStream(socketTcp.getInputStream());
                oout = new ObjectOutputStream(socketTcp.getOutputStream());

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
                            socketTcp.getInetAddress().getHostAddress() + ":" + socketTcp.getPort());
                }
            }

            //ProcessaInfosCli t = new ProcessaInfosCli(socketTcp);
            //t.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }


        /*
        try {
            Socket socketTcp = new Socket(serverAddr, serverPortTCP);

            ProcessaInfosCli t = new ProcessaInfosCli(socketTcp);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}