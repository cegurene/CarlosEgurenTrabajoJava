package entidades;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author charl
 */
public class Paso {
    private boolean cerrado=false;
    private Lock cerrojo = new ReentrantLock();
    private Condition parar = cerrojo.newCondition();

    public void mirar(){
        try{
            cerrojo.lock();
            while(cerrado){
                try{
                    parar.await();
                } catch(InterruptedException ie){ }
            }
        }
        finally{
            cerrojo.unlock();
        }
    }
    
    public void abrir(){
        try{
            cerrojo.lock();
            cerrado=false;
            parar.signalAll();
        }
        finally{
            cerrojo.unlock();
        }
    }
    
    public void cerrar(){
        try{
            cerrojo.lock();
            cerrado=true;
        }
        finally{
            cerrojo.unlock();
        }
    }
}
