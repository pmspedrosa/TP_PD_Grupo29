package Cliente;

import Cliente.threads_cliente.*;
import Constantes.Constantes;


import java.io.*;
import java.net.*;

public class Cliente {
    public static final String CONNECT_REQUEST = "CLIENTE";
    public static final int MAX_SIZE = 256;

    private static boolean sv_off = false;
    private static boolean client_exit = false;

    static Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread th, Throwable ex) {
            sv_off = true;
            
            if(Constantes.DEBUG)
                System.out.println(ex.toString());
        }
    };

    public static void main(String[] args) {
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;
        InetAddress serverAddr = null;
        int serverPortTCP = 0;
        String resposta;

        boolean conectado = false;
        boolean GRDS_on = false;
        int contador_timeout = 0;

        do {
            try (DatagramSocket mySocketUdp = new DatagramSocket()) {
                mySocketUdp.setSoTimeout(Constantes.TIMEOUT * 1000);

                if (args.length != 3) {
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

                if(resposta != null) {
                    GRDS_on = true;

                    if (resposta.equals("SERVER NOT FOUND"))
                        System.out.println("Não existem servidores disponíveis!");

                    else {
                        String[] aux = resposta.split("-");

                        serverAddr = InetAddress.getByName(aux[0]);
                        serverPortTCP = Integer.parseInt(aux[1]);

                        conectado = true;

                        System.out.println("vou ligar a " + serverAddr + ":" + serverPortTCP);
                    }
                }

            } catch (SocketTimeoutException e) {
                System.out.println("Gestor de Servidores offline [Error: " + e.getMessage() +"]");
                contador_timeout++;
                GRDS_on = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Iniciar comunicação com servidor através de thread
            try {
                //Socket socketTcp = new Socket(serverAddr, serverPortTCP);

                if (serverAddr != null) {
                    //
                    File localDirectory;
                    localDirectory = new File(args[2].trim());
                    //
                    ThreadComunicaServidor t = new ThreadComunicaServidor(serverAddr, serverPortTCP, localDirectory);
                    t.setUncaughtExceptionHandler(h);
                    t.start();

                    if(sv_off)
                        conectado = false;

                } else if(GRDS_on) {
                    System.out.println("A tentar estabelecer ligação... (aguarde 10 segundos)\n\n\n");
                    contador_timeout++;
                    Thread.sleep(10000);
                }

                if(contador_timeout == Constantes.CONTADOR_TIMEOUT) {
                    System.out.println("Não existem servidores disponíveis. Por favor contacte o administrador do sistema.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            do{
                try {
                    Thread.sleep(5000);
                    if(sv_off) {
                        sv_off = false;
                        conectado = false;
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while(true);
        }while(!conectado);
    }
}
