package de.uvwxy.phone;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

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
		HashFunction hf = Hashing.murmur3_128(seed);
		HashCode hc = hf.newHasher().putString(deviceId, Charsets.UTF_8).hash();
		Log.i("PHONEID", "hc.toString() = " + hc.toString());
		return hc.toString();
	}
}
