package raspgio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

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

	public static void main(String[] args) throws IOException
	{
		final GpioController controller = GpioFactory.getInstance();
		final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

		Wini wini = new Wini(Paths.get(args[0]).toFile());
		wini.forEach((sectionName, section) ->
		{
			if (sectionName != null)
			{
				Pin pinByName = RaspiPin.getPinByName(sectionName);
				if (pinByName == null)
					throw new IllegalArgumentException("Pin with name " + sectionName + " could not be found.");
				GpioPinDigitalOutput test = controller.provisionDigitalOutputPin(pinByName, PinState.HIGH);
				section.forEach((k, v) ->
				{
					if (k != null)
						server.createContext('/' + k, new GPIOHandler(test, PinState.valueOf(v)));
				});
			}
		});

		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class GPIOHandler implements HttpHandler
	{
		private final GpioPinDigitalOutput	pin;
		private final PinState				state;

		public GPIOHandler(final GpioPinDigitalOutput pin, final PinState state)
		{
			this.pin = pin;
			this.state = state;
		}

		@Override
		public void handle(final HttpExchange t) throws IOException
		{
			pin.setState(state);
			t.sendResponseHeaders(200, -1);
		}
	}
}
