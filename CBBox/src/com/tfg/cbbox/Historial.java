package com.tfg.cbbox;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tfg.cbbox.R;



public class Historial extends android.support.v4.app.FragmentActivity {

	ListView listaRutas, listaPos;
	ListView lv;
	Button boton_mapa;
	String irAruta="";
	String modo_aux = "";
	private static final String[] RUTAS_CLAVES = new String[] { "id_ruta",
			"nombre_ruta", "fecha_ruta", "hora_inicio_ruta" , "modo"};
	/*private static final String[] POSICIONES_CLAVES = new String[] {
			"id_posicion", "fecha_posicion", "hora_posicion", "latitud",
			"longitud", "ruta_foto", "id_ruta" };
*/
	private GoogleMap mMap;


    
    private void drawPolilyne(PolylineOptions options){
    	mMap.clear();
        Polyline polyline = mMap.addPolyline(options);	
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historial);

		setUpMapIfNeeded(); //Para 1 punto

		//drawPolilyne(POLILINEA); // Ruta
		//consultaPosicionPorRuta("0");
		
		// rutaEjemplo();
		//consultaPosicionPorRuta("3");
		if(AdminSQLite.historialVacio(this)){
			System.out.println("vacia");
			Toast.makeText(this, "No hay datos almacenados", Toast.LENGTH_LONG).show();
		}else{
			System.out.println("no vacia");
			mostrarLista();
		}
		
