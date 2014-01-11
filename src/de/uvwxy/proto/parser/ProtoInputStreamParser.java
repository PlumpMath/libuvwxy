package de.uvwxy.proto.parser;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.protobuf.MessageOrBuilder;

public class ProtoInputStreamParser<E extends MessageOrBuilder> {

	protected InputStream mInputStream;
	private IProtoMessageReceiver<E> mMessageReceiveCallback;
	private IProtoMessageParser<E> mMessageParserCallback;

	boolean parserThreadRunning = true;

	Runnable pollProtoSocketThread = new Runnable() {

		@Override
		public void run() {
			while (parserThreadRunning) {

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.i("PARSER", "Thread.sleep(50); InterruptedException e");
					parserThreadRunning = false;
				}

				boolean read = false;
				try {
					//					Log.i("KILLX", "A");
					// Fatal signal 11 (SIGSEGV) at 0x00000008 (code=1) here when connection is gone:
					if (parserThreadRunning && mInputStream != null && mInputStream.available() > 0) {
						// thus we check streamSource.isOpen() before we further check for available data.
						read = true;
					}

					//					Log.i("KILLX", "B");
				} catch (IOException e) {
					//if this stream is closed or an error occurs
					Log.i("PARSER", "catch (IOException e)ff");

					parserThreadRunning = false;
					e.printStackTrace();
				}

				if (read) {
					E mob = mMessageParserCallback.parseMessageFromStream(mInputStream);
					if (mob != null) {
						mMessageReceiveCallback.onReceive(mob);
					}
				}
			}

			Log.i("PARSER", "PARSER STOPPED;");
		}
	};

	Thread threadParse = new Thread(pollProtoSocketThread);

	public ProtoInputStreamParser(IProtoMessageReceiver<E> messageReceiveCallback, IProtoMessageParser<E> messageParserCallback, InputStream inputStream) {
		super();

		Preconditions.checkNotNull(messageReceiveCallback);
		Preconditions.checkNotNull(messageParserCallback);
		Preconditions.checkNotNull(inputStream);

		this.mMessageReceiveCallback = messageReceiveCallback;
		this.mMessageParserCallback = messageParserCallback;
		this.mInputStream = inputStream;

		this.parserThreadRunning = false;
	}

	public synchronized void start() {
		if (parserThreadRunning == false) {
			parserThreadRunning = true;
			threadParse.start();
		}
	}

	public synchronized void stop() {
		parserThreadRunning = false;
		threadParse.interrupt();
	}

	public boolean isRunning() {
		return parserThreadRunning;
	}

}