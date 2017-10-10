package raspgio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.LinkedList;
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

	private static final LinkedList<GpioPinDigitalOutput> allPins = new LinkedList<GpioPinDigitalOutput>();

	public static void main(String[] args) throws IOException {
		GpioController gpioController = GpioFactory.getInstance();		
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		Wini wini = new Wini(Paths.get(args[0], new String[0]).toFile());
		
		wini.forEach((sectionName, section) -> {
			if (sectionName != null) {
				section.forEach((key, value) -> {
					Pin pinByName = RaspiPin.getPinByName((String) key);
					
					if (pinByName == null) {
						throw new IllegalArgumentException("Pin with name " + key + " could not be found.");
					}
					
					LOGGER.finest("PinByName: k='" + key + "'\t context='" + value + "'\t pin.getName='" + pinByName.getName()
							+ "'\t pin.getAddress=" + pinByName.getAddress());
					
					GpioPinDigitalOutput gpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(pinByName, PinState.HIGH);
					allPins.add(gpioPinDigitalOutput);
					
					server.createContext(String.valueOf('/') + value, new GPIOHandler(gpioPinDigitalOutput));
				});
			}
		});
		
		server.createContext("/reset", new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				LOGGER.finer("Reset init");
				
				for (GpioPinDigitalOutput pin : allPins) {
					pin.setState(PinState.HIGH);
					LOGGER.finest("Set pin " + pin.getName() + " to state " + pin.getState());
				}
				
				LOGGER.info("Reset done");				
				t.sendResponseHeaders(200, -1);
			}
		});
		
		server.createContext("/test", new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				LOGGER.finer("Test init");
				
				for (GpioPinDigitalOutput pin : allPins) {
					pin.setState(PinState.LOW);
					LOGGER.finest("Set pin " + pin.getName() + " to state " + (Object) pin.getState());
				}
				
				LOGGER.info("Test done");
				t.sendResponseHeaders(200, -1);
			}
		});
		
		server.createContext("/shutdown", new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {				
				t.sendResponseHeaders(200, -1);				
				System.exit(0);
			}
		});
		
		server.setExecutor(null);
		server.start();
	}

}
