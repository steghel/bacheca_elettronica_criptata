package server;

public class ServitoreMain {
public static void main(String[] args){
    Lista l = new Lista();
    Console.scriviStringa("scrivi il numero della porta del  server");
    int ps = Console.leggiIntero();
    Servitore s = new Servitore(ps,l);
    s.inizioServizio();
  }
}
