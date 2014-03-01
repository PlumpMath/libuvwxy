package de.uvwxy.helper;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class BitmapTools {
	public static final CompressFormat COMPRESS_TYPE = Bitmap.CompressFormat.PNG;
	public static final int COMPRESS_QUALITY = 100;

	public static byte[] drawableToByteArray(Drawable d) {
		Bitmap bmp = ((BitmapDrawable) d).getBitmap();
		return bitmapToByteArray(bmp);
	}

	public static byte[] bitmapToByteArray(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(COMPRESS_TYPE, COMPRESS_QUALITY, stream);
		return stream.toByteArray();
	}

	public static Bitmap byteArrayToDrawable(byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static int dipToPixels(Context ctx, int dip) {
		Resources r = ctx.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap loadScaledBitmap(Context ctx, String path, int dipWidth, int dipHeight) {
		// prefetch size
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// fetch scaled image
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(options, dipToPixels(ctx, dipWidth), dipToPixels(ctx, dipHeight));
		options.inScaled = true;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap loadScaledBitmapPixels(Context ctx, String path, int x, int y) {
		// prefetch size
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// fetch scaled image
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(options, x, y);
		options.inScaled = true;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap loadScaledBitmap(Context ctx, int resId, int dipWidth, int dipHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(ctx.getResources(), resId, options);

		// fetch scaled image
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(options, dipToPixels(ctx, dipWidth), dipToPixels(ctx, dipHeight));
		options.inScaled = true;

		return BitmapFactory.decodeResource(ctx.getResources(), resId, options);
	}

	public static Bitmap scaleBitmap(Bitmap input, int w, int h, boolean alias) {
		Preconditions.checkNotNull(input);

		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setAntiAlias(alias);
		Rect dst = new Rect(0, 0, w, h);
		Rect src = new Rect(0, 0, input.getWidth(), input.getHeight());
		canvas.drawBitmap(input, src, dst, paint);

		return bm;
	}

	public static Drawable getPackageIcon(Context ctx, String packageName) {
		Drawable ret = null;
		PackageManager pk = ctx.getPackageManager();

		try {
			ret = pk.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return ret;
	}
}
