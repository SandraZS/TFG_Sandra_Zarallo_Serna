package com.tfg.cbbox;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.ToggleButton;
import com.tfg.cbbox.R;

public class Configuracion extends Activity {

	private Spinner spinner_almacenado; //spinner_intervalo;
	private ToggleButton toggle_modo;
	private RadioGroup radio_grupo;
	private RadioButton radio_foto, radio_video;

	private int intervalo_almacenado = 0;
//	private int intervalo_foto = 0;
	int aux = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuracion);

		/* spinners */

		ArrayAdapter<CharSequence> adaptador_almacenado = ArrayAdapter
				.createFromResource(this, R.array.valores_almacenado,
						android.R.layout.simple_spinner_item);

		ArrayAdapter<CharSequence> adaptador_intervalo = ArrayAdapter
				.createFromResource(this, R.array.valores_intervalo,
						android.R.layout.simple_spinner_item);

		spinner_almacenado = (Spinner) findViewById(R.id.spinner_almacenado);

		adaptador_almacenado
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adaptador_intervalo
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_almacenado.setAdapter(adaptador_almacenado);

		comprobar();

		spinner_almacenado
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						System.out.println("Buh");
						String x = spinner_almacenado.getSelectedItem()
								.toString();
						System.out.println("x: " + x + "posicion " + pos);
						System.out.println(spinner_almacenado
								.getSelectedItemPosition());

						switch (pos) {
						case 0:
							intervalo_almacenado = 60;
							break;// 1 min.
						case 1:
							intervalo_almacenado = 180;
							break;// 3 min.
						case 2:
							intervalo_almacenado = 300;
							break;// 5 min.
						case 3:
							intervalo_almacenado = 600;
							break;// 10 min.
						case 4:
							intervalo_almacenado = 0;
							break;// Todo el recorrido

						}

						System.out.println(intervalo_almacenado);
						modificarTiempoGuardado();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});
		/* fin spinners */
		/* toggle */

		radio_grupo = (RadioGroup) findViewById(R.id.radio_grupo);
		radio_foto = (RadioButton) findViewById(R.id.radio_foto);
		radio_video = (RadioButton) findViewById(R.id.radio_video);
		comprobarModo();

		/* fin toggle */

		radio_grupo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (radio_foto.isChecked()) {
					System.out.println("Ha pulsado el botón 1");
				} else if (radio_video.isChecked()) {
					System.out.println("Ha pulsado el botón 2");

				}
				modificarModo();
			}

		});
	}

	public void modificarTiempoGuardado() {

		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();

		registro.put("tiempo_guardado", intervalo_almacenado);

		bd.update("usuario", registro, "id_usuario=1", null);

		bd.close();
		System.out.println("Vamos a comprobar");
		comprobar();

	}

	public void modificarModo() {
		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();

		if (radio_foto.isChecked()) {
			registro.put("modo", "foto");
		}

		if (radio_video.isChecked()) {
			registro.put("modo", "video");
		}
		bd.update("usuario", registro, "id_usuario=1", null);

		bd.close();
		System.out.println("Vamos a comprobar");
		comprobarModo();

	}

	public void comprobarModo() {
		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		String x = "foto";
		Cursor c = bd.rawQuery("SELECT modo FROM usuario", null);

		if (c.moveToFirst()) {
			x = c.getString(0);
			System.out.println(c.getString(0));

		}

		if (x.equals("foto")) {
			radio_foto.setChecked(true);
			radio_video.setChecked(false);
		}
		if (x.equals("video")) {
			System.out.println("ES VIIIDEO");
			radio_foto.setChecked(false);
			radio_video.setChecked(true);
		}

		bd.close();
		c.close();

	}

	public void comprobar() {

		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		String x = "60";
		Cursor c = bd.rawQuery("SELECT tiempo_guardado FROM usuario", null);

		if (c.moveToFirst()) {
			x = c.getString(0);
			System.out.println(c.getString(0));
		}

		if (x.equals("60")) {
			aux = 0;
		}
		if (x.equals("180")) {
			aux = 1;
		}
		if (x.equals("300")) {
			aux = 2;
		}
		if (x.equals("600")) {
			aux = 3;
		}
		if (x.equals("0")) {
			aux = 4;
		}

		spinner_almacenado.setSelection(aux);
		bd.close();
		c.close();

	}

	public void activacionModo(View v) {
		if (toggle_modo.isChecked()) {
			radio_foto.setEnabled(true);
			radio_video.setEnabled(true);

		} else {
			radio_foto.setEnabled(false);
			radio_video.setEnabled(false);
		}
	}

	/* Ir a Principal */
	public void irAPrincipal(View v) {

		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

}
