package de.uvwxy.net;

import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.protobuf.MessageOrBuilder;

import de.uvwxy.proto.parser.ProtoInputStreamParser;

public abstract class AConnection {
	protected InputStream in;
	protected OutputStream out;
	protected ConnectionType type;
	protected String address;
	protected ProtoInputStreamParser<? extends MessageOrBuilder> protoInputStreamParser;
	protected Object commLock;

	public AConnection(ConnectionType type, String address, Object commLock) {
		super();
		Preconditions.checkNotNull(address);
		this.type = type;
		this.address = address;
		this.commLock = commLock;
		Log.i("SOCKET", "CREATED");
	}

	public String getRemoteAddress() {
		return address;
	}

	public boolean hasParser() {
		return protoInputStreamParser != null;
	}

	public boolean parserIsRunning() {
		return protoInputStreamParser.isRunning();
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public void setProtoInputStreamParser(ProtoInputStreamParser<? extends MessageOrBuilder> protoInputStreamParser) {
		this.protoInputStreamParser = protoInputStreamParser;
		Log.i("SOCKET", "setting protoInputStreamParser");

	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public ConnectionType getType() {
		return type;
	}

	public void setType(ConnectionType type) {
		this.type = type;
	}

	private boolean hasBeenClosed = false;

	public synchronized boolean hasBeenClosed() {
		return hasBeenClosed;
	}

	public synchronized void close() {
		hasBeenClosed = true;
		Log.i("SOCKET", "CLOSE");
		if (protoInputStreamParser != null) {
			protoInputStreamParser.stop();
		}
		closeImpl();
		if (commLock != null) {
			Log.i("AConnection", "Notifying commLock");
			synchronized (commLock) {
				commLock.notify();
			}
		}
	}

	protected abstract void closeImpl();

	public abstract boolean isConnected();
}
