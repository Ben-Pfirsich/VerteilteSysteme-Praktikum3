package server;

import java.rmi.*;
import client.IChatClientCallback;



public interface IChatServer extends Remote {

	
	 // Ein neuer Benutzerr betritt das System. Zusammen mit dem Benutzer erhalten wir auch
	 // die Callback-Information
  public boolean login (String userID, IChatClientCallback receiver) throws RemoteException;
  
     // Benutzer verlässt das System
  public boolean logout (String userID) throws RemoteException;
  
     // Benutzer verschickt eine Nachricht
  public void chat (String userID, String message) throws RemoteException;
}
