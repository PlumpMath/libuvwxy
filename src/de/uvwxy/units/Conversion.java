package de.uvwxy.units;

public class Conversion {
	public static String ID_DELIM = "_";
	private String a;
	private String b;
	private double factor;

	/**
	 * To convert from a to b we apply the factor to a: a*factor=b. Thus
	 * b/factor=a.
	 * 
	 * @param a
	 * @param b
	 * @param factor
	 */
	public Conversion(String a, String b, double factor) {
		this.a = a;
		this.b = b;
		this.factor = factor;
	}

	public static String createKey(String a, String b) {
		return a + ID_DELIM + b;
	}

	public String getKey() {
		return createKey(a, b);
	}

	public Unit to(Unit to, double value) {
		if (b.equals(to.getName())) {
			return Unit.from(to.getName()).setValue(value * factor);
		} else {
			return Unit.from(to.getName()).setValue(value / factor);
		}
	};
}
