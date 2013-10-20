package de.uvwxy.net.wifi;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;
import android.widget.Toast;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.net.AConnectionSetup;
import de.uvwxy.net.ICancelHandler;
import de.uvwxy.net.IConnectHandler;
import de.uvwxy.net.IProtocol;
import de.uvwxy.net.ISetupCallback;

/**
 * TODOS: callbacks wether a certain action has succeeded
 * 
 * @author Paul Smith
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WIFIP2PConnectionSetup extends AConnectionSetup {
	private Context ctx;

	private WifiP2pManager mP2PManager;
	private WiFiDirectBroadcastReceiver mReceiver;
	private Channel mChannel;

	public WIFIP2PConnectionSetup(Context ctx, ISetupCallback callback) {
		super(callback);

		if (ctx == null) {
			throw new RuntimeException("Context can not be null");
		}
		this.ctx = ctx;

		mP2PManager = (WifiP2pManager) ctx.getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mP2PManager.initialize(ctx, ctx.getMainLooper(), null);

		if (mP2PManager != null) {
			registerReceiver(ctx, mChannel);
		} else {
			Toast.makeText(ctx, "WIFI P2P Manager not supported on this device!", Toast.LENGTH_LONG).show();
		}

	}

	public WIFIP2PConnectionSetup(Context ctx, ISetupCallback callback, Channel channel) {
		super(callback);

		if (ctx == null) {
			throw new RuntimeException("Context can not be null");
		}

		this.ctx = ctx;
		this.mChannel = channel;

		mP2PManager = (WifiP2pManager) ctx.getSystemService(Context.WIFI_P2P_SERVICE);

		if (mP2PManager != null) {
			registerReceiver(ctx, channel);
		} else {
			Toast.makeText(ctx, "WIFI P2P Manager not supported on this device!", Toast.LENGTH_LONG).show();
		}

	}

	private void registerReceiver(Context ctx, Channel channel) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		mReceiver = new WiFiDirectBroadcastReceiver(mP2PManager, channel);

		IntentTools.safeUnregister(ctx, mReceiver);

		ctx.registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * The discovery remains active until a connection is initiated or a p2p
	 * group is formed. OR stopDiscovery is called (which works with API v16+
	 * only)
	 */
	public void startDiscovery() {
		if (mP2PManager == null) {
			return;
		}

		// null: we don't care for now if we succeed. we are happy when a result
		// is broadcast to us.
		mP2PManager.discoverPeers(mChannel, null);
	}

	/**
	 * This only works with API v 16+
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void stopDiscovery() {
		IntentTools.safeUnregister(ctx, mReceiver);
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			mP2PManager.stopPeerDiscovery(mChannel, null);
		}
	}

	@Override
	public void startVisibility(long seconds) {
	}

	@Override
	public void stopVisiblity() {
	}

	/**
	 * Get P2PGroup owner address (via callback)
	 */
	public void getGroupOwnerAddress() {
		mP2PManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {

			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				// TODO:
				// callback.onP2PGroupOwnerAddress(info.groupOwnerAddress);
			}
		});
	}

	/**
	 * Use this to create an access point for legacy devices
	 */
	public void createGroup() {
		mP2PManager.createGroup(mChannel, null);
	}

	private PeerListListener myPeerListListener = new PeerListListener() {

		@Override
		public void onPeersAvailable(WifiP2pDeviceList peers) {
			// TODO: callback.onP2PListUpdate(new
			// ArrayList<WifiP2pDevice>(peers.getDeviceList()));
			for (WifiP2pDevice p : peers.getDeviceList()) {
				mCallback.foundDevice(p.deviceAddress);
			}
		}
	};

	/**
	 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
	 */
	class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

		private WifiP2pManager mManager;
		private Channel mChannel;

		public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
			super();
			this.mManager = manager;
			this.mChannel = channel;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
				if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
					mCallback._log("onP2PStateChanged(true)");
				} else {
					mCallback._log("onP2PStateChanged(false)");
				}
			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				// request available peers from the wifi p2p manager. This is an
				// asynchronous call and the calling activity is notified with a
				// callback on PeerListListener.onPeersAvailable()
				if (mManager != null) {
					mManager.requestPeers(mChannel, myPeerListListener);
				}
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				// Respond to new connection or disconnections
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				// Respond to this device's wifi state changing
			}
		}
	}

	@Override
	public void setInterfaceEnabled(boolean enabled) {
		WifiManager wifiManager = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			wifiManager.setWifiEnabled(enabled);
		}
	}

	@Override
	public ICancelHandler connect(IProtocol protocol, IConnectHandler handler, final String address, Object commLock) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = address;
		config.wps.setup = WpsInfo.PBC;
		mP2PManager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				mCallback._log("onP2PConnectionSuccess (" + address + ")");
			}

			@Override
			public void onFailure(int reason) {
				mCallback._log("onP2PConnectionFail (" + address + ", " + reason + ")");
			}
		});

		return new ICancelHandler() {

			@Override
			public void cancel() {
				mP2PManager.cancelConnect(mChannel, new ActionListener() {

					@Override
					public void onSuccess() {
						mCallback._log("onCancelConnect Success (" + address + ")");
					}

					@Override
					public void onFailure(int reason) {
						mCallback._log("oCancelConnect Fail (" + address + ")");
					}
				});
			}
		};
	}

	@Override
	public ICancelHandler listen(IProtocol protocol) {
		// TODO: implement wifi direct connection establish.
		return null;
	}

}
