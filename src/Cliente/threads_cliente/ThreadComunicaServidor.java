package Cliente.threads_cliente;

import Constantes.Constantes;
import shared_features.Message;
import Cliente.ui.MenuUI;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ThreadComunicaServidor  extends Thread { //tcp
    private InetAddress ip;
    private int porto;
    File localDirectory;

    public ThreadComunicaServidor(/*Socket s*/ InetAddress ip, int porto, File localDirectory) {
        //socket = s;
        this.ip = ip;
        this.porto = porto;
        this.localDirectory = localDirectory;
    }


    @Override
    public void run(){
        try {
            ObjectInputStream oin = null;
            ObjectOutputStream oout = null;

            Scanner sc = new Scanner(System.in);

            while (true) {
                Socket socket = new Socket(ip, porto);

                //Cria os objectos que permitem serializar e deserializar objectos em socket
                oin = new ObjectInputStream(socket.getInputStream());
                oout = new ObjectOutputStream(socket.getOutputStream());

                boolean continua_ciclo_login = true;
                boolean continua_ciclo_registar = true;

                int sair_int = 0;
                int sair2_int = 0;

                do {
                    int opt = MenuUI.menu();

                    switch (opt) {
                        case 1:      /**login*/
                            do {
                                String sair;

                                Message msgLogin = MenuUI.login();
                                if(Constantes.DEBUG)
                                    System.out.println("<DEBUG> " + msgLogin.getArgumentos()[0] + "\t" + msgLogin.getArgumentos()[1]);

                                oout.reset(); //para nao enviar a mensagem anterior
                                oout.writeObject(msgLogin);
                                oout.flush();

                                Message msgFromServer = (Message) oin.readObject();
                                if(Constantes.DEBUG)
                                    System.out.println("<DEBUG> " + msgFromServer.getArgumentos()[0]); //print da mensagem de sucesso/erro

                                if (Boolean.parseBoolean(msgFromServer.getArgumentos()[1])) {
                                    continua_ciclo_login = false;
                                    sair_int = 1;
                                    break;
                                }
                                else {
                                    System.out.println(msgFromServer.getArgumentos()[0] + "\nPretende voltar ao menu inicial? (s/n):");
                                    sair = sc.next();

                                    if (sair.toUpperCase().charAt(0) == 'S') {
                                        break;
                                    }
                                }
                            } while (continua_ciclo_login);
                            break;

                        case 2:     /**registo*/
                            do {
                                String sair;
                                Message msgRegistar = MenuUI.registar();
                                System.out.println("<DEBUG> " + msgRegistar.getArgumentos()[0] + "\t" +
                                        msgRegistar.getArgumentos()[1] + "\t" + msgRegistar.getArgumentos()[2]);

                                oout.reset(); //para nao enviar a mensagem anterior
                                oout.writeObject(msgRegistar);
                                oout.flush();

                                Message msgFromServer = (Message) oin.readObject();

                                System.out.println("<DEBUG> " + msgFromServer.getArgumentos()[0]); //print da mensagem de sucesso/erro

                                if (Boolean.parseBoolean(msgFromServer.getArgumentos()[1])) {
                                    continua_ciclo_registar = false;
                                    //sair_int = 1;
                                    break;
                                }
                                else {
                                    System.out.println(msgFromServer.getArgumentos()[0] + "\nPretende voltar ao menu inicial? (s/n):");
                                    sair = sc.next();

                                    if (sair.toUpperCase().charAt(0) == 'S') {
                                        break;
                                    }
                                }
                            } while(continua_ciclo_registar);
                            break;

                        case 0:
                            // TODO: 26/12/2021
                            //  função para sair
                            return;

                        default:
                            System.out.println("Ocurreu um erro inesperado");
                            break;
                    }
                }while(sair_int == 0);

                //apresenta menu_abrirmsg
                do{
                    int opt1 = MenuUI.menu_principal();


                    switch (opt1){
                        case 1: //LISTA CONTACTOS
                            String[] args_msg = {};
                            Message msgMostraContactos = new Message("Show_contacts", args_msg);

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgMostraContactos);
                            oout.flush();

                            Message msgFromServer1 = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgFromServer1.getArgumentos()[0]); //print da mensagem de sucesso/erro

                            for (String[] utilizador : msgFromServer1.getLista())
                                System.out.println("[" + utilizador[0] + "] " + utilizador[1] + " - " + utilizador[2]);

                            // TODO: 29/12/2021 falta mostrar online
                            break;

                        case 2: //LISTA UTILIZADORES REGISTADOS
                            String[] args = {};
                            Message msgListUsers = new Message("List_users", args);

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgListUsers);
                            oout.flush();

                            Message msgFromServer2 = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("\n" + msgFromServer2.getArgumentos()[0]); //print da mensagem de sucesso/erro

                            for (String []utilizador : msgFromServer2.getLista())
                                System.out.println("[" + utilizador[0] + "] - " + utilizador[1]);
                            break;

                        case 3: //PROCURAR UTILIZADOR
                            Message msgProcura = MenuUI.procurarUtilizador();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgProcura.getArgumentos()[0] );

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgProcura);
                            oout.flush();

                            Message msgFromServer3 = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgFromServer3.getArgumentos()[0]); //print da mensagem de sucesso/erro

                            for (String []utilizador : msgFromServer3.getLista())
                                System.out.println("[" + utilizador[0] + "] " + utilizador[1]);

                            break;

                        case 4: //ADICIONAR CONTACTO
                            Message msgAdd = MenuUI.adicionarContacto();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgAdd.getArgumentos()[0]);

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgAdd);
                            oout.flush();

                            Message msgFromServer4 = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgFromServer4.getArgumentos()[0]); //print da mensagem de sucesso/erro
                            break;

                        case 5: //ELIMINAR CONTACTO
                            Message msgElim = MenuUI.eliminarContacto();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgElim.getArgumentos()[0]);

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgElim);
                            oout.flush();

                            Message msgFromServer5 = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgFromServer5.getArgumentos()[0]); //print da mensagem de sucesso/erro
                            break;

                        case 6:
                            int sair3_int=0;
                            // TODO: 26/12/2021
                            //  func para abrir msgs
                            //  func para abrir msgs
                            //  func para abrir msgs    !!!biiiigggg one!!!

                            do{
                                int opt = MenuUI.menu_abrirMsg();
                                    if(opt == 0)
                                        sair3_int=1;
                                    else {
                                        if(opt == 1){//grupo
                                            Message messageg =  MenuUI.abrirMsg(opt);
//                                            if(Constantes.DEBUG) {
//                                                System.out.println("<DEBUG> " + messageg.getArgumentos()[0] +", "+ messageg.getArgumentos()[1]);
//                                            }
                                            String destino = messageg.getArgumentos()[1];
                                            oout.reset(); //para nao enviar a mensagem anterior
                                            oout.writeObject(messageg);
                                            oout.flush();

                                            Message msgFromServer = (Message) oin.readObject();
//                                            if(Constantes.DEBUG)
//                                                System.out.println("<DEBUG> " + msgFromServer.getArgumentos()[0] + msgFromServer.getArgumentos()[1]); //print da mensagem de sucesso/erro + bool username admin

                                            for (String []mensagensGrupo : msgFromServer.getLista())
                                                System.out.println("\n"+mensagensGrupo[0] + ": \t" + mensagensGrupo[1]);

                                            // TODO: 31/12/2021 Fazer veificação se está inserido no grupo ou não
                                            // TODO: 31/12/2021 Fazer veificação se está inserido no grupo ou não
                                            // TODO: 31/12/2021 Fazer veificação se está inserido no grupo ou não
                                            // TODO: 31/12/2021 Fazer veificação se está inserido no grupo ou não


                                            boolean admin = Boolean.parseBoolean(msgFromServer.getArgumentos()[1]);
                                            int optMg = MenuUI.menu_abrirMsgGrupo(admin);
                                            if(optMg==0){
                                                break;
                                            }else if(optMg == 1){
                                                Message msgg = null;
                                                do{
                                                    System.out.println(destino);
                                                    msgg = MenuUI.mandaMsg(destino, "true");
                                                    if(Constantes.DEBUG) {
                                                        System.out.println("<DEBUG> " + messageg.getArgumentos()[0] + messageg.getArgumentos()[1]);
                                                    }

                                                    oout.reset(); //para nao enviar a mensagem anterior
                                                    oout.writeObject(messageg);
                                                    oout.flush();

                                                    Message msgFromServerg = (Message) oin.readObject();
                                                    if(Constantes.DEBUG)
                                                        System.out.println("<DEBUG> " + msgFromServerg.getArgumentos()[0]);
                                                }while (msgg !=null);
                                            }else if(optMg == 2){
                                                //{"Add_usr_grp", "2"}, //args: nome do grupo, username_add                     done++
                                                    Message addelem = MenuUI.adicionarElementoGrupo(destino);

                                                    oout.reset(); //para nao enviar a mensagem anterior
                                                    oout.writeObject(addelem);
                                                    oout.flush();

                                                    Message msgFromServerg = (Message) oin.readObject();
                                                if(Constantes.DEBUG)
                                                    System.out.println("<DEBUG> " + msgFromServerg.getArgumentos()[0]);
                                                // TODO: 30/12/2021 adicionar elemento grupo
                                                // TODO: 30/12/2021 adicionar elemento grupo
                                                // TODO: 30/12/2021 adicionar elemento grupo

                                            }else if(optMg == 3){

                                                // TODO: 30/12/2021 sair do grupo
                                                //verificar se é admin ou não, se for meter prox elem do grupo como admin
                                                //---trocar nome do admin pelo novo e retirar o novo da lista de membros
                                                //else
                                                //retirar apenas o membro

                                                args = new String[]{String.valueOf(admin)};
                                                Message m = new Message("Leave_grp", args);

                                                oout.reset(); //para nao enviar a mensagem anterior
                                                oout.writeObject(m);
                                                oout.flush();

                                                msgFromServer = (Message) oin.readObject();
                                                if(Constantes.DEBUG)
                                                    System.out.println("<DEBUG> " + msgFromServer.getArgumentos()[0]);
                                                // TODO: 30/12/2021 sair do grupo
                                                // TODO: 30/12/2021 sair do grupo

                                            }else if(optMg == 4 && admin){
                                                Message changename = MenuUI.alterarNomeGrupo(destino);

                                                oout.reset(); //para nao enviar a mensagem anterior
                                                oout.writeObject(changename);
                                                oout.flush();

                                                Message msgFromServerg = (Message) oin.readObject();
                                                if(Constantes.DEBUG)
                                                    System.out.println("<DEBUG> " + msgFromServerg.getArgumentos()[0]);
                                                // TODO: 30/12/2021 alterar nome do grupo
                                                // TODO: 30/12/2021 alterar nome do grupo
                                                // TODO: 30/12/2021 alterar nome do grupo

                                            }else if(optMg == 5 && admin){
                                                Message nome_excluir = MenuUI.excluirMembro(destino);

                                                oout.reset(); //para nao enviar a mensagem anterior
                                                oout.writeObject(nome_excluir);
                                                oout.flush();

                                                Message msgFromServerg = (Message) oin.readObject();
                                                if(Constantes.DEBUG)
                                                    System.out.println("<DEBUG> " + msgFromServerg.getArgumentos()[0]);
                                                // TODO: 30/12/2021 excluir membro
                                                // TODO: 30/12/2021 excluir membro
                                                // TODO: 30/12/2021 excluir membro


                                            }else if(optMg == 6 && admin){
                                                //{"Eliminate_grp", "1"}, //args: nome do grupo
                                                String[] arg = {destino};
                                                Message m = new Message("Eliminate_grp", arg);
                                                oout.reset(); //para nao enviar a mensagem anterior
                                                oout.writeObject(m);
                                                oout.flush();

                                                Message msgFromServerg = (Message) oin.readObject();
                                                if(Constantes.DEBUG)
                                                    System.out.println("<DEBUG> " + msgFromServerg.getArgumentos()[0]);
                                                // TODO: 30/12/2021 eliminar Grupo
                                                // TODO: 30/12/2021 eliminar Grupo
                                                // TODO: 30/12/2021 eliminar Grupo

                                            }



                                        }else{
                                            Message message =  MenuUI.abrirMsg(opt);

                                            if(Constantes.DEBUG)
                                                System.out.println("<DEBUG> " + message.getArgumentos()[0] + message.getArgumentos()[1]);
                                            String destino = message.getArgumentos()[0];

                                            oout.reset(); //para nao enviar a mensagem anterior
                                            oout.writeObject(message);
                                            oout.flush();

                                            Message msgFromServer = (Message) oin.readObject();
                                            if(Constantes.DEBUG)
                                                System.out.println("<DEBUG> " + msgFromServer.getArgumentos()[0]); //print da mensagem de sucesso/erro
                                            if(msgFromServer.getArgumentos()[1]=="false"){
                                                break;
                                            } else{
                                                Message mandamsg = null;
                                                do{
                                                    System.out.println("<destino> " + mandamsg.getArgumentos()[1]);
                                                    mandamsg = MenuUI.mandaMsg(destino, "false");
                                                    /*if (Constantes.DEBUG)
                                                        System.out.println("<DEBUG M> " + mandamsg.getArgumentos()[1]);*/

                                                    oout.reset(); //para nao enviar a mensagem anterior
                                                    oout.writeObject(mandamsg);
                                                    oout.flush();

                                                    Message msgFromServer6 = (Message) oin.readObject();
                                                    if (Constantes.DEBUG)
                                                        System.out.println("<DEBUG> " + msgFromServer6.getArgumentos()[0]); //print da mensagem de sucesso/erro
                                                }while (mandamsg !=null);
                                                break;
                                            }
                                        }
                                    }

                            }while (sair3_int==0);

                            break;

                        case 7: //LISTAR GRUPOS
                            Message msgListGroups = new Message("List_groups", new String[]{});

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgListGroups);
                            oout.flush();

                            Message msgFromServer6 = (Message) oin.readObject();
                            if (Constantes.DEBUG)
                                System.out.print("\n" + msgFromServer6.getArgumentos()[0]); //print da mensagem de sucesso/erro

                            for (String []grupo : msgFromServer6.getLista()) {
                                System.out.printf("%-25s", grupo[0]); //nome do grupo
                                System.out.print(grupo[1]);        //lista de utilizadores
                            }
                            System.out.println();
                            break;

                        case 8: //CRIAR GRUPO
                            Message msgCriarGrupo = MenuUI.criarGrupo();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgCriarGrupo.getArgumentos()[0]);

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgCriarGrupo);
                            oout.flush();

                            Message msgFromServer7 = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgFromServer7.getArgumentos()[0]); //print da mensagem de sucesso/erro
                            break;

                        case 9: //notificações
                            // TODO: 26/12/2021
                            //  func para alterar daodos
                            break;

                        case 10: //editar perfil
                            Message msgEditarPerfil = MenuUI.menu_editarperfil();

                            if(msgEditarPerfil == null)
                                break;

                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgEditarPerfil.getArgumentos()[0]+ msgEditarPerfil.getArgumentos()[1]);

                            oout.reset(); //para nao enviar a mensagem anterior
                            oout.writeObject(msgEditarPerfil);
                            oout.flush();

                            Message msgFromServer = (Message) oin.readObject();
                            if(Constantes.DEBUG)
                                System.out.println("<DEBUG> " + msgFromServer.getArgumentos()[0]); //print da mensagem de sucesso/erro
                            break;

                        case 11:
                            String fileName = "img.png";
                            String localDirectory = "C:/filesToSend";

                            oout.reset();
                            oout.writeObject(new Message("ReceiveFile_FromClient", new String[]{fileName}));
                            oout.flush();

                            ThreadSendFiles tFiles = new ThreadSendFiles(localDirectory, fileName, socket);
                            tFiles.start();
                            break;

                        case 0:
                            // TODO: 26/12/2021
                            //  função para sair
                            sair2_int = 1;
                            break;

                        default:
                            System.out.println("Ocorreu um erro inesperado");
                            break;
                    }
                }while(sair2_int == 0);


//                if (msgFromServer == null) {
//                    System.out.println("O servidor nao enviou qualquer respota antes de"
//                            + " fechar aligacao TCP!");
//                } else {
//                    System.out.println("Recebida a resposta [" + msgFromServer.getCmd() + "] com os argumentos");
//                    for (int i = 0; i < msgFromServer.getNArgumentosByCmd(); i++) {
//                        System.out.println("\t[" + msgFromServer.getArgumentos()[i] + "]");
//                    }
//
//                    System.out.println(" de " +
//                            socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
//                }
            }

        }catch(SocketException e){
            System.out.println("O Servidor foi abaixo.");
            throw new RuntimeException();
        }
        catch (IOException e) {
            e.printStackTrace();
        }  catch(Exception e){
            System.out.println("Ocorreu um erro: \n\t"+e.toString()+"\t" + e.getCause());
        }
    }

}