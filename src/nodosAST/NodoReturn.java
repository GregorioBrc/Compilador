package nodosAST;

public class NodoReturn extends NodoBase{
    private NodoBase expresion;

    public NodoReturn(NodoBase expresion) {
        this.expresion = expresion;
    }

    public NodoBase getExpresion() {
        return expresion;
    }

}
