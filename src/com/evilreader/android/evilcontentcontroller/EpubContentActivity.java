package com.evilreader.android.evilcontentcontroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.evilreader.android.R;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewFragment;
import android.widget.Button;

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
		this.mSectionsPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (EvilreaderViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);		
	}
	

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
		public PagerAdapter(FragmentManager fm, List<EvilWebViewFragment> fragments) {
			super(fm);
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
		
		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}		
	}
	
	public static class EvilWebViewFragment extends Fragment{
		
		public EvilWebViewFragment(){
			super();
		}
		
		private final String javaScriptLibraries = "<html><head><script type='text/javascript' src='file:///android_asset/jquery.js'></script><script type='text/javascript' src='file:///android_asset/rangy-core.js'></script><script type='text/javascript' src='file:///android_asset/rangy-serializer.js'></script><script type='text/javascript' src='file:///android_asset/android.selection.js'></script></head><body>";
		EvilreaderWebView viewer = null;
		String webViewContent = "";
		
		public void SetWebView(EvilreaderWebView webView, String content){
			viewer = webView;
			webViewContent = content;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		        Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			viewer.loadDataWithBaseURL("file:///android_asset/",javaScriptLibraries + webViewContent + "</body></html>", "text/html", "UTF-8", "");
		    return viewer;
		}
	}
}
