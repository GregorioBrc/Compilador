package Registros;

import java.util.HashMap;

public class RegistroFuncion extends RegistroSimbolo {

    private int numParametros;
    private int Ini_Instruc;
    private int Salto_Fin;

    private HashMap<String, RegistroSimbolo> Simbolos;

    public RegistroFuncion(String identificador, int numLinea, int direccionMemoria, int NumPara) {
        super(identificador, numLinea, direccionMemoria);
        numParametros = NumPara;
    }

    public int getIni_Instruc() {
        return Ini_Instruc;
    }

    public void setIni_Instruc(int ini_Instruc) {
        Ini_Instruc = ini_Instruc;
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

    public int getSalto_Fin() {
        return Salto_Fin;
    }

    public void setSalto_Fin(int salto_Fin) {
        Salto_Fin = salto_Fin;
    }
}
