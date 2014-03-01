package de.uvwxy.net;

public interface ISetupCallback {
	public void foundDevice(String address);

	public void discoveryStopped();

	public void _log(String s);

}
