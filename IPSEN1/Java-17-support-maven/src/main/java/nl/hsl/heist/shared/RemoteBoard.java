package nl.hsl.heist.shared;

import nl.hsl.heist.models.Tile;
import nl.hsl.heist.observers.BoardObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface for the board.
 *
 * @author Wesley
 */
public interface RemoteBoard extends Remote {

    public void addObserver(BoardObserver boardObserver) throws RemoteException;
    public Tile getTile(int row, int col) throws RemoteException;

}
