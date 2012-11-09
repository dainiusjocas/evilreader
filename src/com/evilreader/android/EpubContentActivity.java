package com.evilreader.android;

import com.evilreader.android.ContentProviders.EbookFileManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class EpubContentActivity extends Activity {
	//private constants
	private final int resourceIDSample = R.raw.asd;
	
	//private members
	private WebView webviewPage1;
	private int currentChapterIndex;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_content);
        
        webviewPage1 = (WebView) findViewById(R.id.webView1);
        webviewPage1.setOnDragListener(new View.OnDragListener() {
			
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
        
        EbookFileManager instance = EbookFileManager.getInstance();
        instance.Init(getApplicationContext());
        try {
			instance.LoadBookByResourceID(resourceIDSample);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String chapterContent = instance.GetEpubBookChapterContentByChapterNumber(resourceIDSample, currentChapterIndex);
        
        if(chapterContent != null || chapterContent != ""){
	        webviewPage1.setClickable(true);
	        webviewPage1.loadData(chapterContent, "text/html", null);
        }
                
        //The button for going through chapters forward
        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentChapterIndex++;
                
                String chapterContent = EbookFileManager.getInstance().GetEpubBookChapterContentByChapterNumber(resourceIDSample, currentChapterIndex);
                
                if(chapterContent != null || chapterContent != ""){
        	        webviewPage1.setClickable(true);
        	        webviewPage1.loadData(chapterContent, "text/html", null);
                }
            }
        });
        
      //The button for going through chapters backward
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(currentChapterIndex > 0)
				{
					currentChapterIndex--;
					
					String chapterContent = EbookFileManager.getInstance().GetEpubBookChapterContentByChapterNumber(resourceIDSample, currentChapterIndex);
	                
	                if(chapterContent != null || chapterContent != ""){
	        	        webviewPage1.setClickable(true);
	        	        webviewPage1.loadData(chapterContent, "text/html", null);
	                }
				}
				
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_epub_content, menu);
        return true;
    }
}
