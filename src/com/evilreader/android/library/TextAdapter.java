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
 * Purpose of this class is to fill grid with textual information. The only
 * thing that is needed for this class is ArrayList<String> of textual
 * information. And also context of the application.
 * 
 * @author Dainius
 *
 */
public class TextAdapter extends BaseAdapter {
	
	private Context _Context;
	private ArrayList<String> _Titles;
	
	/**
	 * Contructor
	 * 
	 * @param pContext
	 */
	public TextAdapter(Context pContext, ArrayList<String> pFiles) {
		this._Context = pContext;
		this._Titles = pFiles;
	}

	public int getCount() {
		return this._Titles.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
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
		textView.setText(this._Titles.get(position));
		
		return textView;
	}
	
	/**
	 * Gets title from list of titles by position in the list.
	 * 
	 * @param pPosition
	 * @return
	 */
	public String getTitle(int pPosition) {
		String aTitle = this._Titles.get(pPosition);
		return aTitle;
	}

}
