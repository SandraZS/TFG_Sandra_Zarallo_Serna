package com.tfg.cbbox;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tfg.cbbox.R;


public class ItemTramoAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<ItemTramo> items;
	
	
	public ItemTramoAdapter(Activity activity, ArrayList<ItemTramo> items) {
		this.activity = activity;
		this.items = items;
	}


	public int getCount() {
		return items.size();
	}


	public Object getItem(int position) {
		return items.get(position);
	}



	public long getItemId(int position) {
		return items.get(position).getId_tramo();
	}



	public View getView(final int position, View convertView, final ViewGroup parent) {
		View vi=convertView;
		
        if(convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	vi = inflater.inflate(R.layout.item_tramos, null);
        	
        	
        }
            
        ItemTramo item = items.get(position);

        

        
        TextView nombre = (TextView) vi.findViewById(R.id.nombre);
        nombre.setText(item.getNombre());
        
        TextView tipo = (TextView) vi.findViewById(R.id.tipo);
        tipo.setText(String.valueOf(item.getId_tramo()));

        return vi;
	}
}