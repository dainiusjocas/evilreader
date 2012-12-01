package com.evilreader.android.library;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;
import android.util.Log;

public class EvilBook {

	private String _AbsolutePath;
	private Book _Book;
	private Metadata _Metadata;
	
	/**
	 * Constructor
	 * @param pAbsolutePath and I mean absolute path
	 */
	public EvilBook(String pAbsolutePath) {
		this._AbsolutePath = pAbsolutePath;
		Metadata aMetadata;
		try {
			EpubReader aEpubReader = new EpubReader();
			InputStream aInputStream = new FileInputStream(this._AbsolutePath);
			this._Book = aEpubReader.readEpub(aInputStream);
			aMetadata = this._Book.getMetadata();
			this._Metadata = aMetadata;
		} catch (IOException e) {
			Log.e("EVILREADER", "Error while reading epub");
		}
	}
	
	/**
	 * Reads metadata of an evil book and returns string representation of
	 * authors of that book
	 * 
	 * @return authors - string representation of book authors
	 */
	public String getAuthors() {
		String authors = "";
		List<Author> authorsList = this._Metadata.getAuthors();
		int numberOfAuthors = authorsList.size();
		for (int i = 0; i < numberOfAuthors; i++) {
			authors = authors + " " + authorsList.get(i);
		}
		return authors;
	}
	
	/**
	 * Get title of the evil book from books metadata.
	 * 
	 * @return title of the evil book
	 */
	public String getTitle() {
		String title = "";
		title = this._Metadata.getFirstTitle();
		return title;
	}
	
	/**
	 * Get year of publication
	 * 
	 * @return String of format [0-9]{4}
	 */
	public String getYear() {
		String regex = "[0-9]{4}"; // matches only years
		String year = "";
		List<nl.siegmann.epublib.domain.Date> dates = this._Metadata.getDates();
		year = dates.get(0).toString();
		String[] notMatch = year.split(regex, 4);
		year = year.replace(notMatch[0], "");
		year = year.replace(notMatch[1], "");
		return year;
	}
	
	public void getCover() {
		this._Book.getCoverImage();
	}
}
