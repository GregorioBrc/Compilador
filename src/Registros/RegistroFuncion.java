package Registros;

import java.util.HashMap;

public class RegistroFuncion extends RegistroSimbolo{
    
    private int numParametros;
    
    private HashMap<String, RegistroSimbolo> Simbolos;
    
    public RegistroFuncion(String identificador, int numLinea, int direccionMemoria, int NumPara) {
        super(identificador, numLinea, direccionMemoria);
        numParametros = NumPara;
    }
    
    public HashMap<String, RegistroSimbolo> getSimbolos() {
        return Simbolos;
    }
    
    public void setSimbolos(HashMap<String, RegistroSimbolo> simbolos) {
        Simbolos = simbolos;
    }
    
    public int getNumParametros() {
        return numParametros;
    }

}
