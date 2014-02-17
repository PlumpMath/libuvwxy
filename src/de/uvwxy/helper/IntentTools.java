package de.uvwxy.helper;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Preconditions;

import de.uvwxy.phone.PhoneID;

public class IntentTools {
	public static void captureImage(Activity activity, String imagePath, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = new File(imagePath);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		activity.startActivityForResult(intent, requestCode);
	}

	public static void selectImage(Activity activity, int requestCode) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		activity.startActivityForResult(photoPickerIntent, requestCode);
	}

	public static void showImage(Context ctx, String path) {
		showFile(ctx, path, "image/*");
	}

	public static void showAudioFile(Context ctx, String path) {
		showFile(ctx, path, "audio/*");
	}

	public static void showFile(Context ctx, String path, String mimeType) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)), mimeType);
		ctx.startActivity(intent);

	}

	public static final int TYPE_GMAPS = 0;
	public static final int TYPE_OSM = 1;

	public static void shareLocation(Context ctx, Location l, int type, String title, String slat, String slon,
			String salt, String sbear, String sacc, String sspeed, String msgLenTxt, String msgShareViaTxt) {
		String mapUrl = "MapTypeNotFound";
		switch (type) {
		case TYPE_GMAPS:
			mapUrl = getGoogleMapsUrl(l.getLatitude(), l.getLongitude());
			break;
		case TYPE_OSM:
			mapUrl = getOSMMapsUrl(l.getLatitude(), l.getLongitude());
			break;
		default:
			throw new RuntimeException("Map type not found: " + type);
		}
		shareLocation(ctx, mapUrl, l.getLatitude(), l.getLongitude(), l.getAltitude(), l.getBearing(), l.getAccuracy(),
				l.getSpeed(), title, slat, slon, salt, sbear, sacc, sspeed, msgLenTxt, msgShareViaTxt);
	}

	public static void shareLocation(Context ctx, String mapUrl, double lat, double lon, double alt, double bearing,
			double acc, double speed, String title, String slat, String slon, String salt, String sbear, String sacc,
			String sspeed, String msgLenTxt, String msgShareViaTxt) {
		// https://maps.google.com/maps?q=50.070,+7.666
		String msg = mapUrl;

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		msg += "\n" + slat + nf.format(lat) + " °";
		msg += "\n" + slon + nf.format(lon) + " °";
		msg += "\n" + salt + String.format("%.1f", alt) + " m";
		msg += "\n" + sspeed + String.format("%.1f", speed) + " m";
		msg += "\n" + sbear + String.format("%.1f", bearing) + " °";
		msg += "\n" + sacc + String.format("%.1f", acc) + " m";

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
		Toast.makeText(ctx, msgLenTxt + msg.length(), Toast.LENGTH_SHORT).show();
		ctx.startActivity(Intent.createChooser(sharingIntent, msgShareViaTxt));
	}

	public static String getOSMMapsUrl(double lat, double lon) {
		return String.format(Locale.US, "http://www.openstreetmap.org/?lat=%f&lon=%f&zoom=17&layers=C", lat, lon);
	}

	public interface ReturnStringCallback {
		public void result(String s);
	}

	public interface ReturnStringECallback<E> {
		public void result(StringE<E> stringE);
	}

	public static void userSelectString(Activity a, String title, final String[] items,
			final ReturnStringCallback selected) {
		if (a == null || selected == null || items == null) {
			throw new RuntimeException("Parameters are not allowed to be null");
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setTitle(title);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				selected.result(items[item]);
			}
		});
		AlertDialog alert = builder.create();
		if (items.length > 1) {
			// let user choose which number to use
			alert.show();
		} else {
			if (items.length == 1) {
				selected.result(items[0]);
			} else {
				selected.result(null);
			}
		}

	}

	public static <E> void userSelectStringE(Context ctx, String title, final List<StringE<E>> items,
			final ReturnStringECallback<E> selected) {
		if (ctx == null || selected == null || items == null) {
			throw new RuntimeException("Parameters are not allowed to be null");
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);

		String[] itemStrings = new String[items.size()];
		for (int i = 0; i < items.size(); i++) {
			itemStrings[i] = items.get(i).s;
		}

		builder.setItems(itemStrings, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				selected.result(items.get(item));
			}
		});
		AlertDialog alert = builder.create();
		if (items.size() > 1) {
			// let user choose which number to use
			alert.show();
		} else {
			if (items.size() == 1) {
				selected.result(items.get(0));
			} else {
				selected.result(null);
			}
		}

	}

	public static String getGoogleMapsUrl(double lat, double lon) {
		String url = "https://maps.google.com/maps?q=";
		url += lat >= 0 ? "+" : "";
		url += lat;
		url += ",";
		url += lon >= 0 ? "+" : "";
		url += lon;
		return url;
	}

	public static void showURL(Activity act, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		act.startActivity(i);
	}

	public static String getCorrectFormatStr(double dx) {
		String d = ("" + dx);
		int numDecimalPlaces = 0;
		if (d.contains(".") && d.split("\\.").length > 1) {
			numDecimalPlaces = d.split("\\.")[1].length();
		}
		return "%." + numDecimalPlaces + "f";
	}

	public static void scanNode(Activity act, int requestCode) {
		Intent my_intent = new Intent("de.uvwxy.daisy.SCAN_NODE");
		act.startActivityForResult(my_intent, requestCode);
	}

	public static void safeUnregister(Context ctx, BroadcastReceiver br) {
		try {
			ctx.unregisterReceiver(br);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	public static boolean settingsValuesIsSet(Context ctx, String settingsId, String valueId) {
		SharedPreferences settings = ctx.getSharedPreferences(settingsId, Context.MODE_PRIVATE);
		return settings.contains(valueId);
	}

	public static SharedPreferences getSettings(Context ctx, String settingsId) {
		return ctx.getSharedPreferences(settingsId, Context.MODE_PRIVATE);
	}

	public static Editor getSettingsEditor(Context ctx, String settingsId) {
		return ctx.getSharedPreferences(settingsId, Context.MODE_PRIVATE).edit();
	}

	/**
	 * ommit your preferences changes back from this Editor to the
	 * SharedPreferences object it is editing. This atomically performs the
	 * requested modifications, replacing whatever is currently in the
	 * SharedPreferences.
	 * 
	 * Note that when two editors are modifying preferences at the same time,
	 * the last one to call commit wins.
	 * 
	 * If you don't care about the return value and you're using this from your
	 * application's main thread, consider using apply instead.
	 * 
	 * @param editor
	 * @return Returns true if the new values were successfully written to
	 *         persistent storage.
	 */
	public static boolean saveEditor(Editor editor) {
		return editor.commit();
	}

	public static void getFromSettingsOrFromUser(final Context ctx, final String settingsId, final String valueId,
			String textHint, String textMessage, String textTitle, boolean update, final SettingsCallback callback) {
		Preconditions.checkNotNull(ctx);
		Preconditions.checkNotNull(settingsId);
		Preconditions.checkNotNull(valueId);
		Preconditions.checkNotNull(callback);

		final EditText t = new EditText(ctx);
		t.setHint(textHint);

		if (update) {
			t.setText(getSettings(ctx, settingsId).getString(valueId, "not set"));
		}

		if (!update && settingsValuesIsSet(ctx, settingsId, valueId)) {
			callback.onValue(valueId, getSettings(ctx, settingsId).getString(valueId, ""));
			return;
		}

		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);

		alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String userName = "";
				if (t.getText() == null || t.getText().equals("")) {
					// no entered text is random id.
					userName = PhoneID.getId(ctx, (int) (Math.random() * 1000000));
					if (userName.length() > 5) {
						userName = userName.substring(0, 5);
					}

				} else if (t.getText() != null) {
					userName = t.getText().toString();
				}

				saveEditor(getSettingsEditor(ctx, settingsId).putString(valueId, userName));
				callback.onValue(valueId, userName);
				return;

			}
		});

		alertDialog.setCancelable(false);

		alertDialog.setMessage(textMessage);
		alertDialog.setTitle(textTitle);
		alertDialog.setView(t);
		alertDialog.show();

	}

	public static void email(Context context, String emailTo, String emailCC, String subject, List<String> filePaths) {
		// need to "send multiple" to get more than one attachment
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("*/*");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { emailTo });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
		// etLog.getText().toString());
		emailIntent.putExtra(android.content.Intent.EXTRA_CC, new String[] { emailCC });
		// has to be an ArrayList
		ArrayList<Uri> uris = new ArrayList<Uri>();
		// convert from paths to Android friendly Parcelable Uri's
		for (String file : filePaths) {
			File fileIn = new File(file);
			Uri u = Uri.fromFile(fileIn);
			uris.add(u);
		}

		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		context.startActivity(Intent.createChooser(emailIntent, "Send files..."));
	}

	@SuppressLint("NewApi")
	public static void addCopyTextOnClickListner(final Context ctx, final TextView tv, final String label) {
		tv.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
					ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText(label, ((TextView) tv).getText().toString());
					cm.setPrimaryClip(clip);
				} else {
					android.text.ClipboardManager cmDepr = (android.text.ClipboardManager) ctx
							.getSystemService(Context.CLIPBOARD_SERVICE);
					cmDepr.setText(((TextView) tv).getText().toString());
				}
				Toast.makeText(ctx, label + " " + ((TextView) tv).getText().toString(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showHelpDialog(Context ctx, String dialogTitle, String btnBackText, String helpText) {
		final Dialog dialog = new Dialog(ctx);

		ScrollView scrollView = new ScrollView(ctx);
		LinearLayout llVertical = new LinearLayout(ctx);
		llVertical.setOrientation(LinearLayout.VERTICAL);

		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int dip = BitmapTools.dipToPixels(ctx, 8);
		params.setMargins(dip, dip, dip, dip);
		llVertical.setLayoutParams(params);

		dialog.setContentView(scrollView);
		dialog.setTitle(dialogTitle);

		TextView text = new TextView(ctx);
		text.setLayoutParams(params);
		text.setText(helpText);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

		Button btnBack = new Button(ctx);
		btnBack.setText(btnBackText);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		scrollView.addView(llVertical);
		llVertical.addView(text);
		llVertical.addView(btnBack);

		dialog.show();
	}

	public static boolean showHelpOnce(Context ctx, String prefs, String dialogTitle, String btnBackText,
			String helpText) {
		String SHOW_HELP = "SHOW_HELP";
		SharedPreferences settings = ctx.getSharedPreferences(prefs, 0);
		boolean showHelp = settings.getBoolean(SHOW_HELP, true);

		if (showHelp) {

			showHelpDialog(ctx, dialogTitle, btnBackText, helpText);

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(SHOW_HELP, false);
			editor.commit();
			return true;
		}
		return false;
	}

	public static boolean isFirstLaunch(Context ctx, String prefs) {
		String FIRST_LAUNCH = "FIRST_LAUNCH";
		SharedPreferences settings = ctx.getSharedPreferences(prefs, 0);
		boolean firstlaunch = settings.getBoolean(FIRST_LAUNCH, true);

		if (firstlaunch) {

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(FIRST_LAUNCH, false);
			editor.commit();
			return true;
		}
		return false;
	}

	/**
	 * Only works if launched in same process
	 * 
	 * @param act
	 * @param nodeId
	 */
	public static void showNodeData(Activity act, int nodeId) {
		Intent my_intent = new Intent("de.uvwxy.daisy.SHOW_NODE");
		my_intent.addCategory(Intent.CATEGORY_DEFAULT);
		my_intent.putExtra(IntentExtras.INTENT_EXTRA_NODE_ID_INT, nodeId);
		act.startActivity(my_intent);
	}

	public static void toggleSettings(final Context ctx, CheckBox cb, final String settingsID,
			final String settingsKey, boolean defValue) {
		if (cb == null) {
			return;
		}

		SharedPreferences prefs = getSettings(ctx, settingsID);

		cb.setChecked(prefs.getBoolean(settingsKey, defValue));

		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor e = getSettingsEditor(ctx, settingsID);
				e.putBoolean(settingsKey, isChecked);
				e.commit();
			}
		});
	}

	public static void switchSettings(final Context ctx, RadioButton rb, final String settingsID,
			final String settingsKey, final int thisRBID) {
		SharedPreferences prefs = getSettings(ctx, settingsID);
		rb.setChecked(prefs.getInt(settingsKey, 0) == thisRBID);
		Log.d("UVWXY", "settingsID = " + settingsID + " settingsKey = " + settingsKey + " id = " + thisRBID);
		Log.d("UVWXY", "prefs is " + prefs.getInt(settingsKey, 0));
		rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Editor e = getSettingsEditor(ctx, settingsID);
					e.putInt(settingsKey, thisRBID);
					e.commit();
				}
			}
		});
	}
	
	public static boolean isApiLarger(int apiVersion){
		int apiV = android.os.Build.VERSION.SDK_INT;
		return apiV > apiVersion;
	}
}
