package server;

import java.io.*;
import java.net.*;

public class Servitore{
  private final int port;
  private Lista l;
    
  public Servitore(int p,Lista lis){
      port = p;
      l = lis;
  }
  
  public void inizioServizio(){
    ServerSocket ss = null;
    try{ 
        ss = new ServerSocket(port);
    }
    catch (IOException ioe){ 
        System.out.println("Non riesco a mettermi in ascolto "
                + "sulla porta specificata");
        System.exit(1);
    }
    while (true){
        try{
           Socket s = ss.accept();
           ServitoreThread st = new ServitoreThread(s,l);
           st.start();
      }
      catch (IOException ioe)
      { System.out.println(
          "Problemi durante la comunicazione con il cliente: " +
          ioe.getMessage());
      }
    }
  }
}
