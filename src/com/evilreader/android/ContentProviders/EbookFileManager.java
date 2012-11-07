package com.evilreader.android.ContentProviders;

import java.io.InputStream;
import java.util.HashMap;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import android.net.Uri;


public class EbookFileManager {

	//Contains all the loaded "Ebook" from the file system
	private HashMap<String,Book> currentEBookList;	
		
	public EbookFileManager()
	{			
		
	}
	
	private void DownloadFile(Uri url)
	{
		//ToDo Implement
	}
	
	public Book AddEpubFileToEbookList(InputStream inputStream)
	{
		try {
			Book book = (new EpubReader()).readEpub(inputStream);
			if(currentEBookList == null)
			{
				currentEBookList = new HashMap<String, Book>();
			}
			
			currentEBookList.put(book.getTitle(), book);
			return book;
		} catch (Exception e) {
			Exception ex = e;
		}
		
		return null;
	}
}
