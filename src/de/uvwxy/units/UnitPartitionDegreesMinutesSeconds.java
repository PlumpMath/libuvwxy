package de.uvwxy.units;

public class UnitPartitionDegreesMinutesSeconds implements UnitPartition {

	@Override
	public String getValue(int position, int precision, double value) {
		switch (position) {
		case 1:
			// Minutes
			double x = value;
			x = (x - (int) x) * 60.0;
			x = Math.floor(x);
			return String.format("%." + precision + "f", x);
		case 2:
			// Seconds
			x = value;
			x = (x - (int) x) * 60.0;
			x = (x - (int) x) * 60.0;
			return String.format("%." + precision + "f", x);
		default:
			value = Math.floor(value);
			// Degrees
			return String.format("%." + precision + "f", value);
		}
	}
}
