package ParteDistribuida;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EnvioValores extends UnicastRemoteObject implements InterfaceObtenerValores{
    
    private int hormigasObrerasExterior;
    private int hormigasObrerasInterior;
    private int hormigasSoldadoInstruccion;
    private int hormigasSoldadoInvasion;
    private int hormigasCriaZonaComer;
    private int hormigasCriaRefugio;
    
    private Lock cerrojoHOExt = new ReentrantLock();
    private Lock cerrojoHOInt = new ReentrantLock();
    private Lock cerrojoHSIns = new ReentrantLock();
    private Lock cerrojoHSInv = new ReentrantLock();
    private Lock cerrojoHCZC = new ReentrantLock();
    private Lock cerrojoHCR = new ReentrantLock();
    
    public EnvioValores(int hOExt, int hOInt, int hSIns, int hSInv, int hCZC, int hCR) throws RemoteException{
        this.hormigasObrerasExterior = hOExt;
        this.hormigasObrerasInterior = hOInt;
        this.hormigasSoldadoInstruccion = hSIns;
        this.hormigasSoldadoInvasion = hSInv;
        this.hormigasCriaZonaComer = hCZC;
        this.hormigasCriaRefugio = hCR;
    }
    
    public void actualizarHormigasObrerasExterior(boolean sumar){
        cerrojoHOExt.lock();
        try{
            if(sumar){
                hormigasObrerasExterior++;
            }
            else{
                hormigasObrerasExterior--;
            }
        }
        finally{
            cerrojoHOExt.unlock();
        }
    }
    
    public void actualizarHormigasObrerasInterior(boolean sumar){
        cerrojoHOInt.lock();
        try{
            if(sumar){
                hormigasObrerasInterior++;
            }
            else{
                hormigasObrerasInterior--;
            }
        }
        finally{
            cerrojoHOInt.unlock();
        }
    }
    
    public void actualizarHormigasSoldadoInstruccion(boolean sumar){
        cerrojoHSIns.lock();
        try{
            if(sumar){
                hormigasSoldadoInstruccion++;
            }
            else{
                hormigasSoldadoInstruccion--;
            }
        }
        finally{
            cerrojoHSIns.unlock();
        }
    }
    
    public void actualizarHormigasSoldadoInvasion(boolean sumar){
        cerrojoHSInv.lock();
        try{
            if(sumar){
                hormigasSoldadoInvasion++;
            }
            else{
                hormigasSoldadoInvasion--;
            }
        }
        finally{
            cerrojoHSInv.unlock();
        }
    }
    
    public void actualizarHormigasCriaZonaComer(boolean sumar){
        cerrojoHCZC.lock();
        try{
            if(sumar){
                hormigasCriaZonaComer++;
            }
            else{
                hormigasCriaZonaComer--;
            }
        }
        finally{
            cerrojoHCZC.unlock();
        }
    }
    
    public void actualizarHormigasCriaRefugio(boolean sumar){
        cerrojoHCR.lock();
        try{
            if(sumar){
                hormigasCriaRefugio++;
            }
            else{
                hormigasCriaRefugio--;
            }
        }
        finally{
            cerrojoHCR.unlock();
        }
    }
    
    // metodos para la interfaz cliente
    
    public int getHormigasObrerasExterior() throws RemoteException{
        return hormigasObrerasExterior;
    }
    
    public int getHormigasObrerasInterior() throws RemoteException{
        return hormigasObrerasInterior;
    }
    
    public int getHormigasSoldadoInstruccion() throws RemoteException{
        return hormigasSoldadoInstruccion;
    }
    
    public int getHormigasSoldadoInvasion() throws RemoteException{
        return hormigasSoldadoInvasion;
    }
    
    public int getHormigasCriaZonaParaComer() throws RemoteException{
        return hormigasCriaZonaComer;
    }
    
    public int getHormigasCriaRefugio() throws RemoteException{
        return hormigasCriaRefugio;
    }
    
}
