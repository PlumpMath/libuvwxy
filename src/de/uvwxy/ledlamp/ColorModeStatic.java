package de.uvwxy.ledlamp;

import java.io.IOException;

public abstract class ColorModeStatic<T extends LEDDriver> {

	public abstract String getName();

	public abstract void setMode(T driver) throws IOException;

	public static <Z extends LEDDriver> ColorModeStatic<Z> createSimple(final String name, final String command,
			Class<Z> clazz) {
		return new ColorModeStatic<Z>() {

			@Override
			public void setMode(Z driver) throws IOException {
				driver.getCon().getOut().write(command.getBytes());
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}
}
