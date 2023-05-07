package entidades;

import java.util.*;
import javax.swing.JTextField;

/* La clase ListaHormigas permite gestionar las listas de threads en los monitores,
con métodos para meter y sacar threads en ella. Cada vez que una lista se modifica,
se imprime su nuevo contenido en el JTextField que toma como parámetro el constructor. */

public class ListaHormigas{
    ArrayList<String> lista;
    JTextField tf;
    ArrayList<Thread> listaObjeto;
    
    public ListaHormigas(JTextField tf){
        lista=new ArrayList<String>();
        this.tf=tf;
    }
    
    public ListaHormigas(){
        listaObjeto=new ArrayList<Thread>();
    }
    
    public ArrayList<String> getListaHormigasStrings(){
        return lista;
    }
    
    public ArrayList<Thread> getListaHormigas(){
        return listaObjeto;
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
    
    public synchronized void meterLista(Thread th){
        listaObjeto.add(th);
    }
    
    public synchronized void sacarLista(Thread th){
        listaObjeto.remove(th);
    }
}