package com.tfg.cbbox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.tfg.cbbox.R;

public class Perfil extends Activity{

	TextView nombre, apellido, email, telefono;
	Button editar, historial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.perfil);
		nombre = (TextView) findViewById(R.id.text_nombre);
		apellido = (TextView) findViewById(R.id.text_apellido);
		email = (TextView) findViewById(R.id.text_email);
		telefono = (TextView) findViewById(R.id.text_telefono);
		//editar = (Button) findViewById(R.id.boton_editar);
		
		getDatosUsuario();
		
	}
	
	/* Devuelve los datos de usuario*/
	public void getDatosUsuario(){
	
		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		Cursor c = bd.rawQuery("SELECT nombre, apellido, email, telefono FROM usuario", null);
				
		if(c.moveToFirst()){
	
			nombre.setText(c.getString(0));
			apellido.setText(c.getString(1));
			email.setText(c.getString(2));
			telefono.setText(c.getString(3));
			
		
		}else{
			System.out.println("Error en getDatosUsuario");
		}
		c.close();
		bd.close();
		
		
	}

	/* Editar los datos de usuario */
	public void irAResgitro(View v){
		
		Intent i = new Intent(this, Registro.class );
        startActivity(i);
	}
	
	/* Ir a Historial */
	public void irAHistorial(View v){
		
		Intent i = new Intent(this, Historial.class );
        startActivity(i);
	}
	
	/* Ir a Principal */
	public void irAPrincipal(View v){
		
		Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
	}
	/* Ir a Localicacion del coche */
	public void irACoche(View v){
		
		Intent i = new Intent(this, Coche.class );
        startActivity(i);
	}
}
