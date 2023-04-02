package entidades;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMain {

    public static void main(String[] args) {       
        HormigaSoldado hs1 = new HormigaSoldado(1);
        
        hs1.start();
        
    }
    
}
