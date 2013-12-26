package de.uvwxy.units;

public enum Unit {
	METRE("m", 1), //
	FOOT("ft", 1), //
	MILE("mi", 2), //
	YARD("yd", 1),

	METRES_PER_SECOND("m/s", 1), //
	KILOMETRES_PER_HOUR("km/h", 1), //
	MILES_PER_HOUR("mp/h", 1),

	DEGREES("°", 2), //
	DEGREES_MINUTES_SECONDS(new String[] { "°", "'", "''" }, new UnitPartitionDegreesMinutesSeconds(), 0, 0, 0);

	private String[] unit;
	private UnitPrefix[] prefix = new UnitPrefix[] { UnitPrefix.NONE };
	private int[] precision = new int[] { 0 };
	private UnitPartition conv;
	private double v;

	private Unit(String[] s, UnitPartition conv, int... defaultPrecision) {
		this.unit = s;
		this.conv = conv;
		this.precision = defaultPrecision;
	}

	private Unit(String s, int defaultPrecision) {
		this.unit = new String[] { s };
		this.precision[0] = defaultPrecision;
	}

	public Unit to(Unit to) {
		return UnitConversion.convert(this, to);
	}

	public String toString() {
		if (conv == null) {
			return String.format("%." + precision[0] + "f %s%s", v * prefix[0].factor(), prefix[0].s(), unit[0]);
		}

		String ret = "";
		for (int i = 0; i < unit.length; i++) {
			ret += String.format("%s%s ", conv.getValue(i, precision[i], v), unit[i]);
		}
		return ret;
	}

	public Unit setValue(double v) {
		this.v = v;
		return this;
	}

	public double getValue() {
		return this.v;
	}

	public Unit setUnitString(String s) {
		this.unit = new String[] { s };
		return this;
	}

	public Unit setUnitString(String... s) {
		this.unit = s;
		return this;
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
		this.prefix = p;
		return this;
	}
}
