package com.evilreader.android.evilcontentcontroller;

import android.content.Context;

import com.evilreader.android.dbcontroller.DBAdapter;

/*
 * This class is responsible for reading and writing objects
 * into the database for the currently loaded ebook into the memory
 * contained into the EbookContentManager
 * */
public class EvilEbookManager {
	// The singleton instance of this manager
	private static final EvilEbookManager INSTANCE = new EvilEbookManager();
	// Context that we are executing
	private Context _Context;
	// Database controller
	private DBAdapter _DBAdapter;
	
	private EvilEbookManager() {
	}
	
	public void Init(Context contextWrapper) {
		this._Context = contextWrapper;
		this._DBAdapter = new DBAdapter(this._Context);
	}

	public static EvilEbookManager getInstance() {
		return EvilEbookManager.INSTANCE;
	}

	public void CreateBookmark(String bookId, int chapeter, int paragraphNumber){
		this._DBAdapter.open();
		this._DBAdapter.close();
	}
}
