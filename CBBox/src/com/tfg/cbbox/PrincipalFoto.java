package com.tfg.cbbox;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.internal.bt;
import com.tfg.cbbox.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PrincipalFoto extends Activity implements LocationListener,
		Callback {

	int id_posicion;
	// Valores definidos en Configuracion

	int intervalo_posiciones = 5; // tiempo transcurrido entre posiciones
	int tiempo_transcurrido = 0; // Variable auxiliar
	int intervalo_tiempo_almacenado = 120; // Ultimos segundos/minutos almacenados
											// VALORES POR DEFECTO

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

	String ruta_foto = "";
	int id_ruta = 0;
	String fecha_posicion = "";
	String hora_posicion = "";
	String latitud = "";
	String longitud = "";
	String tipo_posicion = "temporal";
	String modo = "foto";

	boolean primera_vez = false;
	boolean modificar = false;
	int contador = 0; // Auxiliar ejemplo

	int i = 0;

	//int posicionesAGuardar = intervalo_tiempo_almacenado / intervalo_posiciones; // Valores
	int posicionesAGuardar = 4;																			// guardados
																					// cada
																					// vez
	int contadorPosiciones = 0;

	final Handler mHandler = new Handler();

	/* CAMARA FOTOS */
	private LayoutInflater myInflater = null;
	Camera myCamera;
	byte[] tempdata;
	boolean myPreviewRunning = false;
	private SurfaceHolder mySurfaceHolder;
	private SurfaceView mySurfaceView;
	Button takePicture;
	Button boton_tramo_interes;
	Button boton_configuracion, boton_perfil, boton_historial;

	/* :( */
	int cont_fotos = 0;
	int cont_pos = 0;
	int cont = 0;
	boolean interes = false;

	/* HILO */
	protected void miHilo() {

		Thread t = new Thread() {
			public void run() {
			
				while (servicioActivo) {

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
				
			
				if(interes){
	
			servicioActivo = true;
					miHilo();
					interes = false;
				}

			}

		};

		t.start();

	}

	/* ACCION DEL HILO */
	/* Obtener localizacion */
	final Runnable ejecutarAccion = new Runnable() {
		public void run() {

			Calendar calendario;
			int seg;
			calendario = Calendar.getInstance();
			seg = calendario.get(Calendar.SECOND);
System.out.println("antes");
			myCamera.takePicture(myShutterCallback, myPictureCallback, myJpeg);
			System.out.println("despues");
		



		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.principal_foto);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);


		/*---------------------------------------------------------------------*/
		/* LOCALIZACION */

		botonActivar = (Button) findViewById(R.id.boton_activarparar);
		boton_tramo_interes = (Button) findViewById(R.id.boton_tramo_interes);
		boton_configuracion = (Button) findViewById(R.id.boton_configuracion);
		boton_perfil = (Button) findViewById(R.id.boton_perfil);
		boton_historial = (Button) findViewById(R.id.boton_historial);
		
		boton_tramo_interes.setEnabled(false);

	
		/* FOTO */
		System.out.println("Foto");

		mySurfaceView = (SurfaceView) findViewById(R.id.surface_foto);
		mySurfaceHolder = mySurfaceView.getHolder();
		mySurfaceHolder.addCallback(this);
		mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		myInflater = LayoutInflater.from(this);
		System.out.println("Foto");
		botonActivar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {


				if (servicioActivo) {
					boton_tramo_interes.setEnabled(false);
					boton_configuracion.setEnabled(true);
					boton_historial.setEnabled(true);
					boton_perfil.setEnabled(true);

					servicioActivo = false;
					pararServicio();
					modificar = false;
					primera_vez = false;
					
					//cambiarTipo("final"); // Cambiar de tipo 'temporal' a 'guardado'
					if(interes){
						System.out.println("Tramo de itneresssss");
						cambiarTipo("tramo de interes");
						interes=false;
						
						System.out.println("VUEVLO!");
						/* Hilo que recoger localizacion */
						//miHilo();
						
					}else{
						
						
						cambiarTipo("final");
					}
			

				} else {
					System.out.println("ACTIVO");
					boton_tramo_interes.setEnabled(true);
					boton_configuracion.setEnabled(false);
					boton_historial.setEnabled(false);
					boton_perfil.setEnabled(false);
					servicioActivo = true;
					System.out.println("Foto");
					/* Hilo que recoger localizacion */
					miHilo();
				}
				

			}
		});

	}
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
	
	public void guardarTramo(View view) {
		interes = true;
		System.out.println("GUARDAR TRAMO");
		if (servicioActivo) {
			
			System.out.println("LO PARO");
			servicioActivo = false;
		
			pararServicio();
			cambiarTipo("tramo de interes");
			modificar = false;
			primera_vez = false;
			cont = 0;
			contadorPosiciones = 0;
			
			} //else {
//			System.out.println("ENTRO?");
//			servicioActivo = true;
//
//			/* Hilo que recoger localizacion */
//			miHilo();
//			
//		}

		
		
	}

	/* FOTO */

	ShutterCallback myShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};

	PictureCallback myPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera myCamera) {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback myJpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera myCamera) {
			// TODO Auto-generated method stub
			if (data != null) {
				tempdata = data;
				try {
					fotoHecha();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	/* Una vez hecha la foto CAMBIAR UBICACION */
	void fotoHecha() throws IOException {

		String path = "/sdcard/CBBox_" + i + ".jpg";
		Bitmap bm = BitmapFactory.decodeByteArray(tempdata, 0, tempdata.length);
		String uri = Images.Media.insertImage(getContentResolver(), bm,
				"CBBox_" + fecha_posicion + "_" + hora_posicion, null);

		System.out.println("URI:" + uri);
		 
		ruta_foto = uri;
		
		
		Uri url = Uri.parse(uri);
		
		System.out.println("URL:" + url);

		bm.recycle();

		myCamera.startPreview();
		
		
		
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

			//borrarVideo(id_pos + contadorPosiciones);
	
			

			obtenerLocalizacion();

			cont++;
			colocar();
			id_pos = obtenerIdPosicionInsertado();


		}
	}

	public void colocar() {
		System.out.println("COLOCoooooooooooo");
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

	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

//		
		
		if (myPreviewRunning) {
			myCamera.stopPreview();
			myPreviewRunning = false;
		}
		Camera.Parameters parameters = myCamera.getParameters();
	    List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

	    // You need to choose the most appropriate previewSize for your app
	    Camera.Size previewSize = previewSizes.get(0);

	    parameters.setPreviewSize(previewSize.width, previewSize.height);
	    myCamera.setParameters(parameters);
	    try {
			myCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    myCamera.startPreview();
	    myPreviewRunning = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		myCamera.stopPreview();
		myPreviewRunning = false;
		myCamera.release();
		myCamera = null;
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

		// Se desactivan las notificaciones
		manejador.removeUpdates(this);

	}

	public void obtenerLocalizacion() {
		// Se activa el servicio de localización

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
	/* Guarda una nueva posicion o modifica en caso de que sea necesario*/
	public void guardarPosicionActual(Location loc) {
		if (loc == null) {// Si no se encuentra localización, se mostrará
			// "Desconocida"

		} else {

			latitud = String.valueOf(loc.getLatitude());
			longitud = String.valueOf(loc.getLongitude());

			

				guardarPosicion();
		
		}
	}

	/* Guarda posicion*/
	public void guardarPosicion() {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();
		id_posicion = 1+Integer.valueOf(siguienteIdPosicion());
		registro.put("id_posicion", id_posicion);
		registro.put("fecha_posicion", fecha_posicion);
		registro.put("hora_posicion", hora_posicion);
		registro.put("latitud", latitud);
		registro.put("longitud", longitud);
		registro.put("ruta_foto", ruta_foto);
		registro.put("id_ruta", id_ruta);
		registro.put("tipo_posicion", tipo_posicion);
	

		bd.insert("posiciones", null, registro);

		bd.close();

	System.out.println("Posicion: OK");
	System.out.println("RUTA: " + ruta_foto);
		Toast.makeText(this, "Se cargaron los datos de la posicion",
				Toast.LENGTH_SHORT).show();
	}

	
	/* Obtiene la primera posicion de la tabla cuyo tipo es 'temporal'*/
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
//			System.out.println("CACA");

		}
		bd.close();
		c.close();

		return id_pos;
	}

	public void modificarPosicion(int id_pos) {
//		System.out.println("Entro a modificarPos");
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		ContentValues registro = new ContentValues();
		registro.put("fecha_posicion", fecha_posicion);
		registro.put("hora_posicion", hora_posicion);
		registro.put("latitud", latitud);
		registro.put("longitud", longitud);
		registro.put("ruta_foto", "JEJE");
		registro.put("id_ruta", id_ruta);
		registro.put("tipo_posicion", tipo_posicion);
		
		int x = bd
				.update("posiciones", registro, "id_posicion=" + id_pos, null);

		if (x == 1) {
//			System.out.println("TODO OK");

		} else {
//			System.out.println("CACA yo id_pos=" + id_pos);

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

	
	/* NO USADO*/
	public void consultaPosicionPorRuta(String id_ruta) {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		System.out.println("ID_RUTA: " + id_ruta);
		Cursor fila = bd
				.rawQuery(
						"select id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto,id_ruta  from posiciones where id_ruta="
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

		
	}
	int consultaRuta(String nombre_ruta) {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		
		Cursor aux = bd.rawQuery(
				"SELECT id_ruta FROM rutas WHERE nombre_ruta='"
						+ nombre_ruta + "' ", null);
		
		int id=0;
		if(aux.moveToFirst()){

			id = aux.getInt(0);

		}
		
		
		

		
		aux.close();
		bd.close();
		return id;

	}

	/* Muestra todas las posiciones de una ruta */
	public Cursor getPosicionesDeRuta(int id_ruta) {
		System.out.println("Entro en getPosicionesDeRuta");
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase db = admin.getReadableDatabase();

		Cursor c = db
				.rawQuery(
						"SELECT id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto, tipo_posicion FROM posiciones WHERE id_ruta="
								+ id_ruta + " ", null);
		 mostrarLista(c);
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
	
	/* Metodo para comprobar LatLon*/
	public void comprobarLatLon(){
		
		//Se comprueba que la diferencia entre el nuevo y el anterior no se mayor de x.
		
		
	}

}
