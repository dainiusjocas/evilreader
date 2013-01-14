package com.evilreader.android.library;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
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
	private int whichItemIsSelected = -1;

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
		registerForContextMenu(this._GridView);
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
		
//		this._GridView.setOnItemLongClickListener(new OnItemLongClickListener() {
//			public boolean onItemLongClick(AdapterView<?> parent, View v, int position,long id)
//		    {
//				displayEvilMessage("This is a LongClick" + position);
//				return true;
//		    }
//		});
		
	}
	
	/**
	 * Scans database for information about ebooks available.
	 * @return
	 */
	private ArrayList<EvilTriple> getEvilTriples() {
		return this.evilLibraryManager.getTitlePathId();
	}
	
	/**
	 * Takes current info about ebooks from db. If at the beginning there were
	 * no info, then scans sdcard directory for books. 
	 * @return ArrayList<String> titles for grid view
	 */
	private ArrayList<String> getEvilBooks() { 
		LibraryActivity.evilTriples = this.getEvilTriples();
		this.titles = new ArrayList<String>();
		for (int i = 0; i < LibraryActivity.evilTriples.size(); i++) {
			this.titles.add(LibraryActivity.evilTriples.get(i).getEvilTitle());
		}
		if (this.titles.get(0).equalsIgnoreCase("" + R.string.no_books)) {
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
        	 displayEvilMessage("Import an Evil Book!");
             return true;
         case R.id.menu_refresh_library:
        	 refreshGridView();
        	 return true;
         case R.id.menu_settings:
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(R.string.evil_context_menu);
	    inflater.inflate(R.menu.export_context_menu, menu);
	    AdapterView.AdapterContextMenuInfo info =
	            (AdapterView.AdapterContextMenuInfo) menuInfo;
	    this.whichItemIsSelected = info.position;
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		EvilExporter anEvilExporter = new EvilExporter(this);
		String aBookId = LibraryActivity.evilTriples.
				get(this.whichItemIsSelected).getEvilId();
		switch (item.getItemId()) {
		case R.id.export_notes:
			anEvilExporter.exportEvilNotes(aBookId);
			break;
		case R.id.export_highlights:
			anEvilExporter.exportEvilHighlights(aBookId);
			break;
		case R.id.export_notes_and_highlights:
			anEvilExporter.exportEvilNotesAndHighlights(aBookId);
			break;
		}
		return super.onContextItemSelected(item);
	}

}
