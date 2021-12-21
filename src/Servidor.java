import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;


class AtendeClientesTCP extends Thread { //tcp
    public AtendeClientesTCP(InetAddress ip, int port) {

    }

    @Override
    public void run() {
        int listeningPort;
        Socket toClientSocket;

        try (ServerSocket socket = new ServerSocket();
             ObjectInputStream in = new ObjectInputStream(socket.accept().getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.accept().getOutputStream());) {

            while (true) {
                //enviar socket.getLocalPort() via udp!

               // toClientSocket = socket.accept();
               // toClientSocket.setSoTimeout(TIMEOUT);

                    Thread.sleep(Servidor.DELAY);
                    System.out.println("Aguarda Ligações!!! PASSARAM 20SEG");

            }
        } catch (InterruptedException e) {} catch (IOException e) {
            e.printStackTrace();
        }
    }
}




public class Servidor implements Runnable{
    static final int DELAY = 5000; //5 seconds


    //public static final int MAX_SIZE = 256;
    public static final String CONNECT_REQUEST = "SERVIDOR";
    public static final int TIMEOUT = 10; //segundos
    public static final int MAX_SIZE = 256;

    private InetAddress ip_GRDS;
    private int porto_GRDS;

    public Servidor(InetAddress ip, int porto){
        //verificar se é válido

        this.ip_GRDS = ip;
        this.porto_GRDS = porto;
    }

    @Override
    public void run() {
        try (DatagramSocket mysocket = new DatagramSocket()){
            while (true) {
                    Thread.sleep(Servidor.DELAY);
                    System.out.println("UDP Conectado porto: " + mysocket.getLocalPort());

                    String sgdbAddress;
                    InetAddress grdsAddress;
                    int grdsPort;
                    DatagramPacket mypacket;
                    String receivedMsg;

                    mypacket = new DatagramPacket(CONNECT_REQUEST.getBytes(), CONNECT_REQUEST.length(), ip_GRDS, porto_GRDS);
                    mysocket.send(mypacket);

                    mypacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    mysocket.receive(mypacket);

                    receivedMsg = new String(mypacket.getData(), 0, mypacket.getLength());
                    System.out.println(receivedMsg);
            }
        } catch (InterruptedException e) {} catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String sgdbAddress;
        InetAddress grdsAddress;
        int grdsPort;
        DatagramPacket mypacket;
        String receivedMsg;

/*
        if(args.length != 3){
            System.out.println("Sintaxe: java Servidor <IP do GRDS> <Porto de escuta do GRDS> <IP do SGBD>");
            return;
        }
*/

        try(DatagramSocket mysocket = new DatagramSocket()) {
            mysocket.setSoTimeout(TIMEOUT * 1000);

            //por omissão, tenta descobrir o GRDS no endereço de multicast 230.30.30.30 e porto UDP 3030
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
                System.out.println("Sintaxe: java Servidor <IP do GRDS> <Porto de escuta do GRDS> <IP do SGBD>");
                System.out.println("OU java Servidor <IP do SGBD>");
                return;
            }

            /* mypacket = new DatagramPacket(CONNECT_REQUEST.getBytes(), CONNECT_REQUEST.length(), grdsAddress, grdsPort);
            mysocket.send(mypacket); -- a thread é responsável pela comunicação

            mypacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            mysocket.receive(mypacket);

            receivedMsg = new String(mypacket.getData(), 0, mypacket.getLength());
            System.out.println(receivedMsg); */

            Thread t;
            t = new Thread(new Servidor(grdsAddress, grdsPort), "Thread " + Math.random()%10);
            t.start();

        } catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        } catch(NumberFormatException e) {
            System.out.println("O porto do GRDS deve ser um inteiro positivo.");
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        } finally{
//            if(mysocket != null){
//                mysocket.close();
//            }
        }
    }
}


//responsável por toda a lógica de
// negócio e pelo armazenamento replicado de ficheiros;


//Na fase de arranque, os clientes e os servidores recebem o-------
// endereço IP e o porto de escuta UDP do GRDS. >> utilização de parametros (ao iniciar o programa cliente)--------
//pode ser inicializado sem parâmetros tem de mandar via multicast (230.30.30.30 e porto UDP 3030)----------


// Os servidores devem enviar, via UDP e com uma periodicidade de 20 segundos,
// uma mensagem ao GRDS com indicação de um porto de escuta TCP (automático)
// em que possam aceitar ligações de clientes. Passados três períodos sem
// receção de mensagens de um determinado servidor, este é “esquecido” pelo
// GRDS;



//Cada servidor acede à mesma base de dados via a API JDBC


//Toda a informação do sistema é armazenada na base de dados partilhada,
// com exceção dos ficheiros disponibilizados pelos utilizadores.
// Estes são armazenados diretamente nos sistemas de ficheiros locais.


//Quando um servidor efetua uma alteração na base dados na sequência de
// uma interação com um cliente este envia ao GRDS, via UDP,
// uma mensagem a informar que houve alterações.

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
