package nl.hsl.heist.observers;

import nl.hsl.heist.models.Card;
import nl.hsl.heist.models.Player;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Observer for the player.
 *
 * @author Wesley
 */
public interface PlayerObserver extends Remote {
    public void onChange() throws RemoteException;
    public void onCardRemove(int index, Player player) throws RemoteException;
}
