package model;

import java.util.List;

public class Trayectoria {
	List<Punto> lista;
	int id;
	public Trayectoria(List<Punto> lista, int id) {
		super();
		this.lista = lista;
		this.id = id;
	}
	public Trayectoria(){
		super();
	}
	public List<Punto> getLista() {
		return lista;
	}
	public void setLista(List<Punto> lista) {
		this.lista = lista;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Trayectoria [lista=" + lista + ", id=" + id + "]";
	}
	
}
