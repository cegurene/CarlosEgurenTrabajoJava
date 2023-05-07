package entidades;

import ParteDistribuida.EnvioValores;
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
    private Lock cerrojoNumeroHormigasAlmacen = new ReentrantLock();
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
    
    private EnvioValores envio;
    
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
    
    public void setAmenaza(boolean amenaza){
        this.amenaza = amenaza;
    }
    
    public boolean getAmenaza(){
        return amenaza;
    }
    
    public void setBarreraAmenaza(CyclicBarrier barrera){
        barreraAmenaza = barrera;
    }
    
    public void setEnvio(EnvioValores envio){
        this.envio = envio;
    }
    
    public void actualizarNumeroHormigasAlmacen(boolean annadir){
        cerrojoNumeroHormigasAlmacen.lock();
        try{
            if(annadir){
                numeroHormigasAlmacen++;
            }
            else{
                numeroHormigasAlmacen--;
            }
        }
        finally{
            cerrojoNumeroHormigasAlmacen.unlock();
        }
    }
    
    public void actualizarHormigasAlmacen(String id, boolean meter, boolean annadirComida){
        if(meter){
            
            try{
                cerrojoAlmacen.lock();
                while(numeroHormigasAlmacen == 10 || (!annadirComida && numeroComidaAlmacen < 0)){
                    esperarAlmacen.await();
                }
            }
            catch (InterruptedException ex) {
                Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
            }            
            finally{
                cerrojoAlmacen.unlock();
                paso.mirar();
                hormigasAlmacen.meter(id);
            }
            
        }
        else{
            paso.mirar();
            hormigasAlmacen.sacar(id);
        }
        actualizarNumeroHormigasAlmacen(meter);
    }
    
    public void actualizarHormigasInstruccion(String id, boolean meter){
        if(meter){
            paso.mirar();
            hormigasInstruccion.meter(id);
        }
        else{
            paso.mirar();
            hormigasInstruccion.sacar(id);
        }
    }
    
    public void actualizarHormigasDescanso(String id, boolean meter){
        if(meter){
            paso.mirar();
            hormigasDescanso.meter(id);
        }
        else{
            paso.mirar();
            hormigasDescanso.sacar(id);
        }
    }
    
    public void actualizarHormigasRefugio(String id, boolean meter){
        if(meter){
            paso.mirar();
            hormigasRefugio.meter(id);
        }
        else{
            paso.mirar();
            hormigasRefugio.sacar(id);
        }
    }
    
    public void actualizarHormigasComer(String id, boolean meter, boolean annadirComida){
        if(meter){
            
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
                paso.mirar();
                hormigasComer.meter(id);
            }
            
        }
        else{
            paso.mirar();
            hormigasComer.sacar(id);
        }
    }
    
    public void actualizarHormigasInsecto(String id, boolean meter){
        if(meter){
            paso.mirar();
            hormigasInsecto.meter(id);
        }
        else{
            paso.mirar();
            hormigasInsecto.sacar(id);
        }
    }
    
    public void actualizarHormigasBuscando(String id, boolean meter){
        if(meter){
            paso.mirar();
            hormigasBuscando.meter(id);
        }
        else{
            paso.mirar();
            hormigasBuscando.sacar(id);
        }
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
        boolean excepcion = false;
        try{
            entrarColonia.acquire();  // pongo semaforo
            String texto = " entra a la colonia.";
            sleep(100);
            paso.mirar();
            FileManager.guardarDatos(texto, idStr);
        } catch (InterruptedException ex) {
            excepcion = true;
            if(comprobarTipoHormmiga(idStr).equals("cria")){
                entrarColonia.release();
                refugio(idStr);
            }
        }
        finally{
            if(!excepcion){
                entrarColonia.release();  // quito semaforo
            }
        }
        
    }
    
    public void salir(String idStr){
        while(true){
            if(salirColonia1.tryAcquire()){
                String texto = " sale de la colonia por la salida 1.";
                try {
                    sleep(100);
                } catch (InterruptedException ex) {}
                paso.mirar();
                FileManager.guardarDatos(texto, idStr);
                salirColonia1.release();
                break;
            }
            if(salirColonia2.tryAcquire()){
                String texto = " sale de la colonia por la salida 2.";
                try {
                    sleep(100);
                } catch (InterruptedException ex) {}
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
        FileManager.guardarDatos(texto, id);
        
        try {
            sleep(4000);  // esperamos 4s
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void almacen(int minimo, int maximo, String id, boolean annadirComida){
        actualizarHormigasAlmacen(id, true, annadirComida);
        
        int rango = maximo - minimo;
        try {
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));  //esperamos un tiempo aleatorio
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
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
        FileManager.guardarDatos(texto, id);
        paso.mirar();
        
        cerrojoComidaAlmacen.lock();
        try{
            if(annadirComida){
                paso.mirar();
                numeroComidaAlmacen = numeroComidaAlmacen + 5;  // annadimos comida
                /*
                cerrojoAlmacen.lock();
                try{
                    paso.mirar();
                    esperarAlmacen.signal();
                }
                finally{
                    cerrojoAlmacen.unlock();
                }*/
            }
            else{
                paso.mirar();
                numeroComidaAlmacen = numeroComidaAlmacen - 5;  // quitamos comida
            }
        }
        finally{
            actualizarComidaAlmacen();
            
            cerrojoComidaAlmacen.unlock();
        }
        paso.mirar();
        actualizarHormigasAlmacen(id, false, annadirComida);
    }
    
    public void instruccion(int minimo, int maximo, String id){
        boolean excepcion = false;
        try{
            actualizarHormigasInstruccion(id, true);
            envio.actualizarHormigasSoldadoInstruccion(true);
            
            String texto = " empieza la instruccion.";
            FileManager.guardarDatos(texto, id);
            paso.mirar();

            int rango = maximo - minimo;

            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
            
        }catch(InterruptedException e){
            excepcion = true;
            paso.mirar();
            envio.actualizarHormigasSoldadoInstruccion(false);
            actualizarHormigasInstruccion(id, false);
            lucharAmenaza(id);
        }
        finally{
            if(!excepcion){
                paso.mirar();
                envio.actualizarHormigasSoldadoInstruccion(false);
                actualizarHormigasInstruccion(id, false);
            }
        }
    }
    
    public void descanso(int tiempo, String id){
        try{
            actualizarHormigasDescanso(id, true);
            
            String texto = " comienza a descansar.";
            paso.mirar();
            FileManager.guardarDatos(texto, id);
            paso.mirar();

            sleep((int)(tiempo * 1000));
            
        }catch(InterruptedException ie){
            paso.mirar();
            actualizarHormigasDescanso(id, false);
            paso.mirar();
            if(comprobarTipoHormmiga(id).equals("soldado")){
                lucharAmenaza(id);
            }
            else{
                if(comprobarTipoHormmiga(id).equals("cria")){
                    refugio(id);
                }
            }
        }
        finally{
            paso.mirar();
            actualizarHormigasDescanso(id, false);
        }

    }
    
    public void refugio(String id){
        String texto = " se mete en el refugio.";
        paso.mirar();
        hormigasRefugio.meter(id);
        envio.actualizarHormigasCriaRefugio(true);
        FileManager.guardarDatos(texto, id);
        
        try{
            cerrojoAmenaza.lock();
            while(amenaza){
                try{
                    esperarAmenaza.await();
                } catch(InterruptedException ie){ }
            }
        }
        finally{
            cerrojoAmenaza.unlock();
            paso.mirar();
            envio.actualizarHormigasCriaRefugio(false);
            hormigasRefugio.sacar(id);
        }
    }
    
    public void zonaComer(int minimo, int maximo, String id, boolean annadirComida){
        try{
            actualizarHormigasComer(id, true, annadirComida);
            
            if(comprobarTipoHormmiga(id).equals("cria")){
                envio.actualizarHormigasCriaZonaComer(true);
            }
            
            cerrojoComidaZonaComer.lock();       
            try{
                if(annadirComida){
                    paso.mirar();
                    numeroComidaZonaComer = numeroComidaZonaComer + 5;                
                }
                else{
                    paso.mirar();
                    numeroComidaZonaComer--;
                }
                actualizarComidaZonaComida();
            }
            finally{
                try{
                    cerrojoZonaComer.lock();
                    esperarZonaComer.signal();
                }
                finally{
                    cerrojoZonaComer.unlock();
                }
                cerrojoComidaZonaComer.unlock();
            }
            
            String texto = " entra a la zona de comer para ";
            String txt;
            if(annadirComida){
                txt = "meter comida.";
            }
            else{
                txt = "comer.";
            }
            texto += txt;
            FileManager.guardarDatos(texto, id);
            paso.mirar();

            int rango = maximo - minimo;
            sleep(minimo * 1000 + (int)(rango * 1000 * Math.random()));
            
        }catch(InterruptedException ie){
            paso.mirar();
            if(comprobarTipoHormmiga(id).equals("cria")){
                envio.actualizarHormigasCriaZonaComer(false);
            }
            
            actualizarHormigasComer(id, false, false);
            paso.mirar();
            if(comprobarTipoHormmiga(id).equals("soldado")){
                lucharAmenaza(id);
            }
            else{
                if(comprobarTipoHormmiga(id).equals("cria")){
                    refugio(id);
                }
            }
            
        }finally{
            paso.mirar();
            if(comprobarTipoHormmiga(id).equals("cria")){
                envio.actualizarHormigasCriaZonaComer(false);
            }
            
            actualizarHormigasComer(id, false, false);
        }
    }
    
    public void lucharAmenaza(String id){
        try {
            paso.mirar();
            salir(id);
            paso.mirar();
            actualizarHormigasInsecto(id, true);
            envio.actualizarHormigasSoldadoInvasion(true);
            
            barreraAmenaza.await();
            String texto = " esta luchando contra el insecto.";
            FileManager.guardarDatos(texto, id);
            paso.mirar();
            sleep(20000);  // simulamos que luchamos contra el insectos
            
            amenaza = false;
            actualizarHormigasInsecto(id, false);
            envio.actualizarHormigasSoldadoInvasion(false);
            
            try{
                cerrojoAmenaza.lock();
                esperarAmenaza.signalAll();
            }
            finally{
                cerrojoAmenaza.unlock();
                entrar(id);
            }
            
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String comprobarTipoHormmiga(String id){
        if(id.charAt(1) == 'C'){
            return "cria";
        }
        else{
            if(id.charAt(1) == 'S'){
                return "soldado";
            }
        }
        return "obrera";
    }

}