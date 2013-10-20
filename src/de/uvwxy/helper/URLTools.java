package de.uvwxy.helper;

import java.util.regex.Pattern;

import android.util.Log;

import com.google.common.base.Preconditions;

public class URLTools {
	public static String getParamValue(String url, String param) {
		Preconditions.checkNotNull(url);
		Preconditions.checkNotNull(param);

		Log.i("SPLIT", "Splitting " + param);

		String[] splitPath = url.split(Pattern.quote("?"));

		if (splitPath.length < 2) {
			return null;
		}

		// use latest matching param found, due to double parameter of id in
		// market url and node id
		String result = null;

		Log.i("SPLIT", "Splitting " + splitPath[0] + " and " + splitPath[1]);

		String[] paramsWithValues = splitPath[1].split(Pattern.quote("&"));

		for (String paramWithValue : paramsWithValues) {
			String[] s = paramWithValue.split("=");

			if (s.length < 2) {
				continue;
			}
			Log.i("SPLIT", s[0] + "=" + s[1]);

			if (param.equals(s[0])) {
				result = s[1];
			}
		}
		return result;
	}
}
