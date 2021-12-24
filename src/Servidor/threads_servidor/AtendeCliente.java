package Servidor.threads_servidor;

import Constantes.Constantes;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class AtendeCliente extends Thread { //tcp
    private final Socket toClientSocket;

    public AtendeCliente(Socket s) {
        toClientSocket = s;
    }

    public void enviaMsg(ObjectOutputStream oout, String msg){
        //Ligar ao cliente
        try{
            System.out.println("\nConectando com cliente...");
            toClientSocket.setSoTimeout(Constantes.TIMEOUT * 1000);

            oout.writeUnshared("Não sou cliente");
            oout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String recebemsg(ObjectInputStream oin) {
        //Ligar ao cliente
        String s = null;

        while(true){
            try {
                System.out.println("\nConectando com cliente...");
                toClientSocket.setSoTimeout(Constantes.TIMEOUT * 1000);

                s = (String) oin.readObject();

                return s;
            } catch (SocketException e) {
                System.out.println("Socket: " + e + " - " + e.getCause());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Não recebi nada : " + e);
            }
        }
    }


    @Override
    public void run() {
        ObjectInputStream oin = null;
        ObjectOutputStream oout = null;

        String msgFromClient;

        try{
            oout = new ObjectOutputStream(toClientSocket.getOutputStream());
            oin = new ObjectInputStream(toClientSocket.getInputStream());

            msgFromClient = (String) oin.readObject();

            if(msgFromClient == null){ //EOF
                return;
            }

            System.out.println("Recebido \"" + msgFromClient.trim() + "\" de " +
                    toClientSocket.getInetAddress().getHostAddress() + ":" + toClientSocket.getPort());

//            try {
//                //simula processamento de 5 segs (5000 milisegundos)
//                Thread.sleep(5000);
//            } catch (InterruptedException e){ }

            //---------------------------------------------------

            oout.reset();
            oout.writeObject("Sou o servidor e recebi [" + msgFromClient + "] de ti");
            oout.flush();

            Thread.sleep(100);

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
}