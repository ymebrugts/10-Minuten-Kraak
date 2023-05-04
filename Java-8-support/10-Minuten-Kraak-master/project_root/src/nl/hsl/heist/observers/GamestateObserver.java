package nl.hsl.heist.observers;

import nl.hsl.heist.models.Player;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
 * Observer for the gamestate
 *
 * @author Wesley
 */
public interface GamestateObserver extends Remote {
    public void onCardPick(List<Player> players) throws RemoteException;
    public void onGameEnd(List<Player> players) throws IOException;
    public void onCardRemove(int index, Player player) throws RemoteException;;
}
