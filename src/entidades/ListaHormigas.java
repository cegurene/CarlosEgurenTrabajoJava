package entidades;

import java.util.*;
import javax.swing.JTextField;

/* La clase ListaThreads permite gestionar las listas de threads en los monitores,
con métodos para meter y sacar threads en ella. Cada vez que una lista se modifica,
se imprime su nuevo contenido en el JTextField que toma como parámetro el constructor. */

public class ListaHormigas{
    ArrayList<String> lista;
    JTextField tf;
    
    public ListaHormigas(JTextField tf){
        lista=new ArrayList<String>();
        this.tf=tf;
    }

    public synchronized void meter(String id){
        lista.add(id);
        imprimir();
    }

    public synchronized void sacar(String id){
        lista.remove(id);
        imprimir();
    }

    public void imprimir(){
        String contenido = "";
        for(int i = 0; i < lista.size(); i++){
           contenido = contenido + lista.get(i) + " ";
        }
        tf.setText(contenido);
    }
}