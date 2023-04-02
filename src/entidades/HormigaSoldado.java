package entidades;
 
public class HormigaSoldado extends Thread{
    private int idNumero = 0;
    private String idStr;
    
    public HormigaSoldado(int id){
        this.idNumero = id;
    }
    
    public void calculoID(){
        if(idNumero < 10){
            idStr = "HS000" + String.valueOf(idNumero);
        }
        else{
            if(idNumero >= 10 && idNumero < 100){
                idStr = "HS00" + String.valueOf(idNumero);
            }
            else{
                if(idNumero >= 100 && idNumero < 1000){
                    idStr = "HS0" + String.valueOf(idNumero);
                }
                else{
                    idStr = "HS" + String.valueOf(idNumero);
                }
            }
        }
    }
    
    public void run(){
        calculoID();
        
        System.out.println(idStr);
    }
}
