package com.evilreader.android.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.evilreader.android.R;
import com.evilreader.android.dbcontroller.DBAdapter;

public class LibraryActivity extends Activity {
	/* Database integration */
	private DBAdapter mDbAdapter;
	
	/* Evil Library Manager */
	private static EvilLibraryManager _EvilLibraryManager;
	
	private static Context _Context;
	private Bundle _SavedInstanceState;
	private static TextAdapter _TextAdapter;
	private static HashMap<String, String> _TitlesAndAbsolutePaths;
	private static ArrayList<String> _Titles = new ArrayList<String>();
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LibraryActivity.setContext(getApplicationContext());
		this._SavedInstanceState = savedInstanceState;
		LibraryActivity.setEvilLibraryManager(new EvilLibraryManager(this));
		
		HashMap<String, String> aHashMapOfTitlesAndPaths = 
				LibraryActivity._EvilLibraryManager.getTitleAndPathHashMap();
		LibraryActivity.setTitlesAndAbsolutePaths(aHashMapOfTitlesAndPaths);
		
		LibraryActivity.setTitles(LibraryActivity.getTitlesFromHashMapOfTitlesAndPaths());
		
		setContentView(R.layout.activity_library);
		
		// Impossible to set fullscreen in layout xml file, so it is done here
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		TextAdapter aTextAdapter = 
				new TextAdapter(LibraryActivity.getAppContext(),
						LibraryActivity.getTitles());
		
		LibraryActivity.setTextAdapter(aTextAdapter);
		gridview.setAdapter(LibraryActivity.getTextAdapter());
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	// here is the place to react when item is pressed
	        	String aTitle = 
	        			LibraryActivity.getTextAdapter().getTitle(position);
	        	String aPath = LibraryActivity._TitlesAndAbsolutePaths.get(aTitle);
	        	Log.e("EVILREADER", aPath);
	        	Bundle extras = new Bundle();
	        	extras.putString("absolute_path", aPath);
	        	Intent aIntent = new Intent(LibraryActivity.this, com.evilreader.android.MainActivity.class);
	        	aIntent.putExtras(extras);
	        	startActivity(aIntent);
	        	//Toast.makeText(LibraryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
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
	private static void setTitles(Set<String> pTitles) {
		LibraryActivity._Titles.addAll(pTitles);
	}
	private static ArrayList<String> getTitles() {
		return LibraryActivity._Titles;
	}
	private static Set<String> getTitlesFromHashMapOfTitlesAndPaths() {
		Set<String> titles = LibraryActivity._TitlesAndAbsolutePaths.keySet();
		return titles;
	}
	private static void setContext(Context pContext) {
		LibraryActivity._Context = pContext;
	}
	private static void setEvilLibraryManager(EvilLibraryManager pEvilLibraryManager) {
		LibraryActivity._EvilLibraryManager = pEvilLibraryManager;
	}
	private static void setTitlesAndAbsolutePaths(HashMap<String, String> pTitlesAndAbsolutePaths) {
		LibraryActivity._TitlesAndAbsolutePaths = pTitlesAndAbsolutePaths;
	}
	
	private static HashMap<String, String> getTitlesAndPaths() {
		return LibraryActivity._TitlesAndAbsolutePaths;
	}
	
	private static TextAdapter getTextAdapter() {
		return LibraryActivity._TextAdapter;
	}
	
	private static void setTextAdapter(TextAdapter pTextAdapter) {
		LibraryActivity._TextAdapter = pTextAdapter;
	}
	
	/**
	 * Get context of this activity
	 * @return
	 */
	public static Context getAppContext() {
        return LibraryActivity._Context;
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
		
		LibraryActivity.renewAdapter();
		
		LibraryActivity.getTextAdapter().notifyDataSetChanged();
		gridview.setAdapter(LibraryActivity.getTextAdapter());
		gridview.setVisibility(View.VISIBLE);
	}
	
	private static TextAdapter renewAdapter() {
		TextAdapter aTextAdapter;
		ArrayList<String> titles = new ArrayList<String>();
		HashMap<String, String> aHashMap = 
				LibraryActivity._EvilLibraryManager.getTitleAndPathHashMap();
		LibraryActivity.setTitlesAndAbsolutePaths(aHashMap);
		titles.addAll(aHashMap.keySet());
		aTextAdapter = new TextAdapter(LibraryActivity._Context, titles);
		LibraryActivity.setTextAdapter(aTextAdapter);
		Log.e("EVILREADER", "" + titles.size());
		return LibraryActivity.getTextAdapter();
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
