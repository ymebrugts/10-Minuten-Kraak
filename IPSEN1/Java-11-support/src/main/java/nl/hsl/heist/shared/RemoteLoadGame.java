package nl.hsl.heist.shared;

import nl.hsl.heist.models.Player;
import nl.hsl.heist.observers.LoadGameObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *  RemoteLoadGame
 * @author wesley
 */
public interface RemoteLoadGame extends Remote {

    public void addObserver(LoadGameObserver loadGameObserver) throws RemoteException;
    public void addPlayersTaken(Player player)throws RemoteException;

}
