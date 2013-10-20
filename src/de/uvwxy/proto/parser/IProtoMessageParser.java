package de.uvwxy.proto.parser;

import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.MessageOrBuilder;

public interface IProtoMessageParser<E extends MessageOrBuilder> {
	/**
	 * To keep the ProtoSocket as generic as possible we have to create a loop
	 * where we can use a specific proto buf object to parse from the stream,
	 * and then feed it back to the receiving callback
	 * 
	 * @param in
	 * @return
	 */
	public E parseMessageFromStream(InputStream in);
}
