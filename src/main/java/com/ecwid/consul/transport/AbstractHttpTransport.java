package com.ecwid.consul.transport;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class AbstractHttpTransport implements HttpTransport {

	static final int DEFAULT_CONNECTION_TIMEOUT = 3000; // 10 sec

	// 10 minutes for read timeout due to blocking queries timeout
	// https://www.consul.io/api/index.html#blocking-queries
	static final int DEFAULT_READ_TIMEOUT = 60000 * 10; // 10 min

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private HttpClient httpClient = new HttpClient(DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);


	@Override
	public RawResponse makeGetRequest(String url) {
		try {
			return httpClient.sendingGetRequest(url);
		} catch (IOException e) {
			throw new TransportException(e);
		}
	}

	@Override
	public RawResponse makePutRequest(String url, String content) {
		try {
			return httpClient.sendingPutRequest(url, content);
		} catch (IOException e) {
			throw new TransportException(e);
		}
	}

	@Override
	public RawResponse makePutRequest(String url, byte[] content) {
		try {
			return httpClient.sendingPutRequest(url, new String(content));
		} catch (IOException e) {
			throw new TransportException(e);
		}
	}

	@Override
	public RawResponse makeDeleteRequest(String url) {
		try {
			return httpClient.sendingDeleteRequest(url);
		} catch (IOException e) {
			throw new TransportException(e);
		}
	}
}