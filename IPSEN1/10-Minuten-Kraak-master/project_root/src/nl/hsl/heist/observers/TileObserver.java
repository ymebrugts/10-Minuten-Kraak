package nl.hsl.heist.observers;

import java.rmi.RemoteException;
/**
 * When a tile change, implement these functions.
 *
 * @author Wesley
 */
public interface TileObserver {
    public void onChange() throws RemoteException;
}
