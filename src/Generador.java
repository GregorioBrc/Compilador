
import Registros.RegistroArray;
import Registros.RegistroFuncion;
import Registros.RegistroSimbolo;
import nodosAST.*;

public class Generador {
	/*
	 * Ilustracion de la disposicion de la memoria en
	 * este ambiente de ejecucion para el lenguaje Tiny
	 *
	 * |t1 |<- mp (Maxima posicion de memoria de la TM
	 * |t1 |<- desplazamientoTmp (tope actual)
	 * |free|
	 * |free|
	 * |... |
	 * |x |
	 * |y |<- gp
	 * 
	 */

	/*
	 * desplazamientoTmp es una variable inicializada en 0
	 * y empleada como el desplazamiento de la siguiente localidad
	 * temporal disponible desde la parte superior o tope de la memoria
	 * (la que apunta el registro MP).
	 * 
	 * - Se decrementa (desplazamientoTmp--) despues de cada almacenamiento y
	 * 
	 * - Se incrementa (desplazamientoTmp++) despues de cada eliminacion/carga en
	 * otra variable de un valor de la pila.
	 * 
	 * Pudiendose ver como el apuntador hacia el tope de la pila temporal
	 * y las llamadas a la funcion emitirRM corresponden a una inserccion
	 * y extraccion de esta pila
	 */
	private static int desplazamientoTmp = 0;
	private static TablaSimbolos tablaSimbolos = null;

	public static void setTablaSimbolos(TablaSimbolos tabla) {
		tablaSimbolos = tabla;
	}

	public static void generarCodigoObjeto(NodoBase raiz) {
		UtGen.LimpiarArchivo();
		UtGen.Imprimir("");
		UtGen.emitirComentario("------ CODIGO OBJETO DEL LENGUAJE TINY GENERADO PARA LA TM ------");
		UtGen.Imprimir("");
		generarPreludioEstandar();
		generar(raiz);
		/* Genero el codigo de finalizacion de ejecucion del codigo */
		UtGen.emitirComentario("Fin de la ejecucion.");
		UtGen.emitirRO("HALT", 0, 0, 0, "");
		UtGen.Imprimir("");
		UtGen.emitirComentario("------ FIN DEL CODIGO OBJETO DEL LENGUAJE TINY GENERADO PARA LA TM ------");
	}

	// Funcion principal de generacion de codigo
	// prerequisito: Fijar la tabla de simbolos antes de generar el codigo objeto
	private static void generar(NodoBase nodo) {
		if (tablaSimbolos != null) {
			if (nodo == null) {
				return;
			}

			if (nodo instanceof NodoIf) {
				generarIf(nodo);
			} else if (nodo instanceof NodoRepeat) {
				generarRepeat(nodo);
			} else if (nodo instanceof NodoAsignacion && !(nodo instanceof NodoAsignacion_Array)) {
				generarAsignacion(nodo);
			} else if (nodo instanceof NodoLeer) {
				generarLeer(nodo);
			} else if (nodo instanceof NodoEscribir) {
				generarEscribir(nodo);
			} else if (nodo instanceof NodoValor) {
				generarValor(nodo);
			} else if (nodo instanceof NodoIdentificador) {
				generarIdentificador(nodo);
			} else if (nodo instanceof NodoOperacion) {
				generarOperacion(nodo);
			} else if (nodo instanceof NodoArray) {
				generarAccesoArray(nodo);
			} else if (nodo instanceof NodoAsignacion_Array) {
				generarAsignacionArray(nodo);
			} else if (nodo instanceof NodoArrayDeclarar) {
			} else if (nodo instanceof NodoFuncionDecl) {
				generarDeclaracionFuncion(nodo);
			} else if (nodo instanceof NodoParametros) {
				generarParams_CallFun(nodo);
			} else if (nodo instanceof NodoReturn) {
				GenerarRetornoFun(nodo);
			} else if (nodo instanceof NodoLlamada) {
				GenerarLLamadaFun(nodo);
			} else {
				UtGen.emitirComentario("BUG: Tipo de nodo a generar desconocido");
			}
			/*
			 * Si el hijo de extrema izquierda tiene hermano a la derecha lo genero tambien
			 */
			if (nodo.TieneHermano())
				generar(nodo.getHermanoDerecha());
		} else
			UtGen.emitirComentario(
					"���ERROR: por favor fije la tabla de simbolos a usar antes de generar codigo objeto!!!");
	}

