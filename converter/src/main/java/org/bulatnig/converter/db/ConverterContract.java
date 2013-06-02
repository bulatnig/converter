package org.bulatnig.converter.db;

import android.provider.BaseColumns;

public enum ConverterContract {
	INSTANCE;

	public static abstract class RateEntry implements BaseColumns {
		public static final String TABLE_NAME = "exchange_rate";
		public static final String COLUMN_NAME_CODE = "code";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_NOMINAL = "nominal";
		public static final String COLUMN_NAME_VALUE = "value";
	}

}
