package com.example.dbmanage.data;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionMetaStructure {

	// List<Version> versions;
	List<Integer> intSortedVersions;
	Map<Integer, ScriptUri> mapScriptUri;
	private static final String fileSeparator = "/";
	public static final String CREATE_SQL_FILE = "create.sql";
	private String dbAssetFolder;

	@SuppressLint("UseSparseArrays")
	public VersionMetaStructure(String dbAssetFolder) {
		this.dbAssetFolder = dbAssetFolder;
		intSortedVersions = new ArrayList<Integer>();
		mapScriptUri = new HashMap<Integer, ScriptUri>();
	}

	public void addVersion(String version) throws NumberFormatException {
		intSortedVersions.add(Integer.valueOf(version));
		mapScriptUri.put(Integer.valueOf(version), new ScriptUri(version));
	}

	public List<Integer> getSortedVersions() {
		if (intSortedVersions.size() == 0)
			return null;
		Collections.sort(intSortedVersions);
		return Collections.unmodifiableList(intSortedVersions);
	}

	public String getUpgradeUri(Integer version) {
		if (mapScriptUri.get(version) == null) {
			return null;
		}
		return ((ScriptUri) mapScriptUri.get(version)).getUpgradeUri();
	}

	public String getDowngradeUri(Integer version) {
		if (mapScriptUri.get(version) == null) {
			return null;
		}
		return ((ScriptUri) mapScriptUri.get(version)).getDowngradeUri();
	}

	public String getCreateUri() {
		String version;
		if (intSortedVersions.size() == 0)
			return null;
		Collections.sort(intSortedVersions);
		version = mapScriptUri.get(intSortedVersions.get(0)).getVersion();
		return dbAssetFolder + fileSeparator + version + fileSeparator
				+ CREATE_SQL_FILE;
	}

	private class ScriptUri {

		private String version;
		private StringBuffer upgradeUri = new StringBuffer();
		private StringBuffer downgradeUri = new StringBuffer();

		public static final String UPGRADE_SQL_FILE = "upgrade.sql";
		public static final String DOWNGRADE_SQL_FILE = "downgrade.sql";

		public ScriptUri(String version) {
			this.version = version;
			upgradeUri.append(dbAssetFolder).append(fileSeparator)
					.append(version).append(fileSeparator)
					.append(UPGRADE_SQL_FILE);
			downgradeUri.append(dbAssetFolder).append(fileSeparator)
					.append(version).append(fileSeparator)
					.append(DOWNGRADE_SQL_FILE);
		}

		public String getUpgradeUri() {
			return upgradeUri.toString();
		}

		public String getDowngradeUri() {
			return downgradeUri.toString();
		}

		public String getVersion() {
			return version;
		}

	}

}
