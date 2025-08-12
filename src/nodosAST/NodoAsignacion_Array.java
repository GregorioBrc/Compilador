package nodosAST;

public class NodoAsignacion_Array extends NodoAsignacion {

		private NodoBase indice;

		public NodoAsignacion_Array(String identificador, NodoBase indice) {
			super(identificador);
			this.indice = indice;
		}

		public NodoAsignacion_Array(String identificador, NodoBase expresion, NodoBase indice) {
			super(identificador, expresion);
			this.indice = indice;
		}

		public NodoBase getIndice() {
			return indice;
		}

		public void setIndice(NodoBase indice) {
			this.indice = indice;
		}

	}
