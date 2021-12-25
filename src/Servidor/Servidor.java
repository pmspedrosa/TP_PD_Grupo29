package Servidor;

import Servidor.threads_servidor.*;

import java.io.IOException;
import java.net.*;

import Constantes.*;

public class Servidor {
    public static void main(String[] args) {
        String sgdbAddress;
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;
        String receivedMsg;

        try(//DatagramSocket mysocket = new DatagramSocket();
            ServerSocket socket_TCP = new ServerSocket((int)Math.random()%9999)) {
            //mysocket.setSoTimeout(Constantes.TIMEOUT * 1000);

            //por omissão, tenta descobrir o Grds.GRDS no endereço de multicast 230.30.30.30 e porto UDP 3030
            if(args.length == 1) {
                grdsAddress = InetAddress.getByName("230.30.30.30");
                grdsPort = 3030;

                /**
                 * verificar se ligação com base de dados é estabelcida ou não!
                 */
            }
            else if (args.length == 3) {
                grdsAddress = InetAddress.getByName(args[0]);
                grdsPort = Integer.parseInt(args[1]);
            }
            else{
                System.out.println("Sintaxe: java Servidor.Servidor <IP do GRDS> <Porto de escuta do GRDS> <IP do SGBD>");
                System.out.println("OU java Servidor.Servidor <IP do SGBD>");
                return;
            }

            Thread t;
            t = new Thread(new Thread_GRDS(grdsAddress, grdsPort, socket_TCP.getLocalPort()), "Thread GRDS");
            t.start(); //thread responsável pela comunicação com o Grds. GRDS 20s/20s


            Socket toClientSocket;
            //Atender clientes
            while(true){

                toClientSocket = socket_TCP.accept();

                if(toClientSocket!= null){

                    //colocar timeout vai fazer com que passado x tempo sem receber nada do cliente, manda-o embora
                    //toClientSocket.setSoTimeout(1000 * Constantes.TIMEOUT);

                    AtendeCliente t2 = new AtendeCliente(toClientSocket);
                    t2.start();

                }
                else {
                    System.out.println("Não Recebi msg de clientes");
                }
            }

        } catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        } catch(NumberFormatException e) {
            System.out.println("O porto do Grds.GRDS deve ser um inteiro positivo.");
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket:\n\t"+e);
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }
    }
}
