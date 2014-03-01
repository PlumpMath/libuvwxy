package de.uvwxy.units;

import java.util.HashMap;

import android.util.Log;

public class UnitConversion {
	private static HashMap<String, Conversion> conversions = new HashMap<String, Conversion>();

	static {
		// src: google conversion tool
		addConversion(new Conversion(Unit.METRE, Unit.FOOT, 3.28084));
		addConversion(new Conversion(Unit.METRE, Unit.MILE, 0.000621371));
		addConversion(new Conversion(Unit.METRE, Unit.YARD, 1.09361));
		addConversion(new Conversion(Unit.METRES_PER_SECOND, Unit.KILOMETRES_PER_HOUR, 3.6));
		addConversion(new Conversion(Unit.METRES_PER_SECOND, Unit.MILES_PER_HOUR, 2.23694));
		addConversion(new Conversion(Unit.DEGREES, Unit.DEGREES_MINUTES_SECONDS, 1.0));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.BAR, 1.0 / 1000.0));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.PASCAL, 100.0));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.HECTO_PASCAL, 1.0));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.TORR, 0.750061683));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.STANDARD_ATMOSPHERE, 0.000986923267));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.POUNDS_PER_SQUARE_INCH, 0.0145037738));
		addConversion(new Conversion(Unit.MILLI_BAR, Unit.TECHNICAL_ATMOSPHERE, 0.0010197162129779));
	}

	public static void addConversion(Conversion conv) {
		conversions.put(conv.getKey(), conv);
	}

	public static Unit convert(Unit from, Unit to) {

		if (from.getName().equals(to.getName())) {
			// identity conversion, but might change prefix
			Unit ret = Unit.valueOf(from.getName());
			ret.setValue(from.getValue());

			return ret;
		}
		String key0 = Conversion.createKey(from.getName(), to.getName());
		String key1 = Conversion.createKey(to.getName(), from.getName());

		Conversion conv = conversions.get(key0);
		conv = conv == null ? conversions.get(key1) : conv;

		if (conv == null) {
			Unit ret = Unit.valueOf(from.getName());
			ret.setValue(from.getValue());
			Log.d("WAI", "ret1 = " + ret);
			return from;
		}

		Unit ret = conv.to(to, from.getValue());

		return ret;
	}
}