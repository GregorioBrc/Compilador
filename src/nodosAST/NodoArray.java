package nodosAST;

public class NodoArray extends NodoBase {
    private String id;
    private NodoBase Arg;

    public NodoArray(String id, NodoBase expresion) {
        super();
        this.id = id;
        this.Arg = expresion;
    }

    public NodoArray() {
        super();
        this.id = "";
        this.Arg = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodoBase getArg() {
        return Arg;
    }

    public void setArg(NodoBase expresion) {
        this.Arg = expresion;
    }

}
