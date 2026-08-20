package com.helicalinsight.test.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

/**
 * Serves the test JPEG over HTTP on 127.0.0.1 so HCR {@code link} tests do not
 * depend on public CDNs.
 */
public final class LocalImageHttpServer implements AutoCloseable {

	private static final String PATH = "/echo-sport.jpg";
	private static final String CLASSPATH_IMAGE = "/test/echo-sport.jpg";

	private final HttpServer server;

	private LocalImageHttpServer(HttpServer server) {
		this.server = server;
	}

	public static LocalImageHttpServer start() throws IOException {
		byte[] jpeg;
		try (InputStream in = LocalImageHttpServer.class.getResourceAsStream(CLASSPATH_IMAGE)) {
			if (in == null) {
				throw new IOException("Missing classpath resource " + CLASSPATH_IMAGE);
			}
			jpeg = in.readAllBytes();
		}
		HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		httpServer.createContext(PATH, exchange -> {
			exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
			exchange.sendResponseHeaders(200, jpeg.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(jpeg);
			}
		});
		httpServer.start();
		return new LocalImageHttpServer(httpServer);
	}

	public String imageUrl() {
		return "http://127.0.0.1:" + server.getAddress().getPort() + PATH;
	}

	@Override
	public void close() {
		server.stop(0);
	}
}
