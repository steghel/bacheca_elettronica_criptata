package server;

import java.io.*;
import java.net.*;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.IllegalBlockSizeException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;//import javax.crypto.spec.SecretKeySpec;

public class ServitoreThread extends Thread {
  
  final int RSAKeySize = 1024;
  final String newline = "\n";
  
  private Socket s;
  private DataInputStream disIn;
  private DataOutputStream dosOut;
  private BufferedReader brIn;
  private PrintWriter pwOut;
  private Lista l;
  //Key pubKey = null;
  private PublicKey chiavePubblicaClient = null;
  private Key chiaveSimmetrica = null;
  private byte[] chiaveSimmetricaCifrata;
  private byte[] digestCriptato;
  private String messaggioDecodificato="";
  private byte[] messaggioCriptato;
  private byte[] messaggioDecifrato;
  private byte[] stringaCriptata; //messaggio criptato con chiave simmetrica
  
  public ServitoreThread(Socket so,Lista lis){
      s = so;
      l = lis;
  }
   
  public void run(){
      //generazione di stream di ingresso e uscita verso il socket
      try{
          Console.scriviStringa("Si e' connesso un cliente");
          disIn = new DataInputStream(s.getInputStream());
          dosOut = new DataOutputStream(s.getOutputStream());
          brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
          pwOut = new PrintWriter(s.getOutputStream(),true);
      }
      catch(Exception e){
            e.printStackTrace();
      } 
      
      ricezioneChiavePubblicaClient();
      
      generaChiaveSimmetrica(); 
      
      creaCifratore();
      
      inviaChiaveSimmetricaCifrata();
      
      
      //riprende l'esecuzione del programma non criptato
      try{  
          boolean continua = true;
          while (continua){
              Menu menu;
              //menu = Menu.fromIndice(disIn.readInt());
              
              //ricevi il codice criptato dal client       
                riceviMessaggioCriptato(); 

                //decodifica messaggio
                decodifica();

                //ricevi firma
                riceviFirma();    
                //Console.scriviStr("digest" + digestCriptato);


                //confronta digest ricevuto con quello generato
                boolean bc=false;
                bc=verificaFirma();
                if(bc==true){
                    menu=Menu.fromIndice(Integer.parseInt(messaggioDecodificato));
                    switch (menu){
                        case MOSTRA_MESSAGGI:
                             mostraMessaggi();
                             break;
                        case INSERISCI_MESSAGGIO:
                             inserisciMessaggio();
                             break;
                        case CANCELLA_MESSAGGIO:
                             cancellaMessaggio(l);
                             break;
                        case ESCI:
                    }
                }
                else{
                        break;
                }
          }
     }
     catch (IOException ioe){
         Console.scriviStringa("Problemi durante la comunicazione "
                 + "con il cliente " + ioe.getMessage());
     }
   
  }
  
  
  
  public void ricezioneChiavePubblicaClient(){
     //ricevi la chiave pubblica dal client
     try{
            //crea un array avente come dimensione quella che ricevi dal client
            byte[]cpb = new byte[disIn.readInt()];
            //ricevi l'array di byte 
            disIn.readFully(cpb);
            //dai byte della chiave ottenuta dal client, ricava un oggetto
            //di tipo key che sarà usato nei cifratori
            X509EncodedKeySpec ks = new X509EncodedKeySpec(cpb);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            chiavePubblicaClient = kf.generatePublic(ks);
     } catch (IOException e) {
            System.out.println("Errore IO");
            System.exit(0);
     } catch (NoSuchAlgorithmException e) {
            System.out.println("Errore algoritmo");
            System.exit(0);
     } catch (InvalidKeySpecException e) {
            System.out.println("Errore chiave non valida");
            System.exit(0);
     }
  }
  
  public void generaChiaveSimmetrica(){
      try{
            // Create a Blowfish key to be encrypted
          //System.out.println("Generating a symmetric (Blowfish) key...");
          KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");          
          keyGenerator.init(128);
          chiaveSimmetrica = keyGenerator.generateKey();
          //Console.scriviStringa("chiaveSimmetrica: " + chiaveSimmetrica );
          //System.out.println("Format: "+ chiaveSimmetrica.getFormat()+chiaveSimmetrica);

          //System.out.println("Generating an RSA key...");
      }catch (NoSuchAlgorithmException e) {
            System.out.println("Errore algoritmo");
            System.exit(0);
      }
  }
  
