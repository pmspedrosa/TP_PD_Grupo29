package shared_features;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    public static final long serialVersionUID = 1;

    private ArrayList<String[]> resultado;

    private String[][] cmds_disp = {
            {"ReceiveFile_FromClient", "1"}, //args: nome_Ficheiro > apenas serve para preparar no servidor a receção do ficheiro
            {"Ready_FromSv", "0"}, //args: - > apenas serve para informar o cliente que pode enviar o ficheiro

            {"notify", "4"}, //args: ip cliente envia notif, porto cliente envia notif, ip_cliente_a_notificar, porto_cli_notificar

            //Perguntas do cliente ao servidor
            {"Login", "2"}, //args: username, pwd                                       --done++
            {"Register", "3"}, //args: nome, username, pwd                              --done++

            {"Show_contacts", "0"}, //args: -                                           --done++
            {"List_users", "0"}, //args: -                                              --done++
            {"List_groups", "0"}, //args: -                                              --done++
            {"Search_usr", "1"}, //args: nome_procurado                                 --done++
            {"Eliminate_contact", "1"}, //args: nome do grupo                           --done++
            {"Add_contact", "1"}, //args: username a adicionar                          --done++
            {"Msg_send", "3"}, //args: group(true/false), destino, msg                  --done++
            {"File_send", "3"}, //args: group(true/false),destino, path_to_file         --done++
            {"Create_grp", "1"}, //args: nome do grupo                                  --done++
            {"Add_usr_grp", "2"}, //args: nome do grupo, username_add                     done++
            {"Leave_grp", "2"}, //args: admin(true/false),username                        done
            {"Change_name_group", "1"}, //args:                             done
            {"Eliminate_grp", "1"}, //args: nome do grupo                                 done
            {"ExcludeMember_grp", "2"}, //args: nome do grupo, username a eliminar        done
            {"Edit_profile", "2"}, //args: 1-nome|2-password, new parametro


            {"Show_msgs", "2"}, //args:  id_utilizador_2, id_grupo                      --done++
            /*

                  id_utilizador_2:   se for nulo o campo grupo tem de existir
                                     e o valor do showall é ignorado (colocado a default: false)
            */
            {"File_request", "2"}, //args: id_utilizador_enviou, id_msg


            //Respostas do servidor
            {"Result_Login", "2"}, //args: msg, estado_de_sucesso
            {"Result_Register", "2"}, //args: msg, estado_de_sucesso

            {"Result_List_Contacts", "2"}, //args: msg, estado_de_sucesso
            {"Result_List_Users", "1"}, //args: msg
            {"Result_List_Groups", "1"}, //args: msg
            {"Result_Search_Users", "1"}, //args: msg
            {"Result_Eliminate_Contact", "2"}, //args: msg, estado_de_sucesso
            {"Result_Add_Contact", "2"}, //args: msg, estado_de_sucesso
            {"Result_Msg_send", "3"},
            {"Result_File_send", "2"}, //args: msg, status_code (erro/success)
            {"Result_Show_Grupos", "2"}, //args: status/permission success/erro, id_utilizador
            {"Result_Create_grp", "2"}, //args: msg, status_code (erro/success)
            {"Result_Leave_grp", "2"}, //args: msg, status_code (erro/success)
            {"Result_Eliminate_grp", "2"}, //args: msg, status_code (erro/success)
            {"Result_ExcludeMember_grp", "2"}, //args: msg, status_code (erro/success)
            {"Reseul_Edit_profile", "2"}, //args: msg, status_code (erro/success)

            {"Result_Show_msgs", "2"}, //args: status_code (erro/success), boolean admin - lista msgs

            {"getIdUser_Resposta", "3"}, //args: status(erro/success), id_user, ip, porto
                                        /*      id_user: se for igual a nulo tem de haver um ip/porto
                                                         para que o servidor consiga localizar na bd o
                                                         id do utilizador que efetuou o pedido

                                                status: se for igual a status que representa success (true)
                                                        os valores ip e porto são ignorados
                                        */
            {"Result_File_request", "2"}, //...

    };
    //cmds_disp[x][0] => nome do comando
    //cmds_disp[x][1] => numero de argumentos que PODE ter

    private String cmd;

    private String[] argumentos;

    private ArrayList<String[]> lista;

    private boolean verifica(String cmd, int n_args){
        for(int i = 0; i < this.cmds_disp.length; i++){
            if(this.cmds_disp[i][0].equals(cmd) && Integer.parseInt(this.cmds_disp[i][1]) >= n_args)
                return true;
        }

        return false;
    }

    public Message(String comando, String[] args){
        if(verifica(comando, args.length)) {
            this.cmd = comando;
            this.argumentos = args;
        }
    }


    public String[] getArgumentos() {
        return argumentos;
    }

    public int getNArgumentosByCmd() {
        for(int i = 0; i < this.cmds_disp.length; i++){
            if(this.cmds_disp[i][0].equals(this.cmd))
                return Integer.parseInt(this.cmds_disp[i][1]);
        }
        return -1;
    }

    public String getCmd() {
        return cmd;
    }

    public void setLista(ArrayList<String[]> lista){ //estas a aplicar o conhecimento de ED já volto
        this.lista = lista; //tou mas nao posso falar ah ah ah o crl deixa tar aqui o comenetario pra depois o pedro ver ya e ele nemvai reparar e depois quem repara é o stor
    }

    public ArrayList<String[]> getLista(){
        return lista;
    }
}
