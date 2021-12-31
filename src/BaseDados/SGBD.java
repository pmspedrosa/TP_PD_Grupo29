package BaseDados;

import Constantes.Constantes;
import shared_features.Message;

import java.sql.*;
import java.util.ArrayList;

public class SGBD {
    static final int TIMEOUT = 10000; //10 seconds
    static final int TABLE_ENTRY_TIMEOUT = 60000; //60 seconds

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String sgbdAddress = null;
    private static String dbName = "bdtp";
    private static String user = "bdtpUser";
    private static String pass = "aERBrJCu%C9kV90&#vSD";

    /**
     * ip: 195.154.179.17
     * nome bd: bdtp
     * user: bdtpUser
     * senha: aERBrJCu%C9kV90&#vSD
     */

    public static void setSgbdAddress(String ip) {
        sgbdAddress = ip;
    }

    public static boolean login(String[] args, String ip, int porto) {
        if (args == null)
            return false;

        String username = args[0];
        String password = args[1];

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement();
             Statement stmt2 = conn.createStatement()) {

            String query = "SELECT * FROM users where username='" + username + "' && password='" + password + "';";

            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next())
                return false;

            else {
                if (stmt2.executeUpdate("update users set last_ip='" + ip + "', last_port=" + porto + " where username='" + username + "';") < 1)
                    System.out.println("Não foi possível guardar o ip/porto do utilizador \"" + username + "\" ( " + ip + ":" + porto + ")");
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Regista um utilizador na base de dados
     *
     * @param args array com {nome, username, password}
     * @return true se registado com sucesso, false caso contrario
     */
    public static boolean registo(String[] args) {
        // TODO: 28/12/2021 para evitar erros de bd verificar se tamanho de username > 20 (dá erro na bd),
        //  nome > 30, password > 25 (!!! Importante)

        if (args == null)
            return false;

        String nome = args[0];
        String username = args[1];
        String password = args[2];

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs1 = stmt.executeQuery("SELECT username FROM users WHERE username='" + username + "';")) {

            if (rs1.next()) //já existe
                return false;
            else { //vai tentar inserir
                String insert = "INSERT INTO users (username, name, password) values('" + username + "', '" + nome + "', '" + password + "');";
                if (stmt.executeUpdate(insert) < 1)
                    return false;
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return false;
    }



    public static Message mostrarContactos(String username) {
        // TODO: 28/12/2021 mostrar se estão online ou offline <<<corrigir>>>

        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            //mostra os nomes com quem o user já trocou mensagens
                String query = "SELECT users.username, users.name, " +
                                    " substring(round((CURRENT_TIMESTAMP - users.last_online)/3600000,2), 1 ,1) as lastOn_horas, " +
                                    " substring(round((CURRENT_TIMESTAMP - users.last_online)/3600000,2), 3 ,4) as lastOn_min" +
                                    " FROM users, contacts WHERE users.username = contacts.username" +
                                    " and contacts.username_own = '"+username+"';";   //, round((CURRENT_TIMESTAMP - users.last_online)/3600000, 2) as lastOn_check

                System.out.println(query);

                ResultSet rs = stmt.executeQuery(query);

                ArrayList<String[]> lista = new ArrayList<>();
                while (rs.next()) {
                    try {
                        String user = rs.getString("users.username");
                        String nome = rs.getString("users.name");
                        String lastOn_horas = rs.getString("lastOn_horas");
                        String lastOn_min = rs.getString("lastOn_min");

                        String lastOn="Indisponível";

                        if (lastOn_horas.equals("0") && Integer.parseInt(lastOn_min) > 5) {
                            lastOn = "Online há " + lastOn_min + " minuto(s).";
                        } else if (Integer.parseInt(lastOn_horas) >= 1) {
                            lastOn = "Online há " + lastOn_horas + " hora(s).";
                        }

                        String[] a = new String[]{user, nome, lastOn};
                        //String[] a = {user, nome};

                        lista.add(a);
                    } catch (Exception e) {
                        System.out.println("\r\n> \r\n\t " + e + "\r\n");
                    }
                } //while

                Message m = new Message("Result_List_Contacts", new String[]{"Lista de Contactos carregada com sucesso.","true"});
                m.setLista(lista);

                for (String []utilizador : m.getLista())
                    System.out.println(utilizador[0] + " " + utilizador[1] + " " + utilizador[2] );

                return m;
        } catch(Exception e){
            System.out.println("Exceção genérica: " + e);
        }

        return new Message("Result_List_Contacts", new String[]{"Não foi possível carregar a lista de contactos.","false"});
    }



    public static Message listUsers(){
        Message msg = null;

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, name FROM users;")) {

            ArrayList<String[]> listaUsers = new ArrayList<>();

            while (rs.next()) {
                try {
                    String user = rs.getString("username");
                    String nome = rs.getString("name");
                    String[] utilizador = {user, nome};
                    listaUsers.add(utilizador);
                } catch (Exception e) {
                    System.out.println("Não foi possivel obter a lista de utilizadores\n\t" + e);
                }
            } //while

            String []args = {"Lista de Utilizadores"};
            msg = new Message("Result_List_Users", args);
            msg.setLista(listaUsers);
            for (String []utilizador : msg.getLista())
                System.out.println(utilizador[0] + " " + utilizador[1]);

        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return msg;
    }


    public static Message listGroups() {
        Message msg = null;

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt1 = conn.createStatement();
             ResultSet rs1 = stmt1.executeQuery("SELECT name, id_group, user_admin FROM groups;")) {

            ArrayList<String[]> listaGrupos = new ArrayList<>();

            while (rs1.next()) {
                try {
                    String nome_grupo = "\n[" + rs1.getString("name") + "]";
                    int id = rs1.getInt("id_group");
                    String admin = "admin: " + rs1.getString("user_admin");

                    ArrayList<String> utilizadores = new ArrayList<>();
                    utilizadores.add(admin);

                    Statement stmt2 = conn.createStatement();
                    String usersQuery = "SELECT username FROM users_groups WHERE id_group = " + id + ";";
                    ResultSet rs2 = stmt2.executeQuery(usersQuery);
                    while (rs2.next()) {
                        try {
                            String nome = rs2.getString("username");

                            utilizadores.add(nome);

                        } catch (Exception e) {
                            System.out.println("Não foi possivel obter a lista de Utilizadores\n\t" + e);
                        }
                    } //while2

                    String []grupo = {nome_grupo, String.valueOf(utilizadores)};
                    listaGrupos.add(grupo);

                } catch (Exception e) {
                    System.out.println("Não foi possivel obter a lista de Grupos\n\t" + e);
                }
            } //while1

            String []args = {"Lista de Grupos"};
            msg = new Message("Result_List_Groups", args);
            msg.setLista(listaGrupos);

            if(Constantes.DEBUG)
                for (String []grupo : msg.getLista())
                    System.out.println(grupo[0] + " " + grupo[1]);

        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return msg;
    }


    public static Message procurarUtilizador(String []args) {
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        Message msg = null;

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, name FROM users WHERE username like \"%"+args[0]+"%\"")) {

            ArrayList<String[]> listaUsers = new ArrayList<>();

            while (rs.next()) {
                try {
                    String user = rs.getString("username");
                    String nome = rs.getString("name");
                    String[] utilizador = {user, nome};
                    listaUsers.add(utilizador);
                } catch (Exception e) {
                    System.out.println("Não foi possivel obter a lista de utilizadores\n\t" + e);
                }
            } //while

            String []ssargs = {"Lista de Utilizadores com Username '"+args[0]+"'"};
            msg = new Message("Result_Search_Users", ssargs);
            msg.setLista(listaUsers);
            /*for (String []utilizador : msg.getLista())
                System.out.println(utilizador[0] + " " + utilizador[1]);*/

        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return msg;
    }


