package com.evilreader.android;

import com.evilreader.android.dbcontroller.DBAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	/* Database integration */
	private DBAdapter mDbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mDbAdapter = new DBAdapter(this);
        this.mDbAdapter.open();
        this.mDbAdapter.close();
        final Button goToLibrary = (Button) findViewById(R.id.go_to_library);
        goToLibrary.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				//Intent mIntent = getIntent();
				Intent mIntent = new Intent(MainActivity.this, com.evilreader.android.library.LibraryActivity.class);
				startActivity(mIntent);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
