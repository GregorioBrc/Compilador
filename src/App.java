import java.io.InputStreamReader;

import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;
import nodosAST.NodoBase;
import nodosAST.Util;

public class App {
    public static void main(String args[]) throws Exception {
        SymbolFactory sf = new DefaultSymbolFactory();
        parser parser_obj;
        if (args.length == 0)
            parser_obj = new parser(new Lexico(new InputStreamReader(System.in), sf), sf);
        else
            parser_obj = new parser(new Lexico(new InputStreamReader(new java.io.FileInputStream(args[0])), sf), sf);

        parser_obj.parse();
        NodoBase root = parser_obj.action_obj.getASTroot();
        System.out.println();
        System.out.println("IMPRESION DEL AST GENERADO");
        System.out.println();
        Util.imprimirAST(root);
        TablaSimbolos ts = new TablaSimbolos();
        ts.cargarTabla(root);
        ts.ImprimirClaves();
        Generador.setTablaSimbolos(ts);
        Generador.generarCodigoObjeto(root);
    }
}
