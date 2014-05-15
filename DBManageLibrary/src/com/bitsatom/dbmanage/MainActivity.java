package com.bitsatom.dbmanage;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.bitsatom.dbmanage.android.DBHelper;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DBHelper dbHelper = new DBHelper(this.getBaseContext(), "test", null, 110);
		SQLiteDatabase db =  dbHelper.getReadableDatabase();
		Log.d("Main Activity : ", "" + db.getVersion());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
