package com.evilreader.android.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.evilreader.android.MainActivity;
import com.evilreader.android.R;

public class LibraryActivity extends Activity {
	
	private static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// set or create base directory for ebooks
		super.onCreate(savedInstanceState);
		LibraryActivity.context = getApplicationContext();
		setContentView(R.layout.activity_library);
		// Impossible to set fullscreen in layout xml file, so it is done here
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	Intent mIntent = new Intent(LibraryActivity.this, com.evilreader.android.epubcontentcontrol.EpubContentActivity.class);
				startActivity(mIntent);
	            //Toast.makeText(LibraryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });

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
        	 // refresh button is pressed
        	 displayAllImages(); // make visible images
        	 displayEvilMessage("Refresh Evil Library!");
        	 return true;
         }
         return false;
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
