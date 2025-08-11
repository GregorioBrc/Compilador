package nodosAST;

public class NodoParametros extends NodoBase {
    
    NodoBase Content;

    public NodoParametros(NodoBase content, NodoBase otroParam) {
        Content = content;
        this.setHermanoDerecha(otroParam);
    }

        public NodoParametros(String content, NodoBase otroParam) {
        Content = new NodoIdentificador(content);
        this.setHermanoDerecha(otroParam);
    }

    public NodoParametros(NodoBase content) {
        Content = content;
    }

    public void setOtroParam(NodoParametros otroParam) {
        setHermanoDerecha(otroParam);
    }

    public NodoBase getContent() {
        return Content;
    }

    public NodoBase getOtroParam() {
        return getHermanoDerecha();
    }

}
