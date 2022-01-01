package Cliente.ui;

import shared_features.Message;

import java.util.*;

public class MenuUI {

    public static Message login(){
        Scanner sc = new Scanner(System.in);

        String username, password;
        System.out.println("\n-------------");
        System.out.println("Login");
        System.out.println("-------------\n");
        do {
            System.out.println("Username: ");
            username = sc.nextLine();
        } while (username.equals("") || username.equals(" ") || username.equals("\n"));
        do {
            System.out.println("\nPassword: ");
            password = sc.nextLine();
        } while (password.equals("") || password.equals(" ") || password.equals("\n"));

        String[] args = {username, password};
        Message msg = new Message("Login", args);
        return msg;
    }


    public static Message registar() {
        Scanner sc = new Scanner(System.in);

        String nome, username, password;
        System.out.println("\n-------------");
        System.out.println("Registar");
        System.out.println("-------------\n");

        do {
            System.out.println("Nome:");
            nome = sc.nextLine();
        }while (nome.equals("") || nome.equals(" ") || nome.equals("\n"));
        do {
            System.out.println("\nUsername:");
            username = sc.nextLine();
        }while (username.equals("") || username.equals(" ") || username.equals("\n"));
        do {
            System.out.println("\nPassword:");
            password = sc.nextLine();
        }while (password.equals("") || password.equals(" ") || password.equals("\n"));


        String[] args = {nome, username, password};
        Message m = new Message("Register", args);
        return m;
    }




    public static Message abrirMsg(int opt) {
        Scanner sc = new Scanner(System.in);
        String nome;

        // null, id_grupo)  --> grupo (lista de args)
        //id_utilizador2, null) --> utilizador (lista de args)

        if(opt==1) {
            System.out.println("\n-------------");
            System.out.println("Nome_Grupo:");
            System.out.println("-------------\n\n");
            do {
                nome = sc.nextLine();
            }while (nome.equals("") || nome.equals(" ") || nome.equals("\n"));

            String[] args_grupo = {"null", nome};
            Message m = new Message("Show_msgs", args_grupo);
            return m;
        }else if(opt==2) {
            System.out.println("\n-------------");
            System.out.println("Username_cliente:");
            System.out.println("-------------\n\n");
            do {
                nome = sc.nextLine();
            } while (nome.equals("") || nome.equals(" ") || nome.equals("\n"));

            String[] args_utilizador = {nome, "null"};
            Message m = new Message("Show_msgs", args_utilizador);
            return m;
        }
        return null;
    }


    // TODO: 28/12/2021 AINDA NÃO FOI FEITO!!!!

    public static Message mandaMsg(String destino,String grupo) {
        //{"Msg_send", "3"}, //args: group(true/false), destino, msg
        //{"File_send", "3"}, //args: group(true/false),destino, path_to_file

        Scanner sc = new Scanner(System.in);
        String msg;
        System.out.println("Para enviar ficheiro escreva \"FILE\" antes do path");

        do {
            System.out.println("Mensagem:");
            msg = sc.nextLine();
        } while (msg.equals("") || msg.equals(" ") || msg.equals("\n"));

        if(msg.contains("\"FILE\"")){
            String[] args_file = {grupo,destino, msg };
            Message m = new Message("File_send", args_file);
            return m;
        }else if(msg.equals("sair") || msg.equals("SAIR")){
            return null;
        }
        String[] args_msg = {grupo,destino, msg};
        Message m = new Message("Msg_send", args_msg);
        return m;
    }



    public static Message procurarUtilizador() {
        String msg = pedirUsername();

        String[] args_msg = {msg};
        Message m = new Message("Search_usr", args_msg);
        return m;
    }



    public static Message eliminarContacto() {
        Scanner sc = new Scanner(System.in);
        String msg;

        do {
            System.out.println("Introduza Username a eliminar: ");
            msg = sc.nextLine();

        }while (msg.equals(" ") || msg.equals("\n"));

        String[] args_msg = {msg};
        Message m = new Message("Eliminate_contact", args_msg);
        return m;
    }



    public static Message adicionarContacto() {
        String username = pedirUsername();

        String[] args = {username};
        Message m = new Message("Add_contact", args);
        return m;
    }

    public static Message adicionarElementoGrupo(String grupo) {
        //{"Add_usr_grp", "2"}, //args: nome do grupo, username_add                     done++

        String username =pedirUsername();

        String[] args = {grupo, username};
        Message m = new Message("Add_usr_grp", args);
        return m;
    }

    public static Message alterarNomeGrupo(String grupo) {
        //{"Add_usr_grp", "2"}, //args: nome do grupo, username_add                     done++
        Scanner sc = new Scanner(System.in);
        String msg;
        do {
            System.out.println("Novo nome Grupo: ");
            msg = sc.nextLine();

        }while (Objects.equals(msg, " ") || Objects.equals(msg, "\n"));

        String[] args = {grupo, msg};
        Message m = new Message("Change_name_group", args);
        return m;
    }


    public static Message excluirMembro(String grupo) {
        //{"ExcludeMember_grp", "2"}, //args: nome do grupo, username a eliminar        done

        String username =pedirUsername();

        String[] args = {grupo, username};
        Message m = new Message("ExcludeMember_grp", args);
        return m;
    }










