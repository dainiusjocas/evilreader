package com.evilreader.android.evilcontentcontroller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.widget.EditText;
import com.evilreader.android.dbcontroller.DBAdapter;

/**
 * This class is for saving bookmarks, highlights and and notes to the db.
 * @author user
 *
 */
public class GemManager {
	// Context that we are executing
	private Context _Context;
	// Database controller
	private DBAdapter _DBAdapter;
	
	/**
	 * Contructor
	 * @param pContext
	 */
	public GemManager(Context pContext) {
		this._Context = pContext;
		this._DBAdapter = new DBAdapter(this._Context);
	}
	
	public void makeNote(String pBookId, String pChapter, String pParagraph) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this._Context);

   	 	alert.setTitle("Make a note");

   	 	// Set an EditText view to get user input 
   	 	final EditText input = new EditText(this._Context);
   	 	alert.setView(input);
   	 	
   	 	final String aBookId = pBookId;
   	 	final String aChapter = pChapter;
   	 	final String aParagraph = pParagraph;

   	 	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
   	 		public void onClick(DialogInterface dialog, int whichButton) {
   	 			CharSequence value = input.getText();
   	 			storeNote(value.toString(), aBookId, aChapter, aParagraph);
   	 		}
   	 	});

   	 	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
   	 		public void onClick(DialogInterface dialog, int whichButton) {
   	 			// Canceled.
   	 		}
   	 	});

   	 	alert.show();
	}
	
	/**
	 * Puts note and its location to the database
	 * @param pNote
	 * @param pBookId
	 * @param pChapter
	 * @param pParagraph
	 */
	public void storeNote(String pNote, String pBookId, String pChapter, 
			String pParagraph) {
		this._DBAdapter.open();
		this._DBAdapter.storeNote(pNote, pBookId, pChapter, pParagraph);
		this._DBAdapter.close();
	}
	
	/**
	 * Gets string representation of all notes
	 * TODO(dainius): now it is not much useful. Just for testing purposes
	 * @param pBookId
	 * @return
	 */
	public String getNotes(String pBookId) {
		String notes = "";
		this._DBAdapter.open();
		Cursor aCursor = this._DBAdapter.fetchAllNotes(pBookId);
		if (!aCursor.moveToFirst()) {
			aCursor.close();
			this._DBAdapter.close();
			return "No notes for this book";
		}
		do {
			notes = notes + 
					aCursor.getString(0)
					+ " "
					+ aCursor.getString(1) 
					+ " "
					+ aCursor.getString(2)
					+ " "
					+ aCursor.getString(3);
			notes = notes + "\n";
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return notes;
	}
	
	/**
	 * Puts bookmark into the database
	 * @param pBookId
	 * @param pChapter
	 * @param pParagraph
	 */
	public void saveBookmark(String pBookId, String pChapter, 
			String pParagraph) {
		this._DBAdapter.open();
		this._DBAdapter.storeBookmark(pBookId, pChapter, pParagraph);
		this._DBAdapter.close();
	}
	
	/**
	 * Gets text representation of all the bookmarks from db
	 * TODO(dainius): redo
	 * @param pBookId
	 * @return
	 */
	public String getBookmarks(String pBookId) {
		String bookmarks = "";
		this._DBAdapter.open();
		Cursor aCursor = this._DBAdapter.fetchBookmarks(pBookId);
		if (!aCursor.moveToFirst()) {
			aCursor.close();
			this._DBAdapter.close();
			return "No Bookmarks for this book";
		}
		do {
			bookmarks = bookmarks + aCursor.getString(0)
					+ " "
					+ aCursor.getString(1) 
					+ " "
					+ aCursor.getString(2);
			bookmarks = bookmarks + "\n";
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return bookmarks;
	}
	
	/**
	 * Saves highlighted text and its location in the database
	 * @param pHighlightedText
	 * @param pBookId
	 * @param pChapter
	 * @param pParagraph
	 */
	public void saveHighlight(String pHighlightedText, String pBookId,
			String pChapter, String pParagraph) {
		this._DBAdapter.open();
		this._DBAdapter.storeHighlight(pBookId, pChapter, pParagraph, 
				pHighlightedText);
		this._DBAdapter.close();
	}
	
	/**
	 * Gets string representation of all the highlights stored in db
	 * Cursor is to those elements:
	 *   {HIGHLIGHT_BOOK_ID, HIGHLIGHT_CHAPTER, HIGHLIGHT_PARAGRAPH, 
			HIGHLIGHT_TEXT};
	 * @param pBookId
	 * @return
	 */
	public String getHighlights(String pBookId) {
		String highlights = "";
		this._DBAdapter.open();
		Cursor aCursor = this._DBAdapter.fetchHighlights(pBookId);
		if (!aCursor.moveToFirst()) {
			aCursor.close();
			this._DBAdapter.close();
			return "No highlights for this book";
		}
		do {
			highlights = highlights + aCursor.getString(0)
					+ " "
					+ aCursor.getString(1) 
					+ " "
					+ aCursor.getString(2)
					+ " "
					+ aCursor.getString(3);
			highlights = highlights + "\n";
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return highlights;
	}

}
