package nodosAST;

public class NodoArray extends NodoBase {
    private String id;
    private NodoBase expresion;

    public NodoArray(String id, NodoBase expresion) {
        super();
        this.id = id;
        this.expresion = expresion;
    }

    public NodoArray() {
        super();
        this.id = "";
        this.expresion = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodoBase getExpresion() {
        return expresion;
    }

    public void setExpresion(NodoBase expresion) {
        this.expresion = expresion;
    }

}
