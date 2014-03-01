package de.uvwxy.net;

import java.io.InputStream;
import java.io.OutputStream;

public class DummyConnection extends AConnection {

	public DummyConnection(ConnectionType type, String address, Object commLock) {
		super(type, address, commLock);
	}

	public DummyConnection(ConnectionType type, InputStream in, OutputStream out, Object commLock) {
		super(type, "dummy", commLock);
		setIn(in);
		setOut(out);
	}

	@Override
	protected void closeImpl() {
	}

	@Override
	public boolean isConnected() {
		return true;
	}

}
