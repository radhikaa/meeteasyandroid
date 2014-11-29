package com.example.meeteasy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meeteasy.model.Contact;
import com.google.android.gms.ads.doubleclick.c;

public class ContactsAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater=null;
	public Resources res;
	Contact tempValues=null;
	EditText selectedContacts=null;
	int i=0;

	public ContactsAdapter(Activity a, ArrayList d,Resources resLocal) {
		activity = a;
		data=d;
		res = resLocal;
		inflater = ( LayoutInflater )activity.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectedContacts = (EditText) a.findViewById(R.id.editText_contacts);
	}

	@Override
	public int getCount() {
		if(data.size()<=0)
			return 1;
		return data.size();
	}

	@Override
	public Object getItem(int position) {       
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder{
		public TextView text;
		public ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		if(convertView==null){
			vi = inflater.inflate(R.layout.contact_item_view, null);
			holder = new ViewHolder();
			holder.text = (TextView) vi.findViewById(R.id.text);
			holder.image=(ImageView)vi.findViewById(R.id.image);
			vi.setTag( holder );
		}
		else {
			holder=(ViewHolder)vi.getTag();
		}
		if(data.size()<=0) {
			holder.text.setText("No Data");

		}
		else {
			tempValues=null;
			tempValues = ( Contact ) data.get(position);
			holder.text.setText(tempValues.getName()+"("+tempValues.getUserPhoneNumber()+")");
			holder.text.setTextColor(Color.BLUE);
			holder.text.setTextSize(20);
			if (tempValues.getImage() != null) {
				holder.image.setImageBitmap(tempValues.getImage());
			}
			vi.setOnClickListener(new OnItemClickListener(position));
		}
		return vi;
	}
	private class OnItemClickListener  implements OnClickListener{           
		private int mPosition;
		ViewHolder holder;
		OnItemClickListener(int position){
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {
			holder=(ViewHolder)arg0.getTag();
			String currentSelection = selectedContacts.getText().toString();
			if (currentSelection == null) {
				currentSelection = "";
			}
			currentSelection += holder.text.getText() + ";";
			selectedContacts.setText(currentSelection);
			notifyDataSetChanged();
		}               
	}   

}