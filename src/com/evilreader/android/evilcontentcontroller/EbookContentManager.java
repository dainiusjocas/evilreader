package com.evilreader.android.evilcontentcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

/*
 * This class is responsible for loading the epub file into the memmory
 * by deviding it's conent into a structure of chapters and pages where
 * every page has set of parapgraphs. Pages are defined as on page has atmost LettersPerPageConstraint
 * in which the html tags are included
 * */
public class EbookContentManager {
	// The singleton instance of this manager
	private static final EbookContentManager INSTANCE = new EbookContentManager();
	static int Width;
	static int Height;
	static int FontSize;
	static int LettersPerPageConstraint = 700;
	static int LinesPerPageConstraint;

	private Context contextWrapper;
	private HashMap<Integer, HashMap<Integer, ArrayList<String>>> currentEbookContentWithinChaptersAndPages;
	// if the index is equal to -1 that means that there is no content in the
	// EBookContent
	private int currentPageNumberIndex;
	private int currentChapterNumberIndex;
	private int lastPageNumberForTheCurrentBook = 0;

	// private constructor for Singleton purpose
	private EbookContentManager() {

	}
	
	// provides logic for calculating how much letters the control,
	// containing the content of the currently loaded book, should visualise without
	// needing to show scroll control for the user.
	private void InitLettersPerPageCount() {
		
	}
	
	@SuppressLint("UseSparseArrays")
	private void InitiateEBookContent(Book book, int progressPage, int progressChapter) {
		currentEbookContentWithinChaptersAndPages = new HashMap<Integer, HashMap<Integer, ArrayList<String>>>();
		currentPageNumberIndex = progressPage;
		currentChapterNumberIndex = progressChapter;
		InputStream inputStream = null;
		Scanner scanner = null;
				
		//Spine spine = null;
		
		try {
			int currentPageNumber = 1;
			int bufferLimiter = 0;
			int chapterNumber = 1;
			
			for (SpineReference spineReference : book.getSpine()
					.getSpineReferences()) {
				currentEbookContentWithinChaptersAndPages.put(chapterNumber++, new HashMap<Integer, ArrayList<String>>());
				inputStream = spineReference.getResource().getInputStream();
				scanner = new Scanner(inputStream, "UTF-8")
						.useDelimiter("</p>");
				
				/*
				 * this if is for when the number of characters is below 700 but there are
				 * no more characters for the current chapter
				 * */
				if(bufferLimiter != 0){
					currentPageNumber++;
					bufferLimiter = 0;
				}
				
				while (scanner.hasNext()) {
					String currentContent = scanner.next();
					
					if(bufferLimiter == 0){
						currentEbookContentWithinChaptersAndPages.get(chapterNumber-1).put(currentPageNumber, new ArrayList<String>());
					}
					
					bufferLimiter += currentContent.length();
					
					if(bufferLimiter < LettersPerPageConstraint){
						currentEbookContentWithinChaptersAndPages.get(chapterNumber-1).get(currentPageNumber).add(currentContent);
						
					}
					
					else if(bufferLimiter >= LettersPerPageConstraint){
						currentEbookContentWithinChaptersAndPages.get(chapterNumber-1).get(currentPageNumber).add(currentContent);
						bufferLimiter = 0;
						currentPageNumber++;
					}
				}
			}
			
			lastPageNumberForTheCurrentBook = currentPageNumber;

		} catch (Exception e) {
			int p = 0;
			
		}

	}

	public static EbookContentManager getInstance() {
		return EbookContentManager.INSTANCE;
	}

	// The contextWrapper as a reference is needed to have access to the local
	// storage of the Application,
	// so this class needs this data
	public void Init(Context ContextWrapper) {
		contextWrapper = ContextWrapper;
	}

	// Method for downloading new epub books to the local resource storage of
	// the Application and load their content
	@SuppressWarnings("unused")
	public void LoadEpubBookByUrl(Uri url) {
		// TODO(Viktor): Implement
	}

	// Method for loading the content from the pointed file
	@SuppressWarnings("unused")
	public void LoadEpubBookByAbsolutePath(String filePath) {
		try {
			// Obsolete
			FileInputStream inputStream = new FileInputStream(new File(filePath));

			Book currentEBook = inputStream != null ? (new EpubReader())
					.readEpub(inputStream) : null;

			if (currentEBook != null) {
				this.InitiateEBookContent(currentEBook, 1, 1);
			} else {
				return;
			}

		} catch (Exception e) {

		}
	}

	// Method used for loading epub files from the Raw resources.
	// Used only for a testing purpose for the implemented functionality in this
	// class ...
	@SuppressLint("UseSparseArrays")
	public void LoadEpubBookByRawResourceID(int resourceID) {
		try {
			// Obsolete
			InputStream inputStream = contextWrapper.getResources()
					.openRawResource(resourceID);

			Book currentEBook = inputStream != null ? (new EpubReader())
					.readEpub(inputStream) : null;

			if (currentEBook != null) {
				this.InitiateEBookContent(currentEBook, 1, 1);
			} else {
				return;
			}

		} catch (Exception e) {

		}
	}

