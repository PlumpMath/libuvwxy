package de.uvwxy.net;

public interface IConnectHandler {

	public void onConnect(IProtocol protocol, AConnection c, String usedAddress);

	public void onConnectTimeout();

}
