package com.tfg.cbbox;

public class ItemArchivo {
	protected String ruta;
	protected String nombre;
	protected String tipo;
	
	
	
	
	public ItemArchivo() {
		this.nombre = "";
		this.ruta = "";
		this.tipo = "";
	
	}
	
	public ItemArchivo(String ruta, String nombre, String tipo) {
		this.ruta = ruta;
		this.nombre = nombre;
		this.tipo = tipo;

	}
	

	public String getRuta() {
		return ruta;
	}
	
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}