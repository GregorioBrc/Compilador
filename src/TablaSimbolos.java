
import nodosAST.*;

import java.util.*;

public class TablaSimbolos {
	private HashMap<String, RegistroSimbolo> tabla;
	private int direccion; // Contador de las localidades de memoria asignadas a la tabla

	public TablaSimbolos() {
		super();
		tabla = new HashMap<String, RegistroSimbolo>();
		direccion = 0;
	}

	public void cargarTabla(NodoBase raiz) {
		while (raiz != null) {
			if (raiz instanceof NodoIdentificador) {
				InsertarSimbolo(((NodoIdentificador) raiz).getNombre(), -1);
			} else if (raiz instanceof NodoArray || raiz instanceof NodoArrayDeclarar) {
				if (raiz instanceof NodoArray) {
					InsertarSimbolo_Array((NodoArray) raiz, -1);
				} else {
					InsertarSimbolo_Array(((NodoArrayDeclarar) raiz).getNd(), -1);
				}
			}

			/* Hago el recorrido recursivo */
			if (raiz instanceof NodoIf) {
				cargarTabla(((NodoIf) raiz).getPrueba());

				cargarTabla(((NodoIf) raiz).getParteThen());

				if (((NodoIf) raiz).getParteElse() != null) {
					cargarTabla(((NodoIf) raiz).getParteElse());
				}

			} else if (raiz instanceof NodoRepeat) {
				cargarTabla(((NodoRepeat) raiz).getCuerpo());

				cargarTabla(((NodoRepeat) raiz).getPrueba());

			} else if (raiz instanceof NodoAsignacion) {
				InsertarSimbolo(((NodoAsignacion) raiz).getIdentificador(), -1);
				cargarTabla(((NodoAsignacion) raiz).getExpresion());
			}

			else if (raiz instanceof NodoEscribir)
				cargarTabla(((NodoEscribir) raiz).getExpresion());

			else if (raiz instanceof NodoOperacion) {
				cargarTabla(((NodoOperacion) raiz).getOpIzquierdo());
				cargarTabla(((NodoOperacion) raiz).getOpDerecho());
			}

			raiz = raiz.getHermanoDerecha();
		}
	}

	// true es nuevo no existe se insertara, false ya existe NO se vuelve a insertar
	public boolean InsertarSimbolo(String identificador, int numLinea) {
		RegistroSimbolo simbolo;
		if (tabla.containsKey(identificador)) {
			return false;
		} else {
			simbolo = new RegistroSimbolo(identificador, numLinea, direccion++);
			tabla.put(identificador, simbolo);
			return true;
		}
	}

	public boolean InsertarSimbolo_Array(NodoArray array, int numLinea) {
		RegistroSimbolo simbolo;
		if (tabla.containsKey(array.getId())) {
			return false;
		} else {
			int ax = ((NodoValor) array.getArg()).getValor();
			simbolo = new RegistroArray(array.getId(), numLinea, direccion, ax);
			direccion += ax;
			tabla.put(array.getId(), simbolo);
			return true;
		}
	}

	public RegistroSimbolo BuscarSimbolo(String identificador) {
		RegistroSimbolo simbolo = (RegistroSimbolo) tabla.get(identificador);
		return simbolo;
	}

	public void ImprimirClaves() {
		System.out.println("*** Tabla de Simbolos ***");
		for (Iterator<String> it = tabla.keySet().iterator(); it.hasNext();) {
			String s = (String) it.next();
			System.out.println("Consegui Key: " + s + " con direccion: " + BuscarSimbolo(s).getDireccionMemoria());
		}
	}

	public int getDireccion(String Clave) {
		return BuscarSimbolo(Clave).getDireccionMemoria();
	}
}
