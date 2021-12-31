package Servidor.threads_servidor;

import BaseDados.SGBD;
import Constantes.Constantes;
import shared_features.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ThreadAtendeCliente extends Thread { //tcp
    private final Socket toClientSocket;
    private String username;

    public ThreadAtendeCliente(Socket s) {
        toClientSocket = s;
    }


    @Override
    public void run() {
        ObjectInputStream oin = null;
        ObjectOutputStream oout = null;

        Message msg;

        try{
            oout = new ObjectOutputStream(toClientSocket.getOutputStream());
            oin = new ObjectInputStream(toClientSocket.getInputStream());

            while (true){
                try {
                    msg = (Message) oin.readObject();
                }
                catch(ClassCastException e){
                    //se receber ficheiro a thread principal "ignora"
                    //(a thread "ReceiveFiles" fica responsável pelo processamento do mesmo)

                    System.out.println("A receber ficheiro!!!" + e.toString());
                    msg = null;
                }

                if (msg == null) { //EOF
                    return;
                }

                System.out.println("Recebido o comando [" + msg.getCmd() + "] com os argumentos");
                for (int i = 0; i < msg.getNArgumentosByCmd(); i++) {
                    System.out.println("\t[" + msg.getArgumentos()[i] + "]");
                }
                System.out.println("\tde " + toClientSocket.getInetAddress().getHostAddress() + ":" + toClientSocket.getPort());

                if(msg.getCmd().equals("ReceiveFile_FromClient")) {
                    ThreadReceiveFiles tFiles = new ThreadReceiveFiles(toClientSocket, msg.getArgumentos()[0]);
                    tFiles.start();
                }else{
                    Message resultado = funcionalidade(msg, toClientSocket.getInetAddress().getHostAddress(), toClientSocket.getPort());

                    oout.reset();
                    oout.writeObject(resultado);
                    oout.flush();
                }

                Thread.sleep(100);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("erro: " + e.getCause() + " - " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e){
            System.out.println("Problema na comunicacao com o cliente " +
                    toClientSocket.getInetAddress().getHostAddress() + ":" +
                    toClientSocket.getPort()+"\n\t" + e);
        } finally{
            try{
                if(oout != null)
                    oout.close();
                if(oin != null)
                    oin.close();
                toClientSocket.close();
            } catch(IOException e){}
        }
    }

    private Message funcionalidade(Message msg, String ip, int porto){
        Message resultado = null;

        switch (msg.getCmd()) {
            case "Login": resultado = login(msg.getArgumentos(), ip, porto); break;
            case "Register": resultado = registo(msg.getArgumentos()); break;
            case "Show_contacts": resultado = mostrarContactos(); break;
            case "List_users": resultado = listUsers(); break;
            case "List_groups": resultado = listGroups(); break;
            case "Search_usr": resultado = procurarUtilizador(msg.getArgumentos());break;
            case "Add_contact": resultado = adicionarContacto(msg.getArgumentos());break;
            case "Eliminate_contact": resultado = eliminarContacto(msg.getArgumentos());break;
            case "Create_grp": resultado = criarGrupo(msg.getArgumentos(), msg.getLista());break;
            case "Show_msgs": resultado = showMsgs(msg.getArgumentos());break;
            case "Send_msg": resultado = mandarMsg(msg.getArgumentos());break;
            case "Edit_profile": resultado = editProfile(msg.getArgumentos());break;

            //...
            default: System.out.println("Opção desconhecida"); break;
        }

        return resultado;
    }




    private Message login(String[] argumentos, String ip, int porto) {
        boolean resultado = SGBD.login(argumentos, ip, porto);
        username = argumentos[0];

        String[] args = {"Login efetuado com sucesso!", "true"};
        String[] args_erro = {"Dados incorretos!", "false"};

        if(resultado){
            return new Message("Result_Login", args);
        }

        return new Message("Result_Login", args_erro);
    }




    private Message registo(String[] argumentos) {
        boolean resultado = SGBD.registo(argumentos);

        String[] args = {"Registo efetuado com sucesso!", "true"};
        String[] args_erro = {"Para se registar tem de preencher todos os campos!", "false"};

        if(resultado) {
            return new Message("Result_Register", args);
        }

        return new Message("Result_Register", args_erro);
    }


    private Message listUsers() {
        Message resultado = SGBD.listUsers();
        return resultado;
    }

    private Message listGroups() {
        Message resultado = SGBD.listGroups();
        return resultado;
    }

    private Message mostrarContactos() {
        return SGBD.mostrarContactos(username);
    }


    private Message procurarUtilizador(String[] argumentos) {
        Message resultado = SGBD.procurarUtilizador(argumentos);
        return resultado;
    }
    
    

    private Message adicionarContacto(String[] argumentos) {
        Message resultado = SGBD.adicionarContacto(argumentos, username);
        return resultado;
    }

    private Message eliminarContacto(String[] argumentos) {
        Message resultado = SGBD.eliminarContacto(argumentos,username);
        return resultado;
    }

    private Message showMsgs(String[] argumentos) {
        Message resultado = SGBD.show_msgs(argumentos,username);
        return resultado;
    }


    private Message mandarMsg(String[] argumentos) {
        Message resultado = SGBD.mandaMsg(argumentos,username);
        return resultado;
    }

    private Message mandaFicheiro(String[] argumentos) {
        // TODO: 26/12/2021 Falta estabelecer ligações com bd e afins
        String[] args = {"O ficheiro foi enviado com sucesso!", "true"};
        String[] args_erro = {"Não foi possível enviar o ficheiro!", "false"};

        if(argumentos[0] == null || argumentos[1] == null){
            if(Constantes.DEBUG)
                System.out.println("DEBUG: NÃO FOI POSSIVEL ENVIAR O FICHEIRO <argumentos>");

            return new Message("Result_File_send", args_erro);
        }

        // TODO: 27/12/2021 receber e processar o ficheiro recebido!
        if(true) { //se argumentos válidos e se foi possível obter a cópia do ficheiro - por debug está a true
            if(Constantes.DEBUG)
                System.out.println("DEBUG: FICHEIRO ENVIADO E PROCESSADO! <correu tudo bem>");
            return new Message("Result_File_send", args);
        }

        if(Constantes.DEBUG)
            System.out.println("DEBUG: NÃO FOI POSSIVEL ENVIAR O FICHEIRO <erro genérico>");

        return new Message("Result_File_send", args_erro);
    }


    private Message editProfile(String[] argumentos) {
        Message resultado = SGBD.editarPerfil(argumentos, username);
        return resultado;
    }


    private Message criarGrupo(String[] argumentos, ArrayList<String[]> lista) {
        // TODO: 26/12/2021 Falta estabelecer ligações com bd e afins
        //{"Create_grp", "2"}, //args: nome do grupo, array com id's para elementos do grupo
        Message resultado = SGBD.criarGrupo(argumentos,lista,username);
        return resultado;
    }
}