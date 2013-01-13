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

public class LibraryActivity extends Activity {
	
	private ArrayList<String> titles = new ArrayList<String>();
	private static ArrayList<EvilTriple> evilTriples; //title, path, id
	private EvilLibraryManager evilLibraryManager;
	private GridView _GridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evil_library);
		// Impossible to set fullscreen in layout xml file, so it is done here
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.evilLibraryManager = 
				new EvilLibraryManager(this.getApplicationContext());

		ArrayList<String> listOfTitles = getEvilBooks();
		
		manageGridView(listOfTitles);
//		
		this._GridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// according to position in the GridView take element from the
				// grid and call for another activity.
				Bundle extras = new Bundle();
				extras.putString("evil_title", LibraryActivity.evilTriples.get(position).getEvilTitle());
				extras.putString("evil_path", LibraryActivity.evilTriples.get(position).getEvilPath());
				extras.putString("evil_id", LibraryActivity.evilTriples.get(position).getEvilId());
				Intent aIntent = new Intent(LibraryActivity.this, 
						com.evilreader.android.evilcontentcontroller.EpubContentActivity.class);
	        	aIntent.putExtras(extras);
	        	startActivity(aIntent);
        		return;	        	
		}});
		
	}
	
	private ArrayList<EvilTriple> getEvilTriples() {
		return this.evilLibraryManager.getTitlePathId();
	}
	
	private ArrayList<String> getEvilBooks() { 
		LibraryActivity.evilTriples = this.getEvilTriples();
		this.titles = new ArrayList<String>();
		for (int i = 0; i < LibraryActivity.evilTriples.size(); i++) {
			this.titles.add(LibraryActivity.evilTriples.get(i).getEvilTitle());
		}
		if (this.titles.get(0).equalsIgnoreCase("NO EVIL BOOKS IN THE LIBRARY")) {
			Log.e("EVILREADER", "NO EVIL BOOKS IN THE LIBRARY");
			evilLibraryManager.refreshListOfEvilBooks();
			LibraryActivity.evilTriples = this.getEvilTriples();
			this.titles = new ArrayList<String>();
			for (int i = 0; i < LibraryActivity.evilTriples.size(); i++) {
				this.titles.add(LibraryActivity.evilTriples.get(i).getEvilTitle());
			}
		}
		return this.titles;
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
        	 //this.evilLibraryManager.saveBookmark();
        	 displayEvilMessage("Import an Evil Book!");
             return true;
         case R.id.menu_refresh_library:
        	 refreshGridView();
        	 return true;
         case R.id.menu_settings:
        	 String notes = this.evilLibraryManager.getBookmarks();
        	 //displayEvilMessage(notes);
        	 displayEvilMessage("Menu settings");
         }
         return false;
	}
	
	/**
	 * Refreshes content that needs to be displayed, and refreshes the grid.
	 */
	private void refreshGridView() {
		evilLibraryManager.refreshListOfEvilBooks();
		ArrayList<String> refreshedContent = getEvilBooks();
		manageGridView(refreshedContent);
		return;
	}
	
	private void manageGridView(ArrayList<String> pContent) {
		this._GridView = (GridView) findViewById(R.id.evil_library_gridview);
		this._GridView.invalidateViews();
		this._GridView.invalidate();
		TextAdapter aTextAdapter = 
				new TextAdapter(getApplicationContext(), pContent);
		aTextAdapter.notifyDataSetChanged();
		this._GridView.setAdapter(aTextAdapter);
		this._GridView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Displays message in a Toast
	 * @param evilMessage
	 */
	private void displayEvilMessage(String evilMessage) {
		Toast.makeText(LibraryActivity.this,
				evilMessage,
		Toast.LENGTH_LONG).show();
		return;
	}

}
