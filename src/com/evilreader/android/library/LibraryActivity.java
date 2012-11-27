package com.evilreader.android.library;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
import com.evilreader.android.dbcontroller.DBAdapter;

public class LibraryActivity extends Activity {
	/* Database integration */
	private DBAdapter mDbAdapter;
	
	/* Evil Library Manager */
	private EvilLibraryManager _EvilLibraryManager;
	
	private static Context context;
	private Bundle _SavedInstanceState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this._SavedInstanceState = savedInstanceState;
		// set or create base directory for ebooks
		this._EvilLibraryManager = EvilLibraryManager.getInstance(this);
		ArrayList<String> fileNames = new ArrayList<String>();
		fileNames = this._EvilLibraryManager.getListOfEvilBooksFromDB();
		// TODO(dainius): code which handles the situation when no books are available
		ArrayList<String> test = new ArrayList<String>();
		test.addAll(fileNames);
		LibraryActivity.context = getApplicationContext();
		setContentView(R.layout.activity_library);
		// Impossible to set fullscreen in layout xml file, so it is done here
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new TextAdapter(this, test));
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(LibraryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
		
		//gridview.setAdapter(new ImageAdapter(this));
		
/*		gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	Intent mIntent = new Intent(LibraryActivity.this, com.evilreader.android.epubcontentcontrol.EpubContentActivity.class);
				startActivity(mIntent);
	            //Toast.makeText(LibraryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });*/

	}
	
	/**
	 * Get context of this activity
	 * @return
	 */
	public static Context getAppContext() {
        return LibraryActivity.context;
    }
	
	public void displayAllImages() {
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setVisibility(View.VISIBLE);
		//v.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_library, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
         switch (item.getItemId()) {
         case R.id.search_book:
        	 // do something here because search button is pressed
        	 displayEvilMessage("Search for Evil Books!");
             return true;
         case R.id.refresh_library:
        	 // TODO(dainius): scan evil library, put books to the db, fill the grid, show the grid
        	 // refresh button is pressed
        	 this._EvilLibraryManager.refreshListOfEvilBooks();
        	 this.displayEvilBooks();
        	 //displayAllImages(); // make visible images
        	 displayEvilMessage("Refresh Evil Library!");
        	 return true;
         }
         return false;
	}
	
	public void displayEvilBooks() {
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.invalidateViews();
		ArrayList<String> mEvilBooks = 
				this._EvilLibraryManager.getListOfEvilBooksFromDB();
		TextAdapter mTextAdapter = new TextAdapter(this, mEvilBooks);
		mTextAdapter.notifyDataSetChanged();
		gridview.setAdapter(mTextAdapter);
		gridview.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Invokes a Toast with specific message
	 * @param message
	 */
	public void displayEvilMessage(String message) {
		Context context = getApplicationContext();
   	 	CharSequence text = message;
   	 	int duration = Toast.LENGTH_SHORT;

   	 	Toast toast = Toast.makeText(context, text, duration);
   	 	toast.show();
	}
	
}
