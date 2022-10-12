package server;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.rmi.*;
import java.util.*;
import java.util.Map.Entry;

import client.IChatClientCallback;

import javax.rmi.ssl.SslRMIClientSocketFactory;

public class ChatServerImpl extends UnicastRemoteObject
        implements IChatServer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final int registryPort = 1099;

    // Hier speichern wir die Callbacks!
    private Map<String, IChatClientCallback> users;


    /**
     * Constructor for the ChatServerImpl object
     *
     * @throws RemoteException Description of Exception
     */
    public ChatServerImpl() throws RemoteException {
        super();
        HashMap<String, IChatClientCallback> callbackHashMap = new HashMap<String, IChatClientCallback>();
        users = Collections.synchronizedMap(callbackHashMap);
    }


    /**
     * registriert den Benutzer und das Callback-Objekt (ChatReceiver) der Server
     * aktualisiert die Benutzerliste über Callback-Aufruf
     *
     * @param userID   Description of Parameter
     * @param receiver Description of Parameter
     * @return Description of the Returned Value
     * @throws RemoteException Description of Exception
     */
    public boolean login(String userID, IChatClientCallback receiver) throws RemoteException {
        if (users.containsKey(userID)) {
            System.out.println("User existiert bereits!");
            return false;
        } else {
            users.put(userID, receiver);
            Object[] userArray = users.keySet().toArray();
            for (String key : users.keySet()) {
                users.get(key).receiveUserLogin(userID, userArray);
            }
            return true;
        }
    }


    /**
     * Description of the Method
     *
     * @param userID Description of Parameter
     * @return Description of the Returned Value
     * @throws RemoteException Description of Exception
     */
    public boolean logout(String userID) throws RemoteException {
        if (users.containsKey(userID)) {
            users.remove(userID);
            Object[] userArray = users.keySet().toArray();
            for (String key : users.keySet()) {
                users.get(key).receiveUserLogout(userID, userArray);
            }
            return true;
        } else {
            System.out.println("User nicht vorhanden.");
            return false;
        }
    }


    /**
     * Description of the Method
     *
     * @param userID  Description of Parameter
     * @param message Description of Parameter
     * @throws RemoteException Description of Exception
     */
    public void chat(String userID, String message) throws RemoteException {
        for (String key : users.keySet()) {
            users.get(key).receiveChat(userID, message);
        }
    }


    /**
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        try {
//            System.out.println("Server startet...");
//
//            LocateRegistry.createRegistry(1099);
//            Naming.bind("rmi://localhost/ChatServer", new ChatServerImpl());
//
//            System.out.println("Server ist gestartet...");

            String path = "resources/";

            System.setProperty("javax.net.ssl.keyStore", path + "server-ks.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "the!server");

            // Trust
            System.setProperty("javax.net.ssl.trustStore", path + "truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "the!truststore");

            // NO TRUST = > SSL Handshake Error
             System.setProperty("javax.net.ssl.trustStore", path + "noClientTruststore.jks");
             System.setProperty("javax.net.ssl.trustStorePassword", "the!truststore");

            Registry registry = LocateRegistry.getRegistry(null, 1099, new SslRMIClientSocketFactory());

            registry.bind("ChatServer", new ChatServerImpl());
            System.out.println("ChatServer bound in registry");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}
