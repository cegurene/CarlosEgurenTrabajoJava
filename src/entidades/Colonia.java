package entidades;

import static java.lang.Thread.sleep;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
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
    
    private Lock cerrojoComidaAlmacen = new ReentrantLock();
    private Lock cerrojoComidaZonaComer = new ReentrantLock();
    
    private int numeroComidaAlmacen = 0;
    private int numeroComidaZonaComer = 0;
    private int numeroHormigasAlmacen = 0;
    private Lock cerrojoAlmacen = new ReentrantLock();
    private Condition esperarAlmacen = cerrojoAlmacen.newCondition();
    
    private JTextField nComidaAlmacen;
    private JTextField nComidaZonaComer;
    private JLabel textoAmenaza;
    
    private Lock cerrojoZonaComer = new ReentrantLock();
    private Condition esperarZonaComer = cerrojoZonaComer.newCondition();
    
    private Paso paso;
    
    private boolean amenaza;
    private CyclicBarrier barreraAmenaza;
    
    private Lock cerrojoAmenaza = new ReentrantLock();
    private Condition esperarAmenaza = cerrojoAmenaza.newCondition();
    
    public Colonia(ListaHormigas refugio, ListaHormigas zonaComer, ListaHormigas zonaDescanso, ListaHormigas instruccion, ListaHormigas almacen, ListaHormigas insecto, ListaHormigas buscando, Paso paso, JTextField nComidaAlmacen, JTextField nComidaZonaComer, JLabel textoAmenaza){
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
        this.textoAmenaza = textoAmenaza;
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
            try{
                cerrojoAlmacen.lock();
                while(numeroHormigasAlmacen == 10 || (!annadirComida && numeroComidaAlmacen == 0)){
                    esperarAlmacen.await();
                }
            }
            finally{
                cerrojoAlmacen.unlock();
            }
            
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
            try{                
                if(annadirComida){
                    paso.mirar();
                    numeroComidaAlmacen++;  // annadimos comida
                    
                    cerrojoAlmacen.lock();
                    try{
                        esperarAlmacen.signal();
                    }
                    finally{
                        cerrojoAlmacen.unlock();
                    }
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
        }
    }
    
    public void instruccion(int minimo, int maximo, String id){
        String texto = " empieza la instruccion.";
        paso.mirar();
        if(amenaza){
            comprobarAmenaza(id);
        }
        hormigasInstruccion.meter(id);
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        if(amenaza){
            hormigasInstruccion.sacar(id);
            comprobarAmenaza(id);
            hormigasInstruccion.meter(id);
        }
        
        int rango = maximo - minimo;
        try {
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        paso.mirar();
        if(amenaza){
            hormigasInstruccion.sacar(id);
            comprobarAmenaza(id);
            hormigasInstruccion.meter(id);
        }
        hormigasInstruccion.sacar(id);
        paso.mirar();
        if(amenaza){
            comprobarAmenaza(id);
        }
    }
    
    public void descanso(int tiempo, String id){
        String texto = " comienza a descansar.";
        paso.mirar();
        if(amenaza){
            comprobarAmenaza(id);
        }
        hormigasDescanso.meter(id);
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        if(amenaza){
            hormigasDescanso.sacar(id);
            comprobarAmenaza(id);
            hormigasDescanso.meter(id);
        }
        
        try {
            sleep((int)(tiempo * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        paso.mirar();
        if(amenaza){
            hormigasDescanso.sacar(id);
            comprobarAmenaza(id);
            hormigasDescanso.meter(id);
        }
        hormigasDescanso.sacar(id);
        paso.mirar();
        if(amenaza){
            comprobarAmenaza(id);
        }
    }
    
    public void refugio(String id){
        String texto = " se mete en el refugio.";
        hormigasRefugio.meter(id);
        FileManager.guardarDatos(texto, id);
    }
    
    public void zonaComer(int minimo, int maximo, String id, boolean annadirComida){
        cerrojoZonaComer.lock();
        try{
            if(!annadirComida && numeroComidaZonaComer == 0){
                esperarZonaComer.await();
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            cerrojoZonaComer.unlock();
        }
        
        String texto = " entra a la zona de comer para ";
        String txt;
        if(annadirComida){
            txt = "meter comida.";
        }
        else{
            txt = "comer.";
        }
        if(amenaza){
            comprobarAmenaza(id);
        }
        paso.mirar();
        hormigasComer.meter(id);
        if(amenaza){
            hormigasComer.sacar(id);
            comprobarAmenaza(id);
            hormigasComer.meter(id);
        }
        texto += txt;
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        if(amenaza){
            hormigasComer.sacar(id);
            comprobarAmenaza(id);
            hormigasComer.meter(id);
        }
        
        cerrojoComidaZonaComer.lock();       
        try{            
            if(annadirComida){
                paso.mirar();
                numeroComidaZonaComer++;
                
                try{
                    cerrojoZonaComer.lock();
                    esperarZonaComer.signal();
                }
                finally{
                    cerrojoZonaComer.unlock();
                }
                
            }
            else{
                paso.mirar();
                numeroComidaZonaComer--;
            }
            actualizarComidaZonaComida();
        }
        finally{
            cerrojoComidaZonaComer.unlock();
            if(amenaza){
                hormigasComer.sacar(id);
                comprobarAmenaza(id);
                hormigasComer.meter(id);
            }
        }
        
        int rango = maximo - minimo;
        try {
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(amenaza){
            hormigasComer.sacar(id);
            comprobarAmenaza(id);
            hormigasComer.meter(id);
        }
        paso.mirar();
        hormigasComer.sacar(id);
        if(amenaza){
            comprobarAmenaza(id);
        }
        paso.mirar();
    }
    
    public void lucharAmenaza(String id){
        try {
            hormigasInsecto.meter(id);
            barreraAmenaza.await();
            String texto = " esta luchando contra el insecto.";
            FileManager.guardarDatos(texto, id);
            sleep(20000);  // simulamos que luchamos contra el insecto
            hormigasInsecto.sacar(id);
            esperarAmenaza.signalAll();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void comprobarAmenaza(String id){
        if(amenaza){  // si hay amenaza
            
            if(id.charAt(1) == 'C'){  // si la hormiga es una cria
                paso.mirar();
                refugio(id);
                
                try{
                    cerrojoAmenaza.lock();
                    while(amenaza){
                        esperarAmenaza.await();
                    }
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally{
                    cerrojoAmenaza.unlock();
                    hormigasRefugio.sacar(id);
                }
                
            }
            else{
                if(id.charAt(1) == 'S'){  // si la hormiga es soldado
                    salir(id);
                    paso.mirar();
                    lucharAmenaza(id);
                    paso.mirar();
                    textoAmenaza.setText("No hay amenaza");
                    amenaza = false;
                }
            }
            
        }
    }
    
}