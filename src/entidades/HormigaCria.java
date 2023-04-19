package entidades;
 
import static java.lang.Thread.sleep;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HormigaCria extends Thread{
    private int idNumero;
    private String idStr;
    private Colonia colonia;
    private Paso paso;
    
    private Lock cerrojo = new ReentrantLock();
    private Condition parar = cerrojo.newCondition();
    
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
        
        while(true){
            colonia.comprobarAmenaza(idStr);
            colonia.zonaComer(3, 5, idStr, true);
            colonia.comprobarAmenaza(idStr);
            paso.mirar();
            colonia.descanso(4, idStr);
            colonia.comprobarAmenaza(idStr);
            paso.mirar();
        }
    }
}
