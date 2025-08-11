package nodosAST;

public class NodoFuncionDecl extends NodoBase {
    String nombre;
    NodoParametros parametros;
    NodoBase cuerpo;
    NodoReturn valorRetorno;

    public NodoFuncionDecl(String nombre, NodoParametros parametros, NodoBase cuerpo, NodoReturn valorRetorno) {
        this.nombre = nombre;
        this.parametros = parametros;
        this.cuerpo = cuerpo;
        this.valorRetorno = valorRetorno;
    }

    public String getNombre() {
        return nombre;
    }


    public NodoParametros getParametros() {
        return parametros;
    }

    public NodoBase getCuerpo() {
        return cuerpo;
    }

    public NodoReturn getValorRetorno() {
        return valorRetorno;
    }
}
