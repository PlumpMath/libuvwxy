package de.uvwxy.net.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import de.uvwxy.helper.Preconditions;
import de.uvwxy.net.AConnectionSetup;
import de.uvwxy.net.ICancelHandler;
import de.uvwxy.net.IConnectHandler;
import de.uvwxy.net.IProtocol;
import de.uvwxy.net.ISetupCallback;

public class BTConnectionSetup extends AConnectionSetup {

	// Source:
	// http://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingAsAServer
	private class BtServerSetupThread extends Thread {
		private BluetoothServerSocket mmServerSocket;
		private BTConnection mmBTCon;
		private IProtocol protocol;
		private BluetoothSocket socket = null;
		private UUID mmUuid;
		private String mmName;

		public BtServerSetupThread(IProtocol protocol, String name, UUID uuid) {
			Preconditions.checkNotNull(uuid);
			Preconditions.checkNotNull(name);
			Preconditions.checkNotNull(protocol);
			this.protocol = protocol;
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			this.mmName = name;
			this.mmUuid = uuid;
			mmServerSocket = null;
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				Log.i("KILLX", "0");
				if (mmServerSocket != null) {
					Log.i("KILLX", "1");
					mmServerSocket.close();
					Log.i("KILLX", "2");
				}
				Log.i("KILLX", "3");
				if (mmBTCon != null && !mmBTCon.isConnected()) {
					Log.i("KILLX", "4");
					mmBTCon.close();
					Log.i("KILLX", "5");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			boolean reset = false;
			@SuppressWarnings("unused")
			int count = 0;
			while (true) {
				//				fffLog.i("KILLX", "I " + count);
				count++;

				if (mmBTCon == null || !mmBTCon.isConnected()) {
					Log.i("BLEU", "Was Not Connected");
					reset = true;
				}

				if (mmBTCon != null && mmBTCon.hasParser() && !mmBTCon.parserIsRunning()) {
					Log.i("BLEU", "Was Not Parser Running");
					reset = true;
				}

				if (reset) {
					Log.i("KILLX", "II");
					cancel();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					Log.i("KILLX", "III");
					loopListen();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					count = 0;
					reset = false;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}

		private void loopListen() {

			// Keep listening until exception occurs or a socket is returned
			while (true) {
				Log.i("KILLX", "IV");
				try {
					Log.i("BLEU", "Listening for " + mmName + "/" + mmUuid);
					mBTAdapter = BluetoothAdapter.getDefaultAdapter();
					mmServerSocket = mBTAdapter.listenUsingRfcommWithServiceRecord(mmName, mmUuid);
					Log.i("BLEU", "Waiting for connection");
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					// if we can not open a socket we land here.
					e.printStackTrace();
					setInterfaceEnabled(false);
					Handler h = new Handler(ctx.getMainLooper());
					h.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(ctx,
									"Bluetooth server failed. Please restart bluetooth MANUALLY in a few seconds",
									Toast.LENGTH_LONG).show();
						}
					});
					break;
				}
				// If a connection was accepted
				if (socket != null) {

					// Do work to manage the connection (in a separate thread)
					// manageConnectedSocket(socket);
					try {
						BTConnection tmpConnection = new BTConnection(socket, socket.getRemoteDevice().getAddress(),
								null);
						tmpConnection.setIn(socket.getInputStream());
						tmpConnection.setOut(socket.getOutputStream());
						tmpConnection.setmBluetoothDevice(socket.getRemoteDevice());
						mmBTCon = tmpConnection;

						protocol.doProtocol(tmpConnection);
						Log.i("BLEU", "Accepted connection from " + socket.getRemoteDevice());

						// will not close the socket, only the server socket.

					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						mmServerSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					break;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Source:
	// http://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingAsAClient
	private class BtClientSetupThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final IConnectHandler mmHandler;
		private IProtocol protocol;
		private Object commLock;
		private BTConnection mmConnection;

		public BtClientSetupThread(IProtocol protocol, IConnectHandler handler, BluetoothDevice device, UUID uuid,
				Object commLock) {
			Preconditions.checkNotNull(uuid);
			Preconditions.checkNotNull(device);
			Preconditions.checkNotNull(handler);
			Preconditions.checkNotNull(protocol);

			this.protocol = protocol;
			this.mmHandler = handler;
			this.commLock = commLock;
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				Log.i("BLEU", "Create RF COMM SOCKET");
				tmp = device.createRfcommSocketToServiceRecord(uuid);
				Log.i("BLEU", "Create RF COMM SOCKET OK");
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {

			// Cancel discovery because it will slow down the connection
			mBTAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				Log.i("BLEU", "Connecting..");
				mmSocket.connect();
				Log.i("BLEU", "Connected..");

			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				Log.i("BLEU", "Connecting failed..");
				cancel();
				return;
			}

			// call back with generic connection object
			try {
				mmConnection = new BTConnection(mmSocket, mmSocket.getRemoteDevice().getAddress(), commLock);
				mmConnection.setIn(mmSocket.getInputStream());
				mmConnection.setOut(mmSocket.getOutputStream());
				mmConnection.setmBluetoothDevice(mmSocket.getRemoteDevice());
				mmHandler.onConnect(protocol, mmConnection, mmSocket.getRemoteDevice().getAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			Log.i("BLEU", "Cancel?");
			if (mmConnection != null) {
				if (!mmConnection.isConnected()) {
					mmConnection.close();
					Log.i("BLEU", "Cancel!");
				}
			} else {
				if (commLock == null) {
					return;
				}
				synchronized (commLock) {
					commLock.notify();
				}
				if (mmSocket != null) {
					try {
						mmSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}

		}
	}

	private Context ctx;

	/**
	 * Broadcast receiver to respond to devices found via BT scan.
	 */
	private class ActionFoundReceiver extends BroadcastReceiver {
		private boolean mmRegistered = false;
		private Context mmCtx;

		public void onReceive(Context context, Intent intent) {
			Log.i("BLEU", "RECEIVED");
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				mCallback.foundDevice(device.getAddress());
			}
		}

		@SuppressWarnings("unused")
		public synchronized void setRegistered(boolean b) {
			mmRegistered = b;
		}

		public synchronized void register(Context ctx) {
			if (this.mmCtx == null) {
				this.mmCtx = ctx;
			}

			if (!mmRegistered) {
				mmRegistered = true;
				mmCtx.registerReceiver(this, new IntentFilter(BluetoothDevice.ACTION_FOUND));
				Log.i("BLEU", "registerReceiver ActionFoundReceiver");

			}
		}

		public synchronized void unRegister() {
			if (mmCtx == null) {
				return;
			}

			try {
				mmCtx.unregisterReceiver(this);
				Log.i("BLEU", "unregisterReceiver ActionFoundReceiver");

				mmRegistered = false;
			} catch (Exception e) {

			}
		}
	};

	private ActionFoundReceiver mActionFoundReceiver;
	private DiscoveryFinishedReceiver mDiscoveryFinishedReceiver;
	private BluetoothAdapter mBTAdapter;

	/**
	 * BroadcastRecevier to respond to discovery being stopped.
	 */
	private class DiscoveryFinishedReceiver extends BroadcastReceiver {
		private boolean mmRegistered = false;
		private Context mmCtx;

		public void onReceive(Context context, Intent intent) {
			Log.i("BLEU", "RECEIVED");

			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// TODO: do sth. when discovery has finished?
				mCallback._log("Discovery stopped");
			}
		}

		public synchronized void register(Context ctx) {
			if (this.mmCtx == null) {
				this.mmCtx = ctx;
			}

			if (!mmRegistered) {
				mmRegistered = true;
				mmCtx.registerReceiver(this, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
				Log.i("BLEU", "registerReceiver DiscoveryFinishedReceiver");

			}
		}

		public synchronized void unRegister() {
			if (mmCtx == null) {
				return;
			}

			try {
				mmCtx.unregisterReceiver(this);
				Log.i("BLEU", "unregisterReceiver DiscoveryFinishedReceiver");

				mmRegistered = false;
			} catch (Exception e) {

			}
		}
	};

	private String mName;

	public UUID mUUID;

	/**
	 * Constructor. Context as needed by android api, BluetoothCallback to pass
	 * discovered devices back to calling code.
	 * 
	 * @param ctx
	 * @param btd
	 */
	public BTConnectionSetup(Context ctx, ISetupCallback callback) {
		super(callback);

		Preconditions.checkNotNull(ctx);

		this.ctx = ctx;

		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	private ICancelHandler ic;

	@Override
	public ICancelHandler connect(IProtocol protocol, IConnectHandler handler, String address, Object commLock) {
		BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
		final BtClientSetupThread t = new BtClientSetupThread(protocol, handler, device, mUUID, commLock);

		// cancel old connections to avoid:
		// "getbluetoothservice() called with no bluetoothmanagercallback"
		// NOTE: THIS SHOUDL NOT BE NEEDED AS WE MONITOR commLco BEFORE
		// RECONNECTING
		// if (ic != null) {
		// ic.cancel();
		//
		// }

		ic = new ICancelHandler() {

			@Override
			public void cancel() {
				t.cancel();
				t.interrupt();
			}
		};

		t.start();

		return ic;
	}

	public String getName() {
		return mName;
	}

	public UUID getUUID() {
		return mUUID;
	}

	@Override
	public ICancelHandler listen(IProtocol protocol) {

		final BtServerSetupThread t = new BtServerSetupThread(protocol, mName, mUUID);
		t.start();

		return new ICancelHandler() {

			@Override
			public void cancel() {
				t.cancel();
				t.interrupt();
			}
		};
	}

	@Override
	public void setInterfaceEnabled(boolean enabled) {
		if (mBTAdapter == null) {
			return;
		}

		if (enabled) {
			mBTAdapter.enable();
		} else {
			mBTAdapter.disable();
		}
	}

	public boolean isInterfaceEnabled() {
		if (mBTAdapter == null) {
			return false;
		}
		return mBTAdapter.isEnabled();
	}

	public void setName(String name) {
		this.mName = name;
	}

	public void setUUID(UUID uUID) {
		mUUID = uUID;
	}

	public void startDiscovery() {

		if (mActionFoundReceiver == null) {
			mActionFoundReceiver = new ActionFoundReceiver();
		}

		if (mDiscoveryFinishedReceiver == null) {
			mDiscoveryFinishedReceiver = new DiscoveryFinishedReceiver();
		}

		mActionFoundReceiver.unRegister();
		mDiscoveryFinishedReceiver.unRegister();

		mActionFoundReceiver.register(ctx);
		mDiscoveryFinishedReceiver.register(ctx);

		//startVisibility(120);
		mBTAdapter.startDiscovery();
		Log.i("BLEU", "startDiscovery");
	}

	public void startVisibility(long seconds) {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, seconds);
		ctx.startActivity(discoverableIntent);
		// TODO: set visible again for 1h after timeout
	}

	public void stopDiscovery() {

		if (mActionFoundReceiver != null) {
			mActionFoundReceiver.unRegister();
		}
		if (mDiscoveryFinishedReceiver != null) {
			mDiscoveryFinishedReceiver.unRegister();
		}

		mBTAdapter.cancelDiscovery();
	}

	@Override
	public void stopVisiblity() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
		ctx.startActivity(discoverableIntent);
	}

}
