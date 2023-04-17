package entidades;

import static java.lang.Thread.sleep;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class Colonia {
    
    private ListaHormigas hormigasAlmacen;
    private ListaHormigas hormigasInstruccion;
    private ListaHormigas hormigasDescanso;
    private ListaHormigas hormigasRefugio;
    private ListaHormigas hormigasComer;
    private ListaHormigas hormigasInsecto;
    private ListaHormigas hormigasBuscando;
    
    private Semaphore entrarColonia = new Semaphore(1, true);
    private Semaphore salirColonia1 = new Semaphore(1, true);
    private Semaphore salirColonia2 = new Semaphore(1, true);
    private Semaphore almacenComida = new Semaphore(10, true);
    
    private Lock cerrojoComidaAlmacen = new ReentrantLock();
    private Lock cerrojoComidaZonaComer = new ReentrantLock();
    
    private int numeroComidaAlmacen = 0;
    private int numeroComidaZonaComer = 0;
    private JTextField nComidaAlmacen;
    private JTextField nComidaZonaComer;
    
    private Paso paso;
    
    private boolean amenaza;
    private CyclicBarrier barreraAmenaza;
    
    public Colonia(ListaHormigas refugio, ListaHormigas zonaComer, ListaHormigas zonaDescanso, ListaHormigas instruccion, ListaHormigas almacen, ListaHormigas insecto, ListaHormigas buscando, Paso paso, JTextField nComidaAlmacen, JTextField nComidaZonaComer){
        numeroComidaAlmacen = 0;
        numeroComidaZonaComer = 0;
        amenaza = false;
        
        this.hormigasRefugio = refugio;
        this.hormigasComer = zonaComer;
        this.hormigasDescanso = zonaDescanso;
        this.hormigasInsecto = insecto;
        this.hormigasInstruccion = instruccion;
        this.hormigasAlmacen = almacen;
        this.hormigasBuscando = buscando;
        this.paso = paso;
        this.nComidaAlmacen = nComidaAlmacen;
        this.nComidaZonaComer = nComidaZonaComer;
    }
    
    public boolean getAmenaza(){
        return amenaza;
    }
    
    public void setAmenaza(boolean amenaza){
        this.amenaza = amenaza;
    }
    
    public void setBarreraAmenaza(CyclicBarrier barreraAmenaza){
        this.barreraAmenaza = barreraAmenaza;
    }
    
    public void actualizarComidaAlmacen(){
        paso.mirar();
        nComidaAlmacen.setText(String.valueOf(numeroComidaAlmacen));
    }
    
    public void actualizarComidaZonaComida(){
        paso.mirar();
        nComidaZonaComer.setText(String.valueOf(numeroComidaZonaComer));     
    }
    
    public void entrar(String idStr){
        try{
            entrarColonia.acquire();  // pongo semaforo
            String texto = " entra a la colonia.";
            paso.mirar();
            FileManager.guardarDatos(texto, idStr);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            entrarColonia.release();  // quito semaforo
        }
        
    }
    
    public void salir(String idStr){
        while(true){
            if(salirColonia1.tryAcquire()){
                String texto = " sale de la colonia por la salida 1.";
                paso.mirar();
                FileManager.guardarDatos(texto, idStr);
                salirColonia1.release();
                break;
            }
            if(salirColonia2.tryAcquire()){
                String texto = " sale de la colonia por la salida 2.";
                paso.mirar();
                FileManager.guardarDatos(texto, idStr);
                salirColonia2.release();
                break;
            }
        }
        
    }
    
    public void buscarComida(String id){
        String texto = " empieza a buscar comida.";
        paso.mirar();
        hormigasBuscando.meter(id);
        FileManager.guardarDatos(texto, id);
        
        try {
            sleep(4000);  // esperamos 4s
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        paso.mirar();
        hormigasBuscando.sacar(id);
    }
    
    public void almacen(int minimo, int maximo, String id, boolean annadirComida){
        try {
            almacenComida.acquire();
            String txt;
            if(annadirComida){
                txt = "meter";
            }
            else{
                txt = "sacar";
            }
            String texto = " entra en el almacen para " + txt + " comida.";
            paso.mirar();
            hormigasAlmacen.meter(id);
            FileManager.guardarDatos(texto, id);
            paso.mirar();
            
            cerrojoComidaAlmacen.lock();
            if(!annadirComida && numeroComidaAlmacen == 0){  // si va a sacar comida y no hay
                cerrojoComidaAlmacen.unlock();
                paso.mirar();
                hormigasAlmacen.sacar(id);
                String msg = " no puede sacar comida del almacen porque esta vacio.";
                paso.mirar();
                FileManager.guardarDatos(msg, id);
                paso.mirar();
                almacenComida.release();
                return;  // salimos del metodo
            }
            
            try{                
                if(annadirComida){
                    paso.mirar();
                    numeroComidaAlmacen++;  // annadimos comida
                }
                else{
                    paso.mirar();
                    numeroComidaAlmacen--;  // quitamos comida
                }
                actualizarComidaAlmacen();
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
            paso.mirar();
            hormigasAlmacen.sacar(id);
            paso.mirar();
            almacenComida.release();
        }
    }
    
    public void instruccion(int minimo, int maximo, String id){
        String texto = " empieza la instruccion.";
        paso.mirar();
        hormigasInstruccion.meter(id);
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        
        int rango = maximo - minimo;
        try {
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        paso.mirar();
        hormigasInstruccion.sacar(id);
        paso.mirar();
    }
    
    public void descanso(int tiempo, String id){
        String texto = " comienza a descansar.";
        paso.mirar();
        hormigasDescanso.meter(id);
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        
        try {
            sleep((int)(tiempo * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        paso.mirar();
        hormigasDescanso.sacar(id);
        paso.mirar();
    }
    
    public void refugio(String id){
        String texto = " se mete en el refugio.";
        hormigasRefugio.meter(id);
        FileManager.guardarDatos(texto, id);
        
        // TODO: esperar hasta que no haya amenaza
        
        hormigasRefugio.sacar(id);
    }
    
    public void zonaComer(int minimo, int maximo, String id, boolean annadirComida){
        String texto = " entra a la zona de comer para ";
        String txt;
        if(annadirComida){
            txt = "meter comida.";
        }
        else{
            txt = "comer.";
        }
        
        paso.mirar();
        hormigasComer.meter(id);
        texto += txt;
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        
        cerrojoComidaZonaComer.lock();
        if(!annadirComida && numeroComidaZonaComer == 0){  // si va a consumir comida y no hay
            cerrojoComidaZonaComer.unlock();
            paso.mirar();
            hormigasComer.sacar(id);
            String msg = " no puede comer porque no hay comida disponible.";
            paso.mirar();
            FileManager.guardarDatos(msg, id);
            return;  // salimos del metodo
        }
       
        try{            
            if(annadirComida){
                paso.mirar();
                numeroComidaZonaComer++;
            }
            else{
                paso.mirar();
                numeroComidaZonaComer--;
            }
            actualizarComidaZonaComida();
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
        
        paso.mirar();
        hormigasComer.sacar(id);
        paso.mirar();
    }

    public void lucharAmenaza(String id){
        try {
            barreraAmenaza.await();
            sleep(20000);  // simulamos que luchamos contra el insecto

            barreraAmenaza.await();
            
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}