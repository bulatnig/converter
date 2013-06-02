package org.bulatnig.converter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public enum ConverterUtils {
	INSTANCE;

	private static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("ru"));

	public static Double parseDouble(String doubleValue) {
		try {
			return numberFormat.parse(doubleValue).doubleValue();
		} catch (ParseException e) {
			throw new ConverterException("Ошибка разбора числа: ожидается double, получен " + doubleValue);
		}
	}

}
