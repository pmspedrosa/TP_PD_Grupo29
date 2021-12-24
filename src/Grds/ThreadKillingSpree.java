package Grds;

import Constantes.Constantes;
import Grds.gestao_servidores.Servidor_classe;

import java.util.ArrayList;

public class ThreadKillingSpree extends Thread {
    private ArrayList<Servidor_classe> s;

    public ThreadKillingSpree(ArrayList<Servidor_classe> s) {
        this.s = s;
    }

    public void atualizaInatividade() {
        for (Servidor_classe sv : s) {
            sv.atualizaInatividade();

            if(sv.getConta_inatividade() > Constantes.InatividadeServidor)
                s.remove(sv);
        }
    }

    public void getServers_ativos() {
        for (Servidor_classe sv : s) {
            System.out.println("\n\t\t\t\t\t\t\t\t\t\t<DEBUG>  || " + sv.getIp() + "-" + sv.getPorto_escuta_UDP() + " ---> Inativ: " + sv.getConta_inatividade() + "\n");
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(Constantes.Tsleep * 1000);
            getServers_ativos();
            atualizaInatividade();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
