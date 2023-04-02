package entidades;
 
public class HormigaObrera extends Thread{
    private int idNumero = 0;
    private String idStr;
    
    public HormigaObrera(int id){
        this.idNumero = id;
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
                
        System.out.println(idStr);
    }
}
