package loggerfuwuduan;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class LogSocketServer implements Runnable {
	private static final Logger logger = Logger.getLogger(LogSocketServer.class);

	public void run() {
		DOMConfigurator.configure(LogSocketServer.class.getResource("log4j.xml"));
		Logger remoteLogger = LogManager.getLogger("testRemote2");
		remoteLogger.debug("begin call listen log info .....");
		ServerSocket serverSocket;
		Socket socket = null;
		try {
			// 如果没有配置端口 则抛出异常
			serverSocket = new ServerSocket(4445);
			while (true) {
//				remoteLogger.info("Waiting to accept a new client.");
				socket = serverSocket.accept();
				InetAddress inetAddress = socket.getInetAddress();
				remoteLogger.debug("Connected to client at " + inetAddress);
				remoteLogger.debug("Starting new socket node.");
				new Thread(new LogSocketNode(socket)).start();
			}
		} catch (Exception e) {
			remoteLogger.error("error in liston info  ", e);
		}
	}

	public static void main(String[] args) {
		LogSocketServer LogSocketServer = new LogSocketServer();
		LogSocketServer.run();
	}
}
