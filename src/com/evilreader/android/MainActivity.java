package com.evilreader.android;

import com.evilreader.android.dbcontroller.DBAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	/* Database integration */
	private DBAdapter mDbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String value = getIntent().getExtras().getString("absolute_path");
        Log.e("EVILREADER", value);
        TextView aTextView = (TextView) findViewById(R.id.textView1);
        Log.e("EVILREADER", "" + aTextView.getText());
        aTextView.setText(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
