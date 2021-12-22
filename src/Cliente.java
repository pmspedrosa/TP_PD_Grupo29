import org.omg.CORBA.WStringSeqHelper;

import java.io.*;
import java.net.*;
import java.sql.Time;
import java.util.Arrays;

public class Cliente {
    public static final String CONNECT_REQUEST = "CLIENTE";
    public static final int TIMEOUT = 10; //segundos
    public static final int MAX_SIZE = 256;



    public static void main(String[] args) {
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;
        InetAddress serverAddr = null;
        int serverPortTCP = 0;
        String resposta;

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

            mypacket = new DatagramPacket(new byte[MAX_SIZE],MAX_SIZE);
            mysocket.receive(mypacket);

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


        BufferedReader bin;
        PrintStream pout;
        String resposta_TCP;


        //Ligar ao servidor
        try(Socket socket = new Socket(serverAddr, serverPortTCP)){

            System.out.println("\nConectando ...");
            socket.setSoTimeout(TIMEOUT*1000);

            pout = new PrintStream(socket.getOutputStream(), true);

            //bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pout.println(TIMEOUT);

            //resposta_TCP = bin.readLine();
            //System.out.println("RespostaTCP: " + resposta_TCP);



            /*
            //Cria os objectos que permitem serializar e deserializar objectos em socket
            oin = new ObjectInputStream(socket.getInputStream());
            oout = new ObjectOutputStream(socket.getOutputStream());

            //Serializa a string TIME_REQUEST para o OutputStream associado a socket
            oout.reset(); //para nao enviar a mensagem anterior
            oout.writeObject(TIME_REQUEST);
            oout.flush();

            //Deserializa a resposta recebida em socket
            response = (Time) oin.readObject();

            if(response == null){
                System.out.println("O servidor nao enviou qualquer respota antes de"
                        + " fechar aligacao TCP!");
            }else{
                System.out.println("Hora indicada pelo servidor: " + response);
            }
            */



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}









//contacta via UDP, o GRDS para receberem
// as coordenadas (IP e porto de escuta TCP) de um servidor ativo----



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
