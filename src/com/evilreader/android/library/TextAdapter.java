/**
 * 
 */
package com.evilreader.android.library;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * @author Dainius
 *
 */
public class TextAdapter extends BaseAdapter {
	
	private Context _Context;
	private ArrayList<String> _Files;
	
	/**
	 * Contructor
	 * 
	 * @param context
	 */
	public TextAdapter(Context context, ArrayList<String> files) {
		this._Context = context;
		this._Files = files;
	}

	public int getCount() {
		return this._Files.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		if (convertView == null) {
			textView = new TextView(_Context);
			textView.setLayoutParams(new GridView.LayoutParams(200, 200));
			//textView.setPadding(8, 8, 8, 8);
		} else {
			textView = (TextView) convertView;
		}
		textView.setText(this._Files.get(position));
		
		return textView;
	}

}
