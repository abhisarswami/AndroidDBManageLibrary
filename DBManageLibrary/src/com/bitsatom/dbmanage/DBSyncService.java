package com.bitsatom.dbmanage;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.bitsatom.dbmanage.android.DBHelper;

public class DBSyncService extends IntentService {
	
	public enum SyncServiceParam{
		DB_NAME("DB_NAME"),
		DB_VERSION("DB_VERSION");
		private String param;
		private SyncServiceParam(String param){
			this.param = param;
		}
		
		@Override
		public String toString(){
			return param;
		}
	}
	
	public DBSyncService() {
		super("DB Sync Service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		String dbName = (String)bundle.get(SyncServiceParam.DB_NAME.toString());
		Integer version = (Integer)bundle.get(SyncServiceParam.DB_VERSION.toString());
		DBHelper dbHelper = new DBHelper(this.getBaseContext(), dbName, null, version);
		SQLiteDatabase db =  dbHelper.getReadableDatabase();
	}

}
