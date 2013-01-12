package com.evilreader.android;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.view.View;

public class TESTActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		final Button libraryButton = (Button) findViewById(R.id.button1);
		libraryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent aIntent = new Intent(TESTActivity.this,
						com.evilreader.android.library.LibraryActivity.class);
				startActivity(aIntent);
			}
		});

		final Button evilBookViewerButton = (Button) findViewById(R.id.button2);
		evilBookViewerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent aIntent = new Intent(TESTActivity.this,
						com.evilreader.android.evilcontentcontroller.EpubContentActivity.class);
				startActivity(aIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}
}
