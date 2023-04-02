package entidades;

public class Variables {
    private int numero = 0;
    private String hormiga;
    private ListaHormigas hormigasDentro;
    
    int getNumero(){
        return numero;
    }
    
    void setNumero(int numero){
        this.numero = numero;
    }

    public String getHormiga() {
        return hormiga;
    }

    public void setHormiga(String hormiga) {
        this.hormiga = hormiga;
    }
}
