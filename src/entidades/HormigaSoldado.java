package entidades;
 
import java.util.concurrent.CyclicBarrier;
import ParteDistribuida.EnvioValores;

public class HormigaSoldado extends Thread{
    private int idNumero;
    private String idStr;
    private Colonia colonia;
    private Paso paso;
    private CyclicBarrier barreraAmenaza1;
    private EnvioValores envio;
    
    public HormigaSoldado(int id, Colonia colonia, Paso paso, EnvioValores envio){
        this.idNumero = id;
        this.colonia = colonia;
        this.paso = paso;
        this.envio = envio;
        calculoID();
    }
    
    public void calculoID(){
        if(idNumero < 10){
            idStr = "HS000" + String.valueOf(idNumero);
        }
        else{
            if(idNumero >= 10 && idNumero < 100){
                idStr = "HS00" + String.valueOf(idNumero);
            }
            else{
                if(idNumero >= 100 && idNumero < 1000){
                    idStr = "HS0" + String.valueOf(idNumero);
                }
                else{
                    idStr = "HS" + String.valueOf(idNumero);
                }
            }
        }
    }
    
    public void run(){        
        int i = 0;
        colonia.entrar(idStr);
        
        while(true){  // hace 6 veces 1 rutina y despues la otra
            try{
                
                if(i % 6 == 0){
                    // cada 6 iteracciones
                    paso.mirar();
                    colonia.zonaComer(0, 3, idStr, false);  // entra en la zona para comer a comer
                }
                else{
                    paso.mirar();
                    colonia.instruccion(2, 8, idStr);  // entra a realizar la instruccion

                    paso.mirar();
                    colonia.descanso(2, idStr);  // entra a descansar
                }
                i++;
            }
            catch(Exception e){
                colonia.lucharAmenaza(idStr);
            }           
        }      
    }
}
