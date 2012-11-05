/**
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * Dainius Jocas <dainius(dot)jocas(at)gmail(dot)com> wrote this file. 
 * As long as you retain this notice you can do whatever you want with this 
 * stuff. If we meet some day, and you think this stuff is worth it, you can 
 * buy me a beer in return.
 * ----------------------------------------------------------------------------
 */

package com.evilreader.android.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is dedicated to control EvilReader communication with database. 
 * @author Dainius Jocas
 *
 */
public class DBAdapter {
	/*
	 * Names and definitions for a DB.
	 */
	public static final String DATABASE_NAME = "evilreaderdb";
	public static final int DATABASE_VERSION = 1;
	
	/*
	 * Name definitions for Notes table. For only one table for now.
	 */
	public static final String NOTES_TABLE_TITLE = "notes";
	public static final String NOTES_ROWID = "notes_id";
	public static final String NOTES_BODY  = "body";
	public static final String NOTES_BOOK_ID = "book_id";
	/* 
	 * TODO(dainius): maybe it would be good somehow to relate a note with 
	 * specific location in an ebook? I think it makes sense.
	 */
	
	//TODO(dainius): describe all the tables;
	//TODO(dainius): add code for all the table handlers. 
	
	/**
     * Database creation sql statement. For now its just one table NOTES!
     */
    private static final String DATABASE_CREATE =
        "create table " 
        + NOTES_TABLE_TITLE 
        + " ( "
        + NOTES_ROWID
        + " integer primary key autoincrement, "
        + NOTES_BODY
        + " text not null, "
        + NOTES_BOOK_ID 
        + " text not null);";
	
	private static final String TAG = "DBAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private final Context mContext;
	
	/**
	 * Constructor - takes the context to allow database to be created or 
	 *   opened.
	 *   
	 *   @param context within which our DB should work
	 */
	public DBAdapter(Context context) {
		this.mContext = context;
	}

	/*
	 * Method for opening DB. If DB cannot be created then SQLException will
	 * be thrown.
	 * 
	 * @return this (self reference)
	 * @throws SQLException if DB cannot be neither opened nor created
	 */
	public DBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/*
     * Releasing resourses related to DB connection.
     * TODO(dainius): when should this method be used?
     */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * We'll see what we can do with this static class.
	 * In documentation it is written that SQLiteOpenHelper class usually is 
	 * use to manage database creation and version management. 
	 * @author dainius
	 *
	 */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_TITLE);
            onCreate(db);
        }
    }
    
    /************************************************************************** 
     * This series of functions is dedicated to work with NOTES table
     *************************************************************************/
    
    /**
     * Create one entry in the DB table NOTES when body of note and bookId of
     * the eBook to which note is assigned are provided. Is success then rowId 
     * is returned, otherwise -1.
     *  
     * @param body - actual content of the note
     * @param bookId - to which book note is assigned
     * @return rowId or -1 if failed
     */
    public long createNote(String body, String bookId) {
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(NOTES_BODY, body);
    	initialValues.put(NOTES_BOOK_ID, bookId);
    	return mDb.insert(NOTES_TABLE_TITLE, null, initialValues);
    }
    
    /**
     * Delete a note with a given note ID
     * @param rowId - id of the note that should be deleted
     * @return true if success, otherwise - false
     */
    public boolean deleteNote(long rowId) {
    	return mDb.delete(NOTES_TABLE_TITLE, NOTES_BOOK_ID + "=" + rowId,
    			null) > 0;
    }
    
    /**
     * Get all notes for a specific book.
     * @return cursor over all selected notes for a specific book.
     */
    public Cursor fetchAllNotes(long bookId) {
    	Cursor cursor = mDb.query(NOTES_TABLE_TITLE,
    			new String[] {NOTES_ROWID, NOTES_BODY},
    			NOTES_BOOK_ID + "=" + bookId, null, null, null,
    			null);
    	return cursor;
    }
    
    /**
     * Return a cursor positioned at the note that matches the given noteId.
     * 
     * @param noteId
     * @return cursor positioned to a matching note
     * @throws SQLException if note cannot be found/retrieved
     */
    public Cursor fetchNote(long noteId) throws SQLException {
    	Cursor mCursor = mDb.query(NOTES_TABLE_TITLE, 
    			new String[] {NOTES_ROWID, NOTES_BODY},
    			NOTES_ROWID + "=" + noteId, null, null, null, null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }
    
    /**
     * Updates specific note with specific content. If success the returns true,
     * otherwise false
     * 
     * @param noteId
     * @param body - new content of the note
     * @return isSuccess - true when update was successful, otherwise false
     */
    public boolean updateNote(long noteId, String body) {
    	ContentValues args = new ContentValues();
    	args.put(NOTES_BODY, body);
    	boolean isSuccess = mDb.update(NOTES_TABLE_TITLE, args,
    			NOTES_ROWID + "=" + noteId, null) > 0;
    	return isSuccess;
    }
    /*************************************************************************/
}
