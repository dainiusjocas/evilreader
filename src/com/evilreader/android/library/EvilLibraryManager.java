package com.evilreader.android.library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
	private Context _Context;
	// Database controller
	private DBAdapter _DBAdapter;
	// Scanner for Evil Library
	// TODO(dainius) I think this is only necessary when evilreader is actually 
	// going to the filesystem. So not in constructor.
	private EvilLibraryScanner _EvilLibraryScanner;
	// List of evil books
	private ArrayList<String> _ListOfEvilBooks = new ArrayList<String>();;
	// Absolute path to the directory
	private File _EvilLibraryDirectory;
	// Constant library folder name
	private final String EVIL_LIBRARY_DIRECTORY = "evilbooks/";
	
	
	
	/**
	 * Constructor
	 *  
	 * @param mContext
	 */
	public EvilLibraryManager(Context mContext) {
		this._Context = mContext;
		this._DBAdapter = new DBAdapter(this._Context);
		this._ListOfEvilBooks = getListOfEvilBooksFromDB();
		// this line makes me susipicious
		this._EvilLibraryDirectory = 
				this.prepareDirectoryForEvilLibrary();
		this._EvilLibraryScanner = 
				new EvilLibraryScanner(this._EvilLibraryDirectory);
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
							this.EVIL_LIBRARY_DIRECTORY);
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
							this.EVIL_LIBRARY_DIRECTORY);
		} else {
			// not writable and not readable 
			return null;
		}
		
		return directoryForEvilLibrary;
	}
	
	private ArrayList<String> getListOfEvilBooksFromDB() {
		ArrayList<String> listOfEvilBooks = new ArrayList<String>();
		this._DBAdapter.open();
		Cursor aCursor = this._DBAdapter.getTitlesOfEvilBooks();
		// Maybe there should be a check if there are elements
		// If not then a message should be displayed.
		if (!aCursor.moveToFirst()) {
			listOfEvilBooks.add("NO EVIL BOOKS IN THE LIBRARY"); // mock for message
			aCursor.close();
			this._DBAdapter.close();
			return listOfEvilBooks;
		}
		do {
			listOfEvilBooks.add(aCursor.getString(0));
		} while(aCursor.moveToNext());
		// query database for list of books that were available in the past
		// and assign it to the variable listOfEvilBooks
		aCursor.close();
		this._DBAdapter.close();
		return listOfEvilBooks;
	}
	
	public ArrayList<String> getListOfEvilBooks() {
		return this._ListOfEvilBooks;
	}
	
	private boolean isEvilLibraryDirectoryPresent() {
		File sdCardMountPoint = Environment.getExternalStorageDirectory();
		File directoryForEvilLibrary = 
				new File(sdCardMountPoint, 
						this.EVIL_LIBRARY_DIRECTORY);
		
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
	private void storeEvilBooks(ArrayList<String> mEvilBooks) {
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
	private void markEvilBooksThatAreNotPresent() {
		this._DBAdapter.open();
		// two columns - [0] - absolute path, [1] - id
		Cursor aCursor = this._DBAdapter.fetchAllEvilBooks();
		if (!aCursor.moveToFirst()) {
			//Log.e("EVILREADER ERROR", "NO EVIL BOOKS IN THE LIBRARY");
			this.refreshListOfEvilBooks();
			aCursor.close();
			this._DBAdapter.close();
			return;
		}
		do {
			File aFile = new File(aCursor.getString(0));
			if (!aFile.exists()) {
				int aRowId = Integer.valueOf(aCursor.getString(1));
				this._DBAdapter.markEvilBookStatus(aRowId, "false");
			}
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
	}
	
	/**
	 * Constructs hash map for EvilBooks <Title, AbsolutePath>
	 * 
	 * @return aHashMap
	 */
	private HashMap<String, String> getTitleAndPathHashMap() {
		this._DBAdapter.open();
		HashMap<String, String> aHashMap = new HashMap<String, String>();
		Cursor aCursor = this._DBAdapter.getTitlesAndPathsOfEvilBooks();
		if (!aCursor.moveToFirst()) {
			//Log.e("EVILREADER2", "NO EVIL BOOKS IN THE LIBRARY");
			aHashMap.put("NO EVIL BOOKS IN THE LIBRARY", "EVILREADER");
			aCursor.close();
			this._DBAdapter.close();
			return aHashMap;
		}
		do {
			aHashMap.put(aCursor.getString(0), aCursor.getString(1));
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return aHashMap;
	}
	
	public ArrayList<EvilTriple> getTitlePathId() {
		this._DBAdapter.open();
		ArrayList<EvilTriple> aTitlePathId = new ArrayList<EvilTriple>();
		Cursor aCursor = this._DBAdapter.getTitlesPathsIdsOfEvilBooks();
		if (!aCursor.moveToFirst()) {
			//Log.e("EVILREADER2", "NO EVIL BOOKS IN THE LIBRARY");
			EvilTriple aTriple =  
					new EvilTriple ("NO EVIL BOOKS IN THE LIBRARY",
							"EVILREADER", "Evilreader");
			aCursor.close();
			this._DBAdapter.close();
			aTitlePathId.add(aTriple);
			return aTitlePathId;
		}
		do {
			EvilTriple anEvilTriple = new EvilTriple(aCursor.getString(0), aCursor.getString(1), aCursor.getString(2));
			aTitlePathId.add(anEvilTriple);
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return aTitlePathId;
	}
	
	// getCoverImages
	// removeBook()
	// get list of ebook covers

}
