package entidades;
 
public class HormigaCria extends Thread{
    private int idNumero = 0;
    private String idStr;
    private Colonia colonia;
    private Paso paso;
    
    public HormigaCria(int id, Colonia colonia, Paso paso){
        this.idNumero = id;
        this.colonia = colonia;
        this.paso = paso;
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
            paso.mirar();
            colonia.descanso(4, idStr);
            paso.mirar();
        }
    }
}