  public void creaCifratore(){
      try{
            /* Create a cipher using the public key to initialize it.
           * We use Electronic CodeBook mode and PKCS1Padding. ECB
           * is good for encrypting small blocks of random data,
           * like a key.
           * PKCS1Padding is standard for most implementations of RSA */
          Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
          cipher.init(Cipher.ENCRYPT_MODE, chiavePubblicaClient);

          // Get the bytes of the blowfish key
          byte[] blowfishKeyBytes = chiaveSimmetrica.getEncoded();

          // Perform the actual encryption on those bytes
          chiaveSimmetricaCifrata = cipher.doFinal(blowfishKeyBytes);
          Console.scriviStringa("chiaveSimmetricaCifrata: " + chiaveSimmetricaCifrata );
      }
      catch (NoSuchPaddingException e) {
            System.out.println("Error obtaining server public key 2.");
            System.exit(0);
      }
      catch (NoSuchAlgorithmException e) {
            System.out.println("Error obtaining server public key 2.");
            System.exit(0);
      }
      catch (InvalidKeyException e) {
            System.out.println("Error obtaining server public key 2.");
            System.exit(0);
      }
      catch (IllegalBlockSizeException e) {
            System.out.println("Error obtaining server public key 2.");
            System.exit(0);
      }
      catch (BadPaddingException e) {
            System.out.println("Error obtaining server public key 2.");
            System.exit(0);
      }

  }
  
  
  
