package com.evilreader.android.bookviewer;

import com.evilreader.android.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class EpubContentActivity extends Activity {
	// private constants
	// TODO(Viktor): in the future we'll use more sophisticated techniques
	private final int resourceIDSample = R.raw.asd;

	// private members
	private WebView webviewPage1;
	private TextView textView1;

	private int currentChapterIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_epub_content);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		webviewPage1 = (WebView) findViewById(R.id.webView1);
		int width = webviewPage1.getWidth();
		int height = webviewPage1.getHeight();
		WebSettings webViewPage1Settings = webviewPage1.getSettings();
		//webViewPage1Settings.get
		int fontSize = webViewPage1Settings.getDefaultFontSize();

		// textView1 = (TextView) findViewById(R.id.textView1);

		EbookFileManager ebookFileManagerInstance = EbookFileManager
				.getInstance();
		ebookFileManagerInstance.Init(getApplicationContext());
		EbookFileManager.Height = height;
		EbookFileManager.Width = width;
		EbookFileManager.FontSize = fontSize;

		try {
			ebookFileManagerInstance
					.LoadEpubBookByRawResourceID(resourceIDSample);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// The button for going through chapters forward
		final Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentChapterIndex++;

				String chapterContent = EbookFileManager.getInstance()
						.GetNextPage();

				if (chapterContent != null || chapterContent != "") {

					webviewPage1.setClickable(true);
					webviewPage1.loadData(chapterContent, "text/html", null);

					// textView1.setText(chapterContent);
				}
			}
		});

		// The button for going through chapters backward
		final Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (currentChapterIndex > 0) {
					currentChapterIndex--;

					String chapterContent = EbookFileManager.getInstance()
							.GetPreviousPage();

					if (chapterContent != null || chapterContent != "") {

						webviewPage1.setClickable(true);
						webviewPage1
								.loadData(chapterContent, "text/html", null);

						/* textView1.setText(chapterContent); */
					}
				}

			}
		});

		// textView1.setText(ebookFileManagerInstance.GetFirstPage());
		webviewPage1.loadData(ebookFileManagerInstance.GetFirstPage(),
				"text/html", "UTF-8");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_epub_content, menu);
		return true;
	}
}
