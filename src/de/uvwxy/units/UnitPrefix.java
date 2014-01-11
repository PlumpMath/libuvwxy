package de.uvwxy.units;

public enum UnitPrefix {
	NONE("", 1), //
	
	DECA("da", Math.pow(10, 1)), //
	HECTO("h", Math.pow(10, 2)), //
	KILO("k", Math.pow(10, 3)), //
	MEGA("M", Math.pow(10, 6)), //
	GIGA("G", Math.pow(10, 9)), //
	TERA("T", Math.pow(10, 12)), //
	PETA("P", Math.pow(10, 15)), //
	EXA("E", Math.pow(10, 18)), //
	ZETTA("Z", Math.pow(10, 21)), //
	YOTTA("Y", Math.pow(10, 24)), //

	DECI("d", Math.pow(10, -1)), //
	CENTI("c", Math.pow(10, -2)), //
	MILLI("m", Math.pow(10, -3)), //
	MICRO("μ", Math.pow(10, -6)), //
	NANO("n", Math.pow(10, -9)), //
	PICO("p", Math.pow(10, -12)), //
	FEMTO("f", Math.pow(10, -15)), //
	ATOO("a", Math.pow(10, -18)), //
	ZEPTO("z", Math.pow(10, -21)), //
	YOCTO("y", Math.pow(10, -24));

	private double factor = 1.0;
	private String s = "";

	private UnitPrefix(String s, double factor) {
		this.factor = factor;
		this.s = s;
	}

	public double factor() {
		return 1.0/this.factor;
	}
	
	public String s() {
		return this.s;
	}
}
