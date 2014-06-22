package com.tfg.cbbox;

import com.tfg.cbbox.AdminSQLite;
import com.tfg.cbbox.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends Activity {

	Button boton_borrar;
	EditText nombre_usuario, apellido_usuario, email_usuario,
			telefono_contacto;
	boolean modificar = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.registro);
		Toast.makeText(this, "Debe estar registrado para utilizar la app CBBox", Toast.LENGTH_LONG).show();
		boton_borrar = (Button) findViewById(R.id.boton_borrar);

		nombre_usuario = (EditText) findViewById(R.id.nombre_usuario);
		apellido_usuario = (EditText) findViewById(R.id.apellido_usuario);
		email_usuario = (EditText) findViewById(R.id.email_usuarios);
		telefono_contacto = (EditText) findViewById(R.id.telefono_contacto);

		/* -------------------- */

		getDatosUsuario();
	

	}

	/* Deja en blanco los EditText */
	public void borrar(View v) {
		nombre_usuario.setText("");
		apellido_usuario.setText("");
		email_usuario.setText("");
		telefono_contacto.setText("");
	}

	/* Registra un usuario */
	public void altaUsuario(View v) {

		/*AdminSQLiteOpenHelper.guardarUsuario(this, nombre_usuario.toString(),
				apellido_usuario.toString(), email_usuario.toString(),
				telefono_contacto.toString());*/
		System.out.println("Entro a altaUsuario");
		
		  AdminSQLite admin = new AdminSQLite(this,
		  "administracion", null, 1); SQLiteDatabase bd =
		  admin.getWritableDatabase();
		  
		  ContentValues registro = new ContentValues();
		  
		  registro.put("nombre", nombre_usuario.getText().toString());
		  registro.put("apellido", apellido_usuario.getText().toString());
		  registro.put("email", email_usuario.getText().toString());
		  registro.put("telefono", telefono_contacto.getText().toString());
		  registro.put("tiempo_guardado", 1);
		  registro.put("modo", "foto");
		  
		  if(AdminSQLite.tablaVacia(this)){
			  bd.insert("usuario", null, registro);
		  }else{
			  System.out.println("No vacia");
			  bd.update("usuario", registro, "id_usuario=1", null);
		  }
		  
		  bd.close();
		 


		System.out.println("Posicion: OK");
		Toast.makeText(this, "Se cargaron los datos de la posicion",
				Toast.LENGTH_SHORT).show();
		 irAPerfil(v);
	}

	/* Devuelve los datos del usuario registrado */
	public void getDatosUsuario(){
		
		System.out.println("Get datos");
		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		Cursor c = bd.rawQuery("SELECT nombre, apellido, email, telefono FROM usuario", null);
				
		if(c.moveToFirst()){
		
			System.out.println(c.getString(0));
			System.out.println(c.getString(1));
			System.out.println(c.getString(2));
			System.out.println(c.getString(3));
	
			nombre_usuario.setText(c.getString(0));
			apellido_usuario.setText(c.getString(1));
			email_usuario.setText(c.getString(2));
			telefono_contacto.setText(c.getString(3));
			
		
		}else{
			System.out.println("Error en getDatosUsuario");
		}
		c.close();
		bd.close();
		
		
	}
	/* Ir a perfil */
	public void irAPerfil(View v){
		System.out.println("Perflil");
		Intent i = new Intent(this, Perfil.class );
        startActivity(i);
	}
}
