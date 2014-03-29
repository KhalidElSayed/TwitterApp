package com.inez.twitterapp.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

public class Helpers {
	
	public static String getRelativeTime(Date date) {
		return (new PrettyTime()).format(date);
	}

	public static Date parseTwitterUTC(String date) throws ParseException {
		String twitterFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
		return sf.parse(date);
	}
	
}
