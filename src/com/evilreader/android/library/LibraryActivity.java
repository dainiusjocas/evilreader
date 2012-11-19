package com.evilreader.android.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// set or create base directory for ebooks
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		// There is no way to set fullscreen view in layout xml file, so it is done here
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(LibraryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
		
		
		
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
        	 // do something here
        	 Context context1 = getApplicationContext();
        	 CharSequence text1 = "Search for Evil Books!";
        	 int duration1 = Toast.LENGTH_SHORT;

        	 Toast toast1 = Toast.makeText(context1, text1, duration1);
        	 toast1.show();
             return true;
         case R.id.refresh_library:
        	 // do smth here
        	 Context context = getApplicationContext();
        	 CharSequence text = "Refresh Evil Library!";
        	 int duration = Toast.LENGTH_SHORT;

        	 Toast toast = Toast.makeText(context, text, duration);
        	 toast.show();
        	 return true;
         }
         return false;
     }
	
}
