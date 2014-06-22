package com.tfg.cbbox;

public class ItemTramo {
	protected long id_tramo;
	protected String nombre;
	
	
	
	public ItemTramo() {
		this.nombre = "";
	
	}
	
	public ItemTramo(long id_tramo, String nombre) {
		this.id_tramo = id_tramo;
		this.nombre = nombre;

	}
	

	public long getId_tramo() {
		return id_tramo;
	}
	
	public void setId_tramo(long id_tramo) {
		this.id_tramo = id_tramo;
	}
	
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