  public void inviaChiaveSimmetricaCifrata(){
     try {
            //invia la lunghezza dell'array di byte
            dosOut.writeInt(chiaveSimmetricaCifrata.length);
            //invia l'array di byte
            dosOut.write(chiaveSimmetricaCifrata);
            //svuota lo stream di uscita
            dosOut.flush();
            //Console.scriviStringa("chiave simmetrica cifrata" + chiaveSimmetricaCifrata);
        } catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
        }
  }
  
  public void decodifica(){
      try{
         
         //byte[] msg = str.getBytes("UTF8"); 
         //crea il cifratore
         Cipher cipher = Cipher.getInstance("Blowfish");
         //inizializza il cifratore con la chiave privata,modalità decrittaz.
         cipher.init(Cipher.DECRYPT_MODE, chiaveSimmetrica);
         //effettua la decrittazione
         messaggioDecifrato = cipher.doFinal(messaggioCriptato);
         messaggioDecodificato=new String(messaggioDecifrato,"UTF8");
        
         
      }
      catch (NoSuchPaddingException e) {
            System.out.println("errore sul padding");
            System.exit(0);
      }
      catch (NoSuchAlgorithmException e) {
            System.out.println("errore sull'algoritmo");
            //System.exit(0);
      }
      catch (InvalidKeyException e) {
            System.out.println("chiave non valida");
            //System.exit(0);
      }
      catch (IllegalBlockSizeException e) {
            System.out.println("errore su dimensione blocchi");
            //System.exit(0);
      }
      catch (BadPaddingException e) {
            System.out.println("errore sul padding 2.");
            //System.exit(0);
      }catch (UnsupportedEncodingException e) {
            System.out.println("Errore sulla codifica");
            System.exit(0);
      }
  }
  
  public void riceviMessaggioCriptato(){
      try{
            //crea un array avente come dimensione quello che ricevi dal client
            messaggioCriptato = new byte[disIn.readInt()];
            //ricevi l'array di byte 
            disIn.readFully( messaggioCriptato);            
            
     } catch (IOException e) {
            System.out.println("Errore nella ricezione del messaggio cifrato ");
            System.exit(0);
     } 
  }
  
  public void riceviFirma(){
      try{
            //crea un array avente come dimensione quello che ricevi dal client
            digestCriptato = new byte[disIn.readInt()];
            //ricevi l'array di byte 
            disIn.readFully( digestCriptato);            
            
     } catch (IOException e) {
            System.out.println("Errore nella ricezione del messaggio cifrato ");
            System.exit(0);
     } 
  }
  
 
  
  public boolean verificaFirma(){
      //1) decodifica firma con chiave pubblica e ottieni il digest del messaggio
      //2) calcola digest del messaggio ricevuto
      //3) confronta i due digest
      boolean autorizza = false;
      try{
            //Adesso è il momento di verificare la firma (signature=firma)
            Signature firma = Signature.getInstance("MD5WithRSA");
            firma.initVerify(chiavePubblicaClient); 
            firma.update(messaggioDecifrato );            
            autorizza = firma.verify(digestCriptato);            
      }
     
      catch (NoSuchAlgorithmException e) {
            System.out.println("errore sull'algoritmo");
            //System.exit(0);
      }
      
      catch (SignatureException e) {
            System.out.println("verificaDigest chiave non valida");
            System.exit(0);
      }
      catch (InvalidKeyException e) {
            System.out.println("chiave non valida");
            //System.exit(0);
      }
      return autorizza; 
  }

  /*public void mostraMessaggi1()throws IOException{
       String st = null;
       String st1 = "";
       // la stringa st contiene tutti i messaggi contenuti nella lista l
       st = l.stampa();
       //invia la stringa con tutti i messaggi al client
       pwOut.println(st);
       //invia al client la stringa STOP per indicargli che le linee
       //da cui è formata la stringa st sono terminate
       pwOut.println("STOP");
   }*/
  
 public void mostraMessaggi()throws IOException{
       String st = null;
       String st1 = "";
       String st2 = "STOP";
       // la stringa st contiene tutti i messaggi contenuti nella lista l
       st = l.stampa();
       //cripta il messaggio
       codifica(st);
       
       //invia il messaggio criptato al server
       inviaMessaggioCriptato();
       
       //invia al client la stringa STOP per indicargli che le linee
       //da cui è formata la stringa st sono terminate
       //pwOut.println("STOP");
       codifica(st2);
       inviaMessaggioCriptato();
   } 
  
  public void inserisciMessaggio()throws IOException{
      
       //ricevi messaggio criptato
       riceviMessaggioCriptato(); 
       
       //decodifica messaggio
       decodifica();
       
       //ricevi firma
       riceviFirma();    
       //Console.scriviStr("digest" + digestCriptato);
       
       
       //confronta digest ricevuto con quello generato
       boolean b=false;
       b=verificaFirma();
       if(b==true){ 
           
            // crea il messaggio sul server e inseriscilo in testa alla lista
            l.insTesta(messaggioDecodificato);
            //mostra messaggio sul server
            l.mostraMessaggio();
            
            
            //***********************
            // invia l'ID al client
            //***********************
            
            
            String iConvert=Integer.toString(l.mostraId());
            codifica(iConvert);
            inviaMessaggioCriptato();
            
       
            //***********************************************
            //invia  il codice canc del messaggio al client
            //***********************************************
                      
            String iConvert1=Integer.toString(l.mostraCodiceCanc());
            codifica(iConvert1);
       
            //invia il messaggio criptato al server
            inviaMessaggioCriptato();        
                    
            
            
            
       }
       else{
           Console.scriviStringa("messaggio manomesso");
           dosOut.writeInt(0000);
           dosOut.writeInt(0000);
       }
   }
  

  
   
   public void cancellaMessaggio(Lista lis)throws IOException {
       boolean eseguiCancellazione=false;
       int cc;
       int id=0;
       int cod=0;
       String st;
       
       //ricevi l'id criptato dal client       
       riceviMessaggioCriptato(); 
       
       //decodifica messaggio
       decodifica();
       
       //ricevi firma
       riceviFirma();    
       //Console.scriviStr("digest" + digestCriptato);
       
       
       //confronta digest ricevuto con quello generato
       boolean b=false;
       b=verificaFirma();
       if(b==true){
           id=Integer.parseInt(messaggioDecodificato);
           eseguiCancellazione=true;
           
       }
       
       
       //ricevi il codice di cancellazione del messaggio (criptato) dal client       
       riceviMessaggioCriptato(); 
       
       //decodifica messaggio
       decodifica();
       
       //ricevi firma
       riceviFirma();    
       //Console.scriviStr("digest" + digestCriptato);
       
       
       //confronta digest ricevuto con quello generato
       boolean bc=false;
       bc=verificaFirma();
       if(bc==true){
           cod=Integer.parseInt(messaggioDecodificato);
           eseguiCancellazione=true;
           
       }
       
       if(eseguiCancellazione){
            st=lis.estElem(id,cod);
            //invia al client il risultato dell'operazione di eliminazione
            pwOut.println(st);
       }else{
            pwOut.println("operazione fallita causa manomissione dati");
       }
       
   }
   
   public void codifica(String str){
      try{
         //crea il cifratore
         Cipher cipher = Cipher.getInstance("Blowfish");
         //inizializza il cifratore con:modalità decrittaz. , chiave simmetrica
         cipher.init(Cipher.ENCRYPT_MODE, chiaveSimmetrica);
         //effettua la crittazione
         byte[] strInByte = str.getBytes("UTF8");
         stringaCriptata = cipher.doFinal(strInByte); 
         
      }
      catch (NoSuchPaddingException e) {
            System.out.println("errore sul padding");
            System.exit(0);
      }
      catch (NoSuchAlgorithmException e) {
            System.out.println("errore sull'algoritmo");
            //System.exit(0);
      }
      catch (InvalidKeyException e) {
            System.out.println("chiave non valida");
            System.exit(0);
      }
      catch (IllegalBlockSizeException e) {
            System.out.println("errore su dimensione blocchi");
            System.exit(0);
      }
      catch (BadPaddingException e) {
            System.out.println("errore sul padding 2.");
            System.exit(0);
      }catch (UnsupportedEncodingException e) {
            System.out.println("Errore sulla codifica");
            System.exit(0);
      }
  }
   
   public void inviaMessaggioCriptato(){
     //invialo al server
      try {
            //invia la lunghezza dell'array di byte
            dosOut.writeInt(stringaCriptata.length);
            //invia l'array di byte
            dosOut.write(stringaCriptata);
            //svuota lo stream di uscita
            dosOut.flush();
      } 
      catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
      }
  } 
}