	public int GetCurrentPageNumber(){
		return currentPageNumberIndex;
	}
	
	public int GetCurrentChapterNumber(){
		return currentChapterNumberIndex;
	}
	
	@SuppressWarnings("unused")
	public String GetFirstPage() {
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			currentChapterNumberIndex = 1;
			currentPageNumberIndex = 1;
			
			String buffer = "";
			for(String paragraph:currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).get(currentPageNumberIndex)){
				buffer += paragraph;
			}
			return buffer;
		}

		return "";
	}

	@SuppressWarnings("unused")
	public String GetLastPage() {
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			currentChapterNumberIndex = currentEbookContentWithinChaptersAndPages.size();
			currentPageNumberIndex = lastPageNumberForTheCurrentBook;
			
			String buffer = "";
			for(String paragraph:currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).get(currentPageNumberIndex)){
				buffer += paragraph;
			}
			return buffer;
		}

		return "";
	}

	public String GetNextPage() {
		
		if(lastPageNumberForTheCurrentBook > currentPageNumberIndex){
			currentPageNumberIndex++;
		}
		
		if(!currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).containsKey(currentPageNumberIndex)){
			currentChapterNumberIndex++;
		}
		
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			String buffer = "";
			for(String paragraph:currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).get(currentPageNumberIndex)){
				buffer += paragraph;
			}
			return buffer;
		}

		return "";
	}

	public String GetPreviousPage() {
		if(currentPageNumberIndex > 1){
			currentPageNumberIndex--;
		}
		if(!currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).containsKey(currentPageNumberIndex)){
			currentChapterNumberIndex--;
		}
		
		if (currentEbookContentWithinChaptersAndPages != null) {
			String buffer = "";
			for(String paragraph:currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).get(currentPageNumberIndex)){
				buffer += paragraph;
			}
			
			return buffer;
		}

		return "";
	}

	/*
	 * If the pageNumber is not presented into the loaded book
	 * it will return an empty string. If the page is found then
	 * the indexes to the chapter and the number of this page are going
	 * to be changed as well.
	 * */
	public String GetPageByNumber(Integer pageNumber){
		int backupcurrentPageIndex = currentPageNumberIndex;
		int backupcurrentChapterNumberIndex = currentChapterNumberIndex;
		
		currentPageNumberIndex = pageNumber;
		int findChapterIndexForPageNumber = 1;
		try {
			while(!currentEbookContentWithinChaptersAndPages.get(findChapterIndexForPageNumber).containsKey(currentPageNumberIndex)){
				findChapterIndexForPageNumber++;
			}
		} catch (Exception e) {
			/*
			 * The page is not found
			 * */
			currentPageNumberIndex = backupcurrentPageIndex;
			currentChapterNumberIndex = backupcurrentChapterNumberIndex;
			return "";
		}		
		currentChapterNumberIndex = findChapterIndexForPageNumber;
		
		
		if (currentEbookContentWithinChaptersAndPages != null
				&& currentPageNumberIndex >= 1) {
			String buffer = "";
			for(String paragraph:currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).get(currentPageNumberIndex)){
				buffer += paragraph;
			}
			
			return buffer;
		}

		/*
		 * The page is not found
		 * */
		currentPageNumberIndex = backupcurrentPageIndex;
		currentChapterNumberIndex = backupcurrentChapterNumberIndex;
		return "";
	}
	
	
	/*
	 * Finds the content of the page for which the parameters are according 
	 * Viktor ToDo: redesign the params
	 * */
	public String GetPageByBookmark(){
		
		if (currentEbookContentWithinChaptersAndPages != null
				&& currentPageNumberIndex >= 0) {
			String buffer = "";
			for(String paragraph:currentEbookContentWithinChaptersAndPages.get(currentChapterNumberIndex).get(currentPageNumberIndex)){
				buffer += paragraph;
			}
			
			return buffer;
		}

		return "";
	}

	/*
	 * Return the number of the first paragraph of the current page
	 * opened into the viewer. This method is supposed to be used for
	 * getting info for saving new bookmarks
	 * 
	 * ToDo: Smoke test xP
	 * */	
	public int GetCurrentFirstParagraphNumber(){
		int paragraphNumber = 1;
		int currentPage = this.currentPageNumberIndex -1;
		int currentChapter = this.currentChapterNumberIndex;
		
		try{
		while(currentChapter > 0){
			while(currentEbookContentWithinChaptersAndPages.get(currentChapter).containsKey(currentPage)){
				paragraphNumber += currentEbookContentWithinChaptersAndPages.get(currentChapter).get(currentPage).size();
				currentPage--;
			}
			currentChapter--;
		}
		}
		catch(Exception ex){
			return 0;
		}
		
		return paragraphNumber;
	}
}
