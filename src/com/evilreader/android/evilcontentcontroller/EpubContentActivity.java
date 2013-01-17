package com.evilreader.android.evilcontentcontroller;

import java.util.List;
import java.util.Vector;

import com.evilreader.android.R;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

public class EpubContentActivity extends FragmentActivity {
	// private constants
	// TODO(Viktor): in the future we'll use more sophisticated techniques
	private final int resourceIDSample = R.raw.asd;
	private static int NUM_ITEMS = 0;

	// private members
	
	//private ArrayList<EvilreaderWebView> webviewPages;
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
		GemManager.getInstance().Init(getApplicationContext());
		EbookContentManager.getInstance().Init(getApplicationContext());
		
		/*
		 * Loading the book and creating all the views and add them to the page viewer
		 */
		
		try {
			EbookContentManager.getInstance().LoadEpubBookByAbsolutePath(filePath, fragments);
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
		Context mCtx;
		
		public void SetWebView(Context ctx,String content){
			webViewContent = content;
			mCtx = ctx;
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
			viewer = new EvilreaderWebView(mCtx);
			viewer.setLayoutParams(lp);
			viewer.setScrollBarStyle(WebView.OVER_SCROLL_ALWAYS);
			viewer.loadDataWithBaseURL("file:///android_asset/",javaScriptLibraries + webViewContent + "</body></html>", "text/html", "UTF-8", "");
						
			return viewer;
		}
		
		
		
		public String GetWebViewContent(){
			return webViewContent;
		}		
	}
}
