package de.uvwxy.units;

public class Conversion {
    public static String ID_DELIM = "_";
    private String a;
    private String b;
    private double factor;
    private ConversionFunction fnConvert;
    private ConversionFunction fnConvertInverse;

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

    /**
     * To convert from a to b we apply the conversion function fn to a: b=
     * fn(a).
     * 
     * @param a
     * @param b
     * @param factor
     */
    public Conversion(String a, String b, ConversionFunction fn, ConversionFunction fnInverse) {
        this(a, b, 0);

        if (fn == null) {
            throw new RuntimeException();
        }

        if (fnInverse == null) {
            throw new RuntimeException();
        }

        this.fnConvert = fn;
        this.fnConvertInverse = fnInverse;
    }

    public static String createKey(String a, String b) {
        return a + ID_DELIM + b;
    }

    public String getKey() {
        return createKey(a, b);
    }

    public Unit to(Unit to, double value) {
        if (b.equals(to.getName())) {
            double ret = value * factor;
            if (fnConvert != null) {
                ret = fnConvert.convert(value);
            }

            return Unit.from(to.getName()).setValue(ret);
        } else {
            double ret = value / factor;
            if (fnConvertInverse != null) {
                ret = fnConvertInverse.convert(value);
            }

            return Unit.from(to.getName()).setValue(ret);
        }
    };
}
