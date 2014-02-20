package de.uvwxy.units;

import java.util.HashMap;

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
	}

	public static void addConversion(Conversion conv) {
		conversions.put(conv.getKey(), conv);
	}

	public static Unit convert(Unit from, Unit to) {
		if (from.name().equals(to.name())) {
			// identity conversion, but might change prefix
			return to.setValue(from.getValue());
		}
		String key0 = Conversion.createKey(from, to);
		String key1 = Conversion.createKey(to, from);

		Conversion conv = conversions.get(key0);
		conv = conv == null ? conversions.get(key1) : conv;

		if (conv == null) {
			return from;
		}

		return conv.to(to, from.getValue());
	}
}
