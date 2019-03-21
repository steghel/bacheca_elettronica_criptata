/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.*;
/**
 *
 * @author sgh
 */
public class SicurezzaClient {
    Object certificato;
    Object chiavePubblica;
    KeyPair keyPair;
    
    public void creazioneChiaviPubblicaPrivata(){
        // pag 109 sicurezza in java wrox
        try{
            // Create an RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            keyPair = keyPairGenerator.genKeyPair();
            System.out.println("Chiave pubblica e privata generata");
            System.out.println(keyPair.getPublic());
            System.out.println(keyPair.getPrivate());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
   
    
    public Object invioChiavePubblica(){
        chiavePubblica = keyPair.getPublic();
        Console.scriviStringa("chiave pubblica inviata");
        return chiavePubblica;
    }
    
    
    
    public byte[]chiavePubblicaInByte(){
        return keyPair.getPublic().getEncoded();
        /*the PublicKeys method "getEncoded()"  gives you an X509 
         * Encoded representation (Byte[]) of the PublicKey.
           This array can then be sent using the socket connection.*/
    }
    
    public KeyPair getKeyPair(){
        return keyPair;
    }
}