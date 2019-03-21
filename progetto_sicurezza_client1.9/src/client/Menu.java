package client;

public enum Menu {
    MOSTRA_MESSAGGI(1), INSERISCI_MESSAGGIO(2), CANCELLA_MESSAGGIO(3),ESCI(4);
    private final int indice;

    private Menu(int i){
        indice = i;
    }

    public int getIndice(){
        return indice;
    }

    public static Menu fromIndice(int b){
      for (Menu m : Menu.values())
      if (m.indice == b){
         return m;
      }
      return null;
    }
}
