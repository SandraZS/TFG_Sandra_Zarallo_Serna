package com.tfg.cbbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tfg.cbbox.R;

public class Coche extends android.support.v4.app.FragmentActivity {
	private GoogleMap mMap;
	public static final PolylineOptions POLILINEA = new PolylineOptions();

 

//	private void drawPolilyne(PolylineOptions options) {
//
//		Polyline polyline = mMap.addPolyline(options);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coche);

		setUpMapIfNeeded();

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
			// Comprueba si hemos tenido éxito en la obtención del mapa.
			if (mMap != null) {
				setUpMap();
			}
		}
	}
	
	private void setUpMap() { //SOLO MUESTRA DONDE ESTA EL COCHE!!!
	       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	    	 mMap.addMarker (new MarkerOptions()
	         .position(new LatLng(41.40338, 2.17403))
	         .title("Mi coche"));
	    	 
	    	 
	    	// Location location = mLocationClient.getLastLocation();
	    	    LatLng latLng = new LatLng(41.40338, 2.17403);
	    	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
	    	    mMap.animateCamera(cameraUpdate);
//	    	    POLILINEA.add(new LatLng(41.40347, 2.17432))
//	    	   // .add(new LatLng(41.40691, 2.16864))
//	    	    .add(new LatLng(41.40364, 2.16437));
//	    	   
//				drawPolilyne(POLILINEA);
//				
//				 mMap.addMarker (new MarkerOptions()
//		         .position(new LatLng(41.40691, 2.16864))
//		         .title("Estoy aqui!"));
//				 CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(41.40691, 2.16864), 17);
//		    	    mMap.animateCamera(cameraUpdate);
//	    	    
	    	
	    	    
	    	  
	 }
	/* FIN MAPAS */
	/* Ir a Principal */
	public void irAPrincipal(View v){
		
		Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
	}

}
