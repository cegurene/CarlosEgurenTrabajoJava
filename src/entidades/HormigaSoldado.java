package entidades;
 
public class HormigaSoldado extends Thread{
    private int idNumero = 0;
    private String idStr;
    Colonia colonia;
    
    public HormigaSoldado(int id, Colonia colonia){
        this.idNumero = id;
        this.colonia = colonia;
        calculoID();
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
        //calculoID();
        
        int i = 0;
        
        while(true){
            if(i % 6 == 0){
                // cada 6 iteracciones
                colonia.zonaComer(0, 3, idStr, false);
            }
            else{
                colonia.instruccion(2, 8, idStr);
                colonia.descanso(2, idStr);
            }
            i++;
        }
        
    }
}
