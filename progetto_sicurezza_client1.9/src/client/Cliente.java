

package client;

import java.io.*;
import java.net.*;
import javax.crypto.Cipher;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class Cliente{
    final int RSAKeySize = 1024;
    final String newline = "\n";
    private byte[] stringaCriptata;
    private byte[] chiaveSimmetricaCifrata;
    private byte[] chiaveSimmetricaDecifrata;
    private byte[] digestCriptato;
    private KeyPair keyPair;
    private SecretKey chiaveSimmetrica;
    private PublicKey chiavePubblica;
    private byte[] messaggioDecifrato; //messaggio decifrato ottenuto come array di byte
    private String messaggioDecodificato; //stringa che ottengo dall'array di byte
    private byte[] messaggioCriptato;//messaggio criptato con chiave simmetrica
                                         //che arriva dal server
    //private SicurezzaClient sicurezzaCliente;    
    private String indirizzoIpServer;
    private int portaServer;
    private String nome ;
    private Socket s;
    private DataInputStream disIn;
    private DataOutputStream dosOut;
    private BufferedReader brIn;
    private PrintWriter pwOut;
    
  
  public Cliente() throws IOException{
     Console.scriviStringa("scrivi l'indirizzo Ip del server");
     indirizzoIpServer = Console.leggiStringa();
     Console.scriviStringa("scrivi il numero della porta del  server");
     portaServer = Console.leggiIntero();
     Console.scriviStringa("scrivi il tuo nome");
     nome = Console.leggiStringa();
     s = new Socket(indirizzoIpServer, portaServer);
     disIn = new DataInputStream(s.getInputStream());
     dosOut = new DataOutputStream(s.getOutputStream());
     brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
     pwOut = new PrintWriter(s.getOutputStream(),true);
     
     //crea una coppia di chiavi
     creaCoppiaChiavi();
     
     //invia la chiave pubblica al server
     inviaChiavePubblica();
     
     //ricevi la chiave simmetrica dal server
     riceviChiaveSimmetricaCriptata();
     
     decriptaChiaveSimmetricaConChiavePrivata();
     
     //esegui le normali funzioni del programma  
     selezionaServizio();
  }
  public void creaCoppiaChiavi(){
        // Initialise RSA
        try{
            KeyPairGenerator RSAKeyGen = KeyPairGenerator.getInstance("RSA");
            RSAKeyGen.initialize(RSAKeySize);
            keyPair = RSAKeyGen.generateKeyPair();
            chiavePubblica = keyPair.getPublic();
        } catch (GeneralSecurityException e) {
            System.out.println(e.getLocalizedMessage() + newline);
            System.out.println("Error initialising encryption. Exiting.\n");
            System.exit(0);
        }
  }

  public void inviaChiavePubblica(){
      // invia la chiave pubblica (codificata in byte) al server
        try {
            //invia la lunghezza dell'array di byte
            dosOut.writeInt(chiavePubblica.getEncoded().length);
            //invia l'array di byte
            dosOut.write(chiavePubblica.getEncoded());
            //svuota lo stream di uscita
            dosOut.flush();
        } catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
        }
  }
  
  
  
  
  
  
  public void riceviChiaveSimmetricaCriptata(){
      //ricevi la chiave pubblica dal client
     try{
            //crea un array avente come dimensione quello che ricevi dal client
            chiaveSimmetricaCifrata = new byte[disIn.readInt()];
            //ricevi l'array di byte 
            disIn.readFully( chiaveSimmetricaCifrata);            
            Console.scriviStringa("chiave Simmetrica rappresentata in byte: " + chiaveSimmetricaCifrata);
     } catch (IOException e) {
            System.out.println("Errore nella ricezione della chiave simmetrica ");
            System.exit(0);
     } 
  }
  
  public void decriptaChiaveSimmetricaConChiavePrivata(){
      try{
         //crea il cifratore
         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         //inizializza il cifratore con la chiave privata,modalità decrittaz.
         cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
         //effettua la decrittazione
         chiaveSimmetricaDecifrata = cipher.doFinal(chiaveSimmetricaCifrata);
         //creo un oggetto key
         chiaveSimmetrica = new SecretKeySpec(chiaveSimmetricaDecifrata, "Blowfish");
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
  
  /*public void inviaMessaggioCriptato1(){
     //invialo al server
      try{  ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt(stringaCriptata.length);
            s.getOutputStream().write(bb.array());
            s.getOutputStream().write(stringaCriptata);
            s.getOutputStream().flush();
      }
      catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
      } 
  }*/
  
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
   
  public void inviaFirma(){
     //invialo al server
      try {
            //invia la lunghezza dell'array di byte
            dosOut.writeInt(digestCriptato.length);
            //invia l'array di byte
            dosOut.write(digestCriptato);
            //svuota lo stream di uscita
            dosOut.flush();
      } 
      catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
      }
  }
  
  public void creaFirma(String s){
      try { //trasforma la stringa in un array di byte
            byte[] dati = s.getBytes("UTF8");
            // I dati vengono firmati 
            Signature firma = Signature.getInstance("MD5WithRSA");
            firma.initSign(keyPair.getPrivate());
            firma.update(dati);
            digestCriptato = firma.sign();            
      }
      catch (UnsupportedEncodingException e) {
            System.out.println("Errore sulla codifica");
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
      catch (SignatureException e) {
            System.out.println("chiave non valida");
            System.exit(0);
      }
  }
  
  public void creaDigest1(String s){
      try {
            // Get the bytes of the data from s
            byte[] data = s.getBytes("UTF8");
            // Get an instance of the Signature object and initialize it
            // with the private key for signing
            Signature sig = Signature.getInstance("MD5WithRSA");
            sig.initSign(keyPair.getPrivate());
            
             // Prepare to sign the data
            sig.update(data);

            // Actually sign it
            digestCriptato = sig.sign(); 
      }
      catch (UnsupportedEncodingException e) {
            System.out.println("Errore sulla codifica");
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
      catch (SignatureException e) {
            System.out.println("chiave non valida");
            System.exit(0);
      }
  }
  
  public void selezionaServizio(){
     boolean continua = true;  
     while(continua){
        Console.scriviStringa("\n SELEZIONA UN SERVIZIO");
        Console.scriviStringa("1) mostra messaggi presenti in bacheca \n2) "
             + "inserisci nuovo messaggio \n3) cancella messaggio \n4) esci\n");
        int n = Console.leggiIntero();
        if((n == 0) ||( n >4 )  ){
            Console.scriviStringa("Selezione errata");
            continue;
        }
        Menu menu;
        menu = Menu.fromIndice(n);
            try{
              switch (menu){
                  
                  case MOSTRA_MESSAGGI:                      
                       //dosOut.writeInt(Menu.MOSTRA_MESSAGGI.getIndice());
                      //converti l'intero in stringa
                        String str=Integer.toString(Menu.MOSTRA_MESSAGGI.getIndice());

                        //codifica la stringa
                        codifica(str);

                        //invia il messaggio criptato al server      
                        inviaMessaggioCriptato();

                        //calcola la firma del messaggio in chiaro
                        creaFirma(str);

                        //invia la firma al server
                        inviaFirma(); 
                       mostraMessaggi();
                       break;
                      
                  case INSERISCI_MESSAGGIO:
                       //invio della scelta selezionata al server
                       //dosOut.writeInt(Menu.INSERISCI_MESSAGGIO.getIndice());
                      String str1=Integer.toString(Menu.INSERISCI_MESSAGGIO.getIndice());

                        //codifica la stringa
                        codifica(str1);

                        //invia il messaggio criptato al server      
                        inviaMessaggioCriptato();

                        //calcola la firma del messaggio in chiaro
                        creaFirma(str1);

                        //invia la firma al server
                        inviaFirma(); 
                       inserisciMessaggio();
                       break;
                      
                  case CANCELLA_MESSAGGIO:
                       //dosOut.writeInt(Menu.CANCELLA_MESSAGGIO.getIndice());
                      String str2=Integer.toString(Menu.CANCELLA_MESSAGGIO.getIndice());

                        //codifica la stringa
                        codifica(str2);

                        //invia il messaggio criptato al server      
                        inviaMessaggioCriptato();

                        //calcola la firma del messaggio in chiaro
                        creaFirma(str2);

                        //invia la firma al server
                        inviaFirma(); 
                       cancellaMessaggio();
                       break;
                      
                  case ESCI:
                       System.exit(0);
              }
           }
           catch (IOException e){
               Console.scriviStringa("Problemi di comunicazione ...");
          }
      }
  }

  public void mostraMessaggi()throws IOException  {
      String st = null;
      // crea una stringa STOP
      String st1 = new String("STOP");
      //ciclo infinito per ricevere i vari messaggi dal server
      while(true){
            //ricevi messaggio criptato
            riceviMessaggioCriptato(); 

            //decodifica messaggio
            decodifica();

            //st = brIn.readLine();
            //  esci dal ciclo se ricevi il messaggio STOP
            if(messaggioDecodificato.equals(st1)){
                break;
            }
            Console.scriviStringa(messaggioDecodificato);
      }
 } 
  
  public void mostraMessaggi1()throws IOException  {
      String st = null;
      // crea una stringa STOP
      String st1 = new String("STOP");
      //ciclo infinito per ricevere i vari messaggi dal server
      while(true){
          st = brIn.readLine();
          //  esci dal ciclo se ricevi il messaggio STOP
          if(st.equals(st1)){
              break;
          }
          Console.scriviStringa(st);
      }
 } 

  
  public void inserisciMessaggio() throws IOException {
      //leggi il messaggio dalla tastiera
      String st,st1;
      int id,cc;
      Console.scriviStringa("Inserisci il testo del messaggio ");
      while(true){
         /* senza il while ,il metodo leggiLinea()invia sempre qualcosa
         in uscita e st1 diventa "", questo crea problemi al server.
         Per evitare questo fatto, faccio uscire il programma dal ciclo
         solo quando st1 diverso da "" cioè dal messaggio vuoto*/
         st = Console.leggiLinea();  
         if (!st.equals("")){  //esci dal while se la stringa st è vuota (st="")
            break;
         }
      }
      
      //crea il messaggio da inviare
      st1 = nome + ": " + "\"" + st + "\"";
      
      //cripta il messaggio
      codifica(st1);
      
      //invia il messaggio criptato al server
      inviaMessaggioCriptato();
      
      //calcola la firma del messaggio in chiaro
      creaFirma(st1);
      
      //invia la firma al server
      inviaFirma();     
      
      
      //ricevi dal server ID e codice di cancellazione
      Console.scriviStringa("Messaggio inviato");
      //*****************************
      //ricevi ID
      //*****************************
      //ricevi messaggio criptato
       riceviMessaggioCriptato(); 
       
       //decodifica messaggio
       decodifica();       
       id=Integer.parseInt(messaggioDecodificato);
       
      //*****************************
      //ricevi codice cancel
      //*****************************
      //ricevi messaggio criptato
       riceviMessaggioCriptato(); 
       
       //decodifica messaggio
       decodifica();       
       cc=Integer.parseInt(messaggioDecodificato); 
       
      //id = disIn.readInt();
      //cc = disIn.readInt();
      Console.scriviStringa("Il messaggio è il numero " + id + " e il codice"
              + " di cancellazione è " + cc);
  }

  
  public void cancellaMessaggio()throws IOException  {
      String st;
      int id,cc,cod;
      
      Console.scriviStringa("Quale messaggio vuoi eliminare?");
      
      //leggi da console il numero del messaggio da cancellare
      id = Console.leggiInt();
      
      //converti l'intero in stringa
      String str=Integer.toString(id);
      
      //codifica la stringa
      codifica(str);
      
      //invia il messaggio criptato al server      
      inviaMessaggioCriptato();
      
      //calcola la firma del messaggio in chiaro
      creaFirma(str);
      
      //invia la firma al server
      inviaFirma(); 
      
      //dosOut.writeInt(id);
      
      // codice del messaggio da cancellare
      Console.scriviStringa("Codice di cancellazione");
      //leggi da console il codice del messaggio da cancellare
      cod = Console.leggiInt();
      
      //converti l'intero in stringa
      String strCod=Integer.toString(cod);
      
      //codifica la stringa
      codifica(strCod);
      
      //invia il messaggio criptato al server      
      inviaMessaggioCriptato();
      
      //calcola la firma del messaggio in chiaro
      creaFirma(strCod);
      
      //invia la firma al server
      inviaFirma(); 
      
      st = brIn.readLine();
      Console.scriviStringa(st);
  }
  
  public void cancellaMessaggio1()throws IOException  {
      String st;
      int id,cc;
      Console.scriviStringa("sono in cancellaMessaggio()");
      Console.scriviStringa("Quale messaggio vuoi eliminare?");
      id = Console.leggiInt();
      dosOut.writeInt(id);
      Console.scriviStringa("Codice di cancellazione");
      cc = Console.leggiInt();
      dosOut.writeInt(cc);
      st = brIn.readLine();
      Console.scriviStringa(st);
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
  
 
}
