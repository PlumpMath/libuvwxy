package de.uvwxy.ledlamp;

import java.io.IOException;
import java.util.ArrayList;

import de.uvwxy.net.AConnection;

/**
 * LED Driver for E/A LED Lamp tag v.e.a
 * 
 */
public class LEDDriverT1V1 extends LEDDriver {

	public LEDDriverT1V1(AConnection con) {
		super(con);
		modeListStatic = new ArrayList<ColorModeStatic<? extends LEDDriver>>();
		modeListStatic.add(ColorModeStatic.createSimple("Automatic", "a", LEDDriverT1V1.class));
		modeListStatic.add(ColorModeStatic.createSimple("2-Color Dim", "d", LEDDriverT1V1.class));
		modeListStatic.add(ColorModeStatic.createSimple("Manual", "m", LEDDriverT1V1.class));
	}

	@Override
	public void setBlue(int b) throws IOException {
		getCon().getOut().write(("b" + b).getBytes());
	}

	@Override
	public void setGreen(int g) throws IOException {
		getCon().getOut().write(("g" + g).getBytes());
	}

	@Override
	public void setRed(int r) throws IOException {
		getCon().getOut().write(("r" + r).getBytes());
	}

	@Override
	public void setSpeed(int s) throws IOException {
		getCon().getOut().write(("s" + s).getBytes());
	}

	@Override
	public void setWhite(int w) throws IOException {
		getCon().getOut().write(("w" + w).getBytes());
	}

}
