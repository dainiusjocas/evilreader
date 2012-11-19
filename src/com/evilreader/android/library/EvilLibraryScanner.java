package com.evilreader.android.library;

import java.io.File;
import java.util.ArrayList;

/**
 * Purpose of this class is to scan default directory for ePubs. This class
 * assumes that directory already exists.
 * 
 * @author Dainius
 *
 */
public class EvilLibraryScanner {
	private File _EvilLibraryDirectory;
	
	/**
	 * Constructor
	 * 
	 * @param evilLibraryDirectory - directory descriptor
	 */
	public EvilLibraryScanner(File evilLibraryDirectory) {
		this._EvilLibraryDirectory = evilLibraryDirectory;
	}
	
	/**
	 * Scans EvilLibrary Directory for ePubs and returns list of names of files
	 * that are ePubs
	 * 
	 * @return ArrayList<String> ePubFilesInEvilLibrary is a list of file 
	 * names in evil library that are ePubs.
	 */
	public ArrayList<String> scanEvilLibraryDirectoryForEPubs() {
		String[] fileNamesInEvilDirectory = this._EvilLibraryDirectory.list();
		if (fileNamesInEvilDirectory == null) {
			return null;
		}
		// (?i) is for case insensitive matching
		String ePubRegEx = "(?i).*.epub";  
		ArrayList<String> ePubFilesInEvilLibrary = new ArrayList<String>();
		for (String fileName : fileNamesInEvilDirectory) {
			if (fileName.matches(ePubRegEx)) {
				ePubFilesInEvilLibrary.add(fileName);
			}
		}
		return ePubFilesInEvilLibrary;
	}

}
