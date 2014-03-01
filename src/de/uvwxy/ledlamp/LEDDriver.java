package de.uvwxy.ledlamp;

import java.io.IOException;
import java.util.ArrayList;

import de.uvwxy.net.AConnection;

public abstract class LEDDriver {

	private AConnection con;

	ArrayList<ColorModeStatic<? extends LEDDriver>> modeListStatic;

	public LEDDriver(AConnection con) {
		this.con = con;
	}

	public AConnection getCon() {
		return con;
	}

	public abstract void setBlue(int b) throws IOException;

	public abstract void setGreen(int g) throws IOException;

	public abstract void setRed(int r) throws IOException;

	public void setRGB(int r, int g, int b) throws IOException {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setWhite(0);
	}

	public void setRGBW(int r, int g, int b, int w) throws IOException {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setWhite(w);
	}

	public abstract void setSpeed(int s) throws IOException;

	public abstract void setWhite(int w) throws IOException;
}
