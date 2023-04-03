package entidades;
 
import java.util.logging.Level;
import java.util.logging.Logger;

public class HormigaObrera extends Thread{
    private int idNumero = 0;
    private String idStr;
    private Colonia colonia;
    boolean par;
    
    public HormigaObrera(int id, Colonia colonia){
        this.idNumero = id;
        this.colonia = colonia;
        this.par =  (id % 2 == 0);
        calculoID();
    }
    
    public void calculoID(){
        if(idNumero < 10){
            idStr = "HO000" + String.valueOf(idNumero);
        }
        else{
            if(idNumero >= 10 && idNumero < 100){
                idStr = "HO00" + String.valueOf(idNumero);
            }
            else{
                if(idNumero >= 100 && idNumero < 1000){
                    idStr = "HO0" + String.valueOf(idNumero);
                }
                else{
                    idStr = "HO" + String.valueOf(idNumero);
                }
            }
        }
    }
    
    public void run(){
        //calculoID();
        
        int i = 1;
        while(true){
            if(i % 10 == 0){
                // cada 10 iteracciones
                colonia.zonaComer(0, 3, idStr, false);
                colonia.descanso(1, idStr);
            }
            else{
                
                if(par){
                    colonia.almacen(1, 2, idStr, false);

                    try {
                        sleep(1000 + (int)(2000*Math.random()));  // viaja a la zona para comer (1-3s)
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    colonia.zonaComer(1, 2, idStr, true);
                }
                else{
                    colonia.salir(idStr);

                    try {
                        sleep(4000);  // tarda 4s en coger comida de fuera
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    colonia.entrar(idStr);
                    colonia.almacen(2, 4, idStr, true);
                }
                
            }
            i++;
        }
             
    }
}
