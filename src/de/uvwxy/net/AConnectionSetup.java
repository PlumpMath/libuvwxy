package de.uvwxy.net;

import com.google.common.base.Preconditions;

/**
 * This is a try to create a general model to create connections using
 * Wifi/BT/[..?]..
 * 
 * @author paul
 * 
 */
public abstract class AConnectionSetup {
	protected ISetupCallback mCallback;

	public AConnectionSetup(ISetupCallback callback) {
		Preconditions.checkNotNull(callback);

		this.mCallback = callback;
	}

	public abstract void setInterfaceEnabled(boolean enabled);

	public abstract void startDiscovery();

	public abstract void stopDiscovery();

	public abstract void startVisibility(long seconds);

	public abstract void stopVisiblity();

	public abstract ICancelHandler connect(IProtocol protocol, IConnectHandler handler, String address, Object commLock);

	/**
	 * Will listen until the cancel handler is evoked.
	 * @param protocol
	 * @param handler
	 * @return
	 */
	public abstract ICancelHandler listen(IProtocol protocol);
}
