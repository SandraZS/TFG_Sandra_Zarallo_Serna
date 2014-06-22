package com.tfg.cbbox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.tfg.cbbox.R;

public class PrincipalVideo extends Activity implements LocationListener,
		Callback {

	int id_posicion = 0;
	// Valores definidos en Configuracion

	int intervalo_posiciones = 5; // tiempo transcurrido entre posiciones
									// FIJO!!!
	int tiempo_transcurrido = 0; // Variable auxiliar
	int intervalo_tiempo_almacenado = 20; // Ultimos segundos/minutos
											// almacenados
											// VALORES POR DEFECTO
	int tiempo_video = 10;
	ListView listaPosiciones;

	/**
	 * Array con las claves de la tabla
	 */
	private static final String[] TABLA_POSICIONES = new String[] {
			"id_posicion", "fecha_posicion", "hora_posicion", "latitud",
			"longitud", "ruta_foto", "id_ruta", "tipo_posicion" };

	/* LOCALIZACION */
	private LocationManager manejador; // Gestor del servicio de localizacion
	private String proveedor;
	private Button botonActivar;
	boolean servicioActivo = false;
	boolean pararPulsado = false;

	int numero_pos_guardadas = 0;
	/* Valores por defecto para posiciones */
	int id_posicion_aux = 0;
	int id_pos;

	String ruta_video = "";
	int id_ruta = 0;
	String fecha_posicion = "";
	String hora_posicion = "";
	String latitud = "";
	String longitud = "";
	String tipo_posicion = "temporal";
	String modo = "video";

	boolean primera_vez = false;
	boolean modificar = false;
	int contador = 0; // Auxiliar ejemplo

	int i = 0;

	int posicionesAGuardar = intervalo_tiempo_almacenado / intervalo_posiciones; // Número de posiciones a guardar por cada tramo
	
	int contadorPosiciones = 0;

	final Handler mHandler = new Handler();
	final Handler mHandler2 = new Handler();

	/* VIDEO */
	private MediaRecorder mediaRecorder = null;
	private MediaPlayer mediaPlayer = null;
	private boolean recording = false;
	private String fileName = null;

	private LayoutInflater myInflater = null;
	Camera myCamera;
	byte[] tempdata;
	boolean myPreviewRunning = false;
	private SurfaceHolder mySurfaceHolder;
	private SurfaceView mySurfaceView;
	Button takePicture;
	Button boton_tramo_interes;
	Button boton_configuracion, boton_perfil, boton_historial;

	ImageButton botonCoche;

	/* :( */
	int cont_fotos = 0;
	int cont_pos = 0;

	/* HILO */
	protected void miHilo() {

		Thread t = new Thread() {
			public void run() {

				while (servicioActivo) {
					System.out.println("Localizacion");
					obtenerFechaYHora();
					mHandler.post(ejecutarAccion); // Accion: recoger
													// localizacion
					try {
						Thread.sleep(intervalo_posiciones * 1000); // En
																	// milisegundos

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

			}

		};

		t.start();

	}

	int video = 1;

	public String siguienteIdRuta() {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		Cursor c = bd.rawQuery("select id_ruta from rutas", null);

		if (c.moveToLast()) {

			String res = c.getString(0);
			c.close();
			bd.close();
			return res;
		}
		bd.close();
		c.close();
		return "1";
	}
	public String siguienteIdPosicion() {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		Cursor c = bd.rawQuery("select id_posicion from posiciones", null);

		if (c.moveToLast()) {

			String res = c.getString(0);
			c.close();
			bd.close();
			return res;
		}
		bd.close();
		c.close();
		return "0";
	}

	protected void miHilo2() {

		Thread t = new Thread() {
			public void run() {
				video = 1;
				while (servicioActivo) {

					/**
					 * Llamamos el mŽtodo que configura el media recorder y le
					 * decimos el archivo de salida
					 */
					prepareRecorder();
					obtenerFechaYHora();

					String aver = Environment.getExternalStorageDirectory()
							.getPath();

					if (id_ruta == 0) {

						id_ruta = Integer.parseInt(siguienteIdRuta());
					}
					ruta_video = Environment.getExternalStorageDirectory()
							+ "/Tramo_" + id_ruta + "_video_" + video + ".mp4";

					mediaRecorder.setOutputFile(ruta_video);

					try {
						/**
						 * Una vez configurado todo llamamos al mŽtodo prepare
						 * que deja todo listo para iniciar la grabaci—n
						 */
						mediaRecorder.prepare();
					} catch (IllegalStateException e) {
					} catch (IOException e) {
						System.out.println("REALLY?");
					}
					/**
					 * Iniciamos la grabaci—n y actualizamos el estatus de la
					 * variable recording
					 */
					mediaRecorder.start();
					// mHandler.post(ejecutarAccion); // Accion: recoger
					// localizacion
					try {
						Thread.sleep(tiempo_video * 1000); // En
						// milisegundos

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mediaRecorder.stop();
					mediaRecorder.reset();
					video++;

				}
				if(interes){
					cambiarTipo("tramo de interes");
					interes=false;
					servicioActivo = true;

					/* Hilo que recoger localizacion */
					miHilo();
					miHilo2();
				}else{
					cambiarTipo("final");
				}
		
			}

		};

		t.start();

	}

	int pos_final = 0;
	int cont = 0;
	boolean change = false;
	boolean interes = false;

	/* ACCION DEL HILO */
	/* Obtener localizacion */
	final Runnable ejecutarAccion = new Runnable() {
		public void run() {
			System.out.println("ACCION 1");

			cont_fotos++;

			if (!primera_vez && servicioActivo) {

				guardarRuta();
			}

			if (contadorPosiciones < posicionesAGuardar) {

				obtenerLocalizacion();

				contadorPosiciones++;
				cont++;
				
				if (!primera_vez) {

					primera_vez = true; // OJO, SOLO UNA VEZ!
					id_pos = obtenerIdPosicionInsertado(); // Para sobreescribir
					
					id_posicion_aux = id_pos;
				}

			} else {

				
				borrarVideo(id_pos + contadorPosiciones);
		
				change = true;

				obtenerLocalizacion();

				cont++;
				colocar();
				id_pos = obtenerIdPosicionInsertado();


			}
		}
	};

	public void colocar() {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		ContentValues registro = new ContentValues();

		int aux1 = id_pos+1;
		int aux2 = id_pos;
		int i = 0;
		do {
			System.out.println("cont=" + cont);
			System.out.println("La posicion "+aux1+" la paso a la posicion " + aux2);
			Cursor c = bd
					.rawQuery(
							"SELECT  fecha_posicion, hora_posicion, latitud, longitud, ruta_foto, id_ruta, tipo_posicion FROM posiciones WHERE id_posicion="
									+ aux1 + "", null);
			if (c.moveToFirst()) {
				System.out.println("existe");
				registro.put("fecha_posicion", c.getString(0));
				registro.put("hora_posicion", c.getString(1));
				registro.put("latitud", c.getString(2));
				registro.put("longitud", c.getString(3));
				registro.put("ruta_foto", c.getString(4));
				registro.put("id_ruta", c.getString(5)); // ruta_archivo
				registro.put("tipo_posicion", c.getString(6));
			}
			
			c.close();
			
			bd.update("posiciones", registro, "id_posicion=" + aux2 + "", null);
			//bd.delete("posiciones", "id_posicion=" + aux1, null);
			System.out.println("Guardamos " + posicionesAGuardar);
			if((aux1-id_pos) >= posicionesAGuardar){
				System.out.println("TOCA BORRAR");
			
				bd.delete("posiciones", "id_posicion=" + aux1, null);
				
			}
		
			
			aux1++;
			aux2++;
			System.out.println(i+"="+cont);
			i++;
			
			
			//getPosiciones();
		} while (i != cont-1 );
		
		getPosiciones();
		bd.close();

	}
	
	
	
	public void borrarVideo(int id) {
		System.out.println("BORRO " + id + "de un total de posiciones: " + cont +"+"+id_pos);
		

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();

		Cursor c = bd
				.rawQuery(
						"SELECT id_posicion, ruta_foto, tipo_posicion FROM posiciones WHERE id_posicion="
								+ id + "", null);

		if (c.moveToFirst()) {

			registro.put("tipo_posicion", "borrar");
			registro.put("ruta_foto", " ");
			bd.update("posiciones", registro, "id_posicion=" + c.getString(0),
					null);

			
			if(!comprobarVideo(c.getString(1))){
				System.out.println("Ya no hay mas de este video: " + c.getString(1) );
				File video = new File(c.getString(1));
				video.delete();
				bd.delete("posiciones", "id_posicion=" +c.getString(0), null);
			}
			

		}

		c.close();
		bd.close();

	}

	public boolean comprobarVideo(String ruta){

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		boolean existe = false;
		Cursor c = bd
				.rawQuery(
						"SELECT id_posicion FROM posiciones WHERE ruta_foto='"
								+ ruta + "'", null);
		
		if(c.moveToFirst()){
			existe = true;
		}
		c.close();
		bd.close();
		return existe;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.principal_video);

		/*---------------------------------------------------------------------*/
		/* LOCALIZACION */

		botonActivar = (Button) findViewById(R.id.boton_activarparar);
		boton_tramo_interes = (Button) findViewById(R.id.boton_tramo_interes);
		botonCoche = (ImageButton) findViewById(R.id.boton_coche);
		boton_perfil = (Button) findViewById(R.id.boton_perfil);
		boton_configuracion = (Button) findViewById(R.id.boton_configuracion);
		boton_historial = (Button) findViewById(R.id.boton_historial);
		/* FOTO */
		fileName = Environment.getExternalStorageDirectory() + "/test.mp4";
		mySurfaceView = (SurfaceView) findViewById(R.id.surface_video);
		SurfaceHolder holder = mySurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// myInflater = LayoutInflater.from(this);
		boton_tramo_interes.setEnabled(false);
		
		
		getPosiciones();

		botonActivar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				
				if (servicioActivo) {
					boton_tramo_interes.setEnabled(false);
					boton_configuracion.setEnabled(true);
					boton_perfil.setEnabled(true);
					boton_historial.setEnabled(true);
					servicioActivo = false;
					recording = false;
					pararServicio();
					modificar = false;
					primera_vez = false;
//					if (cont > contadorPosiciones) {
//						colocar();
//					}

					// cambiarTipo("fijo"); // Cambiar de tipo 'temporal' a
					// 'fijo'

				} else {
					boton_configuracion.setEnabled(false);
					boton_perfil.setEnabled(false);
					boton_historial.setEnabled(false);
					boton_tramo_interes.setEnabled(true);
					
					servicioActivo = true;

					/* Hilo que recoger localizacion */
					miHilo();
					miHilo2();

				}

			}
		});

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	/**
	 * Inicializamos los recursos asociados a las variables para administrar la
	 * grabaci—n y reproducci—n. Se verifica si las variables son nulas (para
	 * ejecutar este c—digo solo una vez) y luego de inicializarlas se coloca el
	 * SurfaceHolder como display para la vista previa de la grabaci—n y para la
	 * vista de la reproducci—n
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setPreviewDisplay(holder.getSurface());
		}

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(holder);
		}
	}

	/**
	 * Liberamos los recursos asociados a las variables para administrar la
	 * grabaci—n y reproducci—n
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mediaRecorder.release();
		mediaPlayer.release();
	}

	/**
	 * MŽtodo para preparar la grabaci—n, configurando los atributos de la
	 * fuente para audio y video, el formado y el codificador.
	 */
	public void prepareRecorder() {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	}

	/* FOTO FIN */

	/* Acceder a la ventana de configuracion de la aplicacion */
	public void irAConfiguracion(View v) {
		Intent i = new Intent(this, Configuracion.class);
		startActivity(i);
	}

	/* Acceder a la ventana del perfil de la aplicacion */
	public void irAPerfil(View v) {
		Intent i = new Intent(this, Perfil.class);
		startActivity(i);
	}

	/* Acceder a la ventana del historial de la aplicacion */
	public void irAHistorial(View v) {
		Intent i = new Intent(this, Historial.class);
		startActivity(i);
	}

	public void pararServicio() {
		// Se para el servicio de localización
		servicioActivo = false;
		change = false;
		manejador.removeUpdates(this);

	}

	public void obtenerLocalizacion() {
		
		// Crea el objeto que gestiona las localizaciones
		manejador = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		// obtiene el mejor proveedor en función del criterio asignado
		// (la mejor precisión posible)
		proveedor = manejador.getBestProvider(c, true);

		// Se activan las notificaciones de localización con los parámetros:
		// proveedor, tiempo mínimo de actualización, distancia mínima,
		// Locationlistener
		manejador.requestLocationUpdates(proveedor, 1000, 1, this);
		// Obtenemos la última posición conocida dada por el proveedor
		Location loc = manejador.getLastKnownLocation(proveedor);

		i++;
		guardarPosicionActual(loc);

	}

	/* Guarda una nueva posicion o modifica en caso de que sea necesario */
	public void guardarPosicionActual(Location loc) {
		if (loc == null) {// Si no se encuentra localización, se mostrará
			// "Desconocida"

		} else {

			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());

			

				guardarPosicion();
		
		}

	}

	/* Guarda posicion */
	public void guardarPosicion() {
		System.out.println("Guardo posicion "+ cont + "con ruta " + ruta_video);
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();
		id_posicion = 1+Integer.valueOf(siguienteIdPosicion());
		System.out.println("ID_POSICION= "+id_posicion);
		registro.put("id_posicion", id_posicion );
		registro.put("fecha_posicion", fecha_posicion);
		registro.put("hora_posicion", hora_posicion);
		registro.put("latitud", latitud);
		registro.put("longitud", longitud);
		registro.put("ruta_foto", ruta_video);
		registro.put("id_ruta", id_ruta); // ruta_archivo
		registro.put("tipo_posicion", tipo_posicion);

		bd.insert("posiciones", null, registro);

		bd.close();

		// System.out.println("Posicion: OK");
		Toast.makeText(this, "Se cargaron los datos de la posicion",
				Toast.LENGTH_SHORT).show();
	}

	/* Obtiene la primera posicion de la tabla cuyo tipo es 'temporal' */
	public int obtenerIdPosicionInsertado() {
		int id_pos = -1;

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		Cursor c = bd
				.rawQuery(
						"select id_posicion from posiciones where tipo_posicion='temporal'",
						null);

		if (c.moveToFirst()) {
			id_pos = Integer.parseInt(c.getString(0));
		} else {
			// System.out.println("CACA");

		}
		bd.close();
		c.close();

		return id_pos;
	}

	public void modificarPosicion(int id_pos) {
		// System.out.println("Entro a modificarPos");
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();
		registro.put("fecha_posicion", fecha_posicion);
		registro.put("hora_posicion", hora_posicion);
		registro.put("latitud", latitud);
		registro.put("longitud", longitud);
		registro.put("ruta_foto", ruta_video);
		registro.put("id_ruta", id_ruta);
		registro.put("tipo_posicion", tipo_posicion);
		int x = bd
				.update("posiciones", registro, "id_posicion=" + id_pos, null);

		if (x == 1) {
			// System.out.println("TODO OK");

		} else {
			// System.out.println("CACA yo id_pos=" + id_pos);

		}
		bd.close();
	}

	/* NO USADO */
	public void consultaPosicion(String id_pos) {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		Cursor fila = bd
				.rawQuery(
						"select id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto,id_ruta  from posiciones where id_posicion="
								+ id_pos + "", null);

		if (fila.moveToFirst()) {

			System.out.println("RESULTADO:");
			System.out.println(fila.getString(0));
			System.out.println(fila.getString(1));
			System.out.println(fila.getString(2));
			System.out.println(fila.getString(3));
			System.out.println(fila.getString(4));
			System.out.println(fila.getString(5));
			System.out.println(fila.getString(6));

		} else
			Toast.makeText(this, "No existe una posicion con dicho id",
					Toast.LENGTH_SHORT).show();
		bd.close();
		fila.close();

	}

	/* NO USADO */
	public void consultaPosicionPorRuta(String id_ruta) {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		System.out.println("ID_RUTA: " + id_ruta);
		Cursor fila = bd
				.rawQuery(
						"select id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto,id_ruta,tipo_posicion  from posiciones where id_ruta="
								+ id_ruta + "", null);

		if (fila.moveToFirst()) {

			do {

				System.out.println("RESULTADO:");
				System.out.println(fila.getString(0));
				System.out.println(fila.getString(1));
				System.out.println(fila.getString(2));
				System.out.println(fila.getString(3));
				System.out.println(fila.getString(4));
				System.out.println(fila.getString(5));
				System.out.println(fila.getString(6));
				System.out.println(fila.getString(7));

			} while (fila.moveToNext());

		} else
			Toast.makeText(this, "No existe una posicion con dicho id",
					Toast.LENGTH_SHORT).show();
		bd.close();
		fila.close();

	}

	public void guardarRuta() {
		System.out.println("NUEVA RUTA");
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();

		obtenerFechaYHora();

		registro.put("nombre_ruta", "Tramo_" + fecha_posicion + "_"
				+ hora_posicion);
		registro.put("fecha_ruta", fecha_posicion);
		registro.put("hora_inicio_ruta", hora_posicion);
		registro.put("modo", modo);

		bd.insert("rutas", null, registro);
		bd.close();

		Toast.makeText(this, "Se cargaron los datos de la ruta",
				Toast.LENGTH_SHORT).show();

		id_ruta = consultaRuta("Tramo_" + fecha_posicion + "_" + hora_posicion);

		System.out.println("Ruta creada: " + id_ruta);

		ruta_video = Environment.getExternalStorageDirectory() + "/Tramo_"
				+ id_ruta + "_video_" + video + ".mp4";

		System.out.println("Ruta de video: " + ruta_video);
	}

	int consultaRuta(String nombre_ruta) {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		Cursor aux = bd.rawQuery(
				"SELECT id_ruta FROM rutas WHERE nombre_ruta='" + nombre_ruta
						+ "' ", null);

		int id = 0;
		if (aux.moveToFirst()) {

			id = aux.getInt(0);

		}

		aux.close();
		bd.close();
		return id;

	}

	/* Muestra todas las posiciones de una ruta */
	public Cursor getPosicionesDeRuta(int id_ruta) {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase db = admin.getReadableDatabase();

		Cursor c = db
				.rawQuery(
						"SELECT id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto FROM posiciones WHERE id_ruta="
								+ id_ruta + " ", null);
		// mostrarLista(c);
		return c;
	}

	/* Muestra todas las posiciones de la tabla */
	public Cursor getPosiciones() {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase db = admin.getReadableDatabase();

		Cursor c = db.query("posiciones", TABLA_POSICIONES, null, null, null,
				null, null);
		mostrarLista(c);
		db.close();
		return c;
	}

	public void borrarPosicion(int pos) {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		int cant = bd.delete("posiciones", "id_posicion=" + pos + "", null);
		bd.close();

	}

	public void mostrarLista(Cursor cursor) {

		int x = 0;

		while (cursor.moveToNext()) {
			System.out.println("nueva pos:");
			for (x = 0; x < 8; x++) {

				System.out.println(cursor.getString(x));
			}

		}

		cursor.close();
	}

	public void obtenerFechaYHora() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

		fecha_posicion = formatoFecha.format(c.getTime());
		hora_posicion = formatoHora.format(c.getTime());

	}

	public static ArrayList<String> cursorToArrayNombres(Cursor c) {
		ArrayList<String> nombres = new ArrayList<String>();

		// Si el cursor contiene datos los añadimos al ArrayList
		if (c.moveToFirst()) {
			do {
				nombres.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
		return nombres;
	}

	public void guardarTramo(View view) {
		interes = true;

		if (servicioActivo) {

			servicioActivo = false;
			recording = false;
			pararServicio();
			modificar = false;
			primera_vez = false;
			cont = 0;
			contadorPosiciones = 0;


		} else {
			servicioActivo = true;

			/* Hilo que recoger localizacion */
			miHilo();
			miHilo2();

		}

		
		
	}

	public void cambiarTipo(String tipo) {

		id_posicion_aux = id_pos;

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();

		Cursor c = bd
				.rawQuery(
						"select id_posicion from posiciones where tipo_posicion='temporal'",
						null);

		if (c.moveToFirst()) {

			do {
				registro.put("tipo_posicion", tipo);
				bd.update("posiciones", registro,
						"id_posicion=" + c.getString(0), null);
			} while (c.moveToNext());

			

		}
		c.close();
		bd.close();
	}

	public void guardarCoche(View v) {
		manejador = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);

		proveedor = manejador.getBestProvider(c, true);

		manejador.requestLocationUpdates(proveedor, 1000, 1, this);

		Location loc = manejador.getLastKnownLocation(proveedor);

		String coche = "";
		coche = coche + loc.getLatitude() + ", " + loc.getLongitude();
		System.out.println(coche);

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/* Metodo para comprobar LatLon */
	public void comprobarLatLon() {

		// Se comprueba que la diferencia entre el nuevo y el anterior no se
		// mayor de x.

	}

}
