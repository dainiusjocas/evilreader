package com.evilreader.android.library;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

/**
 * This class is responsible for managing the Evil ebooks Library.
 * 
 * @author Dainius
 *
 */
public class EvilLibraryManager {
	
	// Scanner for Evil Library
	private EvilLibraryScanner _EvilLibraryScanner;
	
	private File _EvilLibraryDirectory;
	
	// Constant library folder name
	private static final String EVIL_LIBRARY_DIRECTORY = "evilbooks/";
	
	// Part of singleton design pattern
	private static EvilLibraryManager _Instance = null;
	
	/* not public constructor */
	protected EvilLibraryManager() {
		this._EvilLibraryDirectory = 
				this.prepareDirectoryForEvilLibrary();
		this._EvilLibraryScanner = 
				new EvilLibraryScanner(this._EvilLibraryDirectory);
	}
	
	/**
	 * Gets instance of EvilLibraryManager.
	 * 
	 * @return instance of EvilLibraryManager
	 */
	public static EvilLibraryManager getInstance() {
		if (EvilLibraryManager._Instance == null) {
			EvilLibraryManager._Instance = new EvilLibraryManager();
		}
		return EvilLibraryManager._Instance;
	}
	
	/**
	 * Checks if directory for eBooks exists, and if not creates it. Then
	 * returns directory descriptor.
	 * 
	 * @return File directoryForEvilLibrary
	 */
	private File prepareDirectoryForEvilLibrary() {
		File sdCardMountPoint = Environment.getExternalStorageDirectory();
		File directoryForEvilLibrary = 
				new File(sdCardMountPoint, 
						EvilLibraryManager.EVIL_LIBRARY_DIRECTORY);
		
		if (!directoryForEvilLibrary.exists()) {
			directoryForEvilLibrary.mkdir();
		}
		
		return directoryForEvilLibrary;
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
	
	// getListOfEvilBooks() 
	// refreshListOfEvilBooks()
	// getCoverImages
	// removeBook()
	

}
