package entidades;
 
public class HormigaCria extends Thread{
    private int idNumero = 0;
    private String idStr;
    
    public HormigaCria(int id){
        this.idNumero = id;
    }
    
    public void calculoID(){
        if(idNumero < 10){
            idStr = "HC000" + String.valueOf(idNumero);
        }
        else{
            if(idNumero >= 10 && idNumero < 100){
                idStr = "HC00" + String.valueOf(idNumero);
            }
            else{
                if(idNumero >= 100 && idNumero < 1000){
                    idStr = "HC0" + String.valueOf(idNumero);
                }
                else{
                    idStr = "HC" + String.valueOf(idNumero);
                }
            }
        }
    }
    
    public void run(){
        calculoID();
        
        System.out.println(idStr);
    }
}
