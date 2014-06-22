package com.tfg.cbbox;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;
import com.tfg.cbbox.R;

public class ArchivosVideo extends Activity {
	ListView lista_archivos;

/*	private static final String[] RUTAS_CLAVES = new String[] { "id_ruta",
			"nombre_ruta", "fecha_ruta", "hora_inicio_ruta" };
	private static final String[] POSICIONES_CLAVES = new String[] {
			"id_posicion", "fecha_posicion", "hora_posicion", "latitud",
			"longitud", "ruta_foto", "id_ruta" };*/

	String ruta_repetida = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.archivos_video);

		if (AdminSQLite.historialVacio(this)) {

		} else {

//			Bundle bundle = getIntent().getExtras();
//
//			String ruta = "1";
//			ruta = bundle.getString("id_ruta_pasada").toString();

		}

		lista_archivos = (ListView) findViewById(R.id.lista_archivos);

		final ArrayList<ItemArchivo> itemsArchivo = obtenerItems();

		final ItemArchivoAdapter adapter = new ItemArchivoAdapter(this,
				itemsArchivo);

		lista_archivos.setAdapter(adapter);

		lista_archivos
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int posicion, long id) {

						VideoView muestra_video = (VideoView) findViewById(R.id.muestra_video);

						muestra_video.start();

					}
				});
	}

	private ArrayList<ItemArchivo> obtenerItems() {
		ArrayList<ItemArchivo> items = new ArrayList<ItemArchivo>();
		System.out.println("entro en items");

		Bundle bundle = getIntent().getExtras();
		String ruta = bundle.getString("id_ruta_pasada").toString();

		Cursor x = consultaPosicionPorRuta(ruta);

		if (x.moveToFirst()) {
			System.out.println("hay cosas!");

			do {

				x.getString(0);
				x.getString(1);
				x.getString(2);

				// Porque hemos guardado la ruta varias veces
				if (!ruta_repetida.equals(x.getString(5))) {

					ruta_repetida = x.getString(5);

					items.add(new ItemArchivo(x.getString(5), "CBBox_"
							+ x.getString(2), x.getString(7)));
				}

			} while (x.moveToNext());

		} else {

		}
		x.close();

		return items;
	}

	public Cursor consultaPosicionPorRuta(String id_ruta) {
		AdminSQLite admin = new AdminSQLite(this, "administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		Cursor fila = bd
				.rawQuery(
						"select id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto,id_ruta,tipo_posicion  from posiciones where id_ruta="
								+ id_ruta + "", null);
		System.out.println(":)");

		if (fila.moveToFirst()) {

			do {

			} while (fila.moveToNext());

		} else
			Toast.makeText(
					this,
					"Lo sentimos, la información no fue guardada correctamente.",
					Toast.LENGTH_SHORT).show();
		bd.close();

		fila.moveToFirst();
		return fila;
	}

	public void irAHistorial(View v) {

		Intent i = new Intent(this, Historial.class);
		startActivity(i);
	}

}
