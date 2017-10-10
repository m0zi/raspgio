package raspgio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class GPIOHandler implements HttpHandler {
	
	private final static Logger LOGGER = Logger.getLogger(GPIOHandler.class.getName());

	private final GpioPinDigitalOutput pin;

	public GPIOHandler(final GpioPinDigitalOutput pin) {
		this.pin = pin;
	}

	@Override
	public void handle(final HttpExchange t) throws IOException {
		final String state = GPIOHandler.splitQuery(t.getRequestURI()).get("state").get(0);

		if (state == null) {
			t.sendResponseHeaders(404, -1L);
		}

		switch (state.toLowerCase()) {

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

		default:
			t.sendResponseHeaders(405, -1L);
			break;
		}

		LOGGER.info(String.valueOf(this.pin.getName()) + " was set to " + this.pin.getState());

		t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		t.sendResponseHeaders(200, -1L);
	}

	public static Map<String, List<String>> splitQuery(final URI uri) throws UnsupportedEncodingException {
		final Map<String, List<String>> queryPairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = uri.getQuery().split("&");

		for (String pair : pairs) {
			final int index = pair.indexOf("=");
			final String key = (index > 0) ? URLDecoder.decode(pair.substring(0, index), "UTF-8") : pair;

			if (!queryPairs.containsKey(key)) {
				queryPairs.put(key, new LinkedList<String>());
			}

			final String value = (index > 0 && pair.length() > index + 1)
					? URLDecoder.decode(pair.substring(index + 1), "UTF-8") : null;

			queryPairs.get(key).add(value);
		}

		return queryPairs;
	}
}