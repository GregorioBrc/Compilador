public class RegistroArray extends RegistroSimbolo {
    private int tamano;

    public RegistroArray(String identificador, int numLinea,
            int direccionMemoria, int tamano) {
        super(identificador, numLinea, direccionMemoria);
        this.tamano = tamano;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }


}