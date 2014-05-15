package com.bitsatom.dbmanage.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.util.Log;

public class SQLParser {

	private static final String MULTILINE_COMMENT_START = "/*";
	private static final String MULTILINE_COMMENT_END = "*/";
	private static final String SINGLELINE_COMMENT = "--";
	private static final String TAG = "SQLParser";

	public List<String> parseSqlFile(String sqlFile,
			AssetManager assetManager) throws IOException {
		List<String> sqlIns = null;
		Log.d(TAG, "File to be parsed '" + sqlFile + "'");
		InputStream is = assetManager.open(sqlFile);
		try {
			sqlIns = parseSqlFile(is);
		} finally {
			is.close();
		}
		return sqlIns;
	}

	public List<String> parseSqlFile(InputStream is) throws IOException {
		String script = removeComments(is);
		return splitSqlScript(script, ';');
	}

	private String removeComments(InputStream is) throws IOException {

		StringBuilder sql = new StringBuilder();

		InputStreamReader isReader = new InputStreamReader(is);
		try {
			BufferedReader buffReader = new BufferedReader(isReader);
			try {
				String line;
				String multiLineComment = null;
				while ((line = buffReader.readLine()) != null) {
					line = line.trim();

					if (multiLineComment == null) {
						if (line.startsWith(MULTILINE_COMMENT_START)) {
							if (!line.endsWith("}")) {
								Log.d(TAG, "Found multiline comment");
								multiLineComment = MULTILINE_COMMENT_START;
							}
							/*
							 * } else if (line.startsWith("{")) { if
							 * (!line.endsWith("}")) { multiLineComment = "{"; }
							 */
						} else if (!line.startsWith(SINGLELINE_COMMENT)
								&& !line.equals("")) {
							Log.d(TAG, "SQL line '" + line + "'");
							sql.append(line);
						}
					} else if (multiLineComment.equals(MULTILINE_COMMENT_START)) {
						if (line.endsWith(MULTILINE_COMMENT_END)) {
							Log.d(TAG, "Multiline comment ended");
							multiLineComment = null;
						}
						/*
						 * } else if (multiLineComment.equals("{")) { if
						 * (line.endsWith("}")) { multiLineComment = null; }
						 */
					}

				}
			} finally {
				buffReader.close();
			}

		} finally {
			isReader.close();
		}

		return sql.toString();
	}

	private List<String> splitSqlScript(String script, char delim) {
		List<String> statements = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inLiteral = false;
		char[] content = script.toCharArray();
		for (int i = 0; i < script.length(); i++) {
			if (content[i] == 39) {
				Log.d(TAG, "Found literal : " + (!inLiteral ? "Start" : "End"));
				inLiteral = !inLiteral;
			}
			if (content[i] == delim && !inLiteral) {
				if (sb.length() > 0) {
					Log.d(TAG, "SQL statement '" + sb + "'");
					statements.add(sb.toString().trim());
					sb = new StringBuilder();
				}
			} else {
				Log.d(TAG, "Appending to sql statement : " + content[i]);
				sb.append(content[i]);
			}
		}
		if (sb.length() > 0) {
			statements.add(sb.toString().trim());
		}
		Log.d(TAG, "SQL statements " + statements);
		return statements;
	}

}
