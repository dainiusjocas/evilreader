package com.evilreader.android.evilcontentcontroller;

import java.util.List;
import java.util.Vector;

import com.evilreader.android.R;
import com.evilreader.android.library.LibraryActivity;
import com.evilreader.android.tableOfContents.ToCActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

public class EpubContentActivity extends FragmentActivity {
	// private constants
	// TODO(Viktor): in the future we'll use more sophisticated technique
	private final int resourceIDSample = R.raw.asd;
	private static int NUM_ITEMS = 0;

	// private members
	private GemManager gemManager;
	private EvilreaderViewPager mViewPager;
	private PagerAdapter mSectionsPagerAdapter;
	private List<EvilWebViewFragment> fragments = new Vector<EvilWebViewFragment>();
	private String currentBookId = "";

	private int currentChapterIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/*
		 * Setting the layout of the activity
		 * */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_epub_content);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
						
		/*
		 * Getting the necessary params from the intent to load the epub
		 * */
		String filePath = getIntent().getExtras().getString("evil_path");
		currentBookId = getIntent().getExtras().getString("evil_id");
		
		/*
		 * Initiating managers
		 * */
		this.gemManager = new GemManager(getApplicationContext(), this);
		EbookContentManager.getInstance().Init(getApplicationContext(),this);
		
		/*
		 * Loading the book and creating all the views and add them to the page viewer
		 */
		
		try {
			EbookContentManager.getInstance().LoadEpubBookByAbsolutePath(filePath, currentBookId, fragments);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		this.mSectionsPagerAdapter  = new PagerAdapter(this, fragments);
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (EvilreaderViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(updateContentManager);		
	}
	
	private OnPageChangeListener updateContentManager = new OnPageChangeListener() {
		
		public void onPageSelected(int arg0) {
			EbookContentManager.getInstance().GetPageContentByPageNumber(arg0 + 1);
			
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_epub_content, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Handles menu button clicks.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
         switch (item.getItemId()) {
         case R.id.table_of_content:
        	 Intent aIntent = new Intent(this, ToCActivity.class);
        	 Bundle extras = new Bundle();
        	 extras.putString("evil_id", this.currentBookId);
        	 aIntent.putExtras(extras);
        	 startActivityForResult(aIntent, 2);
         }
         
         return false;
	}
	
	/*
	 * Callback from the TOCActivity for goto workflow by chapternumber or bookmark
	 * */
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
            	String bookmarkId = data.getExtras().getString("bookmark");
            	String chapterId = data.getExtras().getString("chapter");
            	
            	if(bookmarkId != null && bookmarkId != ""){
            		
            		int[] bookMark = this.gemManager.getBookmarkById(bookmarkId);
            		
            		int pageNumber = EbookContentManager.getInstance().GetPageNumberByBookmark(bookMark);
            		if(pageNumber != -1){
            			this.mViewPager.setCurrentItem(pageNumber, true);
            		}
            	}
            	else if(chapterId != null && chapterId != ""){
            		int pageNumber = EbookContentManager.getInstance().GetPageNumberByChapterNumber(Integer.parseInt(chapterId));
            		if(pageNumber != -1){
            			this.mViewPager.setCurrentItem(pageNumber, true);
            		}
            	}
            }
        }
    }
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class PagerAdapter extends FragmentPagerAdapter {

		private List<EvilWebViewFragment> fragments;
		/**
		 * @param fm
		 * @param fragments
		 */
		public PagerAdapter(FragmentActivity activity, List<EvilWebViewFragment> fragments) {
			super(activity.getSupportFragmentManager());
			this.fragments = fragments;
			
		}
	
		@Override
		public int getCount() {
			return this.fragments.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			EvilWebViewFragment fragment = fragments.get(arg0);			
			return fragment;
		}
	}
	
	public static class EvilWebViewFragment extends Fragment{
		
		public EvilWebViewFragment(){
			super();
		}
		
		private final String javaScriptLibraries = "<html><head><script type='text/javascript' src='file:///android_asset/jquery.js'></script><script type='text/javascript' src='file:///android_asset/rangy-core.js'></script><script type='text/javascript' src='file:///android_asset/rangy-serializer.js'></script><script type='text/javascript' src='file:///android_asset/android.selection.js'></script></head><body>";
		EvilreaderWebView viewer = null;
		String webViewContent = "";
		String currentBookId;
		Context mCtx;
		Activity activity;
		
		public void SetWebView(Context ctx,String content, String bookId, Activity currentActivity){
			this.webViewContent = content;
			this.currentBookId = bookId;
			this.mCtx = ctx;
			this.activity = currentActivity;
		}
		
		@Override  
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		        Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			viewer = new EvilreaderWebView(mCtx, activity);
			viewer.setLayoutParams(lp);
			viewer.setScrollBarStyle(WebView.OVER_SCROLL_ALWAYS);
			viewer.loadDataWithBaseURL("file:///android_asset/",javaScriptLibraries + webViewContent + "</body></html>", "text/html", "UTF-8", "");
			viewer.SetCurrentBookId(currentBookId);
			
			return viewer;
		}
		
		
		
		public String GetWebViewContent(){
			return webViewContent;
		}		
	}
}
