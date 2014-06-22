package com.tfg.cbbox;

import java.util.ArrayList;



import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tfg.cbbox.R;

public class ItemArchivoAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<ItemArchivo> items;
	
	
	public ItemArchivoAdapter(Activity activity, ArrayList<ItemArchivo> items) {
		this.activity = activity;
		this.items = items;
	}


	public int getCount() {
		return items.size();
	}


	public Object getItem(int position) {
		return items.get(position);
	}



	public String getItemNombre(int position) {
		return items.get(position).getNombre();
	}



	public View getView(final int position, View convertView, final ViewGroup parent) {
		View vi=convertView;
		
        if(convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	vi = inflater.inflate(R.layout.item_archivo, null);
        	
        	
        }
            
        ItemArchivo item = items.get(position);

        

        
        TextView nombre = (TextView) vi.findViewById(R.id.nombre);
        nombre.setText(item.getNombre());
        
        TextView tipo = (TextView) vi.findViewById(R.id.tipo);
        tipo.setText(item.getTipo());
        
        
        
        ImageView image = (ImageView) vi.findViewById(R.id.imagen);
       // int imageResource = activity.getResources().getIdentifier(item.getRuta(), null, activity.getPackageName());
        //image.setImageDrawable(activity.getResources().getDrawable(imageResource));
        System.out.println("ea"+item.getRuta());
        image.setImageURI(Uri.parse(item.getRuta()));
        
        return vi;
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}
