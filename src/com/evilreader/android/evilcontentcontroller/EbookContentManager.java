package com.evilreader.android.evilcontentcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.evilreader.android.evilcontentcontroller.EpubContentActivity.EvilWebViewFragment;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

/*
 * This class is responsible for loading the epub file into the memmory
 * by deviding it's conent into a structure of chapters and pages where
 * every page has set of parapgraphs. Pages are defined as on page has atmost LettersPerPageConstraint
 * in which the html tags are included
 * */
public class EbookContentManager {
	// The singleton instance of this manager
	private static final EbookContentManager INSTANCE = new EbookContentManager();
	
	/*
	 * These fields are ment for the separation logic
	 * Not implemented properly.
	 * */
	static int Width;
	static int Height;
	static int FontSize;
	static int LettersPerPageConstraint = 600;
	static int LinesPerPageConstraint;

	private Activity activity;
	private Context contextWrapper;
	/*
	 * A structure for mapping a single page to it's paragraphs and chapters
	 * */
	private HashMap<Integer, Pair<Integer, ArrayList<Integer>>> currentEbookContentWithinChaptersAndPages;
	// EBookContent
	private String currentBookId;
	private int currentPageNumberIndex;
	private int currentChapterNumberIndex;
	private List<EvilWebViewFragment> fragments;
	private int lastPageNumberForTheCurrentBook = 0;

	// private constructor for Singleton purpose
	private EbookContentManager() {

	}
	
	// provides logic for calculating how much letters the control,
	// containing the content of the currently loaded book, should visualise without
	// needing to show scroll control for the user.
	// ToDo - this functionality is still not reaching a desired result
	private void InitLettersPerPageCount() {
		
	}
	
