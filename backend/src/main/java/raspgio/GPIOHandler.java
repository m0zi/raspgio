package raspgio;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class GPIOHandler implements HttpHandler {

	private final static Logger LOGGER = Logger.getLogger(GPIOHandler.class.getName());

	private final GpioPinDigitalOutput pin;

	private final String statesFile;

	private final Properties states;

	public GPIOHandler(final GpioPinDigitalOutput pin, Properties states, String statesFile) {
		this.pin = pin;
		this.statesFile = statesFile;
		this.states = states;
	}

	@Override
	public void handle(final HttpExchange httpExchange) throws IOException {
		final String action = GPIOHandler.splitQuery(httpExchange.getRequestURI()).get("action").get(0);

		if (action == null) {
			httpExchange.sendResponseHeaders(404, -1L);
		}

		switch (action.toLowerCase()) {
			case "toggle": {
				this.pin.toggle();
				break;
			}
	
			case "on": {
				this.pin.setState(PinState.LOW);
				break;
			}
	
			case "off": {
				this.pin.setState(PinState.HIGH);
				break;
			}
	
			case "get": {
				break;
			}
	
			default: {
				httpExchange.sendResponseHeaders(405, -1L);
				break;
			}
		}

		PinState pinState = this.pin.getState();
		String response = "{ \"state\": " + pinState.isLow() + " }";

		LOGGER.info(String.valueOf(this.pin.getName()) + " was set to " + pinState);
		LOGGER.info(String.valueOf("Server Response:  " + response));

		httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		httpExchange.getResponseHeaders().add("Content-Type", "application/json");

		DataOutputStream out = new DataOutputStream(httpExchange.getResponseBody());

		httpExchange.sendResponseHeaders(200, 0);

		out.writeBytes(response);
		out.flush();
		out.close();

		states.setProperty(this.pin.getName(), pinState.toString());
		states.store(new FileOutputStream(this.statesFile), null);
	}

	private static Map<String, List<String>> splitQuery(final URI uri) throws UnsupportedEncodingException {
		final Map<String, List<String>> queryPairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = uri.getQuery().split("&");

		for (String pair : pairs) {
			final int index = pair.indexOf("=");
			final String key = (index > 0) ? URLDecoder.decode(pair.substring(0, index), "UTF-8") : pair;

			if (!queryPairs.containsKey(key)) {
				queryPairs.put(key, new LinkedList<String>());
			}

			final String value = (index > 0 && pair.length() > index + 1) ? URLDecoder.decode(pair.substring(index + 1), "UTF-8") : null;

			queryPairs.get(key).add(value);
		}

		return queryPairs;
	}
}