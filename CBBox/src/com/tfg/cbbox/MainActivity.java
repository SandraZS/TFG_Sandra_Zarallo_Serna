package com.tfg.cbbox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import com.tfg.cbbox.R;

public class MainActivity extends Activity {

	Button boton_comenzar, boton_configuracion, boton_perfil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		boton_comenzar = (Button) findViewById(R.id.boton_comenzar);
		boton_configuracion = (Button) findViewById(R.id.boton_configuracion);
		boton_perfil = (Button) findViewById(R.id.boton_perfil);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Acceder a la ventana de configuracion de la aplicacion */
	public void irAConfiguracion(View v) {

		/* Comprobar si el usuario está registrado */

		if (AdminSQLite.tablaVacia(this)) {
			System.out.println("VACIA");
			Intent i = new Intent(this, Registro.class);
			startActivity(i);
		} else {
			System.out.println("NO VACIA");
			Intent i = new Intent(this, Configuracion.class);
			startActivity(i);
		}

	}

	/* Acceder a la ventana del perfil de la aplicacion */
	public void irAPerfil(View v) {

		/* Comprobar si el usuario está registrado */

		if (AdminSQLite.tablaVacia(this)) {
			System.out.println("VACIA");
			Intent i = new Intent(this, Registro.class);
			startActivity(i);
		} else {
			System.out.println("NO VACIA");
			Intent i = new Intent(this, Perfil.class);
			startActivity(i);
		}

	}

	/* Acceder a la ventana principal de la aplicacion */
	public void irAPrincipal(View v) {
/* Comprobar si el usuario está registrado */
		
		if (AdminSQLite.tablaVacia(this)) {
			System.out.println("VACIA");
			Intent i = new Intent(this, Registro.class);
			startActivity(i);
		} else {
			System.out.println("NO VACIA");
			
			AdminSQLite admin = new AdminSQLite(this,
					"administracion", null, 1);
			SQLiteDatabase bd = admin.getWritableDatabase();

			String x = "foto";
			Cursor c = bd.rawQuery("SELECT modo FROM usuario", null);

			if (c.moveToFirst()) {
				x = c.getString(0);
				System.out.println(c.getString(0));
				
			}

			if (x.equals("foto")) {
				Intent i = new Intent(this, PrincipalFoto.class);
				startActivity(i);
			}
			if (x.equals("video")) {
				Intent i = new Intent(this, PrincipalVideo.class);
				startActivity(i);
			}

			bd.close();
			c.close();
			
		}
	}
	
	public void irAHistorial(View v) {
/* Comprobar si el usuario está registrado */
		
		if (AdminSQLite.tablaVacia(this)) {
			System.out.println("VACIA");
			Intent i = new Intent(this, Registro.class);
			startActivity(i);
			
		} else {
			System.out.println("NO VACIA");
			
			Intent i = new Intent(this, Historial.class);
			startActivity(i);
		}
	}

}
