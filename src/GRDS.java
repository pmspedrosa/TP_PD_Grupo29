public class GRDS {
    public static void main(String[] args) {
            System.out.println("tu é que és");
    }
}

//responsável por manter o registo dos servidores ativos, direcionar as aplicações cliente
// para os diversos servidores e difundir informação relevante;


//a base de dados fica responsável por guardar os servidores (array de servers)
//tem de verificar qd algum se desconecta



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