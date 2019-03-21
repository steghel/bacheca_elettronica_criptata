package client;

import java.io.*;

public class ClienteMain {
    public static void main(String[] args){
        Cliente cl = null;
        try{
            cl = new Cliente();
        }
        catch (IOException e){
             Console.scriviStringa("Problemi di comunicazione ...");
        }
    }
}

        