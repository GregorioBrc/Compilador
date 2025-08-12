package nodosAST;

public class NodoArrayDeclarar extends NodoBase {
    NodoArray Nd;

    public NodoArrayDeclarar(NodoArray nd) {
        Nd = nd;
    }

    public NodoArray getNd() {
        return Nd;
    }
}