	private static void generarIf(NodoBase nodo) {
		NodoIf n = (NodoIf) nodo;
		int localidadSaltoElse, localidadSaltoEnd, localidadActual;
		if (UtGen.debug)
			UtGen.emitirComentario("-> if");
		/* Genero el codigo para la parte de prueba del IF */
		generar(n.getPrueba());
		localidadSaltoElse = UtGen.emitirSalto(1);
		UtGen.emitirComentario("If: el salto hacia el else debe estar aqui");
		/* Genero la parte THEN */
		generar(n.getParteThen());
		if (n.getParteElse() == null) {
			// Salto al final del if sin else
			localidadActual = UtGen.emitirSalto(0);
			UtGen.cargarRespaldo(localidadSaltoElse);
			UtGen.emitirRM_Abs("JEQ", UtGen.AC, localidadActual, "if: jmp hacia final");
			UtGen.restaurarRespaldo();

		} else {
			// Preparando Saltos
			localidadSaltoEnd = UtGen.emitirSalto(1);
			UtGen.emitirComentario("If: el salto hacia el final debe estar aqui");
			localidadActual = UtGen.emitirSalto(0);
			UtGen.cargarRespaldo(localidadSaltoElse);
			UtGen.emitirRM_Abs("JEQ", UtGen.AC, localidadActual, "if: jmp hacia else");
			UtGen.restaurarRespaldo();

			/* Genero la parte ELSE */
			generar(n.getParteElse());
			localidadActual = UtGen.emitirSalto(0);
			UtGen.cargarRespaldo(localidadSaltoEnd);
			UtGen.emitirRM_Abs("LDA", UtGen.PC, localidadActual, "if: jmp hacia el final");
			UtGen.restaurarRespaldo();

		}

		if (UtGen.debug)
			UtGen.emitirComentario("<- if");
	}

	private static void generarRepeat(NodoBase nodo) {
		NodoRepeat n = (NodoRepeat) nodo;
		int localidadSaltoInicio;
		if (UtGen.debug)
			UtGen.emitirComentario("-> repeat");
		localidadSaltoInicio = UtGen.emitirSalto(0);
		UtGen.emitirComentario("repeat: el salto hacia el final (luego del cuerpo) del repeat debe estar aqui");
		/* Genero el cuerpo del repeat */
		generar(n.getCuerpo());
		/* Genero el codigo de la prueba del repeat */
		generar(n.getPrueba());
		UtGen.emitirRM_Abs("JEQ", UtGen.AC, localidadSaltoInicio, "repeat: jmp hacia el inicio del cuerpo");
		if (UtGen.debug)
			UtGen.emitirComentario("<- repeat");
	}

	private static void generarAsignacion(NodoBase nodo) {
		NodoAsignacion n = (NodoAsignacion) nodo;
		int direccion;
		if (UtGen.debug)
			UtGen.emitirComentario("-> asignacion");
		/* Genero el codigo para la expresion a la derecha de la asignacion */
		generar(n.getExpresion());
		/* Ahora almaceno el valor resultante */
		direccion = tablaSimbolos.getDireccion(n.getIdentificador());
		UtGen.emitirRM("ST", UtGen.AC, direccion, UtGen.GP,
				"asignacion: almaceno el valor para el id " + n.getIdentificador());
		if (UtGen.debug)
			UtGen.emitirComentario("<- asignacion");
	}

	private static void generarLeer(NodoBase nodo) {
		NodoLeer n = (NodoLeer) nodo;
		int direccion;
		if (UtGen.debug)
			UtGen.emitirComentario("-> leer");
		UtGen.emitirRO("IN", UtGen.AC, 0, 0, "leer: lee un valor entero ");

		if (n.getIdent() instanceof NodoArray) {
			generarAsignacionArray(new NodoAsignacion_Array((NodoArray)n.getIdent()));
		} else {
			direccion = tablaSimbolos.getDireccion(n.getIdentificador());
			UtGen.emitirRM("ST", UtGen.AC, direccion, UtGen.GP,
					"leer: almaceno el valor entero leido en el id " + n.getIdentificador());
		}

		if (UtGen.debug)
			UtGen.emitirComentario("<- leer");
	}

	private static void generarEscribir(NodoBase nodo) {
		NodoEscribir n = (NodoEscribir) nodo;
		if (UtGen.debug)
			UtGen.emitirComentario("-> escribir");
		/* Genero el codigo de la expresion que va a ser escrita en pantalla */
		generar(n.getExpresion());
		/* Ahora genero la salida */
		UtGen.emitirRO("OUT", UtGen.AC, 0, 0, "escribir: genero la salida de la expresion");
		if (UtGen.debug)
			UtGen.emitirComentario("<- escribir");
	}

