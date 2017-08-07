package raspgio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.ini4j.Wini;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class RaspberryPiGPIOHomeControl
{

	private final static Logger								LOGGER	= Logger.getLogger(RaspberryPiGPIOHomeControl.class.getName());

	private static final LinkedList<GpioPinDigitalOutput>	allPins	= new LinkedList<GpioPinDigitalOutput>();

	public static void main(String[] args) throws IOException
	{
		GpioController gpioController = GpioFactory.getInstance();
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		Wini wini = new Wini(Paths.get(args[0], new String[0]).toFile());
		wini.forEach((sectionName, section) ->
		{
			if (sectionName != null)
			{
				section.forEach((k, v) ->
				{
					Pin pinByName = RaspiPin.getPinByName((String) k);
					if (pinByName == null) { throw new IllegalArgumentException("Pin with name " + k + " could not be found."); }
					LOGGER.finest("PinByName: k='" + k + "'\t context='" + v + "'\t pin.getName='" + pinByName.getName() + "'\t pin.getAddress=" + pinByName.getAddress());
					GpioPinDigitalOutput test = gpioController.provisionDigitalOutputPin(pinByName, PinState.HIGH);
					allPins.add(test);
					server.createContext(String.valueOf('/') + v, new GPIOHandler(test));
				});
			}
		});
		server.createContext("/reset", new HttpHandler()
		{

			@Override
			public void handle(HttpExchange t) throws IOException
			{
				LOGGER.finer("Reset init");
				for (GpioPinDigitalOutput pin : allPins)
				{
					pin.setState(PinState.HIGH);
					LOGGER.finest("Set pin " + pin.getName() + " to state " + pin.getState());
				}
				LOGGER.info("Reset done");
				t.sendResponseHeaders(200, -1);
			}
		});
		server.createContext("/test", new HttpHandler()
		{

			@Override
			public void handle(HttpExchange t) throws IOException
			{
				LOGGER.finer("Test init");
				for (GpioPinDigitalOutput pin : allPins)
				{
					pin.setState(PinState.LOW);
					LOGGER.finest("Set pin " + pin.getName() + " to state " + (Object) pin.getState());
				}
				LOGGER.info("Test done");
				t.sendResponseHeaders(200, -1);
			}
		});
		server.createContext("/shutdown", new HttpHandler()
		{

			@Override
			public void handle(HttpExchange t) throws IOException
			{
				t.sendResponseHeaders(200, -1);
				t.getResponseBody().write("HalloWelt".getBytes());
				System.exit(0);
			}
		});
		server.setExecutor(null);
		server.start();
	}

	static class GPIOHandler implements HttpHandler
	{
		private final GpioPinDigitalOutput pin;

		public GPIOHandler(final GpioPinDigitalOutput pin)
		{
			this.pin = pin;
		}

		@Override
		public void handle(final HttpExchange t) throws IOException
		{
			final String state = splitQuery(t.getRequestURI()).get("state").get(0);
			if (state == null)
			{
				t.sendResponseHeaders(404, -1L);
			}
			switch (state.toLowerCase())
			{
				case "toggle":
				{
					this.pin.toggle();
					break;
				}
				case "on":
				{
					this.pin.setState(PinState.LOW);
					break;
				}
				case "off":
				{
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

		public static Map<String, List<String>> splitQuery(final URI uri) throws UnsupportedEncodingException
		{
			final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
			final String[] pairs = uri.getQuery().split("&");
			for (String pair : pairs)
			{
				final int idx = pair.indexOf("=");
				final String key = (idx > 0) ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
				if (!query_pairs.containsKey(key))
				{
					query_pairs.put(key, new LinkedList<String>());
				}
				final String value = (idx > 0 && pair.length() > idx + 1) ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
				query_pairs.get(key).add(value);
			}
			return query_pairs;
		}
	}
}