    public static Message adicionarContacto(String []args, String username) {
        //pesquisar para ver se cliente username tem o cantacto, senao envia mensagem
        //se sim add contacto

        //args[0] -> username contacto a eliminar
        Message msg = null;
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            /**INSERT INTO `contacts` (`username_own`, `username`) VALUES ('pedro', 'pedrovski');*/
            int rs2 = stmt.executeUpdate("INSERT INTO contacts (username_own, username) VALUES ('"+username+"','"+args[0]+"')");
            if(rs2==0) {
                msg = new Message("Result_Add_Contact", new String[]{"Erro ao adicionar Contacto","false"});
           }
            msg = new Message("Result_Add_Contact", new String[]{"Contacto encontrado e adicionado","true"});
            //}
        } catch(Exception e){
            System.out.println("Exceção genérica: " + e);
        }

        return msg;
    }


    public static Message eliminarContacto(String []args, String username) {
        //pesquisar para ver se cliente username tem o contacto, senao envia mensagem
        //se sim eliminar contacto

        //args[0] -> username contacto a eliminar
        Message msg = null;
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)) {
            String query2 = "Delete FROM contacts WHERE username_own = '"+ username + "' and username ='"+ args[0] + "'" ;
                PreparedStatement stmt = conn.prepareStatement(query2);
                stmt.execute();
            String query3 = "Delete FROM contacts WHERE username_own = '"+ args[0] + "' and username ='"+ username+ "'" ;
            PreparedStatement stmt1 = conn.prepareStatement(query3);
            stmt1.execute();
            msg = new Message("Result_Eliminate_Contact", new String[]{"Contacto encontrado e eliminado da lista de contactos","true"});
            return msg;
        } catch(Exception e){
            System.out.println("Exceção genérica: " + e);
            msg = new Message("Result_Eliminate_Contact", new String[]{"Erro ao eliminar contacto","false"});
            return msg;
        }
    }


    public static Message criarGrupo(String[] argumentos, ArrayList<String[]> lista, String username) {
        Message msg = null;
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            /**INSERT INTO `contacts` (`username_own`, `username`) VALUES ('pedro', 'pedrovski');*/
            int rs2 = stmt.executeUpdate("INSERT INTO groups (user_admin, name) VALUES ('"+username+"','"+argumentos[0]+"')");
            if(rs2==0) {
                return new Message("Result_Add_Contact", new String[]{"Erro ao adicionar Contacto","false"});
            }
            ResultSet rs = stmt.executeQuery("SELECT id_group FROM groups WHERE user_admin like '" +username+ "' and name like '"+argumentos[0]+"'");

            rs.next();
            int id = rs.getInt("id_group");

            for (String[] a : lista) {
                if(verificaUtilizador(a[0])) {
                    int rse = stmt.executeUpdate("INSERT INTO users_groups (username, id_group) VALUES ('" + a[0] + "','" + id + "')");
                    if (rse == 0) {
                        return new Message("Result_Add_Contact", new String[]{"Erro ao adicionar Contacto", "false"});
                    }
                }
            }
            msg = new Message("Result_Add_Contact", new String[]{"Grupo criado com sucesso","true"});
        } catch(Exception e){
            System.out.println("Exceção genérica: " + e);
        }

        return msg;
    }




    public static Message show_msgs(String[] args, String username) {
        //{null, nomeGrupo} -- grupo
        //{username, null}  -- utilizador
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        Message msg = null;
        String admin = "false";

        System.out.println(args[0] + " " + args[1]);

        if(args[0].equals("null") && !args[1].equals("null")) {    //show_msgs - grupo
            try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
                 Statement stmt = conn.createStatement()){


                 ResultSet rs = stmt.executeQuery("Select groups.user_admin, messages.username, messages.text FROM messages, group_msg, groups" +
                         " WHERE messages.id_msg = group_msg.id_msg and groups.id_group = group_msg.id_group" +
                         " and groups.name = '"+args[1]+"'") ;

                ArrayList<String[]> listaMsgsGroup = new ArrayList<>();
                String admin_name = null;
                while (rs.next()) {
                    try {
                        admin_name = rs.getString("user_admin");
                        String user = rs.getString("username");
                        String text = rs.getString("text");
                        String[] mensagens = {user, text};
                        listaMsgsGroup.add(mensagens);

                        System.out.println(user + ": " + text);

                    } catch (Exception e) {
                        System.out.println("Não foi possivel obter a lista de utilizadores\n\t" + e);
                    }
                } //while

                if(username.equals(admin_name))
                    admin="true";

                String mm = "Lista de Mensagens e Username do grupo " + args[1];

                String[] ssargs = {mm, admin};
                msg = new Message("Result_Show_msgs", ssargs);
                msg.setLista(listaMsgsGroup);
//            for (String []utilizador : msg.getLista())
//                System.out.println(utilizador[0] + " " + utilizador[1]);
                return msg;

            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        } else if(!args[0].equals("null") && args[1].equals("null")){        //show_msgs - utilizador
            try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
                 Statement stmt = conn.createStatement();
                 //group_msg, messages

                 ResultSet rs = stmt.executeQuery("Select messages.text FROM messages, user_msg" +
                         " WHERE messages.id_msg = user_msg.id_msg " +
                         " and user_msg.username = '"+args[0]+"' and messages.username = '"+username+"'")) {

                ArrayList<String[]> listaMsgsUtilizador = new ArrayList<>();

                while (rs.next()) {
                    try {
                        String text = rs.getString("text");
                        String[] mensagens = {text};
                        listaMsgsUtilizador.add(mensagens);
                    } catch (Exception e) {
                        System.out.println("Não foi possivel obter a lista de mensagens\n\t" + e);
                    }
                } //while
                String mm = "Lista de Mensagens com" + args[1];
                String[] ssargs = {mm, "true"};
                msg = new Message("Result_Show_msgs", ssargs);
                msg.setLista(listaMsgsUtilizador);
                return msg;
            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        String[] ssargs = {"Erro ao aceder às mensagens", "false"};
        msg = new Message("Result_Show_msgs", ssargs);
        return msg;
    }






    public static Message mandaMsg(String[] args, String username) {
       // {"Msg_send", "3"}, //args: group(true/false), destino, msg

        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        Message msg = null;
        String admin = "false";

        System.out.println(args[0] + " " + args[1]);

        if(args[0].equals("true")) {    //mandaMsg ->grupo
            try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
                 Statement stmt = conn.createStatement()){

                int rs = stmt.executeUpdate("INSERT INTO messages ( username, text, state, hasRead,isFile) VALUES ('"+username+"','"+args[2]+"'"+ 1+ 0+0+")");
                if(rs==0) {
                    return new Message("Result_Msg_send", new String[]{"Erro ao enviar msg","false"});
                }

                ResultSet rsr = stmt.executeQuery("Select max(id_msg) FROM messages" +
                        " WHERE username like '"+username+"' and text ='"+args[2]+"'") ;

                rsr.next();
                int id_msg = rsr.getInt("id_msg");
                int id_grupo = getIdGrupo(args[1]);

                int rs2 = stmt.executeUpdate("INSERT INTO group_msg (id_group, id_msg) VALUES ('"+id_grupo+"','"+id_msg+"')");
                if(rs2==0) {
                    return new Message("Result_Msg_send", new String[]{"Erro ao enviar msg","false"});
                }

//                ResultSet rs = stmt.executeQuery("Select groups.user_admin, messages.username, messages.text FROM messages, group_msg, groups" +
//                        " WHERE messages.id_msg = group_msg.id_msg and groups.id_group = group_msg.id_group" +
//                        " and groups.name = '"+args[1]+"'") ;


                String mm = "Mensagem enviada com sucesso para " + args[1];

                String[] ssargs = {mm, admin};
                msg = new Message("Result_Show_msgs", ssargs);
                return msg;

            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        } else if(!args[0].equals("false")){        //show_msgs - utilizador       // {"Msg_send", "3"}, //args: group(true/false), destino, msg

            try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
                 Statement stmt = conn.createStatement()){

                int rs = stmt.executeUpdate("INSERT INTO messages ( username, text, state, hasRead,isFile) VALUES ('"+username+"','"+args[2]+"'"+ 1+ 0+0+")");
                if(rs==0) {
                    return new Message("Result_Msg_send", new String[]{"Erro ao enviar msg","false"});
                }

                ResultSet rsr = stmt.executeQuery("Select id_msg FROM messages" +
                        " WHERE username like '"+username+"' and '"+args[2]+"' and state = 1 and hasRead = 0 and isFile=0") ;

                rsr.next();
                int id_msg = rsr.getInt("id_msg");

                int rs2 = stmt.executeUpdate("INSERT INTO user_msg (username, id_msg) VALUES ('"+args[1]+"','"+id_msg+"')");
                if(rs2==0) {
                    return new Message("Result_Msg_send", new String[]{"Erro ao enviar msg","false"});
                }

//                ResultSet rs = stmt.executeQuery("Select groups.user_admin, messages.username, messages.text FROM messages, group_msg, groups" +
//                        " WHERE messages.id_msg = group_msg.id_msg and groups.id_group = group_msg.id_group" +
//                        " and groups.name = '"+args[1]+"'") ;


                String mm = "Mensagem enviada com sucesso para " + args[1];

                String[] ssargs = {mm, admin};
                msg = new Message("Result_Show_msgs", ssargs);
                return msg;

            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        String[] ssargs = {"Erro ao enviar mensagem", "false"};
        msg = new Message("Result_Msg_send", ssargs);
        return msg;

    }








    public static Message editarPerfil(String[] argumentos, String username) {
        //{"Edit_profile", "2"}, //args: 1-nome|2-username|3-password, new parametro
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        Message msg = null;
        String query;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)) {
            if (argumentos[0].equals("1")) {
                query = "update users set name = '"+argumentos[1]+"' where username like '"+username+"'";
            } else {
                query = "update users set `password` = '"+argumentos[1]+"'where username like '"+username+"'";
            }
            PreparedStatement preparedStmt = conn.prepareStatement(query);

            preparedStmt.executeUpdate();

            String[] ssargs = {"Alterado com sucesso", "true"};
            msg = new Message("Result_Edit_profile", ssargs);
            return msg;

        } catch (SQLException e) {
        System.out.println(e);
        e.printStackTrace();
            String[] ssargs = {"Erro ao enviar mensagem", "false"};
            msg = new Message("Result_Msg_send", ssargs);
            return msg;

    }
    }





    /**funções gerais*/


    private static int getIdGrupo(String name) {
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT id_group FROM groups WHERE name like '" +name+ "'");
            if(rs.next()){
                int id = rs.getInt("id_group");
                    return id ;
            }

        }catch(Exception e){
            System.out.println("Exceção genérica: " + e);
        }
        return -1;
    }



    private static Boolean verificaContacto(String username, String username2) {
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT contacts FROM groups WHERE username_own like '" +username+ "' and username like '"+username2+"'");
            if(rs.next()){
                ResultSet rs2 = stmt.executeQuery("SELECT contacts FROM groups WHERE username_own like '" +username2+ "' and username like '"+username+"'");
                if(rs2.next())
                    return true;
            }

        }catch(Exception e){
            System.out.println("Exceção genérica: " + e);
        }
        return false;
    }


    private static Boolean verificaUtilizador(String username) {                                             //é uma  geral
        String dbUrl = "jdbc:mysql://" + sgbdAddress + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT users FROM groups WHERE username like '" +username+ "'");
            if(rs.next()){
                return true;
            }

        }catch(Exception e){
            System.out.println("Exceção genérica: " + e);
        }
        return false;
    }


}


//responsável pela gestão e persistência da informação de suporte ao
// sistema (contas de utilizadores, estado de ligação dos utilizadores,
// histórico de mensagens trocadas e notificações de ficheiros disponibilizados,
// grupos, contactos estabelecidos, etc).