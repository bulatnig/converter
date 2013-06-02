package org.bulatnig.converter;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.bulatnig.converter.db.ConverterContract;
import org.bulatnig.converter.db.ConverterDbHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UpdateRatesTask extends AsyncTask<String, Integer, Boolean> {

	private final MainActivity activity;

	public UpdateRatesTask(MainActivity activity) {
		this.activity = activity;
	}

	protected Boolean doInBackground(String... urls) {
		Boolean updated = null;
		try {
			Document document = load(urls[0]);
			if (isUpToDate(document)) {
				updated = false;
			} else {
				save(document);
				updateTimestamp(document);
				updated = true;
			}
		} catch (Exception e) {
			Log.e("UpdateRatesTask", "Ошибка обновления курсов валют", e);
		}
		return updated;
	}

	private void save(Document document) {
		NodeList nodes = document.getDocumentElement().getElementsByTagName("Valute");
		ConverterDbHelper mDbHelper = new ConverterDbHelper(activity);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		db.beginTransaction();
		try {
			clearTable(db);
			saveCurrencies(nodes, db);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	private void updateTimestamp(Document document) {
		SharedPreferences sharedPref = getSharedPreferences();
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(RATES_TIMESTAMP, getDocumentDate(document));
		editor.commit();
	}

	private boolean isUpToDate(Document document) {
		SharedPreferences sharedPref = getSharedPreferences();
		String timestamp = sharedPref.getString(RATES_TIMESTAMP, null);
		return timestamp != null && timestamp.equals(getDocumentDate(document));
	}

	private String getDocumentDate(Document document) {
		NamedNodeMap attributes = document.getFirstChild().getAttributes();
		Node node = attributes.getNamedItem("Date");
		return node.getNodeValue();
	}

	private SharedPreferences getSharedPreferences() {
		return activity.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
	}

	private void saveCurrencies(NodeList nodes, SQLiteDatabase db) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Element valute = (Element) nodes.item(i);
			ContentValues values = prepareRow(valute);
			db.insert(ConverterContract.RateEntry.TABLE_NAME, null, values);
		}
	}

	private void clearTable(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + ConverterContract.RateEntry.TABLE_NAME);
	}

	private ContentValues prepareRow(Element valute) {
		ContentValues values = new ContentValues();
		values.put(ConverterContract.RateEntry.COLUMN_NAME_CODE, getChildValue(valute, "CharCode"));
		values.put(ConverterContract.RateEntry.COLUMN_NAME_NAME, getChildValue(valute, "Name"));
		values.put(ConverterContract.RateEntry.COLUMN_NAME_NOMINAL,
				Integer.valueOf(getChildValue(valute, "Nominal")));
		values.put(ConverterContract.RateEntry.COLUMN_NAME_VALUE,
				ConverterUtils.parseDouble(getChildValue(valute, "Value")));
		return values;
	}

	private String getChildValue(Element element, String tagName) {
		NodeList nodes = element.getElementsByTagName(tagName);
		if (nodes.getLength() == 1) {
			Node node = nodes.item(0);
			if (node.getChildNodes().getLength() == 1) {
				return node.getFirstChild().getNodeValue();
			}
		}
		throw new ConverterException("Ожидается один дочерний элемент " + tagName + " с текстом");
	}

	private Document load(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				return documentBuilder.parse(in);
			} finally {
				urlConnection.disconnect();
			}
		} catch (Exception e) {
			throw new ConverterException("Ошибка чтения " + urlString, e);
		}
	}

	@Override
	protected void onPostExecute(Boolean updated) {
		if (updated == null) {
			showNotification(R.string.update_failed);
		} else if (updated) {
			showNotification(R.string.update_successful);
			new PopulateSpinnerTask(activity).execute();
		} else {
			showNotification(R.string.update_not_required);
		}
	}

	private void showNotification(int messageId) {
		Toast.makeText(activity, messageId, Toast.LENGTH_SHORT).show();
	}

	private static final String PREFERENCE_FILE_NAME = "org.bulatnig.converter.UpdateRatesTask.PREFERENCES";
	private static final String RATES_TIMESTAMP = "RATES_TIMESTAMP";

}