package loggerfuwuduan;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class LogSocketNode implements Runnable {

	Socket socket;
	ObjectInputStream ois;
	private static final Logger logger=Logger.getLogger(LogSocketNode.class);
	private static final Logger getPushLogger(String loggerName){
		return Logger.getLogger(loggerName);
	}

	public LogSocketNode(Socket socket) {
		this.socket = socket;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (Exception e) {
			logger.error("Could not open ObjectInputStream to " + socket, e);
		}
	}

	public void run() {
		LoggingEvent event;
		try {
			if (ois != null) {
				while (true) {
					// read an event from the wire
					event = (LoggingEvent) ois.readObject();
					getPushLogger(event.getLoggerName()).info(event.getMessage());;
				}

			}

		} catch (java.io.EOFException e) {
			logger.error("Caught java.io.EOFException closing conneciton.");
		} catch (java.net.SocketException e) {
			logger.error("Caught java.net.SocketException closing conneciton.");
		} catch (IOException e) {
			logger.error("Caught java.io.IOException: " + e);
			logger.error("Closing connection.");
		} catch (Exception e) {
			logger.error("Unexpected exception. Closing conneciton.", e);
		} finally {
			if (ois != null) {
				try {
					logger.debug("begin close ois");
					ois.close();
					logger.debug("end close ois");
				} catch (Exception e) {
					logger.error("Could not close connection.", e);
				}
			}
			if (socket != null) {
				try {
					logger.debug("begin close socket");
					socket.close();
					logger.debug("end close socket");
				} catch (IOException ex) {
					logger.error("socket close error ", ex);
				}
			}
		}
	}
}