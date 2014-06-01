package de.uvwxy.units;

import android.util.Log;

public class Unit {
    public static final String DEGREES = "DEGREES";
    public static final String DEGREES_MINUTES_SECONDS = "DEGREES_MINUTES_SECONDS";

    public static final String FOOT = "FOOT";
    public static final String KILOMETRE = "KILOMETRE";
    public static final String METRE = "METRE";
    public static final String MILE = "MILE";
    public static final String YARD = "YARD";

    public static final String KILOMETRES_PER_HOUR = "KILOMETRES_PER_HOUR";
    public static final String METRES_PER_SECOND = "METRES_PER_SECOND";
    public static final String MILES_PER_HOUR = "MILES_PER_HOUR";

    public static final String BAR = "BAR";
    public static final String MILLI_BAR = "MILLI_BAR";
    public static final String PASCAL = "PASCAL";
    public static final String HECTO_PASCAL = "HECTO_PASCAL";
    public static final String POUNDS_PER_SQUARE_INCH = "POUNDS_PER_SQUARE_INCH";
    public static final String STANDARD_ATMOSPHERE = "STANDARD_ATMOSPHERE";
    public static final String TECHNICAL_ATMOSPHERE = "TECHNICAL_ATMOSPHERE";
    public static final String TORR = "TORR";

    public static final String CELSIUS = "CELSIUS";
    public static final String FAHRENHEIT = "FAHRENHEIT";
    public static final String KELVIN = "KELVIN";

    public static Unit from(String name) {
        return valueOf(name);
    }

    public static Unit valueOf(String name) {
        if (BAR.equals(name)) {
            return new Unit(BAR, "bar", 2);
        } else if (DEGREES.equals(name)) {
            return new Unit(DEGREES, "°", 2);
        } else if (DEGREES_MINUTES_SECONDS.equals(name)) {
            return new Unit(DEGREES_MINUTES_SECONDS, new String[] { "°", "'", "''" },
                    new UnitPartitionDegreesMinutesSeconds(), 0, 0, 2);
        } else if (FOOT.equals(name)) {
            return new Unit(FOOT, "ft", 1);
        } else if (HECTO_PASCAL.equals(name)) {
            return new Unit(HECTO_PASCAL, "hPa", 2);
        } else if (KILOMETRES_PER_HOUR.equals(name)) {
            return new Unit(KILOMETRES_PER_HOUR, "km/h", 1);
        } else if (KILOMETRE.equals(name)) {
            return new Unit(METRE, "m", 1).setPrefix(UnitPrefix.KILO);
        } else if (METRE.equals(name)) {
            return new Unit(METRE, "m", 1);
        } else if (METRES_PER_SECOND.equals(name)) {
            return new Unit(METRES_PER_SECOND, "m/s", 1);
        } else if (MILE.equals(name)) {
            return new Unit(MILE, "mi", 2);
        } else if (MILES_PER_HOUR.equals(name)) {
            return new Unit(MILES_PER_HOUR, "mp/h", 1);
        } else if (MILLI_BAR.equals(name)) {
            return new Unit(MILLI_BAR, "mbar", 2);
        } else if (PASCAL.equals(name)) {
            return new Unit(PASCAL, "Pa", 2);
        } else if (POUNDS_PER_SQUARE_INCH.equals(name)) {
            return new Unit(POUNDS_PER_SQUARE_INCH, "psi", 2);
        } else if (STANDARD_ATMOSPHERE.equals(name)) {
            return new Unit(STANDARD_ATMOSPHERE, "atm", 3);
        } else if (TECHNICAL_ATMOSPHERE.equals(name)) {
            return new Unit(TECHNICAL_ATMOSPHERE, "at", 3);
        } else if (TORR.equals(name)) {
            return new Unit(TORR, "Torr", 2);
        } else if (YARD.equals(name)) {
            return new Unit(YARD, "yd", 1);
        } else if (CELSIUS.equals(name)) {
            return new Unit(CELSIUS, "°C", 1);
        } else if (FAHRENHEIT.equals(name)) {
            return new Unit(FAHRENHEIT, "°F", 1);
        } else if (KELVIN.equals(name)) {
            return new Unit(KELVIN, "K", 1);
        }

        return new Unit(METRE, "m", 1);
    }

    private UnitPartition conv;
    private String name;
    private int[] precision = new int[] { 0 };
    private UnitPrefix[] prefix = new UnitPrefix[] { UnitPrefix.NONE };
    private String[] unit;
    private double v;

    public Unit(String name, String string, int i) {
        this.name = name;
        this.unit = new String[] { string };
        this.conv = null;
        this.precision = new int[] { i };
    }

    public Unit(String name, String[] s, UnitPartition conv, int... defaultPrecision) {
        this.name = name;
        this.unit = s;
        this.conv = conv;
        this.precision = defaultPrecision;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return this.v;
    }

    public Unit setPrecision(int precision) {
        this.precision[0] = precision;
        return this;
    }

    public Unit setPrecision(int... precision) {
        this.precision = precision;
        return this;
    }

    public Unit setPrefix(UnitPrefix p) {
        this.prefix = new UnitPrefix[] { p };
        return this;
    }

    public Unit setPrefix(UnitPrefix... p) {
        Log.d("WAI", "Prefix: ? " + p);
        this.prefix = p;
        return this;
    }

    public UnitPrefix[] getPrefix() {
        return this.prefix;
    }

    public Unit setUnitString(String s) {
        this.unit = new String[] { s };
        return this;
    }

    public Unit setUnitString(String... s) {
        this.unit = s;
        return this;
    }

    public Unit setValue(double v) {
        this.v = v;
        return this;
    }

    public Unit to(Unit to) {
        return UnitConversion.convert(this, to);
    }

    public String toString() {
        if (conv == null) {
            return String.format("%." + precision[0] + "f %s%s", v * prefix[0].factor(), prefix[0].s(), unit[0]);
        }
        Log.d("WAI", "prefix: " + prefix[0]);
        String ret = "";
        for (int i = 0; i < unit.length; i++) {
            ret += String.format("%s%s ", conv.getValue(i, precision[i], v), unit[i]);
        }
        return ret;
    }
}
