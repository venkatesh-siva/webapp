package edu.csye.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

	public static String getTimeZoneDate() {
		Date now = new Date(); 
		String formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(now);
		return formattedDate;
	}
}
