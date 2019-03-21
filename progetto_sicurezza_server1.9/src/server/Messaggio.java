
package server;

class Messaggio {
        private static int contatore ;
        private int id ;  // id è diverso in ogni messaggio
        private String testo;
        private int codiceCanc;
        private Messaggio pun;
        
        //costruttore
        public Messaggio(String testo){
            contatore++;
            id=contatore;
            codiceCanc= (int)(Math.random()*100000000);
            this.testo = testo;
        }
        
        public int getId () {
            return id;
        }
        
        public int getCodiceCanc () {
            return codiceCanc;
        }
        
        public Messaggio getPun(){
            return pun;
        }
        
        public String getTesto(){
            return testo;
        }
        
         
        public void setPun(Messaggio m){
            pun = m;
        }
        
        public void stampaMessaggio(){
             Console.scriviStringa("Messaggio " + id + ", " + testo + "\n" + codiceCanc );
        }
}