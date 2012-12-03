/**
 * 
 */
package com.evilreader.android.dbcontroller;

import android.database.Cursor;

/**
 * This interface describes behaviour of EVILBOOK table for EvilReader Android
 * application.
 * 
 * @author Dainius Jocas
 */
public interface EvilBookTable {
    
    /**
     * Constructs an Id for an EvilBook in that way: takes first word of title
     * concatenates it with publication year, first word of author and absolute
     * path. Similar to Google Scholar BibTeX format.
     * 
     * This is only a recommendation how construct an EvilBook Id.
     * 
     * @param String pTitle
     * @param String pAuthor
     * @param String pYear
     * 
     * @return String 
     */
    String constructEvilBookId(String pTitle, String pAuthor, String pYear, 
    		String pAbsolutePath);
    
    /**
     * Stores an EvilBook info into the DB
     * 
     * @param pTitle
     * @param pAuthor
     * @param pYear
     * @param pFilename
     * @param pAbsolutePath
     * @return long rowid
     */
    long storeEvilBook(String pTitle, String pAuthor, String pYear,
    		String pFilename, String pAbsolutePath);
    
    /**
     * Gets cursor for filenames of EvilBooks
     * 
     * @return Cursor
     */
    Cursor getFilenamesOfEvilBooks();
    
    /**
     * Gets titles of EvilBooks
     * 
     * @return Cursor
     */
    Cursor getTitlesOfEvilBooks();
    
    /**
     * Fetches a cursor to all EvilBooks
     * 
     * @return Cursor
     */
    Cursor fetchAllEvilBooks();
    
   /**
    * Set column EVILBOOK_IS_PRESENT to false for specified row id.
    * 
    * @param long pEvilBookRowId
    * @return boolean if true - OK, false - strange because it should be true.
    */
   boolean markEvilBookStatus(long pEvilBookRowId, String pTrueOrFalse);
   
// TODO(dainius) Cursor getAbsolutePathOfEvilBook(String pEvilBookId);
// TODO(dainius) boolean removeBook();
   
}
