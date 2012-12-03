package com.evilreader.android.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.evilreader.android.dbcontroller.DBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

/**
 * This class is responsible for managing the Evil ebooks Library.
 * 
 * @author Dainius
 *
 */
public class EvilLibraryManager {
	
	// Context that we are executing
	Context _Context;
	// Database controller
	private DBAdapter _DBAdapter;
	// Part of singleton design pattern
	private static EvilLibraryManager _Instance = null;
	
	// Scanner for Evil Library
	// TODO(dainius) I think this is only necessary when evilreader is actually 
	// going to the filesystem. So not in constructor.
	private EvilLibraryScanner _EvilLibraryScanner;
	
	// List of books
	private ArrayList<String> _ListOfEvilBooks;
	
	// Path to the directory
	private File _EvilLibraryDirectory;
	
	// Constant library folder name
	private static final String EVIL_LIBRARY_DIRECTORY = "evilbooks/";
	
	
	
	/* not public constructor */
	protected EvilLibraryManager(Context mContext) {
		this._Context = mContext;
		this._DBAdapter = new DBAdapter(this._Context);
		this._ListOfEvilBooks = new ArrayList<String>();
		this._ListOfEvilBooks = getListOfEvilBooksFromDB();
		// this line makes me susipicious
		this._EvilLibraryDirectory = 
				this.prepareDirectoryForEvilLibrary();
		this._EvilLibraryScanner = 
				new EvilLibraryScanner(this._EvilLibraryDirectory);
	}
		
	public ArrayList<String> getListOfEvilBooksFromDB() {
		ArrayList<String> listOfEvilBooks = new ArrayList<String>();
		this._DBAdapter.open();
		Cursor cursor = this._DBAdapter.getTitlesOfEvilBooks();
		// Maybe there should be a check if there are elements
		// If not then a message should be displayed.
		if (!cursor.moveToFirst()) {
			listOfEvilBooks.add("NO EVIL BOOKS IN THE LIBRARY"); // mock for message
			return listOfEvilBooks;
		}
		do {
			listOfEvilBooks.add(cursor.getString(0));
		} while(cursor.moveToNext());
		// query database for list of books that were available in the past
		// and assign it to the variable listOfEvilBooks
		this._DBAdapter.close();
		return listOfEvilBooks;
	}
	
	public ArrayList<String> getListOfEvilBooks() {
		return this._ListOfEvilBooks;
	}
	
	/**
	 * Gets instance of EvilLibraryManager.
	 * 
	 * @return instance of EvilLibraryManager
	 */
	public static EvilLibraryManager getInstance(Context mContext) {
		if (EvilLibraryManager._Instance == null) {
			EvilLibraryManager._Instance = new EvilLibraryManager(mContext);
		}
		return EvilLibraryManager._Instance;
	}
	
	/**
	 * Checks if directory in the external storage (e.g. sdcard) for eBooks
	 * exists, and if not creates it. If external storage is not writable and
	 * evil directory does not exist, then null is returned. 
	 * 
	 * @return File directoryForEvilLibrary, returns NULL if something is bad
	 * TODO(dainius): what if external storage doesn't exist?
	 */
	private File prepareDirectoryForEvilLibrary() {
		File directoryForEvilLibrary = null;
		// Check if external storage is available for reading and writing
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			// so we can check if exists directory we need and if not create
			directoryForEvilLibrary = 
					new File(Environment.getExternalStorageDirectory(), 
							EvilLibraryManager.EVIL_LIBRARY_DIRECTORY);
			if (!isEvilLibraryDirectoryPresent()) {
				directoryForEvilLibrary.mkdir();
			}
			
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// we can only read media
			// so check if exists directory, and if not return NULL
			if (!isEvilLibraryDirectoryPresent()) {
				return null; // no directory, no work
			}
			directoryForEvilLibrary = 
					new File(Environment.getExternalStorageDirectory(), 
							EvilLibraryManager.EVIL_LIBRARY_DIRECTORY);
		} else {
			// not writable and not readable 
			return null;
		}
		
		return directoryForEvilLibrary;
	}
	
	private boolean isEvilLibraryDirectoryPresent() {
		File sdCardMountPoint = Environment.getExternalStorageDirectory();
		File directoryForEvilLibrary = 
				new File(sdCardMountPoint, 
						EvilLibraryManager.EVIL_LIBRARY_DIRECTORY);
		
		if (!directoryForEvilLibrary.exists()) {
			return false; // evil directory does not exist
		} else {
			return true;
		}
	}
	
	/**
	 * Get list of filenames in the EvilLibrary directory that are ePubs
	 * 
	 * @return ArrayList of file names of ePub files
	 * TODO(dainius): this method should be private
	 * TODO(dainius): what if null is returned?
	 */
	public ArrayList<String> getListOfFileNamesOfePubFiles() {
		ArrayList<String> listOfFileNamesOfePubFiles = 
				this._EvilLibraryScanner.scanEvilLibraryDirectoryForEPubs();
		return listOfFileNamesOfePubFiles;
	}
	
	/*
	 * Strores evil books in the database. 
	 * 
	 * @param mEvilBooks list of filenames (not absolute path) of epubs. 
	 */
	public void storeEvilBooks(ArrayList<String> mEvilBooks) {
		String title;
		String authors;
		String year;
		String filename;
		String path;
		for (String evilBook : mEvilBooks) {
			
			String anAbsolutePath = 
					this._EvilLibraryDirectory.getAbsolutePath() 
					+ "/" 
					+ evilBook;
			
			EvilBook aEvilBook = new EvilBook(anAbsolutePath);
			aEvilBook.getTitle();
			authors = aEvilBook.getAuthors().trim();
			title = aEvilBook.getTitle();
			year = aEvilBook.getYear();
			filename = evilBook;
			path = anAbsolutePath;
			this._DBAdapter.open();
			this._DBAdapter.storeEvilBook(title, authors, year, filename, path);
			this._DBAdapter.close();
		}
	}
	
	// getListOfEvilBooks() 
	public void refreshListOfEvilBooks() {
		ArrayList<String> evilFiles = getListOfFileNamesOfePubFiles();
		storeEvilBooks(evilFiles);
		markEvilBooksThatAreNotPresent();
	}
	
	/**
	 * Marks evil books that file path is not valid.
	 */
	public void markEvilBooksThatAreNotPresent() {
		this._DBAdapter.open();
		// two columns - [0] - absolute path, [1] - id
		Cursor aCursor = this._DBAdapter.fetchAllEvilBooks();
		if (!aCursor.moveToFirst()) {
			Log.e("EVILREADER", "NO EVIL BOOKS IN THE LIBRARY");
		}
		do {
			File aFile = new File(aCursor.getString(0));
			if (!aFile.exists()) {
				int aRowId = Integer.valueOf(aCursor.getString(1));
				this._DBAdapter.markEvilBookStatus(aRowId, "false");
			}
		} while(aCursor.moveToNext());
		this._DBAdapter.close();
	}
	
	// getCoverImages
	// removeBook()
	// get list of ebook covers

}
