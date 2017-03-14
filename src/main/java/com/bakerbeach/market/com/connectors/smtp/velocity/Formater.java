package com.bakerbeach.market.com.connectors.smtp.velocity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formater {
	
	public String currency(Object number) {
		if (number != null) {
			NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);
			nf.setMinimumIntegerDigits(1);
			StringBuilder buffer;
			buffer = new StringBuilder().append(nf.format(number)).append("EUR");
			return buffer.toString();
		} else {
			return null;
		}
	}
	
	public String date(Date date, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.GERMANY);
			return sdf.format(date);
		} catch (Exception e) {
			return "";
		}
	}

}
