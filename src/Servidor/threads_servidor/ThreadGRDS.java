package Servidor.threads_servidor;

import Constantes.Constantes;

import java.io.*;
import java.net.*;

public class ThreadGRDS implements Runnable{
    public static final int MAX_SIZE = 256;
    public static final String CONNECT_REQUEST = "SERVIDOR";

    private InetAddress ip_GRDS;
    private int porto_GRDS;
    private int porto_tcp;


    public ThreadGRDS(InetAddress ip, int porto, int porto_tcp){
        //verificar se é válido

        this.ip_GRDS = ip;
        this.porto_GRDS = porto;
        this.porto_tcp = porto_tcp;
    }


    @Override
    public void run() {
        boolean conectado = false;
        boolean GRDS_on = false;
        int contador_timeout = 0;

        do {
            try (DatagramSocket mysocket = new DatagramSocket()) {
                mysocket.setSoTimeout(Constantes.TIMEOUT * 1000);

                while (true) {
                    if (Constantes.DEBUG)
                        System.out.println("\t\t\t\t\t\t\t\t\t\t\tDEBUG: UDP Conectado porto: " + mysocket.getLocalPort());

                    DatagramPacket mypacket;
                    String receivedMsg;

                    String mensagem = CONNECT_REQUEST + "-" + this.porto_tcp;
                    mypacket = new DatagramPacket(mensagem.getBytes(), mensagem.length(), ip_GRDS, porto_GRDS);
                    mysocket.send(mypacket);

                    mypacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    mysocket.receive(mypacket);

                    receivedMsg = new String(mypacket.getData(), 0, mypacket.getLength());
                    //System.out.println(receivedMsg);

                    if(receivedMsg != null) {
                        conectado = true;
                    }

                    Thread.sleep(Constantes.INATIVIDADE * 1000);
                }

            } catch (SocketTimeoutException e) {
                contador_timeout++;
                System.out.println("\nGRDS está offline: \n\t" + e + "\nA tentar estabelecer ligação...");
                if (contador_timeout >= 3)
                    System.out.println("Não foi possivel estabelecer ligação com o GRDS.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while (!conectado && contador_timeout < Constantes.CONTADOR_TIMEOUT);
    }
}






//responsável por toda a lógica de
// negócio e pelo armazenamento replicado de ficheiros;


//Na fase de arranque, os clientes e os servidores recebem o-------
// endereço IP e o porto de escuta UDP do Grds.GRDS. >> utilização de parametros (ao iniciar o programa cliente)--------
//pode ser inicializado sem parâmetros tem de mandar via multicast (230.30.30.30 e porto UDP 3030)----------


// Os servidores devem enviar, via UDP e com uma periodicidade de 20 segundos,
// uma mensagem ao Grds.GRDS com indicação de um porto de escuta TCP (automático)
// em que possam aceitar ligações de clientes. Passados três períodos sem
// receção de mensagens de um determinado servidor, este é “esquecido” pelo
// Grds.GRDS;



//Cada servidor acede à mesma base de dados via a API JDBC


//Toda a informação do sistema é armazenada na base de dados partilhada,
// com exceção dos ficheiros disponibilizados pelos utilizadores.
// Estes são armazenados diretamente nos sistemas de ficheiros locais.


//Quando um servidor efetua uma alteração na base dados na sequência de
// uma interação com um cliente este envia ao Grds.GRDS, via UDP,
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
// os servidores que recebem a informação vinda do Grds.GRDS também
// obtêm o ficheiro via uma ligação TCP temporária estabelecida
// com o servidor no endereço IP e porto TCP indicados.
// A transferência deve ser feita em background;



//Quando é solicitada a eliminação de um ficheiro pelo utilizador
// que o disponibilizou, a mensagem enviada para o Grds.GRDS também deve
// incluir esta indicação para que todos os servidores apaguem o ficheiro
// nos seus sistemas de ficheiros locais;


//Qualquer servidor que identifique na base de dados um utilizador marcado
// como estando online e que não tenha esse estado revalidado há mais
// de 30 segundos deve alterá-lo para offline;



//Quando um servidor termina de forma ordenada/intencional, este encerra
//as ligações TCP ativas, o que faz com que os clientes que se encontram
// ligados a ele também terminem de forma ordenada, informa o Grds.GRDS e
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
