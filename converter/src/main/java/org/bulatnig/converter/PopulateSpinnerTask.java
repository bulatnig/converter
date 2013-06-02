package org.bulatnig.converter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import org.bulatnig.converter.db.ConverterContract;
import org.bulatnig.converter.db.ConverterDbHelper;

public class PopulateSpinnerTask extends AsyncTask<Object, Object, Cursor> {

	private final MainActivity activity;
	private final UpdateRatesTask updateRatesTask;
	private final String updateUrl;

	public PopulateSpinnerTask(MainActivity activity) {
		this(activity, null, null);
	}

	public PopulateSpinnerTask(MainActivity activity, UpdateRatesTask updateRatesTask, String updateUrl) {
		this.activity = activity;
		this.updateRatesTask = updateRatesTask;
		this.updateUrl = updateUrl;
	}

	@Override
	protected Cursor doInBackground(Object... objects) {
		ConverterDbHelper mDbHelper = new ConverterDbHelper(activity);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {
				ConverterContract.RateEntry._ID,
				ConverterContract.RateEntry.COLUMN_NAME_CODE,
				ConverterContract.RateEntry.COLUMN_NAME_NAME,
				ConverterContract.RateEntry.COLUMN_NAME_NOMINAL,
				ConverterContract.RateEntry.COLUMN_NAME_VALUE
		};
		String sortOrder = ConverterContract.RateEntry.COLUMN_NAME_CODE;

		return db.query(ConverterContract.RateEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
	}

	@Override
	protected void onPostExecute(Cursor cursor) {
		setSpinnerAdapter(cursor);
		if (updateRatesTask != null)
			updateRatesTask.execute(updateUrl);
	}

	private void setSpinnerAdapter(Cursor cursor) {
		Spinner currencyFrom = (Spinner) activity.findViewById(R.id.spinner_currency_from);
		String[] from = new String[]{ConverterContract.RateEntry.COLUMN_NAME_CODE};
		int[] to = new int[]{android.R.id.text1};
		SimpleCursorAdapter currencyFromAdapter = new SimpleCursorAdapter(activity, android.R.layout.simple_spinner_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		currencyFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencyFrom.setAdapter(currencyFromAdapter);
	}
}
