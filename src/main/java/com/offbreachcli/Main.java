package com.offbreachcli;

import com.github.britooo.looca.api.util.Conversor;

import java.util.Scanner;
import com.github.britooo.looca.api.core.Looca;

import java.util.SortedMap;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        HardwareData data = new HardwareData();
        User usuario = new User();
        DatabaseConnection dbConnection = new DatabaseConnection();
        System.out.println("-".repeat(22));
        System.out.println("|       Login       |");
        System.out.println("-".repeat(22));
        System.out.println("| Digite Email:     |");
        String email = leitor.nextLine();
        System.out.println("-".repeat(22));
        System.out.println("|Digite Senha:      |");
        String senha = leitor.nextLine();
        System.out.println("-".repeat(22));


        dbConnection.setConnection(email, senha);
        String emailUser = dbConnection.getEmail();
        String senhaUser = dbConnection.getSenha();



        if (emailUser.equals(email) && senhaUser.equals(senha)) {
            data.setHostname();
            dbConnection.fkClinica = dbConnection.getFkClinica();
            dbConnection.saveHardwareData();
            data.cadastrarSistema();
            dbConnection.verifyHostname();
            dbConnection.saveDataInLoop();

        }else {
        }



    }
}

