import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;


public class GRDS {
    public static final String SV_CONNECT_REQUEST = "SERVIDOR";
    public static final String CL_CONNECT_REQUEST = "CLIENTE";
    private static final int MAX_SIZE = 120;
    private static final int TIMEOUT = 10; //10 seg (multiplica por 1000 abaixo)

   //private ArrayList<Servidores> servers_ativos = new ArrayList<>();
    private static int last_sv_distribuido = -1; //guarda último sv distribuido para o cliente


//    public ArrayList<Servidores> getServers_ativos(){
//        return servers_ativos;
//    }
//
//    public int getNum_Servers_ativos(){
//        return servers_ativos.size();
//    }

    public static boolean contem(InetAddress ip, int porto, ArrayList<Servidor_classe> s){      //verificar se o servidor que está a contactar já existe na lista de svs ativos ou não
        for(Servidor_classe sv : s) {
            System.out.println("\n<<DEBUG> " + ip + "-" +porto + " || " + sv.getIp() + "-" + sv.getPorto_escuta_UDP() + "\n");
            if (sv.getPorto_escuta_UDP() == porto) { //falta verificar ip!!!
                return true;
            }
        }
        return false;
    }

    public static void setServer(ArrayList<Servidor_classe> servers_ativos, InetAddress ip, int porto){
        //ip e porto UDP!!!

        if(contem(ip, porto, servers_ativos)) {
            System.out.println("JÁ EXISTE!!!");
        }else{
            Servidor_classe s = new Servidor_classe(ip, porto);
            servers_ativos.add(s);
        }
    }

//    public void remove_sv(Servidores s){
//        servers_ativos.remove(s);
//    }


    //cliente contacta GRDS para receber o IP  e o Porto de Escuta TCP do Servidor
    public static void distribui_svs(ArrayList<Servidor_classe> servers_ativos, InetAddress ip_cliente, int porto_cliente){
        if(last_sv_distribuido > servers_ativos.size()-1)
            last_sv_distribuido = -1;

        if(last_sv_distribuido == -1)
            comunica_via_udp(ip_cliente, porto_cliente, servers_ativos.get(++last_sv_distribuido));

        else
            comunica_via_udp(ip_cliente, porto_cliente, servers_ativos.get(last_sv_distribuido));

        System.out.println("DEBUG SV: Size: " + servers_ativos.size() + "| atual: " + last_sv_distribuido++);
    }

    public static void comunica_via_udp(InetAddress ip, int porto, Servidor_classe s){

        DatagramPacket packet = null;

        try(DatagramSocket socket = new DatagramSocket(); //autocloseable
            ByteArrayOutputStream bout  = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout)){

            socket.setSoTimeout(TIMEOUT*1000);

            //Serializar servidores para um array de bytes encapsulado por bout
            oout.writeUnshared(s);

            //Construir um datagrama UDP com o resultado da serialização
            packet = new DatagramPacket(bout.toByteArray(), bout.size(), ip, porto);
            socket.send(packet);

        } catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }
    }

    public void verifica_inatividade(){
        //.. threads
    }

    public static void atualizaUDP(InetAddress ip, int portoTCP, int portoUDP, ArrayList<Servidor_classe> s){      //verificar se o servidor que está a contactar já existe na lista de svs ativos ou não
        for(Servidor_classe sv : s) {
            if (sv.getIp() == ip && sv.getPorto_escuta_TCP() == portoTCP) {
                    sv.setPorto_escuta_UDP(portoUDP);
            }
        }
    }


    public static void main(String[] args) {
        ArrayList<Servidor_classe> servers_ativos = new ArrayList<>();

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
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                receivedMsg = new String(packet.getData(), 0, packet.getLength());

                System.out.println("\nRecebido \"" + receivedMsg + "\" de " +
                        packet.getAddress().getHostAddress() + ":" + packet.getPort());


                if(!receivedMsg.equals(SV_CONNECT_REQUEST)){  //Recebe ligação do servidor

                    if(receivedMsg.equals(CL_CONNECT_REQUEST)) {  //Recebe ligação do cliente
                        //responseMsg = "CONNECTED Cliente"; //mensagem de sucesso

                        // packet.setData(responseMsg.getBytes());
                        //packet.setLength(responseMsg.length());

                        distribui_svs(servers_ativos, packet.getAddress(), packet.getPort());

                        //socket.send(packet);
                    }
                    continue;
                }

                setServer(servers_ativos, packet.getAddress(), packet.getPort());

                //setServer(packet.getAddress().getHostAddress(), packet.getPort());

                //System.out.println(getServers_ativos().toString());
                System.out.println("\nSvs ativos: " + servers_ativos.size());
                int i = 0;
                for(Servidor_classe s : servers_ativos) {
                    System.out.println(i + ". " + s.getIp().getHostAddress() + " | porto udp " + s.getPorto_escuta_UDP() + " | porto tcp " + + s.getPorto_escuta_TCP());
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
