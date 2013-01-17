package com.evilreader.android.tableOfContents;  
 import java.util.ArrayList;

import com.evilreader.android.R;
import com.evilreader.android.dbcontroller.DBAdapter;
import android.app.TabActivity;  
import android.content.Intent;
import android.content.res.Resources;  
import android.os.Bundle;  
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

/**
 * Purpose of this class is to display contents of the book in two tabs and 
 * when one item either chapter or bookmark is selected return result to the
 * calling activity.
 * @author Dainius
 *
 */
public class ToCActivity extends TabActivity {
	 
      @Override  
      public void onCreate(Bundle savedInstanceState) {  
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_to_c);
           res = getResources();  
           TabHost tabHost = getTabHost();  
           TabHost.TabSpec spec;  
           spec = tabHost.newTabSpec("status").setIndicator("TOC").setContent(R.id.layoutTab1);  
           
           ListView listView = (ListView) findViewById(R.id.toclist);

           String aBookId  = getIntent().getExtras().getString("evil_id");
           
           String[] chapterValues = prepareTOC(aBookId);

           // Define a new Adapter
           // First parameter - Context
           // Second parameter - Layout for the row
           // Third parameter - ID of the TextView to which the data is written
           // Forth - the Array of data

           ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
             android.R.layout.simple_list_item_1, android.R.id.text1, chapterValues);
           listView.setAdapter(adapter); 
           listView.setOnItemClickListener(new OnItemClickListener() {
        	   public void onItemClick(AdapterView<?> parent, View view,
        			   int position, long id) {
        		// Set result
       			Intent extra = new Intent();
       			extra.putExtra("chapter", "" + position);
       			setResult(RESULT_OK, extra);
       			// Finish the activity
       			finish();
        	   }
        	 });
           
           tabHost.addTab(spec);  
           
           spec = tabHost.newTabSpec("actionitems").setIndicator("Bookmarks").setContent(R.id.layoutTab2);  
           ListView bookmarksList = (ListView) findViewById(R.id.bookmarkslist);
           DBAdapter db = new DBAdapter(this);
     	   final ArrayList<String> bookmarkIds = db.getAllBookmarkIdsByBookID(aBookId);
           String[] bookmarkValues = prepareBookmarks(bookmarkIds);
           ArrayAdapter<String> bookmarkAdapter = new ArrayAdapter<String>(this,
                   android.R.layout.simple_list_item_1, android.R.id.text1, bookmarkValues);
           bookmarksList.setAdapter(bookmarkAdapter);
           
           bookmarksList.setOnItemClickListener(new OnItemClickListener() {
        	   public void onItemClick(AdapterView<?> parent, View view,
        			   int position, long id) {
        		   // Set result
        		   Intent extra = new Intent();
        		   if (null == bookmarkIds) {
        			   extra.putExtra("bookmark", "null");
        		   } else {
        			   extra.putExtra("bookmark", bookmarkIds.get(position));
        		   }
        		   setResult(RESULT_OK, extra);
        		   // Finish the activity
        		   finish();
        	   }
        	 });
                 
           tabHost.addTab(spec);  
           tabHost.setCurrentTab(0);  
      }  
      protected Resources res; 
      
      /**
       * Generate an array of strings that will be displayed in the list of TOC.
       * @param pBookId
       * @return
       */
      private String[] prepareTOC(String pBookId) {
    	  String[] values;
    	  DBAdapter db = new DBAdapter(this);
    	  int numberOfItems = db.getNumberOfEvilBookChapters(pBookId);
    	  if (0 == numberOfItems) { 
    		  values = new String[1];
    		  values[0] = "Cover Page";
    		  return values;
    	  }
    	  values = new String[numberOfItems];
    	  values[0] = "Cover Page";
    	  for (int i = 1; i < numberOfItems; i++) {
    		  values[i] = "Chapter# " + (i);
    	  }
    	  return values;
      }
      
      /**
       * Prepare an array of string that will be displayed in the list of 
       * bookmarks.
       * @param pBookmarkIds
       * @return
       */
      private String[] prepareBookmarks(ArrayList<String> pBookmarkIds) {
    	  String[] values;
    	  if (null == pBookmarkIds) {
    		  values = new String[1];
    		  values[0] = "No bookmarks";
    		  return values;
    	  }
    	  int numberOfItems = pBookmarkIds.size();
    	  values = new String[numberOfItems];
    	  for (int i = 0; i < numberOfItems; i++) {
    		  values[i] =  "Bookmark#" + " " + (i + 1);
    	  }
    	  return values;
      }
 }  