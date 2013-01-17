/**
 * 
 */
package com.evilreader.android.dictionary;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Dainius Jocas
 *
 */
public class DictionaryChecker {
	// for dictionary
	private static final int REQUEST_DICTIONARY = 2;
	public static final String PICK_RESULT_ACTION  = "colordict.intent.action.PICK_RESULT";
	public static final String EXTRA_QUERY = "EXTRA_QUERY";
	public static final String EXTRA_FULLSCREEN = "EXTRA_FULLSCREEN";
	public static final String EXTRA_HEIGHT = "EXTRA_HEIGHT";
	public static final String EXTRA_WIDTH = "EXTRA_WIDTH";
	public static final String EXTRA_GRAVITY = "EXTRA_GRAVITY";
	public static final String EXTRA_MARGIN_LEFT = "EXTRA_MARGIN_LEFT";
	
	/**
	 * Call the ColorDict intent to display small window with translation of
	 * one word.
	 * @param pWord
	 * @param pContext
	 */
	public static void translateWord(String pWord, Context pContext) {
		Intent anDictIntent = new Intent(PICK_RESULT_ACTION);
		anDictIntent.putExtra(EXTRA_QUERY, pWord); //Search Query
		anDictIntent.putExtra(EXTRA_FULLSCREEN, false); //
   	 	anDictIntent.putExtra(EXTRA_HEIGHT, 400);
   	 	anDictIntent.putExtra(EXTRA_GRAVITY, Gravity.BOTTOM);
   	 	anDictIntent.putExtra(EXTRA_MARGIN_LEFT, 100);
   	 	if (!isIntentAvailable(pContext, anDictIntent)) {
   	 		Toast.makeText(pContext,
				"Install ColorDict.",
				Toast.LENGTH_LONG).show();
   	 	} else {
   	 	((Activity) pContext).startActivityForResult(anDictIntent,
   	 			REQUEST_DICTIONARY); 
   	 	}
	}
	
	/**
	 * Let' check if the ColorDict is available in the system.
	 * @param context
	 * @param intent
	 * @return
	 */
	private static boolean isIntentAvailable(Context context, Intent intent) {
		 final PackageManager packageManager = context.getPackageManager();
		 List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				 PackageManager.MATCH_DEFAULT_ONLY);
		 return list.size() > 0;  
		}

}
