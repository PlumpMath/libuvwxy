package de.uvwxy.net.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.google.common.base.Preconditions;

import de.uvwxy.net.AConnection;
import de.uvwxy.net.ConnectionType;

public class BTConnection extends AConnection {
	public BTConnection(BluetoothSocket socket, String address, Object commLock) {
		super(ConnectionType.BLUETOOTH, address, commLock);
		Preconditions.checkNotNull(socket);
		this.mSocket = socket;
	}

	private BluetoothDevice mBluetoothDevice;
	private BluetoothSocket mSocket;

	@Override
	public void closeImpl() {
		try {
			if (in != null) {
				in.close();
			}

			if (out != null) {
				out.close();
			}

			if (mSocket != null) {
				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BluetoothDevice getmBluetoothDevice() {
		return mBluetoothDevice;
	}

	public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
		this.mBluetoothDevice = mBluetoothDevice;
	}

	@Override
	public boolean isConnected() {
		if (mSocket == null) {
			Log.i("BTSOCKET", "Socket == null");
			return false;
		}

//		Log.i("BTSOCKET", "mSocket.isConnected() = " + mSocket.isConnected());
//		try {
//			Log.i("BTSOCKET", "mSocket.getInputStream().available() = " + mSocket.getInputStream().available());
//		} catch (IOException e) {
//			e.printStackTrace();
//
//			return false;
//		}

		return !hasBeenClosed();
	}
}
