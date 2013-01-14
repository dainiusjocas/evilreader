package com.evilreader.android.library;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import com.evilreader.android.dbcontroller.DBAdapter;

public class EvilExporter {

	private Context _Context;
	private DBAdapter _DBAdapter;
	
	/**
	 * Constructor
	 * @param pContext
	 */
	public EvilExporter(Context pContext) {
		this._Context = pContext;
		this._DBAdapter = new DBAdapter(pContext);
	}
	
	/**
	 * Gets all the necessary info about the book represented by book ID and
	 * sends an email with that information.
	 * @param pBookId
	 */
	public void exportEvilNotes(String pBookId) {
		String aTitle = this._DBAdapter.getEvilBookTitle(pBookId);
		String anAuthor = this._DBAdapter.getEvilBookAuthor(pBookId);
		String aYear = this._DBAdapter.getEvilBookYear(pBookId);
		String anEmailSubject = prepareEmailSubject(anAuthor, aTitle, aYear);
		String aBodyOfNotes = "List of notes: \n" 
				+ getAllFormatedNotes(pBookId);
		String aBodyOfEmail = "Author: "
				+ anAuthor
				+ "\n"
				+ "Title: "
				+ aTitle
				+ "\n"
				+ "Year: "
				+ aYear
				+ "\n"
				+ "----------------------------------\n"
				+ aBodyOfNotes;
		sendEvilEmail(anEmailSubject, aBodyOfEmail);
	}
	
	/**
	 * Cursor: {NOTE_BOOK_ID, NOTE_CHAPTER, NOTE_PARAGRAPH, NOTE_BODY}
	 * @param pBookId
	 * @return
	 */
	private String getAllFormatedNotes(String pBookId) {
		String notes = "";
		this._DBAdapter.open();
		Cursor aCursor = this._DBAdapter.fetchAllNotes(pBookId);
		if (!aCursor.moveToFirst()) {
			aCursor.close();
			this._DBAdapter.close();
			return "No notes for this book";
		}
		do {
			notes = notes
					+ "- '"
					+ aCursor.getString(3)
					+ "' (ch: "
					+ aCursor.getString(1) 
					+ ", par:  "
					+ aCursor.getString(2)
					+ ");\n";
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return notes;
	}
	
	/**
	 * Gets all the highlights from the book and send then by email.
	 * @param pBookId
	 */
	public void exportEvilHighlights(String pBookId) {
		String aTitle = this._DBAdapter.getEvilBookTitle(pBookId);
		String anAuthor = this._DBAdapter.getEvilBookAuthor(pBookId);
		String aYear = this._DBAdapter.getEvilBookYear(pBookId);
		String anEmailSubject = prepareEmailSubject(anAuthor, aTitle, aYear);
		String aBodyOfHighlights = "List of highlights: \n" 
					+ getAllFormatedHighlights(pBookId);
		String aBodyOfEmail = "Author: "
				+ anAuthor
				+ "\n"
				+ "Title: "
				+ aTitle
				+ "\n"
				+ "Year: "
				+ aYear
				+ "\n"
				+ "----------------------------------\n"
				+ aBodyOfHighlights;
		sendEvilEmail(anEmailSubject, aBodyOfEmail);
	}
	
	/**
	 * Gets string representation of all the highlights stored in db
	 * Cursor is to those elements:
	 *   {HIGHLIGHT_BOOK_ID, HIGHLIGHT_CHAPTER, HIGHLIGHT_PARAGRAPH, 
			HIGHLIGHT_TEXT};
	 * @param pBookId
	 * @return
	 */
	public String getAllFormatedHighlights(String pBookId) {
		String highlights = "";
		this._DBAdapter.open();
		Cursor aCursor = this._DBAdapter.fetchHighlights(pBookId);
		if (!aCursor.moveToFirst()) {
			aCursor.close();
			this._DBAdapter.close();
			return "No highlights for this book";
		}
		do {
			highlights = highlights 
					+ "- '"
					+ aCursor.getString(3)
					+ "' (ch: "
					+ aCursor.getString(1) 
					+ ", par:  "
					+ aCursor.getString(2)
					+ ");\n";
		} while(aCursor.moveToNext());
		aCursor.close();
		this._DBAdapter.close();
		return highlights;
	}

	/**
	 * Get both notes and highlights of the book and sends them by email.
	 * @param pBookId
	 */
	public void exportEvilNotesAndHighlights(String pBookId) {
		String aTitle = this._DBAdapter.getEvilBookTitle(pBookId);
		String anAuthor = this._DBAdapter.getEvilBookAuthor(pBookId);
		String aYear = this._DBAdapter.getEvilBookYear(pBookId);
		String anEmailSubject = prepareEmailSubject(anAuthor, aTitle, aYear);
		String aBodyOfNotes = "List of notes: \n" 
					+ getAllFormatedNotes(pBookId);
		String aBodyOfHighlights = "List of highlights: \n" 
					+ getAllFormatedHighlights(pBookId);
		String aBodyOfEmail = "Author: "
				+ anAuthor
				+ "\n"
				+ "Title: "
				+ aTitle
				+ "\n"
				+ "Year: "
				+ aYear
				+ "\n"
				+ "----------------------------------\n"
				+ aBodyOfNotes
				+ "\n"
				+ "----------------------------------\n"
				+ aBodyOfHighlights;
		sendEvilEmail(anEmailSubject, aBodyOfEmail);
	}
	
	/**
	 * Prepares email subject. Output format: "<title> (<year>), by <author>
	 * @param pAuthor
	 * @param pTitle
	 * @param pYear
	 * @return
	 */
	private String prepareEmailSubject(String pAuthor, String pTitle,
			String pYear) {
		String emailSubject = pTitle + " (" + pYear + ") by " + pAuthor;
		return emailSubject;
	}
	
	/**
	 * Sends email with provided information.
	 * @param pEmailSubject
	 * @param pEmailText
	 */
	private void sendEvilEmail(String pEmailSubject, String pEmailText) {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, "");		  
		email.putExtra(Intent.EXTRA_SUBJECT, pEmailSubject);
		email.putExtra(Intent.EXTRA_TEXT, pEmailText);
		email.setType("message/rfc822");
		this._Context.startActivity(Intent.createChooser(email,
				"Choose an Email client :"));
	}
}
