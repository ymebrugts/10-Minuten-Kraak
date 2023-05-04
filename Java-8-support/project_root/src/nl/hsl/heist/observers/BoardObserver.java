package nl.hsl.heist.observers;

import nl.hsl.heist.shared.RemoteBoard;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Observer for the board.
 *
 * @author Wesley
 */
public interface BoardObserver extends Remote{

    /**
     * @since 0.1
     * @param tile
     *            the model that is changed
     * @throws RemoteException
     *             when the connection between RMI client and server is
     *             compromised
     */
    public void modelChanged(RemoteBoard tile) throws RemoteException;
}
