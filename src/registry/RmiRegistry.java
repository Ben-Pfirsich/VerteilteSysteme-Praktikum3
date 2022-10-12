package registry;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.registry.LocateRegistry;

public class RmiRegistry {

	public static void main(String[] args) throws Exception {
		String path = "resources/";
		System.setProperty("javax.net.ssl.keyStore", path + "server-ks.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "the!server");

		System.setProperty("javax.net.ssl.trustStore", path + "truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "the!truststore");

		// Start RMI registry on port 1099
		LocateRegistry.createRegistry(1099, new SslRMIClientSocketFactory(),
				new SslRMIServerSocketFactory(null, null, true));
		System.out.println("RMI registry running on port 1099");
		// Sleep forever
		Thread.sleep(Long.MAX_VALUE);
	}
}