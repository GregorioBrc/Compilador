package nodosAST;

public class NodoFor extends NodoBase {
    // FOR ID:var ASSIGN exp:ini TO simple_exp:fin DO stmt_seq:body
    String Id_Var;
    NodoBase Ini_Exp;
    NodoBase Fin_Exp;
    NodoBase Body;

    public NodoFor(String id_Var, NodoBase ini_Exp, NodoBase fin_Exp, NodoBase body) {
        Id_Var = id_Var;
        Ini_Exp = ini_Exp;
        Fin_Exp = fin_Exp;
        Body = body;
    }

    public NodoBase For_to_Repeat() {

        NodoAsignacion initAsig = new NodoAsignacion(Id_Var, Ini_Exp);

        NodoOperacion condEntrada = new NodoOperacion(
                new NodoIdentificador(Id_Var),
                tipoOp.menor_igual,
                Fin_Exp);

        NodoOperacion sumaUno = new NodoOperacion(
                new NodoIdentificador(Id_Var),
                tipoOp.mas,
                new NodoValor(1));

        NodoAsignacion inc = new NodoAsignacion(Id_Var, sumaUno);

        NodoBase bodyMasInc = Body;
        if (bodyMasInc != null) {
            NodoBase cursor = bodyMasInc;
            while (cursor.TieneHermano())
                cursor = cursor.getHermanoDerecha();
            cursor.setHermanoDerecha(inc);
        } else {
            bodyMasInc = inc;
        }

        NodoOperacion condSalida = new NodoOperacion(
                new NodoIdentificador(Id_Var),
                tipoOp.mayor_igual,
                Fin_Exp);

        NodoRepeat bucle = new NodoRepeat(bodyMasInc, condSalida);

        NodoIf envoltura = new NodoIf(condEntrada, bucle);

        initAsig.setHermanoDerecha(envoltura);
        return initAsig;
    }

}
