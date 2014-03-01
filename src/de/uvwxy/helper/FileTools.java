package de.uvwxy.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

public class FileTools {

	public static String getAndCreateExternalFolder(String folderEndingWithSlash) {
		String ret = null;

		if (isStorageAvailable()) {
			ret = getExternalStorageFolder().getAbsolutePath() + "/" + folderEndingWithSlash;
		}

		File f = new File(ret);
		if (!f.exists())
			f.mkdirs();
		return ret;
	}

	public static File getExternalStorageFolder() {
		return Environment.getExternalStorageDirectory();
	}

	/**
	 * Returns true if we have a writable external storage.
	 * TODO: return available, or rename to writeable
	 */
	public static boolean isStorageAvailable() {
		@SuppressWarnings("unused")
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		return mExternalStorageWriteable;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static String getRealPathFromURI(Context ctx, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader cl = new CursorLoader(ctx, contentUri, proj, null, null, null);
		Cursor cursor = cl.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static String[] readLinesOfFile(int numLines, String pathToFile) {
		File f = new File(pathToFile);

		if (!f.exists()) {
			return null;
		}

		if (numLines <= 0) {
			return null;
		}

		String lines[] = new String[numLines];

		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			for (int i = 0; i < numLines; i++) {
				lines[i] = br.readLine();
			}

			br.close();
			fr.close();

			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
}
