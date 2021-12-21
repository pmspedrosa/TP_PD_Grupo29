public class Servidores {

    private String ip;
    private int porto_escuta_UDP;       //porto de escuta UDP
    private int porto_escuta_TCP;           //porto de escuta TCP
    private int conta_inatividade;      //contador de periodos sem receção


    public Servidores(String ip, int porto){
        this.ip = ip;
        this.porto_escuta_UDP = porto;
        this.porto_escuta_TCP = -1;
        this.conta_inatividade = 0;
        this.porto_escuta_UDP = 5010;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPorto_escuta_TCP() {
        return porto_escuta_TCP;
    }

    public void setPorto_escuta_TCP(int porto_escuta_TCP) {
        this.porto_escuta_TCP = porto_escuta_TCP;
    }

    public int getConta_inatividade() {
        return conta_inatividade;
    }

    public void setConta_inatividade(int conta_inatividade) {
        this.conta_inatividade = conta_inatividade;
    }

    public int getPorto_escuta_UDP() {
        return porto_escuta_UDP;
    }

    public void setPorto_escuta_UDP(int porto_escuta_UDP) {
        this.porto_escuta_UDP = porto_escuta_UDP;
    }
}
