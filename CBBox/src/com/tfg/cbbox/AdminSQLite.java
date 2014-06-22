package com.tfg.cbbox;

import com.tfg.cbbox.AdminSQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLite extends SQLiteOpenHelper {

	public AdminSQLite(Context context, String nombre, CursorFactory factory,
			int version) {
		super(context, nombre, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE usuario(id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT, apellido TEXT, email TEXT, telefono INTEGER, tiempo_guardado INTEGER, modo TEXT, coche TEXT)");
		db.execSQL("CREATE TABLE rutas(id_ruta INTEGER PRIMARY KEY, nombre_ruta TEXT, fecha_ruta TEXT, hora_inicio_ruta TEXT, modo TEXT)"); // Añadir
																																			// id_usuario
		db.execSQL("CREATE TABLE posiciones(id_posicion INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fecha_posicion TEXT, hora_posicion TEXT, latitud TEXT, longitud TEXT, ruta_foto TEXT, id_ruta INTEGER, tipo_posicion TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
		db.execSQL("DROP TABLE IF EXISTS usuario");
		db.execSQL("DROP TABLE IF EXISTS rutas");
		db.execSQL("DROP TABLE IF EXISTS posiciones");
		db.execSQL("CREATE TABLE usuario(id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT, apellido TEXT, email TEXT, telefono INTEGER, tiempo_guardado INTEGER, modo TEXT, coche TEXT)");
		db.execSQL("CREATE TABLE rutas(id_ruta INTEGER PRIMARY KEY, nombre_ruta TEXT, fecha_ruta TEXT, hora_inicio_ruta TEXT)"); // Añadir
																																	// id_usuario
		db.execSQL("CREATE TABLE posiciones(id_posicion INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fecha_posicion TEXT, hora_posicion TEXT, latitud TEXT, longitud TEXT, ruta_foto TEXT, id_ruta INTEGER, tipo_posicion TEXT)");
	}

	/**
	 * Metodo que comprueba si la tabla usuario está vacia o no. Se empleará
	 * para comprobar si el usuraio está registrado a la app
	 */

	public static boolean tablaVacia(Context context) {

		boolean res = true;
		AdminSQLite admin = new AdminSQLite(context, "administracion", null, 1);
		SQLiteDatabase bd = admin.getReadableDatabase();
		Cursor c = bd.rawQuery(
				"SELECT id_usuario FROM usuario WHERE id_usuario=1", null);

		if (c.moveToNext()) {
			res = false;
		} else {
			res = true;
		}
		bd.close();
		c.close();
		return res;

	}

	/** Metodo que comprueba si el historial de tramos esta vacio o no */
	public static boolean historialVacio(Context context) {
		boolean res = true;
		AdminSQLite admin = new AdminSQLite(context, "administracion", null, 1);
		SQLiteDatabase bd = admin.getReadableDatabase();
		Cursor c = bd.rawQuery("SELECT id_ruta FROM rutas", null);

		if (c.moveToNext()) {
			res = false;
		} else {
			res = true;
		}
		bd.close();
		c.close();
		return res;

	}

	/* Guardar usuario */
	public static void guardarUsuario(Context context, String nombre,
			String apellido, String email, String telefono) {
		System.out.println("Guardar usuario");
	}

}
