package Grds;

import Constantes.Constantes;
import Grds.gestao_servidores.Servidor_classe;

import java.util.ArrayList;

public class ThreadVerificaInatividade extends Thread {
    private Servidor_classe s;

    public ThreadVerificaInatividade(Servidor_classe s) {
        this.s = s;
    }

    public void atualizaInatividade() {
        s.atualizaInatividade();
    }

    /*public void removeServers(){
        s.removeIf(sv -> sv.getConta_inatividade() > Constantes.INATIVIDADE_SERVIDOR);
    }*/


    /*public void getServers_ativos() {
        for (Servidor_classe sv : s) {
            System.out.println("\n\t\t\t\t\t\t\t\t\t\t<DEBUG>  || " + sv.getIp() + "-" + sv.getPorto_escuta_UDP() + " ---> Inativ: " + sv.getConta_inatividade());
        }
    }*/


    @Override
    public void run() {
        try {
            while(true) {
                //getServers_ativos();

                //removeServers();
                Thread.sleep((Constantes.INATIVIDADE + 1)  * 1000);

                //System.out.println("\n\t\t\t\t\t\t\t\t\t\t<DEBUG>  || " + s.getIp() + "-" + s.getPorto_escuta_UDP() + " ---> Inativ: " + s.getConta_inatividade());
                atualizaInatividade();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}