	private static void generarValor(NodoBase nodo) {
		NodoValor n = (NodoValor) nodo;
		if (UtGen.debug)
			UtGen.emitirComentario("-> constante");
		UtGen.emitirRM("LDC", UtGen.AC, n.getValor(), 0, "cargar constante: " + n.getValor());
		if (UtGen.debug)
			UtGen.emitirComentario("<- constante");
	}

	private static void generarIdentificador(NodoBase nodo) {
		NodoIdentificador n = (NodoIdentificador) nodo;
		int direccion;
		if (UtGen.debug)
			UtGen.emitirComentario("-> identificador");
		direccion = tablaSimbolos.getDireccion(n.getNombre());
		UtGen.emitirRM("LD", UtGen.AC, direccion, UtGen.GP, "cargar valor de identificador: " + n.getNombre());
		if (UtGen.debug)
			UtGen.emitirComentario("-> identificador");
	}

	private static void generarOperacion(NodoBase nodo) {
		NodoOperacion n = (NodoOperacion) nodo;
		if (UtGen.debug)
			UtGen.emitirComentario("-> Operacion: " + n.getOperacion());
		/* Genero la expresion izquierda de la operacion */
		generar(n.getOpIzquierdo());
		/*
		 * Almaceno en la pseudo pila de valor temporales el valor de la operacion
		 * izquierda
		 */
		UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP,
				"op: push en la pila tmp el resultado expresion izquierda");
		/* Genero la expresion derecha de la operacion */
		generar(n.getOpDerecho());
		/* Ahora cargo/saco de la pila el valor izquierdo */
		UtGen.emitirRM("LD", UtGen.AC1, ++desplazamientoTmp, UtGen.MP,
				"op: pop o cargo de la pila el valor izquierdo en AC1");
		switch (n.getOperacion()) {
			case mas:
				UtGen.emitirRO("ADD", UtGen.AC, UtGen.AC1, UtGen.AC, "op: +");
				break;
			case menos:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: -");
				break;
			case por:
				UtGen.emitirRO("MUL", UtGen.AC, UtGen.AC1, UtGen.AC, "op: *");
				break;
			case entre:
				UtGen.emitirRO("DIV", UtGen.AC, UtGen.AC1, UtGen.AC, "op: /");
				break;
			case mod:
				UtGen.emitirRM("ST", UtGen.AC1, desplazamientoTmp--, UtGen.MP, "mod: push left");
				UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP, "mod: push right");
				UtGen.emitirRO("DIV", UtGen.AC, UtGen.AC1, UtGen.AC, "mod: q = left / right");
				UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP, "mod: push q");
				UtGen.emitirRM("LD", UtGen.AC, ++desplazamientoTmp, UtGen.MP, "mod: pop q -> AC");
				UtGen.emitirRM("LD", UtGen.AC1, ++desplazamientoTmp, UtGen.MP, "mod: pop right -> AC1");
				UtGen.emitirRO("MUL", UtGen.AC, UtGen.AC1, UtGen.AC, "mod: p = right * q");
				UtGen.emitirRM("LD", UtGen.AC1, ++desplazamientoTmp, UtGen.MP, "mod: pop left -> AC1");
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "mod: left - p");
				break;
			case menor:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: <");
				UtGen.emitirRM("JLT", UtGen.AC, 2, UtGen.PC, "voy dos instrucciones mas alla si verdadero (AC<0)");
				UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "caso de falso (AC=0)");
				UtGen.emitirRM("LDA", UtGen.PC, 1, UtGen.PC, "Salto incondicional para evitar el caso verdadero");
				UtGen.emitirRM("LDC", UtGen.AC, 1, UtGen.AC, "caso de verdadero (AC=1)");
				break;
			case menor_igual:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: <=");
				UtGen.emitirRM("JLE", UtGen.AC, 2, UtGen.PC, "voy dos instrucciones mas alla si verdadero (AC<=0)");
				UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "caso de falso (AC=0)");
				UtGen.emitirRM("LDA", UtGen.PC, 1, UtGen.PC, "Salto incondicional para evitar el caso verdadero");
				UtGen.emitirRM("LDC", UtGen.AC, 1, UtGen.AC, "caso de verdadero (AC=1)");
				break;
			case mayor:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: >");
				UtGen.emitirRM("JGT", UtGen.AC, 2, UtGen.PC, "voy dos instrucciones mas alla si verdadero (AC>0)");
				UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "caso de falso (AC=0)");
				UtGen.emitirRM("LDA", UtGen.PC, 1, UtGen.PC, "Salto incondicional para evitar el caso verdadero");
				UtGen.emitirRM("LDC", UtGen.AC, 1, UtGen.AC, "caso de verdadero (AC=1)");
				break;
			case mayor_igual:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: >=");
				UtGen.emitirRM("JGE", UtGen.AC, 2, UtGen.PC, "voy dos instrucciones mas alla si verdadero (AC>=0)");
				UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "caso de falso (AC=0)");
				UtGen.emitirRM("LDA", UtGen.PC, 1, UtGen.PC, "Salto incondicional para evitar el caso verdadero");
				UtGen.emitirRM("LDC", UtGen.AC, 1, UtGen.AC, "caso de verdadero (AC=1)");
				break;
			case igual:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: ==");
				UtGen.emitirRM("JEQ", UtGen.AC, 2, UtGen.PC, "voy dos instrucciones mas alla si verdadero (AC==0)");
				UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "caso de falso (AC=0)");
				UtGen.emitirRM("LDA", UtGen.PC, 1, UtGen.PC, "Salto incondicional para evitar el caso verdadero");
				UtGen.emitirRM("LDC", UtGen.AC, 1, UtGen.AC, "caso de verdadero (AC=1)");
				break;
			case distinto:
				UtGen.emitirRO("SUB", UtGen.AC, UtGen.AC1, UtGen.AC, "op: !=");
				UtGen.emitirRM("JNE", UtGen.AC, 2, UtGen.PC, "voy dos instrucciones mas alla si verdadero (AC!=0)");
				UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "caso de falso (AC=0)");
				UtGen.emitirRM("LDA", UtGen.PC, 1, UtGen.PC, "Salto incondicional para evitar el caso verdadero");
				UtGen.emitirRM("LDC", UtGen.AC, 1, UtGen.AC, "caso de verdadero (AC=1)");
				break;
			default:
				UtGen.emitirComentario("BUG: tipo de operacion desconocida");
		}
		if (UtGen.debug)
			UtGen.emitirComentario("<- Operacion: " + n.getOperacion());
	}

	private static void generarAsignacionArray(NodoBase nodo) {
		NodoAsignacion_Array n = (NodoAsignacion_Array) nodo;
		int dir_base = tablaSimbolos.getDireccion(n.getIdentificador());

		// Generar Lado derecho, guarda AC
		if (n.getExpresion() != null) {
			generar(n.getExpresion());
		}
		UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP,
				"op: push Resultado");

		// Generar Lado Indice, guarda AC
		generar(n.getIndice());
		UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP,
				"op: push Indice");

		UtGen.emitirRM("LD", UtGen.AC, ++desplazamientoTmp, UtGen.MP, "Cargar indice");

		UtGen.emitirRM("LD", UtGen.AC1, ++desplazamientoTmp, UtGen.MP, "Cargar Contenido");

		UtGen.emitirRM("ST", UtGen.AC1, dir_base, UtGen.AC,
				"Asignacion Array: Almaceno el valor en la direccion base + indice");
	}

	private static void generarAccesoArray(NodoBase nodo) {
		NodoArray n = (NodoArray) nodo;
		int dir_base = tablaSimbolos.getDireccion(n.getId());

		generar(n.getArg());
		UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP,
				"op: push Indice");
		UtGen.emitirRM("LD", UtGen.AC1, ++desplazamientoTmp, UtGen.MP, "Cargar Indice");

		UtGen.emitirRM("LD", UtGen.AC, dir_base, UtGen.AC1, "Cargar Valor Array");
	}

	private static void generarDeclaracionFuncion(NodoBase nodo) {
		NodoFuncionDecl n = (NodoFuncionDecl) nodo;
		int Temp_ = desplazamientoTmp;

		if (UtGen.debug)
			UtGen.emitirComentario("-> Funcion: " + n.getNombre());

		tablaSimbolos.EntrarAmbito(n.getNombre());
		int dir_base = tablaSimbolos.getDireccion(n.getNombre());
		int Cant_Para = NodoParametros.NumParametros(n.getParametros());

		int Salto = UtGen.emitirSalto(1);
		((RegistroFuncion) tablaSimbolos.BuscarSimbolo(n.getNombre())).setIni_Instruc(UtGen.emitirSalto(0));

		UtGen.emitirRM("LD", UtGen.AC, desplazamientoTmp, UtGen.MP,
				"Funcion: Direccion de retorno de la pila temporal");

		UtGen.emitirRM("ST", UtGen.AC, dir_base, UtGen.GP,
				"Funcion: Almaceno Direccion de retorno en la pila temporal");

		for (int i = 1; i <= Cant_Para; i++) {
			UtGen.emitirRM("LD", UtGen.AC, desplazamientoTmp - i, UtGen.MP,
					"Funcion: Cargar el parametro " + i + " desde la pila temporal");

			UtGen.emitirRM("ST", UtGen.AC, (dir_base + 1) + Cant_Para - i, UtGen.GP,
					"Funcion: Almaceno el parametro " + i + " en la direccion base de la funcion");
		}

		if (UtGen.debug)
			UtGen.emitirComentario("-> Cuerpo de la funcion: " + n.getNombre());
		generar(n.getCuerpo());

		if (UtGen.debug)
			UtGen.emitirComentario("-> Valor de retorno de la funcion: " + n.getNombre());
		generar(n.getValorRetorno());

		// Cargar Direccion retorno Lugar 0 del ambito
		UtGen.emitirRM("LD", UtGen.AC1, dir_base, UtGen.GP,
				"Funcion: Cargar Direccion al registro");

		UtGen.emitirRM("JNE", UtGen.PC, 1, UtGen.AC1, "Salto hacia la direccion de retorno de la funcion");

		tablaSimbolos.SalirAmbito(n.getNombre(), true);

		int Local_Act = UtGen.emitirSalto(0);
		UtGen.cargarRespaldo(Salto);
		UtGen.emitirRM_Abs("JNE", UtGen.PC, Local_Act, "Funcion: Salto hacia el final de la funcion");
		UtGen.restaurarRespaldo();
		desplazamientoTmp = Temp_;
	}

	private static void generarParams_CallFun(NodoBase params) {
		if (params == null) {
			return;
		} else {
			if (UtGen.debug)
				UtGen.emitirComentario("-> Parametros");

			generar(((NodoParametros) params).getContent());

			UtGen.emitirRM("ST", UtGen.AC, desplazamientoTmp--, UtGen.MP,
					"op: push Parametro: " + ((NodoParametros) params).getContent().toString());

			generarParams_CallFun(params.getHermanoDerecha());
		}
	}

	private static void GenerarRetornoFun(NodoBase nodo) {
		NodoReturn n = (NodoReturn) nodo;
		if (UtGen.debug)
			UtGen.emitirComentario("-> Retorno Funcion");

		if (n.getExpresion() != null) {
			generar(n.getExpresion());
		} else {
			UtGen.emitirRM("LDC", UtGen.AC, 0, UtGen.AC, "Retorno: No hay valor de retorno, uso 0");
		}
	}

	private static void GenerarLLamadaFun(NodoBase nodo) {
		NodoLlamada n = (NodoLlamada) nodo;
		if (UtGen.debug)
			UtGen.emitirComentario("-> Llamada Funcion: " + n.getNombre());

		int SaltoFun = ((RegistroFuncion) tablaSimbolos.BuscarSimbolo(n.getNombre())).getIni_Instruc();
		int des_ = desplazamientoTmp--;
		generar(n.getArg());

		UtGen.emitirRM("ST", UtGen.PC, des_, UtGen.MP,
				"op: push Direccion de retorno de la funcion: " + n.getNombre());

		UtGen.emitirRM_Abs("JNE", UtGen.PC, SaltoFun, "Llamada Funcion: Salto hacia la funcion " + n.getNombre());
	}

	// TODO: enviar preludio a archivo de salida, obtener antes su nombre
	private static void generarPreludioEstandar() {
		UtGen.emitirComentario("Compilacion TINY para el codigo objeto TM");
		UtGen.emitirComentario("Archivo: " + "NOMBRE_ARREGLAR");
		/* Genero inicializaciones del preludio estandar */
		/* Todos los registros en tiny comienzan en cero */
		UtGen.emitirComentario("Preludio estandar:");
		UtGen.emitirRM("LD", UtGen.MP, 0, UtGen.AC, "cargar la maxima direccion desde la localidad 0");
		UtGen.emitirRM("ST", UtGen.AC, 0, UtGen.AC, "limpio el registro de la localidad 0");
	}

}
