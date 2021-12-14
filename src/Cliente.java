import java.io.IOException;
import java.net.*;

public class Cliente {
    public static final String CONNECT_REQUEST = "SERVIDOR";
    public static final int TIMEOUT = 10; //segundos


    public static void main(String[] args) {
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;


        try (DatagramSocket mysocket = new DatagramSocket()) {
            mysocket.setSoTimeout(TIMEOUT * 1000);

            if (args.length != 2) {
                System.out.println("Sintaxe: java Servidor <IP do GRDS> <Porto de escuta do GRDS>");
                return;
            }

            grdsAddress = InetAddress.getByName(args[0]);
            grdsPort = Integer.parseInt(args[1]);

            mypacket = new DatagramPacket(CONNECT_REQUEST.getBytes(), CONNECT_REQUEST.length(), grdsAddress, grdsPort);
            mysocket.send(mypacket);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}



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




//através do qual os utilizadores interagem com o sistema

//contacta via UDP, o GRDS para receberem
// as coordenadas (IP e porto de escuta TCP) de um servidor ativo



//perda de ligação - novo contacto com GRDS - novo server
//o user n se deve aperceber da perda de ligação!

//Quando um servidor recebe a informação mencionada no ponto anterior,
// este informa os clientes afetados aos quais se encontra conectado
// que houve uma alteração na base de dados e que estes devem atualizar
// as suas vistas (mensagens/notificações visualizadas, número de
// mensagens/notificação não visualizadas, pedidos de contacto ou de
// adesão a grupos, etc.);


//Quando está em causa a disponibilização de um ficheiro,
// os servidores que recebem a informação vinda do GRDS também
// obtêm o ficheiro via uma ligação TCP temporária estabelecida
// com o servidor no endereço IP e porto TCP indicados.
// A transferência deve ser feita em background;



//Quando é solicitada a eliminação de um ficheiro pelo utilizador
// que o disponibilizou, a mensagem enviada para o GRDS também deve
// incluir esta indicação para que todos os servidores apaguem o ficheiro
// nos seus sistemas de ficheiros locais;




//Qualquer servidor que identifique na base de dados um utilizador marcado
// como estando online e que não tenha esse estado revalidado há mais
// de 30 segundos deve alterá-lo para offline;




//Quando um servidor termina de forma ordenada/intencional, este encerra
//as ligações TCP ativas, o que faz com que os clientes que se encontram
// ligados a ele também terminem de forma ordenada, informa o GRDS e
// atualiza a informação na base de dados.






//depois de estabelecer ligação

//1
//iniciar sessão ou registar
//nome
//password

//2
//alterar dados de registo
//listar e pesquisa de users
//...