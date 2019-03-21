package server;

class Lista{
    private Messaggio testaLista ;
    public Lista(){}
    
    public void insTesta(String tes){
        Messaggio msg = new Messaggio(tes);
        msg.setPun(testaLista);
		testaLista = msg;
    }

    public String estElem(int id,int cc){
        Messaggio rs = testaLista;
        Messaggio rt = null;
		while (rs != null && rs.getId()!= id){
            rt = rs;
            rs = rs.getPun();
        }
		if (rs == testaLista && rs.getCodiceCanc()== cc){
             testaLista = rs.getPun();
             return "operazione riuscita";
        }
        else {
           if(rs.getCodiceCanc()== cc){
                rt.setPun(rs.getPun());
                return "operazione riuscita";
           }
           else{
                return "operazione fallita, codice di cancellazione non corretto";
           }
        }
    }
    
    public void mostraMessaggio(){
            testaLista.stampaMessaggio();
    }
    
    public int mostraId() {
        return testaLista.getId();
        //return testaLista.getId();
    }
    
    public int mostraCodiceCanc() {
        return testaLista.getCodiceCanc();
    }
    
    public String stampa (){
        String st= "";
        if ( testaLista == null){
            return "Lista vuota";
        }
        else{
            Messaggio rr = testaLista;
            while (rr != null){
                st +=  "  Messaggio " + rr.getId() + "  " + rr.getTesto() + '\n' ;
                rr = rr.getPun();
            }
        }
        return st;
     }
 }
    
    


