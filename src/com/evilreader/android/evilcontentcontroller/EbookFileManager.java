package com.evilreader.android.evilcontentcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

public class EbookFileManager {
	// The singleton instance of this manager
	private static final EbookFileManager INSTANCE = new EbookFileManager();
	static int Width;
	static int Height;
	static int FontSize;
	static int LettersPerPageConstraint = 500;
	static int LinesPerPageConstraint;

	private Context contextWrapper;
	private ArrayList<String> currentEbookContentWithinPages;
	// if the index is equal to -1 that means that there is no content in the
	// EBookContent
	private int currentEBookContentIndex;

	// private constructor for Singleton purpose
	private EbookFileManager() {

	}
	
	// provides logic for calculating how much letters the control,
	// containing the content of the currently loaded book, should visualise without
	// needing to show scroll control for the user.
	private void InitLettersPerPageCount() {
		
	}
	
	private void InitiateEBookContent(Book book, int progress) {
		currentEbookContentWithinPages = new ArrayList<String>();
		currentEBookContentIndex = progress;
		InputStream inputStream = null;
		Scanner scanner = null;
				
		//Spine spine = null;
		
		try {
			int currentPageLetterCount = 0;
			int newPageCreatedFlag = 0;
			String buffer = "";
			
			for (SpineReference spineReference : book.getSpine()
					.getSpineReferences()) {
				inputStream = spineReference.getResource().getInputStream();
				scanner = new Scanner(inputStream, "UTF-8")
						.useDelimiter("</p>");
				;
				while (scanner.hasNext()) {
					String currentContent = scanner.next();
					
					if(currentContent.length() <= LettersPerPageConstraint/2)
					{
						buffer += currentContent;
					}
					
					if(buffer.length() >= LettersPerPageConstraint){
						currentEbookContentWithinPages.add(buffer);
						buffer = "";
					}
					/*	
					if(newPageCreatedFlag == 0 && contentForLoading.length() != 0) {
						//currentPageLetterCount
					}
						
					currentEbookContentWithinPages.add(contentForLoading);*/
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static EbookFileManager getInstance() {
		return EbookFileManager.INSTANCE;
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
				this.InitiateEBookContent(currentEBook, -1);
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
				this.InitiateEBookContent(currentEBook, -1);
			} else {
				return;
			}

		} catch (Exception e) {

		}
	}

	@SuppressWarnings("unused")
	public String GetFirstPage() {
		if (currentEbookContentWithinPages != null) {
			currentEBookContentIndex = 0;

			return currentEbookContentWithinPages.get(currentEBookContentIndex);
		}

		return "";
	}

	@SuppressWarnings("unused")
	public String GetLastPage() {
		if (currentEbookContentWithinPages != null) {
			currentEBookContentIndex = currentEbookContentWithinPages.size() - 1;

			return currentEbookContentWithinPages.get(currentEBookContentIndex);
		}

		return "";
	}

	public String GetNextPage() {
		currentEBookContentIndex++;
		if (currentEbookContentWithinPages != null
				&& currentEbookContentWithinPages.size() > currentEBookContentIndex) {

			return currentEbookContentWithinPages.get(currentEBookContentIndex);
		}

		return "";
	}

	public String GetPreviousPage() {
		currentEBookContentIndex--;
		if (currentEbookContentWithinPages != null
				&& currentEBookContentIndex >= 0) {

			return currentEbookContentWithinPages.get(currentEBookContentIndex);
		}

		return "";
	}

}
