package com.evilreader.android.library;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.evilreader.android.R;

public class EvilLibraryActivity extends Activity {
	
	private ArrayList<String> titles = new ArrayList<String>();
	private static ArrayList<EvilTriple> evilTriples; //title, path, id
	private EvilLibraryManager evilLibraryManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evil_library);
		// Impossible to set fullscreen in layout xml file, so it is done here
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.evilLibraryManager = new EvilLibraryManager(this.getApplicationContext());

		getEvilBooks();
		
		GridView gridview = (GridView) findViewById(R.id.evil_library_gridview);
		TextAdapter aTextAdapter = 
				new TextAdapter(getApplicationContext(), this.titles);
		gridview.setAdapter(aTextAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// according to position take element from the grid and call for another activity.
				Log.e("EVILPAR", "pos: " + position);
				Log.e("EVILPAR", "title" + EvilLibraryActivity.evilTriples.get(position).getEvilTitle());
				Log.e("EVILPAR", "path" + EvilLibraryActivity.evilTriples.get(position).getEvilPath());
				Log.e("EVILPAR", "id" + EvilLibraryActivity.evilTriples.get(position).getEvilId());
				Bundle extras = new Bundle();
				extras.putString("evil_title", EvilLibraryActivity.evilTriples.get(position).getEvilTitle());
				extras.putString("evil_path", EvilLibraryActivity.evilTriples.get(position).getEvilPath());
				extras.putString("evil_id", EvilLibraryActivity.evilTriples.get(position).getEvilId());
				Intent aIntent = new Intent(EvilLibraryActivity.this, com.evilreader.android.TESTActivity.class);
	        	aIntent.putExtras(extras);
	        	startActivity(aIntent);
        		return;	        	
		}});
		
	}
	
	private ArrayList<EvilTriple> getEvilTriples() {
		return this.evilLibraryManager.getTitlePathId();
	}
	
	private void getEvilBooks() { 
		this.evilTriples = this.getEvilTriples();
		this.titles = new ArrayList<String>();
		for (int i = 0; i < this.evilTriples.size(); i++) {
			this.titles.add(this.evilTriples.get(i).getEvilTitle());
		}
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_evil_library, menu);
		return true;
	}
	
	/**
	 * Handles menu button clicks.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
         switch (item.getItemId()) {
         case R.id.menu_import_book:
        	 // do something here because search button is pressed
        	 displayEvilMessage("Import an Evil Book!");
             return true;
         case R.id.menu_refresh_library:
        	 refreshGridView();
        	 return true;
         case R.id.menu_settings:
        	 // TODO(dainius)
        	 displayEvilMessage("Menu settings");
         }
         return false;
	}
	
	/**
	 * Refreshes content that needs to be displayed, and refreshes the grid.
	 */
	private void refreshGridView() {
		// getting new content ------------------------------------------------
		ArrayList<String> refreshedContent;
		evilLibraryManager.refreshListOfEvilBooks();
		getEvilBooks();
		refreshedContent = this.titles;
		// refreshing the gridview---------------------------------------------
		GridView gridview = (GridView) findViewById(R.id.evil_library_gridview);
		gridview.invalidateViews();
		gridview.invalidate();
		TextAdapter aTextAdapter = 
				new TextAdapter(getApplicationContext(), refreshedContent);
		aTextAdapter.notifyDataSetChanged();
		gridview.setAdapter(aTextAdapter);
		gridview.setVisibility(View.VISIBLE);
		// --------------------------------------------------------------------
		return;
	}
	
	/**
	 * Displays message in a Toast
	 * @param evilMessage
	 */
	private void displayEvilMessage(String evilMessage) {
		Toast.makeText(EvilLibraryActivity.this,
				evilMessage,
		Toast.LENGTH_LONG).show();
		return;
	}

}
