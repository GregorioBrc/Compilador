package nodosAST;

public class NodoLlamada extends NodoBase{
    String nombre;
    NodoBase Arg;

    public NodoLlamada(String nombre, NodoBase arg) {
        this.nombre = nombre;
        Arg = arg;
    }

    public String getNombre() {
        return nombre;
    }

    public NodoBase getArg() {
        return Arg;
    }
}
