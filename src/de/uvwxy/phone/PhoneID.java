package de.uvwxy.phone;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.provider.Settings.Secure;

public class PhoneID {
	/**
	 * Creates a hash of the Secure.ANDROID_ID with the given salt. Returns a
	 * string containing each byte of asBytes(), in order, as a two-digit
	 * unsigned hexadecimal number in lower case.
	 * 
	 * @param ctx
	 * @param seed
	 * @return
	 */
	public static String getId(Context ctx, int seed) {
		String deviceId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
		MessageDigest md = null;
		//Every implementation of the Java platform is required to support the following standard MessageDigest algorithms:
		//		MD5
		//		SHA-1
		//		SHA-256

		byte[] b = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		if (md == null) {
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		if (md == null) {
			try {
				b = deviceId.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		if (md != null && b != null) {
			byte[] d = md.digest(b);
			return new String(d);
		}
		return null;
	}
}
