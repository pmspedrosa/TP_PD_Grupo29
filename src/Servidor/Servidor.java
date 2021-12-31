package Servidor;

import BaseDados.SGBD;
import Servidor.threads_servidor.*;

import java.io.IOException;
import java.net.*;

public class Servidor {

    public static void main(String[] args) {
        String sgbdAddress = null;
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;
        String receivedMsg;

        try (//DatagramSocket mysocket = new DatagramSocket();
             ServerSocket socket_TCP = new ServerSocket((int) Math.random() % 9999)) {
            //mysocket.setSoTimeout(Constantes.TIMEOUT * 1000);

            //por omissão, tenta descobrir o Grds.GRDS no endereço de multicast 230.30.30.30 e porto UDP 3030
            if (args.length == 1) {
                grdsAddress = InetAddress.getByName("230.30.30.30");
                grdsPort = 3030;

                /**
                 * verificar se ligação com base de dados é estabelcida ou não!
                 */
            } else if (args.length == 3) {
                grdsAddress = InetAddress.getByName(args[0]);
                grdsPort = Integer.parseInt(args[1]);
                sgbdAddress = args[2];
            } else {
                System.out.println("Sintaxe: java Servidor.Servidor <IP do GRDS> <Porto de escuta do GRDS> <IP do BaseDados.SGBD>");
                System.out.println("OU java Servidor.Servidor <IP do BaseDados.SGBD>");
                return;
            }

            SGBD.setSgbdAddress(sgbdAddress);

            Thread t;
            t = new Thread(new ThreadGRDS(grdsAddress, grdsPort, socket_TCP.getLocalPort()), "Thread GRDS");
            t.start(); //thread responsável pela comunicação com o GRDS 20s/20s

            Socket toClientSocket;

            //Atender clientes
            while (true) {

                toClientSocket = socket_TCP.accept();

                if (toClientSocket != null) {

                    ThreadAtendeCliente t2 = new ThreadAtendeCliente(toClientSocket);
                    t2.start();

                } else {
                    System.out.println("Não Recebi msg de clientes");
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do Grds.GRDS deve ser um inteiro positivo.");
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }
    }
}
