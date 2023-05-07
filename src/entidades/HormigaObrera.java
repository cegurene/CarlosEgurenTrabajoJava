package entidades;
 
import ParteDistribuida.EnvioValores;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HormigaObrera extends Thread{
    private int idNumero;
    private String idStr;
    private Colonia colonia;
    private boolean par;
    private Paso paso;
    private EnvioValores envio;
    
    public HormigaObrera(int id, Colonia colonia, Paso paso, EnvioValores envio){
        this.idNumero = id;
        this.colonia = colonia;
        this.par =  (id % 2 == 0);
        this.paso = paso;
        this.envio = envio;
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
    
    public void comportamientoPar(){
        paso.mirar();
        colonia.almacen(1, 2, idStr, false);  //entra en el almacen a sacar comida

        try {
            sleep(1000 + (int)(2000*Math.random()));  // viaja a la zona para comer (1-3s)
        } catch (InterruptedException ex) {
            Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
        }

        paso.mirar();
        colonia.zonaComer(1, 2, idStr, true);  // entra en la zona para comer para dejar comida
    }
    
    public void comportamientoImpar(){
        paso.mirar();
        colonia.salir(idStr);  // sale de la colonia
        envio.actualizarHormigasObrerasInterior(false);
        envio.actualizarHormigasObrerasExterior(true);

        paso.mirar();
        colonia.actualizarHormigasBuscando(idStr, true);  // busca comida
        paso.mirar();
        colonia.buscarComida(idStr);
        paso.mirar();
        colonia.actualizarHormigasBuscando(idStr, false);  // termina de buscar comida

        paso.mirar();
        colonia.entrar(idStr);  // entra a la colonia
        envio.actualizarHormigasObrerasExterior(false);
        envio.actualizarHormigasObrerasInterior(true);

        paso.mirar();
        colonia.almacen(2, 4, idStr, true);  // entra en el almacen a dejar comida
    }
    
    public void run(){
        int i = 1;
        envio.actualizarHormigasObrerasExterior(true);
        colonia.entrar(idStr);
        envio.actualizarHormigasObrerasExterior(false);
        envio.actualizarHormigasObrerasInterior(true);
        while(true){
            if(i % 10 == 0){
                // cada 10 iteracciones
                paso.mirar();
                colonia.zonaComer(0, 3, idStr, false);  // entra en la zona para comer a comer
                paso.mirar();
                
                paso.mirar();
                colonia.descanso(1, idStr);  // entra en la zona de descanso
            }
            else{
                if(par){
                    comportamientoPar();
                }
                else{
                    comportamientoImpar();
                }
            }
            i++;
        }
             
    }
}
