package com.evilreader.android;

import com.evilreader.android.dbcontroller.DBAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
