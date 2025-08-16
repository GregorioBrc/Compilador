package nodosAST;

public class NodoLeer extends NodoBase {
	private String id;
	private NodoBase Ident;

	public NodoLeer(String identificador) {
		super();
		this.id = identificador;
		Ident = new NodoIdentificador(identificador);
	}

	public NodoLeer(NodoArray Arr) {
		super();
		this.id = Arr.getId();
		Ident = Arr;
	}

	public NodoLeer(NodoIdentificador Nd_i) {
		super();
		this.id = Nd_i.getNombre();
		Ident = Nd_i;
	}

	public NodoLeer() {
		super();
		id = "";
	}

	public String getIdentificador() {
		return id;
	}

	public void setExpresion(String identificador) {
		this.id = identificador;
	}

	public NodoBase getIdent() {
		return Ident;
	}

}
