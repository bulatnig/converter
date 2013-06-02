package org.bulatnig.converter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConverterDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Converter.db";

	private static final int DATABASE_VERSION = 1;

	public ConverterDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP_TABLE);
		onCreate(db);
	}

	private static final String SQL_CREATE_TABLE =
			"CREATE TABLE " + ConverterContract.RateEntry.TABLE_NAME + " (" +
					ConverterContract.RateEntry._ID + " INTEGER PRIMARY KEY," +
					ConverterContract.RateEntry.COLUMN_NAME_CODE + " TEXT," +
					ConverterContract.RateEntry.COLUMN_NAME_NAME + " TEXT," +
					ConverterContract.RateEntry.COLUMN_NAME_NOMINAL + " INTEGER," +
					ConverterContract.RateEntry.COLUMN_NAME_VALUE + " REAL" +
					" )";

	private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + ConverterContract.RateEntry.TABLE_NAME;
}
