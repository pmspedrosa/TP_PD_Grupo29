package ui;

import java.util.Scanner;

public class MenuUI {
    public static void menu_escolhaInicial(){
        int opt;
        Scanner sc = new Scanner(System.in);
        do{
            System.out.println("Introduza opt: ");
            opt = sc.nextInt();
        }while(opt != 0);
    }


}