	@SuppressLint("UseSparseArrays")
	/*
	 * Loading the book into the UI.
	 * @param progressPage: used for Go To page functionality
	 * @param progressChapter: used for Go To chapter functionality 
	 * ToDo: implement progressPage and progressChapter logic;
	 * */
	private void InitiateEBookContent(Book book, String bookId, int progressPage, int progressChapter) {
		currentBookId = bookId;
		currentEbookContentWithinChaptersAndPages = new HashMap<Integer, Pair<Integer, ArrayList<Integer>>>();
		currentPageNumberIndex = progressPage;
		currentChapterNumberIndex = progressChapter;
		InputStream inputStream = null;
		Scanner scanner = null;
		
		try {
			//Counters for mapping the currently loaded book with it's
			//current state - page number, chapter number, paragraph number
			int currentPageNumber = 1;
			int currentParagraphNumber = 1;
			int chapterNumber = 0;
			List<Integer> paragraphs = new ArrayList<Integer>();
			
			int bufferLimiter = 0;
			String bufferPage = "";
			
			
			for (SpineReference spineReference : book.getSpine().getSpineReferences()) {				
				inputStream = spineReference.getResource().getInputStream();
				scanner = new Scanner(inputStream, "UTF-8").useDelimiter("</p>");
				
				chapterNumber++;
				
				/*
				 * this if is for when the number of characters is below 700 but there are
				 * no more characters for the current chapter
				 * */
				
				if(bufferLimiter != 0){
					/*
					 * Creating the view for the UI representing one page of the loaded book.
					 * */
					
					EvilWebViewFragment fragment = new EvilWebViewFragment();
					fragment.SetWebView(this.contextWrapper, bufferPage, currentBookId,activity);
					fragments.add(fragment);
					
					currentEbookContentWithinChaptersAndPages.put(currentPageNumber, new Pair<Integer,ArrayList<Integer>>(chapterNumber, new ArrayList<Integer>(paragraphs)));
					
					currentPageNumber++;
					paragraphs = new ArrayList<Integer>();
					bufferLimiter = 0;
					bufferPage = "";
				}
				
				bufferPage = "";
				while (scanner.hasNext()) {
					String currentContent = scanner.next();
					
					bufferLimiter += currentContent.length();
					bufferPage += currentContent;
					paragraphs.add(currentParagraphNumber);
					currentParagraphNumber++;
					
					if(bufferLimiter < LettersPerPageConstraint/2){
						
						continue;
					}
					
					else if(bufferLimiter >= LettersPerPageConstraint){
						/*
						 * Creating the view for the UI representing one page of the loaded book.
						 * */
						
						EvilWebViewFragment fragment = new EvilWebViewFragment();
						fragment.SetWebView(this.contextWrapper, bufferPage,currentBookId, activity);
						fragments.add(fragment);
						
						currentEbookContentWithinChaptersAndPages.put(currentPageNumber, new Pair<Integer,ArrayList<Integer>>(chapterNumber, new ArrayList<Integer>(paragraphs)));
						
						bufferLimiter = 0;
						bufferPage = "";
						currentPageNumber++;
						paragraphs = new ArrayList<Integer>();
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
	public void Init(Context ContextWrapper, Activity currentActivity) {
		this.contextWrapper = ContextWrapper;
		this.activity = currentActivity;
	}

	// Method for downloading new epub books to the local resource storage of
	// the Application and load their content
	@SuppressWarnings("unused")
	public void LoadEpubBookByUrl(Uri url) {
		// TODO(Viktor): Implement
	}

	// Method for loading the content from the pointed file
	@SuppressWarnings("unused")
	public void LoadEpubBookByAbsolutePath(String filePath, String bookId, List<EvilWebViewFragment> fragments) {
		try {
			// Obsolete
			this.fragments = fragments;
			FileInputStream inputStream = new FileInputStream(new File(filePath));

			Book currentEBook = inputStream != null ? (new EpubReader())
					.readEpub(inputStream) : null;
					
			/*TableOfContents table = currentEBook.getTableOfContents();
			List<TOCReference> tocList = table.getTocReferences();
			TOCReference toc = tocList.get(1);
			Resource resource = toc.getResource();
			Reader reader = resource.getReader();
			reader.*/
			
			if (currentEBook != null) {
				this.InitiateEBookContent(currentEBook, bookId, 1, 1);
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
	public void LoadEpubBookByRawResourceID(int resourceID, String bookId) {
		try {
			// Obsolete
			InputStream inputStream = contextWrapper.getResources()
					.openRawResource(resourceID);

			Book currentEBook = inputStream != null ? (new EpubReader())
					.readEpub(inputStream) : null;

			if (currentEBook != null) {
				this.InitiateEBookContent(currentEBook, bookId, 1, 1);
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
	public String GetFirstPageContent() {
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			currentChapterNumberIndex = 1;
			currentPageNumberIndex = 1;			
			
			return this.fragments.get(currentPageNumberIndex).GetWebViewContent();
		}

		return "";
	}

	@SuppressWarnings("unused")
	public String GetLastPageContent() {
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			currentPageNumberIndex = currentEbookContentWithinChaptersAndPages.size();
			currentChapterNumberIndex = currentEbookContentWithinChaptersAndPages.get(currentPageNumberIndex).first;
			
			return this.fragments.get(currentPageNumberIndex).GetWebViewContent();
		}

		return "";
	}

	public String GetNextPageContent() {
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			if(currentEbookContentWithinChaptersAndPages.size() > currentPageNumberIndex){
				currentPageNumberIndex++;
			}
			
			if(currentEbookContentWithinChaptersAndPages.get(currentPageNumberIndex).first != currentPageNumberIndex){
				currentChapterNumberIndex++;
			}		
		
			
			return this.fragments.get(currentPageNumberIndex).GetWebViewContent();
		}

		return "";
	}

	public String GetPreviousPageContent() {
		if (currentEbookContentWithinChaptersAndPages != null) {
			
			if(currentPageNumberIndex > 1){
				currentPageNumberIndex--;
			}
			
			if(currentEbookContentWithinChaptersAndPages.get(currentPageNumberIndex).first != currentPageNumberIndex){
				currentChapterNumberIndex--;
			}
			
			return this.fragments.get(currentPageNumberIndex).GetWebViewContent();
		}

		return "";
	}

	/*
	 * If the pageNumber is not presented into the loaded book
	 * it will return an empty string. If the page is found then
	 * the indexes to the chapter and the number of this page are going
	 * to be changed as well.
	 * */
	public String GetPageContentByPageNumber(Integer pageNumber){
		
		if((pageNumber >= 1 || pageNumber <= currentEbookContentWithinChaptersAndPages.size()) && currentEbookContentWithinChaptersAndPages != null){
			this.currentPageNumberIndex = pageNumber;
			this.currentChapterNumberIndex = currentEbookContentWithinChaptersAndPages.get(currentPageNumberIndex).first;
			
			return this.fragments.get(currentPageNumberIndex).GetWebViewContent();
		}
		
		return "";
	}
	
	
	/*
	 * Finds the content of the page for which the parameters are according
	 * */
	public int GetPageNumberByBookmark(int[] bookMark){
		
		if (currentEbookContentWithinChaptersAndPages != null
				&& currentPageNumberIndex >= 0) {
			for (int i = 1; i <= currentEbookContentWithinChaptersAndPages.size(); i++) {
				if(bookMark[1] == this.currentEbookContentWithinChaptersAndPages.get(i).second.get(0)){
					currentPageNumberIndex = i-1;
					currentChapterNumberIndex = bookMark[0];
					return currentPageNumberIndex;
				}
			}			
		}

		return -1;
	}
	
	public int GetPageNumberByChapterNumber(int chapterNumber){
		if (currentEbookContentWithinChaptersAndPages != null
				&& currentPageNumberIndex >= 0) {
			for (int i = 1; i <= currentEbookContentWithinChaptersAndPages.size(); i++) {
				if(chapterNumber == this.currentEbookContentWithinChaptersAndPages.get(i).first){
					currentPageNumberIndex = i;
					currentChapterNumberIndex = chapterNumber;
					return currentPageNumberIndex;
				}
			}
			
			
		}
		
		return -1;
	}

	/*
	 * Return the number of the first paragraph of the current page
	 * opened into the viewer. This method is supposed to be used for
	 * getting info for saving new bookmarks
	 * 
	 * ToDo: Smoke test xP
	 * */	
	public int GetCurrentFirstParagraphNumber(){
		return this.currentEbookContentWithinChaptersAndPages.get(currentPageNumberIndex).second.get(0);
	}
}
