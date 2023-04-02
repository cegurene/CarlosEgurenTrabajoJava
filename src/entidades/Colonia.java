package entidades;

import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Colonia {
    
    private ListaHormigas hormigasDentro;
    private ListaHormigas hormigasFuera;
    private ListaHormigas hormigasAlmacen;
    private ListaHormigas hormigasInstruccion;
    private ListaHormigas hormigasDescanso;
    private ListaHormigas hormigasRefugio;
    private ListaHormigas hormigasComer;
    
    private Semaphore entrarColonia = new Semaphore(1, true);
    private Semaphore salirColonia1 = new Semaphore(1, true);
    private Semaphore salirColonia2 = new Semaphore(1, true);
    private Semaphore almacenComida = new Semaphore(10, true);
    
    private Lock cerrojoComidaAlmacen = new ReentrantLock();
    private Lock cerrojoComidaZonaComer = new ReentrantLock();
    
    private int numeroComidaAlmacen;
    private int numeroComidaZonaComer;
    
    public Colonia(){
        numeroComidaAlmacen = 0;
        numeroComidaZonaComer = 0;
    }
    
    public int getComidaZonaComer(){
        try{
            cerrojoComidaZonaComer.lock();
        }
        finally{
            cerrojoComidaZonaComer.unlock();
            return numeroComidaZonaComer;
        }
    }
    
    public void setComidaZonaComer(int numero){
        try{
            cerrojoComidaZonaComer.lock();
            numeroComidaZonaComer = numero;
        }
        finally{
            cerrojoComidaZonaComer.unlock();
        }
    }
    
    public void entrar(String idStr){
        try{
            entrarColonia.acquire();
            hormigasFuera.sacar(idStr);
            hormigasDentro.meter(idStr);
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            entrarColonia.release();
        }
        
    }
    
    public void salir(){
        // TODO
    }
    
    public void almacen(int minimo, int maximo, String id, boolean annadirComida){
        try {
            almacenComida.acquire();
            hormigasAlmacen.meter(id);
            
            try{
                cerrojoComidaAlmacen.lock();
                
                if(annadirComida){
                    numeroComidaAlmacen++;  // annadimos comida
                }
                else{
                    numeroComidaAlmacen--;  // quitamos comida
                }
            }
            finally{
                cerrojoComidaAlmacen.unlock();
            }
            
            int rango = maximo - minimo;
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));  //esperamos un tiempo aleatorio
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            hormigasAlmacen.sacar(id);
            almacenComida.release();
        }
    }
    
    public void instruccion(int minimo, int maximo, String id){
        hormigasInstruccion.meter(id);
        
        int rango = maximo - minimo;
        try {
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        hormigasInstruccion.sacar(id);
    }
    
    public void descanso(int tiempo, String id){
        hormigasDescanso.meter(id);
        
        try {
            sleep((int)(tiempo * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        hormigasDescanso.sacar(id);
    }
    
    public void refugio(String id){
        hormigasRefugio.meter(id);
        
        // TODO: esperar hasta que no haya amenaza
        
        hormigasRefugio.sacar(id);
    }
    
    public void zonaComer(int minimo, int maximo, String id){
        hormigasComer.meter(id);
        
        try{
            cerrojoComidaZonaComer.lock();

            numeroComidaZonaComer--;
        }
        finally{
            cerrojoComidaZonaComer.unlock();
        }
        
        int rango = maximo - minimo;
        try {
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        hormigasComer.sacar(id);
    }
}
