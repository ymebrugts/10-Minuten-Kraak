package nl.hsl.heist.shared;

import nl.hsl.heist.models.RegisteredPlayer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemotePlayer extends Remote {

	void updateClients(List<RegisteredPlayer> registeredPlayers) throws RemoteException;
	String getName() throws RemoteException;

}