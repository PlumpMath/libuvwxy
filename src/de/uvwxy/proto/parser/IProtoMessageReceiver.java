package de.uvwxy.proto.parser;

import com.google.protobuf.MessageOrBuilder;

public interface IProtoMessageReceiver<E extends MessageOrBuilder> {
	public void onReceive(E mob);
}
