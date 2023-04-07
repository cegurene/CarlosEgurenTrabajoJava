package entidades;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileManager {
    private static String path = System.getProperty("user.dir") + "\\historial\\";
    private static Lock cerrojo = new ReentrantLock();

    /**
     * Metodo para guardar el historial del programa
     * 
     * @param texto
     * @param idStr
     */
    public static void guardarDatos(String texto, String idStr){
        String nombreFichero="evolucionColonia" + ".txt";
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        try{
            fichero = new FileWriter(path + nombreFichero, true);
            pw = new PrintWriter(fichero);

            pw.println("La hormiga" + tipoHormiga(idStr) + idStr + texto);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if(null != fichero){
                    fichero.close();
                }
            }catch(Exception e2) {
                e2.printStackTrace();
           }
        }
    }
    
    /**
    * 
    * @param fecha fecha a formatear
    * @return fecha formateada
    */
    public static String formatearFechaHora(Date fecha){
        String pattern = "dd-mm-yyyy hh.mm.ss: ";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(fecha);
   }
    
    public static String tipoHormiga(String idStr){
        String tipo;
        if(idStr.charAt(1) == 'C'){
            tipo = " cria ";
        }
        else{
            if(idStr.charAt(1) == 'S'){
                tipo = " soldado ";
            }
            else{
                tipo = " obrera ";
            }
        }
        return tipo;
    }
}
