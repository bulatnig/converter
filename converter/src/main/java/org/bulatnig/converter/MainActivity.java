package org.bulatnig.converter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.bulatnig.converter.db.ConverterContract;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initSpinner();
		new PopulateSpinnerTask(this, new UpdateRatesTask(this), "http://www.cbr.ru/scripts/XML_daily.asp").execute();
	}

	private void initSpinner() {
		Spinner currencyFrom = (Spinner) findViewById(R.id.spinner_currency_from);
		currencyFrom.setOnItemSelectedListener(new SpinnerItemSelectedListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
			String countryCode = cursor.getString(cursor.getColumnIndexOrThrow(ConverterContract.RateEntry.COLUMN_NAME_VALUE));
			Toast.makeText(getApplicationContext(), countryCode, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

}
