package entidades;
 
public class HormigaObrera extends Thread{
    private int idNumero = 0;
    private String idStr;
    private Colonia colonia;
    boolean par;
    
    public HormigaObrera(int id, Colonia colonia){
        this.idNumero = id;
        this.colonia = colonia;
        this.par =  (id % 2 == 0);
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
    
    public void run(){
        calculoID();
        
        if(par){
            colonia.almacen(1, 2, idStr, false);
            
            sleep(1000 + (int)(2000*Math.random()));  // viaja a la zona para comer (1-3s)
        }
        else{
            
        }
                
        System.out.println(idStr);
    }
}
