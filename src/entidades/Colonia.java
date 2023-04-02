package entidades;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Colonia {
    
    Semaphore entrarColonia = new Semaphore(1, true);
    Semaphore salirColonia1 = new Semaphore(1, true);
    Semaphore salirColonia2 = new Semaphore(1, true);
    ListaHormigas dentroColonia;
    
    public Colonia(){
        
    }
    
    public void entrar(String idStr){
        try{
            entrarColonia.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            entrarColonia.release();
        }
        
    }
    
    public void salir(){
        
    }
}
