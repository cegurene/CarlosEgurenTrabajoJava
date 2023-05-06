package entidades;
 
import ParteDistribuida.EnvioValores;

public class HormigaCria extends Thread{
    private int idNumero;
    private String idStr;
    private Colonia colonia;
    private Paso paso;
    private EnvioValores envio;
    
    public HormigaCria(int id, Colonia colonia, Paso paso, EnvioValores envio){
        this.idNumero = id;
        this.colonia = colonia;
        this.paso = paso;
        this.envio = envio;
        calculoID();
    }
    
    public void calculoID(){
        if(idNumero < 10){
            idStr = "HC000" + String.valueOf(idNumero);
        }
        else{
            if(idNumero >= 10 && idNumero < 100){
                idStr = "HC00" + String.valueOf(idNumero);
            }
            else{
                if(idNumero >= 100 && idNumero < 1000){
                    idStr = "HC0" + String.valueOf(idNumero);
                }
                else{
                    idStr = "HC" + String.valueOf(idNumero);
                }
            }
        }
    }
    
    public void run(){
        colonia.entrar(idStr);
        
        while(true){
            try{
                
                paso.mirar();
                colonia.zonaComer(3, 5, idStr, false);  // entra en la zona para comer a comer

                paso.mirar();
                colonia.descanso(4, idStr);  // entra en la zona de descanso
                
            }
            catch(Exception e){
                colonia.refugio(idStr);
            }
        }
    }
}
