package Grds;
import Constantes.Constantes;
import Grds.gestao_servidores.Servidor_classe;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class GRDS {
    public static final String SV_CONNECT_REQUEST = "SERVIDOR";
    public static final String CL_CONNECT_REQUEST = "CLIENTE";
    private static final int MAX_SIZE = 120;

    private static ArrayList<Servidor_classe> servers_ativos = new ArrayList<>();
    private static int last_sv_distribuido = 0; //guarda último sv distribuido para o cliente


    public static boolean contem(InetAddress ip, int porto){      //verificar se o servidor que está a contactar já existe na lista de svs ativos ou não
        for(Servidor_classe sv : servers_ativos) {

            if (sv.getIp().equals(ip) && sv.getPorto_escuta_UDP() == porto) {
                sv.setConta_inatividade(0);
                return true;
            }
        }
        return false;
    }


    public static void setServer(InetAddress ip, int porto, int porto_tcp){
        if(contem(ip, porto)) {
            System.out.println("Recebido sinal de vida de: " + ip + ":" + porto);
        } else{
            Servidor_classe s = new Servidor_classe(ip, porto, porto_tcp);
            servers_ativos.add(s);

            ThreadVerificaInatividade thread = new ThreadVerificaInatividade(s);
            thread.start();
        }
    }


    //distribui os clientes pelos servidores ativos, segundo politica round-robin
    public static void distribui_svs(ArrayList<Servidor_classe> servers_ativos, InetAddress ip_cliente, int porto_cliente){
        int size = servers_ativos.size();
        int cont=0;

        if(last_sv_distribuido >= servers_ativos.size()-1)
            last_sv_distribuido = 0;

        if(servers_ativos.size() == 0){
            comunica_via_udp(ip_cliente, porto_cliente, null);
            return;
        }

//        int contador = 0;
//        for (Servidor_classe sv : servers_ativos){
//            if(sv.getConta_inatividade() > 0)
//                contador++;
//        }
//
//        if(contador == servers_ativos.size())
//            comunica_via_udp(ip_cliente, porto_cliente, null);

        else{
            if(last_sv_distribuido == 0){ //primeiro elemento do array de servidores ativos
                if(servers_ativos.get(0).getConta_inatividade() > 0) {
                    do {
                        last_sv_distribuido++;

                        if (last_sv_distribuido > servers_ativos.size()-1) {
                            last_sv_distribuido = 0;
                            comunica_via_udp(ip_cliente, porto_cliente, null);
                            return;
                        }
                    } while (servers_ativos.get(last_sv_distribuido).getConta_inatividade() > 0);
                }

                comunica_via_udp(ip_cliente, porto_cliente, servers_ativos.get(last_sv_distribuido));

                if(servers_ativos.get(0).getConta_inatividade() == 0){
                    last_sv_distribuido++;
                }
            }

            else{ //elemento random
                if(servers_ativos.get(last_sv_distribuido).getConta_inatividade() > 0){
                    do{
                        if(cont > servers_ativos.size()) {
                            comunica_via_udp(ip_cliente, porto_cliente, null);
                            return;
                        }

                        last_sv_distribuido++;
                        cont++;

                        if(last_sv_distribuido > servers_ativos.size()){
                            last_sv_distribuido = 0;
                        }
                    }while(servers_ativos.get(last_sv_distribuido).getConta_inatividade() > 0);
                }
                comunica_via_udp(ip_cliente, porto_cliente, servers_ativos.get(last_sv_distribuido));

                last_sv_distribuido++;

                if(last_sv_distribuido > servers_ativos.size()-1){
                    last_sv_distribuido = 0;
                }
            }
        }
    }


    //contacta Cliente para enviar o IP e o porto de escuta TCP do Servidor designado
    public static void comunica_via_udp(InetAddress ip, int porto, Servidor_classe s){
        System.out.println("comunica_via_udp()");
        DatagramPacket packet = null;

        try(DatagramSocket socket = new DatagramSocket()){
            socket.setSoTimeout(Constantes.TIMEOUT*1000);

            String dadosServidor;

            if(s != null) {
                dadosServidor = ip.getHostAddress() + "-" + s.getPorto_escuta_TCP();
            }else{
                dadosServidor = "SERVER NOT FOUND";
            }
            //Construir um datagrama UDP com o resultado da serialização
            packet = new DatagramPacket(dadosServidor.getBytes(), dadosServidor.length(), ip, porto);
            socket.send(packet);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Problema de I/O\n\t" + e);
        }
    }


    public static void atualizaUDP(InetAddress ip, int portoTCP, int portoUDP, ArrayList<Servidor_classe> s){      //verificar se o servidor que está a contactar já existe na lista de svs ativos ou não
        for(Servidor_classe sv : s) {
            if (sv.getIp() == ip && sv.getPorto_escuta_TCP() == portoTCP) {
                sv.setPorto_escuta_UDP(portoUDP);
            }
        }
    }


    public static void main(String[] args){

        int listeningPort;
        DatagramSocket socket = null;
        DatagramPacket packet; //para receber os pedidos e enviar as respostas
        String receivedMsg, responseMsg;

        if (args.length != 1) {
            System.out.println("Sintaxe: java TcpTimeServer listeningPort");
            return;
        }


        try {
            listeningPort = Integer.parseInt(args[0]); //5001;
            socket = new DatagramSocket(listeningPort);

            System.out.println("GRDS inicializado por protocolo UDP...");

            while(true){
                servers_ativos.removeIf(sv -> sv.getConta_inatividade() >= Constantes.INATIVIDADE_SERVIDOR);

                System.out.println("--------------Esperando--------------");

                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                receivedMsg = new String(packet.getData(), 0, packet.getLength());

                System.out.println("\nRecebido \"" + receivedMsg + "\" de " +
                        packet.getAddress().getHostAddress() + ":" + packet.getPort());


                if(!receivedMsg.split("-")[0].equals(SV_CONNECT_REQUEST)){  //Recebe ligação do servidor

                    if(receivedMsg.equals(CL_CONNECT_REQUEST)) {   //Recebe ligação do cliente
                        for(Servidor_classe sv : servers_ativos){
                            System.out.println(sv.getPorto_escuta_TCP() + " | inatividade = " + sv.getConta_inatividade());
                        }

                        distribui_svs(servers_ativos, packet.getAddress(), packet.getPort());

                        /**
                         * responder ao cliente caso nao exista servidores.
                         */
                    }

                    continue;
                }

                //caso tenha recebido ligação de um servidor...

                setServer(packet.getAddress(), packet.getPort(),
                        Integer.parseInt(receivedMsg.split("-")[1]));

                //System.out.println(getServers_ativos().toString());
                System.out.println("\nSvs ativos: " + servers_ativos.size());
                int i = 0;
                for(Servidor_classe s : servers_ativos) {
                    System.out.println(i + ". " + s.getIp().getHostAddress() + " | porto udp " + s.getPorto_escuta_UDP() + " | porto tcp " + s.getPorto_escuta_TCP() + " | inativ " + s.getConta_inatividade());
                    i++;
                }

                responseMsg = "CONNECTED Servidor"; //mensagem de sucesso

                packet.setData(responseMsg.getBytes());
                packet.setLength(responseMsg.length());

                socket.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}






//responsável por manter o registo dos servidores ativos, direcionar as aplicações cliente
// para os diversos servidores(round - robin) e difundir informação relevante;-----



//Na fase de arranque, os clientes e os servidores recebem o
// endereço IP e o porto de escuta UDP do GRDS. >> utilização de parametros (ao iniciar o programa cliente)-----


//se houver mais que um sv ativo ele distribui os clientes de acordo com escalonamento circular-----


// Os servidores devem enviar, via UDP e com uma periodicidade de 20 segundos,
// uma mensagem ao GRDS com indicação de um porto de escuta TCP (automático)
// em que possam aceitar ligações de clientes. Passados três períodos sem
// receção de mensagens de um determinado servidor, este é “esquecido” pelo
// GRDS;



//Os clientes são encaminhados apenas para
// servidores sem mensagens periódicas em falta;


//Quando um servidor efetua uma alteração na base dados na sequência de
// uma interação com um cliente este envia ao GRDS, via UDP,
// uma mensagem a informar que houve alterações.
//listener
//GRDS reencaminha a informação recebida, via UDP,
// para os restantes servidores ativos.


//Caso tenha sido disponibilizado um ficheiro,
// também devem ser fornecidos o endereço IP do servidor onde
// o ficheiro se encontra e o porto de escuta TCP (automático)
// onde este aceita ligações destinadas exclusivamente à transferência
// de ficheiros;


//Quando um servidor recebe a informação mencionada no ponto anterior,
// este informa os clientes afetados aos quais se encontra conectado
// que houve uma alteração na base de dados e que estes devem atualizar
// as suas vistas (mensagens/notificações visualizadas, número de
// mensagens/notificação não visualizadas, pedidos de contacto ou de
// adesão a grupos, etc.);



//lista de servidores ativos
//ARRAY com servidores { id_sv (index do array), ip_sv, porto de escuta, cont_tentativas }
//quando se adiciona um novo sv verificar se ele já n existe no array (infos duplicadas)


//ARRAY com clientes { ip_client, porto_escuta cliente, id_sv }
//quando se adiciona um novo cliente verificar se ele já n existe no array (infos duplicadas)
//saber a que servidores estão ligados os clientes afetados de modo
//a apenas informar os servidores necessários



//Quando está em causa a disponibilização de um ficheiro,
// os servidores que recebem a informação vinda do GRDS também
// obtêm o ficheiro via uma ligação TCP temporária estabelecida
// com o servidor no endereço IP e porto TCP indicados.
// A transferência deve ser feita em background;



//Quando é solicitada a eliminação de um ficheiro pelo utilizador
// que o disponibilizou, a mensagem enviada para o GRDS também deve
// incluir esta indicação para que todos os servidores apaguem o ficheiro
// nos seus sistemas de ficheiros locais;



//garantir que quando um servidor se connecta este efetua as alterações
// necessárias (apagar ficheiros, duplicar ficheiros,..) /*EXTRA*/ OU
// informar ao utilizador que as informações poderão estar desatualizadas/incorretas




//Quando um servidor termina de forma ordenada/intencional, este encerra
//as ligações TCP ativas, o que faz com que os clientes que se encontram
// ligados a ele também terminem de forma ordenada, informa o GRDS e
// atualiza a informação na base de dados.
