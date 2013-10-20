package de.uvwxy.net;

public enum ConnectionType {
	BLUETOOTH(0), WIFI_MAC(1), IP(2), XBEE(3);

	private int VALUE = -1;

	private ConnectionType(int t) {
		this.VALUE = t;
	}
	
	public int getValue(){
		return VALUE;
	}

}
