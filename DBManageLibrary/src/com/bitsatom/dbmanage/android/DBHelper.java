package com.bitsatom.dbmanage.android;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bitsatom.dbmanage.parser.DBAssetParser;
import com.bitsatom.dbmanage.parser.SQLParser;
import com.example.dbmanage.data.VersionMetaStructure;

public class DBHelper extends SQLiteOpenHelper {

	Context ctx;
	DBAssetParser assetParser;
	VersionMetaStructure versions;
	SQLParser parser;
	int version;
	private static final String TAG = SQLiteOpenHelper.class.getSimpleName();

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		this(context, name, factory, version, null);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version, String dbScriptBaseFolder) {
		super(context, name, factory, version);
		this.ctx = context;
		this.version = version;
		parser = new SQLParser();
		assetParser = new DBAssetParser();
		try {
			if (dbScriptBaseFolder == null) {
				versions = assetParser.parse(context.getAssets());
			} else {
				versions = assetParser.parse(dbScriptBaseFolder,
						context.getAssets());
			}
		} catch (DBManageException e) {
			throw new RuntimeException("Error parsing assets", e);
		}
		Log.i(TAG, "Parsed the db assets structure");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			List<Integer> lstVersions = versions.getSortedVersions();
			Log.i(TAG, "Creating Database version : " + lstVersions.get(0));
			executeSQL(versions.getCreateUri(), db);
			Iterator<Integer> lstIterator = lstVersions.listIterator(1);
			while (lstIterator.hasNext()) {
				Integer version = lstIterator.next();
				if (version <= this.version) {
					Log.d(TAG, "Updating to version " + version);
					executeSQL(versions.getUpgradeUri(version), db);
				}
			}
		} catch (IOException exception) {
			throw new RuntimeException("Database creation failed", exception);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVesion, int newVersion) {
		Log.i(TAG, "Updating Database from version : " + oldVesion + " to "
				+ newVersion);
		List<Integer> lstVersions = versions.getSortedVersions();
		int index = lstVersions.indexOf(Integer.valueOf(newVersion));
		boolean doneUpgrade = false;
		if (newVersion > oldVesion && index != -1) {
			Iterator<Integer> lstIterator = lstVersions.listIterator(index);
			try {
				while (lstIterator.hasNext() && !doneUpgrade) {
					Integer version = lstIterator.next();
					if (version > oldVesion && version <= newVersion) {
						Log.d(TAG, "Updating to version " + version);
						executeSQL(versions.getUpgradeUri(version), db);
					}
					if (version >= newVersion) {
						doneUpgrade = true;
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Database upgrade failed", e);
			}
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVesion, int newVersion) {
		Log.i(TAG, "Downgrading Database from version : " + oldVesion + " to "
				+ newVersion);
		List<Integer> lstVersions = versions.getSortedVersions();
		int index = lstVersions.indexOf(Integer.valueOf(oldVesion));
		boolean doneDowngrade = false;
		if (newVersion < oldVesion
				&& lstVersions.contains(Integer.valueOf(newVersion))) {
			ListIterator<Integer> lstIterator = lstVersions.listIterator(index);
			try {
				while (lstIterator.hasPrevious() && doneDowngrade) {
					Integer version = lstIterator.previous();
					if (version < oldVesion && version >= newVersion) {
						Log.d(TAG, "Downgrading to version '" + version + "'");
						executeSQL(versions.getDowngradeUri(version), db);
					}
					if (version <= newVersion) {
						doneDowngrade = true;
					}
				}

			} catch (IOException e) {
				throw new RuntimeException("Database downgrade failed", e);
			}
		}

	}

	public void executeSQL(String sqlFile, SQLiteDatabase db)
			throws SQLException, IOException {
		for (String sqlStatement : parser
				.parseSqlFile(sqlFile, ctx.getAssets())) {
			Log.d(TAG, "Executing SQL statement '" + sqlStatement + "'");
			db.execSQL(sqlStatement);
		}
	}
}
