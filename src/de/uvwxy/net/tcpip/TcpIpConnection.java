package de.uvwxy.net.tcpip;

import java.io.IOException;
import java.net.Socket;

import de.uvwxy.helper.Preconditions;
import de.uvwxy.net.AConnection;
import de.uvwxy.net.ConnectionType;

public class TcpIpConnection extends AConnection {

	public TcpIpConnection(Socket socket, Object commLock) {
		super(ConnectionType.IP, socket.getInetAddress().toString().replace("/", ""), commLock);
		Preconditions.checkNotNull(socket);
		this.mSocket = socket;

		try {
			this.out = socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			this.in = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket mSocket;

	@Override
	protected void closeImpl() {
		try {
			if (mSocket.isConnected()) {

				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isConnected() {
		if (mSocket == null) {
			return false;
		}

		return mSocket.isConnected() && !mSocket.isClosed();
	}
}
