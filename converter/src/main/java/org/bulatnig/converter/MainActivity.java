package org.bulatnig.converter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    populateSpinners();
    }

	private void populateSpinners() {
		Spinner currencyFrom = (Spinner) findViewById(R.id.spinner_currency_from);
		List<Map<String, String>> values = new ArrayList<Map<String, String>>();
		values.add(addData("USD"));
		values.add(addData("RUB"));
		String[] from = new String[] { "currency" };
		int[] to = new int[] { android.R.id.text1 };
		SimpleAdapter currencyFromAdapter = new SimpleAdapter(this, values, android.R.layout.simple_spinner_item, from, to);
		currencyFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencyFrom.setAdapter(currencyFromAdapter);
		currencyFrom.setOnItemSelectedListener(new SpinnerItemSelectedListener());
		currencyFrom.setSelection(1);
	}

	private Map<String, String> addData(String colorName) {
		Map<String, String> mapList = new HashMap<String, String>();
		mapList.put("currency" + colorName, colorName);
		return mapList;
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
			String currencyFrom = (String)((HashMap)parent.getItemAtPosition(position)).get("currency");
			Toast.makeText(
					parent.getContext(),
					"Selected Color:-  " + currencyFrom,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
    
}
