package com.evilreader.android.ContentProviders;

import java.io.InputStream;
import java.util.HashMap;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

public class EbookFileManager  {
	//The singleton instance of this manager
	private static final EbookFileManager INSTANCE = new EbookFileManager();
	
	//Contains all the currently loaded "Ebook" from the file system
	//Keep all the active .epub books that the user is working(reading) with
	private HashMap<Integer, Book> currentEBookList;
	private Context contextWrapper;
	
	//private constructor for Singleton purpose
	private EbookFileManager()
	{
		
	}
	
	public static EbookFileManager getInstance() {
    	return EbookFileManager.INSTANCE;
    }
	
	//The contextWrapper as a reference is needed to have access to the local storage of the Application,
	//so this class needs this data
	public void Init(Context ContextWrapper)
	{
		contextWrapper = ContextWrapper;
	}
	
	//Method for downloading new epub books to the local resource storage of the Application
	@SuppressWarnings("unused")
	private void DownloadEpubFile(Uri url)
	{
		//ToDo Implement
	}
	
	//Adds books to the memory of the application.
	@SuppressLint("UseSparseArrays")
	public Book AddEpubFileToEbookList(InputStream inputStream, int resourceID)
	{
		try {
			Book book = (new EpubReader()).readEpub(inputStream);
			if(currentEBookList == null)
			{
				currentEBookList = new HashMap<Integer, Book>();
			}
			
			currentEBookList.put(resourceID, book);
			return book;
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	//Finds a .epub book by id from the local resources of the application
	public void LoadBookByResourceID(int resourceID) throws Exception
	{
		InputStream inputStream = contextWrapper.getResources().openRawResource(resourceID);
		if(inputStream != null)
		{
			AddEpubFileToEbookList(inputStream, resourceID);
		}
		else
		{
			throw new Exception("The pointed book doesn't exist on the local storage.");
		}			
	}
	
	//Gets the content of the pages pointed in the arguments as a pure string(html). If the book is not loaded into the current list
	//It will be loaded if it is in the resources
	public String GetEpubBookChapterContentByChapterNumber(int resourceID, int chapterNumber)
	{		
		//ToDo @Viktor - handle special cases
		String chapterContent = "";
		InputStream inputStream;
		java.util.Scanner scanner;
		
		try {
			Book book = currentEBookList.get(resourceID);
			Spine spine = book.getSpine();
			
			Resource res1 = spine.getSpineReferences().get(chapterNumber).getResource();
			inputStream = res1.getInputStream();
			scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
			chapterContent = scanner.hasNext() ? scanner.next() : "";
			
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		return chapterContent;
	}
	
	
}
