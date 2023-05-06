package ParteDistribuida;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceObtenerValores extends Remote{
    int getHormigasObrerasExterior() throws RemoteException;
    int getHormigasObrerasInterior() throws RemoteException;
    int getHormigasSoldadoInstruccion() throws RemoteException;
    int getHormigasSoldadoInvasion() throws RemoteException;
    int getHormigasCriaZonaParaComer() throws RemoteException;
    int getHormigasCriaRefugio() throws RemoteException;
}