		// borrarTodoPos();
		// mostrarListaPosiciones("1");
	}
	/* MAPAS*/
	@Override
	protected void onResume() {
		super.onResume();
	//	setUpMapIfNeeded();
	}

	public void crearPolilinea(){
		
		
		
		
	}
	
	private void setUpMapIfNeeded() {
		// Si el nMap esta null entonces es porque no se instancio el mapa.
		if (mMap == null) {
			// Intenta obtener el mapa del SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Comprueba si hemos tenido Ã©xito en la obtenciÃ³n del mapa.
			if (mMap != null) {
				//setUpMap();
			}
		}
	}
	
	private void setUpMap() {
	       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	    	 mMap.addMarker (new MarkerOptions()
	         .position(new LatLng(41.40338, 2.17403))
	         .title("Estoy aqui!"));
	    	 
	    	 
	    	// Location location = mLocationClient.getLastLocation();
	    	    LatLng latLng = new LatLng(41.40338, 2.17403);
	    	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
	    	    mMap.animateCamera(cameraUpdate);
	    	    
	    	  
	 }
	/* FIN MAPAS */

	public Cursor getRutas() {
		System.out.println("Entro en getRutas");
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getReadableDatabase();

		Cursor c = bd
				.query("rutas", RUTAS_CLAVES, null, null, null, null, null);
	c.moveToFirst();
		System.out.println(c.getString(0));
		//mostrarLista(c);
		bd.close();
		return c;
	}

	 private ArrayList<ItemTramo> obtenerItems() {
	    	ArrayList<ItemTramo> items = new ArrayList<ItemTramo>();
	    	System.out.println("entro en items");
	    	Cursor tramos = getRutas();
	    	
	    	if(tramos.moveToFirst()){
	    		System.out.println("hay cosas!");
	    		do{
	    		tramos.getString(0);
	    		tramos.getString(1);
	    		tramos.getString(2);
	    		System.out.println("A ver: " + tramos.getString(0));
	    		System.out.println("A ver: " + tramos.getString(1));
	    		System.out.println("A ver: " + tramos.getString(2));
	    		items.add(new ItemTramo(Long.parseLong(tramos.getString(0)), tramos.getString(1)));
	    		
	    		}while(tramos.moveToNext());
	    		
	    	}
	    	tramos.close();
	    	
	    	return items;
	    }
	
	 

	/* Mostrar lista de rutas */
	public void mostrarLista() {

		ArrayList lista = new ArrayList<String>();
		
			lv = (ListView)findViewById(R.id.lista_rutas);
	        
	        final ArrayList<ItemTramo> itemsCompra = obtenerItems();
	        
	        final ItemTramoAdapter adapter = new ItemTramoAdapter(this, itemsCompra);
	        
	        lv.setAdapter(adapter);

	        System.out.println("ey q pa");
	       
	    	lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int posicion, long id) {
					System.out.println("WEEEEEEE!" + posicion + " id " + id);
	
					
					System.out.println(lv.getItemAtPosition(posicion));
					System.out.println("..."
							+ lv.getItemIdAtPosition(posicion));
					mostrarListaPosiciones((int)lv.getItemIdAtPosition(posicion));
					
					
					//////
					irAruta = String.valueOf(itemsCompra.get(posicion).getId_tramo());
					System.out.println(itemsCompra.get(posicion).getId_tramo());					
					
				}
			});
	       
	}

	/* Mostrar lista de rutas */
	public void mostrarListaPos(Cursor cursor) {

		ArrayList lista = new ArrayList<String>();

		lista = cursorToArrayNombres(cursor);
		// listausuarios = (TextView) findViewById(R.id.list);FUNCIONA!!
		// listausuarios.setText(string);// FUNCIONA!!

		ListAdapter adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, lista);

		//listaPos = (ListView) findViewById(R.id.lista_pos);
		listaPos.setAdapter(adaptador);

		listaPos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int posicion, long id) {
				System.out.println("WEEEEEEE!" + posicion + " id " + id);

				System.out.println(listaPos.getItemAtPosition(posicion));
				
			}
		});

	}

	public static ArrayList<String> cursorToArrayNombres(Cursor c) {
		ArrayList<String> nombres = new ArrayList<String>();

		// Si el cursor contiene datos los añadimos al ArrayList
		if (c.moveToFirst()) {
			do {
				nombres.add(c.getString(1));
				System.out.println("A ver que hay.. " + c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
		return nombres;
	}

	/* NO VALE: añadir ruta de ejemplo para pruebas */
	public void rutaEjemplo() {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getReadableDatabase();

		ContentValues registro = new ContentValues();
		registro.put("nombre_ruta", "prueba3");
		registro.put("fecha_ruta", "fecha prueba3");
		registro.put("hora_inicio_ruta", "hora  prueba3");

		bd.insert("rutas", null, registro);
		bd.close();
	}

	/* Borra toda la tabla */
	public void borrarTodo() {
		AdminSQLite admin = new AdminSQLite(this,
		"administracion", null, 1);
		SQLiteDatabase bd = admin.getReadableDatabase();

		bd.delete("rutas", null, null);
		bd.close();
	}

	/* Borra toda la tabla */
	public void borrarTodoPos() {

		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getReadableDatabase();

		bd.delete("posiciones", null, null);
		bd.close();
	}

	/* Mostrar posiciones de una ruta dado su nombre */
	public void mostrarListaPosiciones(int i) {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		System.out.println("id_ruta: " + i);
		Cursor aux = bd.query("rutas", RUTAS_CLAVES, "id_ruta="
				+ i + "", null, null, null, null);

		if (aux.moveToFirst()) {
			System.out.println("id buscado: " + aux.getString(0));
			System.out.println(aux.getString(4));
		modo_aux = aux.getString(4);

		String o = aux.getString(0);
		System.out.println("O: " + o);
		consultaPosicionPorRuta(o);
		}
		aux.close();
		bd.close();
	
		
	}

	public void consultaPosicionPorRuta(String id_ruta) {
		AdminSQLite admin = new AdminSQLite(this,
				"administracion", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		System.out.println("ID_RUTA: " + id_ruta);
		Cursor fila = bd
				.rawQuery(
						"select id_posicion,fecha_posicion,hora_posicion,latitud,longitud,ruta_foto,id_ruta,tipo_posicion  from posiciones where id_ruta="
								+ id_ruta + "", null);
		System.out.println(":)");
		
		if (fila.moveToFirst()) {
			PolylineOptions POLILINEA = new PolylineOptions();
			do {

				System.out.println("RESULTADO:");
				System.out.println("log " + fila.getString(0));
				System.out.println("log " +fila.getString(1));
				System.out.println("log " +fila.getString(2));
				System.out.println("log " +fila.getString(3));
				System.out.println("log " +fila.getString(4));
				System.out.println("log " +fila.getString(5));
				System.out.println("log " +fila.getString(6));
				System.out.println("log " +fila.getString(7));
				
				POLILINEA.add(new LatLng(Double.parseDouble(fila.getString(3)),Double.parseDouble(fila.getString(4))));
				drawPolilyne(POLILINEA);
				
				 mMap.addMarker (new MarkerOptions()
		         .position(new LatLng(Double.parseDouble(fila.getString(3)),Double.parseDouble(fila.getString(4))))
		         .title("Estoy aqui!"));
				 CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(fila.getString(3)),Double.parseDouble(fila.getString(4))), 17);
		    	    mMap.animateCamera(cameraUpdate);
				
			} while (fila.moveToNext());

		} else
			Toast.makeText(this, "Lo sentimos, la información no fue guardada correctamente.",
					Toast.LENGTH_SHORT).show();
		bd.close();
		fila.close();

	}
	
	
	
	// Va a un sitio u otro dependiendo de si ha guardado fotos o videos.
	public void irAArchivos(View view){
		System.out.println("Entro");
		
		if(modo_aux.equals("foto")){
			System.out.println("es una fooooto");
			Intent i= new Intent(this, ArchivosFoto.class);
			i.putExtra("id_ruta_pasada", irAruta);
			startActivity(i);
		}
		
		if(modo_aux.equals("video")){
			System.out.println("es un viiiideo");
			Intent i= new Intent(this, ArchivosVideo.class);
			i.putExtra("id_ruta_pasada", irAruta);
			startActivity(i);
		}
		
		
		System.out.println("nada?");
	}
	
	/* Ir a Principal */
	public void irAPrincipal(View v){
		
		Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
	}
}
