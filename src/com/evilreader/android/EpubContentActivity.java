package com.evilreader.android;

import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;

import com.evilreader.android.ContentProviders.EbookFileManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class EpubContentActivity extends Activity {
	//private members
	public String someText = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_content);
        
        EbookFileManager ebookFileManager = new EbookFileManager();
        
        Book book = ebookFileManager.AddEpubFileToEbookList(getResources().openRawResource(R.raw.asd));
        
        Spine spine = book.getSpine();
        
        for (SpineReference bookSection : spine.getSpineReferences()) {
            Resource res = bookSection.getResource();
                try {
                    InputStream is = res.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    someText += s.hasNext() ? s.next() : "";
                    //do something with stream
                } catch (IOException e) {
                }               
        }
        
        WebView webview = new WebView(this);
        webview.setClickable(true);
        webview.loadData(someText, "text/html", null);
        setContentView(webview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_epub_content, menu);
        return true;
    }
}
