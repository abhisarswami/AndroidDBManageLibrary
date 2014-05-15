package com.bitsatom.dbmanage.parser;

import java.io.IOException;

import android.content.res.AssetManager;
import android.util.Log;

import com.bitsatom.dbmanage.android.DBManageException;
import com.example.dbmanage.data.VersionMetaStructure;

public class DBAssetParser {

	public static final String DB_ASSET_FOLDER = "db";
	
	private Object lock = new Object();

	public VersionMetaStructure parse(AssetManager assetManager) throws DBManageException {
		return parse(DB_ASSET_FOLDER, assetManager);
	}

	public VersionMetaStructure parse(String baseDBAssetFolder,
			AssetManager assetManager) throws DBManageException {
		VersionMetaStructure versions = new VersionMetaStructure(baseDBAssetFolder);
		try {
			synchronized (lock) {
				String[] assetList = assetManager.list(baseDBAssetFolder);
				for (String strVersion : assetList) {
					Log.d(DBAssetParser.class.toString(), "Version : " + strVersion);
					versions.addVersion(strVersion);
				}
			}
		} catch (IOException e) {
			throw new DBManageException("Error getting assets under '" + baseDBAssetFolder + "'");
		} catch (NumberFormatException e) {	
			throw new DBManageException("Invalid version number");
		}
		return versions;
	}

}
