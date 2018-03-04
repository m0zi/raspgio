package raspgio;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Properties;
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
public class RaspberryPiGPIOHomeControl {

	private final static Logger LOGGER = Logger.getLogger(RaspberryPiGPIOHomeControl.class.getName());

	private static final LinkedList<GpioPinDigitalOutput> gpioPins = new LinkedList<GpioPinDigitalOutput>();

	public static void main(String[] args) throws IOException {
		Properties states = new Properties();
		states.load(new FileInputStream(args[1]));

		// initialize controller
		GpioController gpioController = GpioFactory.getInstance();
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
		Wini wini = new Wini(Paths.get(args[0], new String[0]).toFile());
		
		wini.forEach((sectionName, section) -> {
			if (sectionName != null) {
				section.forEach((key, value) -> {
					Pin pin = RaspiPin.getPinByName((String) key);

					if (pin == null) {
						throw new IllegalArgumentException("Pin with name " + key + " could not be found.");
					}

					LOGGER.finest("PinByName: k='" + key + "'\t context='" + value + "'\t pin.getName='" + pin.getName() + "'\t pin.getAddress=" + pin.getAddress());

					GpioPinDigitalOutput gpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(pin, PinState.HIGH);
					gpioPins.add(gpioPinDigitalOutput);

					httpServer.createContext(String.valueOf('/') + value, new GPIOHandler(gpioPinDigitalOutput, states, args[1]));
				});
			}
		});

		// load states
		for (GpioPinDigitalOutput pin : gpioPins) {
			pin.setState(states.containsKey(pin.getName()) ? PinState.valueOf(states.getProperty(pin.getName())) : PinState.HIGH);
		}

		// add shutdown routine
		httpServer.createContext("/shutdown", new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				t.sendResponseHeaders(200, -1);
				System.exit(0);
			}
		});

		// start server
		httpServer.setExecutor(null);
		httpServer.start();
	}

}
