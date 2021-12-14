import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;


public class GRDS {
    public static void main(String[] args) {
            System.out.println("tu é que és");
    }
}



/*public class UdpSerializedTimeServer {
    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        int listeningPort;
        DatagramSocket socket = null;
        DatagramPacket packet; //para receber os pedidos e enviar as respostas

        ByteArrayInputStream bin;
        ObjectInputStream oin;

        ByteArrayOutputStream bout;
        ObjectOutputStream oout;

        String receivedMsg;
        Calendar calendar;

        if(args.length != 1){
            System.out.println("Sintaxe: java UdpSerializedTimeServerIncomplete listeningPort");
            return;
        }

        try{

            listeningPort = Integer.parseInt(args[0]);
            socket = new DatagramSocket(listeningPort);

            System.out.println("UDP Time Server iniciado...");

            while(true){

                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                //Deserializar os bytes recebidos (objecto do tipo String)
                bin = new ByteArrayInputStream(packet.getData(), 0 , packet.getLength());
                oin = new ObjectInputStream(bin);
                receivedMsg = (String)oin.readObject();

                System.out.println("Recebido \"" + receivedMsg + "\" de " +
                        packet.getAddress().getHostAddress() + ":" + packet.getPort());

                if(!receivedMsg.equalsIgnoreCase(TIME_REQUEST)){
                    continue;
                }

                calendar = GregorianCalendar.getInstance();

                //Serializar o objecto calendar para bout
                bout = new ByteArrayOutputStream();
                oout = new ObjectOutputStream(bout);
                oout.writeObject(calendar);
                oout.flush();

                packet.setData(bout.toByteArray());
                packet.setLength(bout.size());

                //O ip e porto de destino ja' se encontram definidos em packet
                socket.send(packet);

            }

        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }
}
*/









/*public class UdpSerializedTimeClient {

    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args)
    {

        InetAddress serverAddr = null;
        int serverPort = -1;

        ByteArrayInputStream bin;
        ObjectInputStream oin;

        ByteArrayOutputStream bout;
        ObjectOutputStream oout;

        DatagramSocket socket = null;
        DatagramPacket packet = null;
        Calendar response;

        if(args.length != 2){
            System.out.println("Sintaxe: java UdpSerializedTimeClientIncomplete serverAddress serverUdpPort");
            return;
        }

        try{

            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT*1000);

            //Serializar a String TIME para um array de bytes encapsulado por bout
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);
            oout.writeUnshared(TIME_REQUEST);

            //Construir um datagrama UDP com o resultado da serialização
            packet = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr,
                    serverPort);

            socket.send(packet);

            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            //Deserializar o fluxo de bytes recebido para um array de bytes encapsulado por bin
            bin = new ByteArrayInputStream(packet.getData(), 0 , packet.getLength());
            oin = new ObjectInputStream(bin);
            response = (Calendar) oin.readObject();

            System.out.println("Hora indicada pelo servidor: " + response.getTime());

        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }

*/










//responsável por manter o registo dos servidores ativos, direcionar as aplicações cliente
// para os diversos servidores(round - robin) e difundir informação relevante;



//Na fase de arranque, os clientes e os servidores recebem o
// endereço IP e o porto de escuta UDP do GRDS. >> utilização de parametros (ao iniciar o programa cliente)


//se houver mais que um sv ativo ele distribui os clientes de acordo com escalonamento circular


//Os servidores devem enviar, via UDP e com uma periodicidade de 20 segundos,
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