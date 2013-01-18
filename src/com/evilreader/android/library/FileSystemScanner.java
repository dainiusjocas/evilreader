package com.evilreader.android.library;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

/**
 * Purpose of this class is to scan default directory for ePubs. This class
 * assumes that directory already exists.
 * 
 * @author Dainius Jocas
 *
 */
public class FileSystemScanner {
	private File _EvilLibraryDirectory;
	
	/**
	 * Constructor. Initializes _EvilLibraryDirectory which should be scanned.
	 * 
	 * @param evilLibraryDirectory - directory descriptor
	 */
	public FileSystemScanner(File evilLibraryDirectory) {
		this._EvilLibraryDirectory = evilLibraryDirectory;
	}
	
	/**
	 * Scans EvilLibrary Directory for ePubs and returns list of names of files
	 * that are ePubs. To be an ePub means that your ePub file has an extension
	 * .epub. Extension is case insensitive.
	 * 
	 * @return ArrayList<String> ePubFilesInEvilLibrary is a list of file 
	 * names in evil library that are ePubs.
	 */
	public ArrayList<String> scanEvilLibraryDirectoryForEPubs() {
		// (?i) is for case insensitive matching and .[^_] is to get rid of 
		// files that MACOSX Finder creates
		String ePubRegEx = ".[^_](?i).*.epub";  
		ArrayList<String> ePubFilesInEvilLibrary = new ArrayList<String>();
		String[] fileNamesInEvilDirectory = this._EvilLibraryDirectory.list();
		if (fileNamesInEvilDirectory == null) {
			ePubFilesInEvilLibrary.add("NO EVIL LIBRARY");
			// Error Message, but not null
			return ePubFilesInEvilLibrary;
		}
		for (String fileName : fileNamesInEvilDirectory) {
			if (fileName.matches(ePubRegEx)) {
				ePubFilesInEvilLibrary.add(fileName);
			}
		}
		return ePubFilesInEvilLibrary;
	}

}
