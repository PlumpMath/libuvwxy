
package de.uvwxy.helper;

import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;

public class DateTools {
	public static String getDateTime(Context context, long timestamp) {
		Date date = new Date(timestamp);
		String res = DateFormat.getDateFormat(context).format(date);
		res += " " + DateFormat.getTimeFormat(context).format(date);
		return res;
	}
	
	public static String getDateTimeLong(Context context, long timestamp){
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(timestamp));
	}
}
