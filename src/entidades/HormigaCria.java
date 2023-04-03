package entidades;
 
public class HormigaCria extends Thread{
    private int idNumero = 0;
    private String idStr;
    private Colonia colonia;
    
    public HormigaCria(int id, Colonia colonia){
        this.idNumero = id;
        this.colonia = colonia;
        calculoID();
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
        //calculoID();
        
        while(true){
            colonia.zonaComer(3, 5, idStr, true);
            colonia.descanso(4, idStr);
        }
    }
}
