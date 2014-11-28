package com.example.meeteasy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	   private LayoutInflater inflater;
	  private ArrayList<CustomObject> objects;

	   private class ViewHolder {
	      TextView textView1;
	      TextView textView2;
	   }

	   public CustomAdapter(Context context, ArrayList<CustomObject> objects) {
	      inflater = LayoutInflater.from(context);
	      this.objects = objects;
	   }

	   public int getCount() {
	      return objects.size();
	   }

	   public CustomObject getItem(int position) {
	      return objects.get(position);
	   }

	   public long getItemId(int position) {
	      return position;
	   }

	   public View getView(int position, View convertView, ViewGroup parent) {
	      ViewHolder holder = null;
	      if(convertView == null) {
	         holder = new ViewHolder();
	         convertView = inflater.inflate(R.layout.your_view_layout, null);
	         holder.textView1 = (TextView) convertView.findViewById(R.id.id_textView1);
	        holder.textView2 = (TextView) convertView.findViewById(R.id.list_id_textView2);
	         convertView.setTag(holder);
	      } else {
	         holder = (ViewHolder) convertView.getTag();
	      }
	      holder.textView1.setText(objects.get(position).getprop1());
	      holder.textView2.setText(objects.get(position).getprop2());
	      return convertView;
	   }
	}