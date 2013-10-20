package de.uvwxy.net.tcpip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

import com.google.common.base.Preconditions;

import de.uvwxy.net.AConnectionSetup;
import de.uvwxy.net.ICancelHandler;
import de.uvwxy.net.IConnectHandler;
import de.uvwxy.net.IProtocol;
import de.uvwxy.net.ISetupCallback;

public class TcpIPConnectionSetup extends AConnectionSetup {

	public TcpIPConnectionSetup(ISetupCallback callback) {
		super(callback);
	}

	private int mPort = 41952;

	private class TcpIpServerSetupThread extends Thread {
		ServerSocket mmSocketServer;
		Socket sck;
		int mmPort;
		private IProtocol protocol;

		public TcpIpServerSetupThread(IProtocol protocol, int port) {
			Preconditions.checkNotNull(protocol);
			this.mmPort = port;
			this.protocol = protocol;
		}

		@Override
		public void run() {
			while (true) {
				// TODO: listen in a loop
				if (sck == null || sck.isClosed()) {
					cancel();

					loopListen();

				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void loopListen() {

			try {
				mmSocketServer = new ServerSocket(mmPort);
				Log.i("SERVER", "new ServerSocket");
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (mmSocketServer == null) {
				Log.i("SERVER", "sckServe == null");
				return;
			}

			try {
				// BLOCK
				sck = mmSocketServer.accept();
				mmSocketServer.close();

				Log.i("SERVER", "accepted");

				// connected
				protocol.doProtocol(new TcpIpConnection(sck, null));
				Log.i("SERVER", "on connect from " + sck.getInetAddress().toString().replace("/", ""));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				if (mmSocketServer != null) {
					mmSocketServer.close();
				}
			} catch (IOException e) {
			}
		}

	}

	private class TcpIpClientSetupThread extends Thread {
		Socket sck;
		private IConnectHandler handler;
		int mmPort;
		long mmTimeoutMS;
		String mmAddress;
		private IProtocol protocol;
		private Object commLock;

		public TcpIpClientSetupThread(IProtocol protocol, IConnectHandler handler, String address, int port, Object commLock) {
			Preconditions.checkNotNull(handler);
			Preconditions.checkNotNull(address);

			this.handler = handler;
			this.mmPort = port;
			this.mmAddress = address;
			this.commLock = commLock;
			this.protocol = protocol;
		}

		@Override
		public void run() {
			if (mmTimeoutMS != 0) {
				Handler h = new Handler();
				h.postDelayed(new Runnable() {

					@Override
					public void run() {
						// only cancel if no connection yet
						if (sck == null) {
							cancel();
							handler.onConnectTimeout();
						} else {
							// ignore
						}
					}
				}, mmTimeoutMS);
			}

			// if yes, do so
			try {
				Log.i("Client", "connecting");

				sck = new Socket(mmAddress, mmPort);
				Log.i("Client", "connected");

				handler.onConnect(protocol, new TcpIpConnection(sck, commLock), mmAddress);
			} catch (IOException e) {
				e.printStackTrace();
				handler.onConnectTimeout();
				cancel();
			}

		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			if (sck != null && sck.isConnected()) {
				try {
					if (sck != null) {
						// will handle commLock aswell
						sck.close();
						Log.i("TCPIP", "Cancel!");
					}
				} catch (IOException e) {
				}
			} else {
				// manually release comm lock
				if (commLock == null) {
					return;
				}
				synchronized (commLock) {
					commLock.notify();
				}
			}
		}

	}

	// TODO: This is not implemented yet
	@Override
	public void setInterfaceEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	// TODO: This is not implemented yet
	@Override
	public void startDiscovery() {
		// TODO Auto-generated method stub

	}

	// TODO: This is not implemented yet
	@Override
	public void stopDiscovery() {
		// TODO Auto-generated method stub

	}

	// TODO: This is not implemented yet
	@Override
	public void startVisibility(long seconds) {
		// TODO Auto-generated method stub

	}

	// TODO: This is not implemented yet
	@Override
	public void stopVisiblity() {
		// TODO Auto-generated method stub

	}

	@Override
	public ICancelHandler connect(IProtocol protocol, IConnectHandler handler, String address, Object commLock) {
		final TcpIpClientSetupThread t = new TcpIpClientSetupThread(protocol, handler, address, mPort, commLock);
		t.start();

		return new ICancelHandler() {

			@Override
			public void cancel() {
				t.cancel();
			}
		};
	}

	@Override
	public ICancelHandler listen(IProtocol protocol) {
		final TcpIpServerSetupThread t = new TcpIpServerSetupThread(protocol, mPort);
		t.start();
		return new ICancelHandler() {

			@Override
			public void cancel() {
				t.cancel();
				t.interrupt();
			}
		};
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		this.mPort = port;
	}
}
