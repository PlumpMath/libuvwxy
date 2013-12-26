package de.uvwxy.units;

public class Conversion {
	public static String ID_DELIM = "_";
	private Unit a;
	private Unit b;
	private double factor;
	private String id;

	/**
	 * To convert from a to b we apply the factor to a: a*factor=b. Thus
	 * b/factor=a.
	 * 
	 * @param a
	 * @param b
	 * @param factor
	 */
	public Conversion(Unit a, Unit b, double factor) {
		this.a = a;
		this.b = b;
		this.factor = factor;
	}
	public static String createKey(Unit a, Unit b){
		return a.name() + ID_DELIM + b.name();
	}
	public String getKey() {
		return createKey(a,b);
	}

	public Unit to(Unit to, double value) {
		if (b.name().equals(to.name())) {
			return to.setValue(value * factor);
		} else {
			return to.setValue(value / factor);
		}
	};
}
