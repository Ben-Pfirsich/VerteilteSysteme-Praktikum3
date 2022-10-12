package client;
import java.rmi.*;


public interface IChatClientCallback extends Remote {
	// Ein Benutzer hat eine Nachricht an den Server geschickt
  public void receiveChat (String userID, String message) throws RemoteException;
   //  Ein neuer Benutzer hat sich beim Server angemeldet
  public void receiveUserLogin (String userID, Object [] users) throws RemoteException;
  //   Ein Benutzer hat sich beim Server abgemeldet
  public void receiveUserLogout (String userID, Object [] users) throws RemoteException;
}