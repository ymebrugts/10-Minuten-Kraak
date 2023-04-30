package nl.hsl.heist.observers;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Observer for loading game.
 *
 * @author Wesley
 */
public interface LoadGameObserver extends Remote {
    public void removePlayer(String playerName) throws RemoteException;
    public void loadGame() throws IOException, NotBoundException;
}