    public static Message criarGrupo() {
        //{"Create_grp", "1"}, //args: nome do grupo  done++
        Scanner sc = new Scanner(System.in);
        boolean sair=false;
        String nome, elemento;
        int cont=0;
        System.out.println("\nNome Grupo: ");

        do {
            nome = sc.nextLine();
        } while (nome.equals("") || nome.equals(" ") || nome.equals("\n"));

        ArrayList<String[]> elementos_grupo = new ArrayList<>();
        System.out.println("\n-------------");
        System.out.println("Nomes Elementos do Grupo:");
        System.out.println("escreva [sair] quando todos os elementos tiverem sido adicionados");
        do{
            System.out.println("Elemento " +cont+ ":");
            elemento = sc.nextLine();
            if(!elemento.toUpperCase().equals("SAIR")){
                String[] a = new String[]{elemento};
                elementos_grupo.add(a);
                cont++;
            }else{
                sair=true;
            }
        }while (!sair);

        String[] args_msg = {nome};
        Message m = new Message("Create_grp", args_msg);
        m.setLista(elementos_grupo);
        return m;
    }






    /**************************************************************/
    /**************************************************************/
    /**************************Menus*******************************/
    /**************************************************************/
    /**************************************************************/


    public static int menu(){
        int opt;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("Menu:\n");
            System.out.println("1 - Login");
            System.out.println("2 - Registar");
            System.out.println("0 - Sair");

            System.out.print("> ");

            while (!sc.hasNextInt())
                sc.next();
            opt = sc.nextInt();

        }while(opt!=0 && opt!=1 && opt!=2);
        return opt;
    }




    public static int menu_principal() {
        int opt;
        Scanner sc = new Scanner(System.in);

        do{
            System.out.println("\n 1 - Mostrar Contactos (Msg/Grupos)");
            System.out.println(" 2 - Listar Utilizadores");
            System.out.println(" 3 - Procurar Utilizador");
            System.out.println(" 4 - Adicionar Contacto");
            System.out.println(" 5 - Eliminar Contacto");
            System.out.println(" 6 - Abrir Msg");                                   // TODO: 28/12/2021 AINDA NÃO FOI FEITO!!!!
            System.out.println(" 7 - Listar Grupos");
            System.out.println(" 8 - Criar Grupo");
            System.out.println(" 9 - Notificações");                                 // TODO: 28/12/2021 AINDA NÃO FOI FEITO!!!!
            System.out.println("10 - Editar Perfil");                                // TODO: 28/12/2021 AINDA NÃO FOI FEITO!!!
            System.out.println("0 - Terminar Sessão");
            // TODO: 28/12/2021 Terminar sessão

            System.out.print("> ");

            opt = sc.nextInt();
        }while(opt<0 || opt>11);

        return opt;
    }




    public static int menu_abrirMsg() {
        Scanner sc = new Scanner(System.in);
        int opt;
        do {

            String id_utilizador, id_grupo;
            System.out.println("\n-------------");
            System.out.println("Abrir Msg");
            System.out.println("-------------\n\n");

            System.out.println("1 - Grupo");
            System.out.println("2 - Utilizador");
            System.out.println("0 - Voltar");

            System.out.print("> ");

            opt = sc.nextInt();
        }while (opt!=0 && opt!=1 && opt!=2);
        return opt;
    }


    public static int menu_abrirMsgGrupo(boolean admin) {
        Scanner sc = new Scanner(System.in);
        int opt;
        do {

            String id_utilizador, id_grupo;
            System.out.println("\n-------------");
            System.out.println("Grupo");
            System.out.println("-------------\n\n");

            System.out.println("1 - Mandar msgs");
            System.out.println("2 - Adicionar Utilizador");
            System.out.println("3 - Sair do Grupo");
            if(admin) {
                System.out.println("-----------Opções de Administrador---------");
                System.out.println("4 - Alterar Nome do Grupo");
                System.out.println("5 - Excluir Membro");
                System.out.println("6 - Eliminar Grupo");
            }
            System.out.println("0 - Voltar");

            System.out.print("> ");

            opt = sc.nextInt();
        }while (opt!=0 && opt!=1 && opt!=2 && opt!=3 && (admin && opt!=4) && (admin && opt!=5) && (admin && opt!=6) );
        return opt;
    }


    public static Message menu_editarperfil() {
        int opt;
        Scanner sc = new Scanner(System.in);

        do{
            System.out.println("\n1 - Mudar Nome");
            System.out.println("2 - Mudar Password");
            System.out.println("0 - Voltar");

            System.out.print("> ");
            opt = sc.nextInt();

        }while(opt!=0 && opt!=1 && opt!=2);

//sc.next();

        String msg = null;
        if(opt!=0) {
            do {
                if (opt == 1)
                    System.out.println("Nome: ");
                if (opt == 2)
                    System.out.println("Password: ");
                msg = sc.nextLine();
            } while (msg.equals("") || msg.equals(" ") || msg.equals("\n"));

            String opts = String.valueOf(opt);

            String[] args_msg = {opts, msg};
            Message m = new Message("Edit_profile", args_msg);
            return m;
        }
        return null;

    }




    public static String pedirUsername() {
        Scanner sc = new Scanner(System.in);
        String username;
        do {
            System.out.println("\nUsername: ");
            username = sc.nextLine();
        }while (username.equals("") || username.equals(" ") || username.equals("\n"));

        return username;
    }

}