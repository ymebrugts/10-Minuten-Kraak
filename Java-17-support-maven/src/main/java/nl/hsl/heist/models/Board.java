package nl.hsl.heist.models;

import nl.hsl.heist.observers.BoardObserver;
import nl.hsl.heist.observers.TileObserver;
import nl.hsl.heist.shared.RemoteBoard;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
/**
 * Board with tiles for the game.
 * @author Wesley
 */
public class Board implements Serializable, RemoteBoard, TileObserver {

	private static final long serialVersionUID = -5518149266753155080L;
	/**
	 * Matrix of tiles.
	 */
	protected Tile[][] matrix ;
	private ArrayList<BoardObserver> observers = new ArrayList<BoardObserver>();

	/**
	 * Get tile by row and col.
	 */
	public Tile getTile(int row, int col) {
		return matrix[row][col];		
	}

	public void emptyObservers() {
	    observers.removeAll(observers);
    }

    /**
     * Add a boardObserver and notify them
     * @see #notifyObservers()
     *
     */
	@Override
	public void addObserver(BoardObserver boardObserver) throws RemoteException {
	    synchronized (observers) {
            observers.add(boardObserver);
        }
        try {
            notifyObservers();
        } catch (RemoteException e) {
            observers.remove(boardObserver);
            e.printStackTrace();
        }
	}

	/**
	 * Notifies all the Observers that the model is changed.
	 *
	 * @throws RemoteException
	 * when the connection between RMI client and server is
	 * compromised
	 */
	public void notifyObservers() throws RemoteException {
	    synchronized (observers) {
            for (BoardObserver boardObserver : observers) {
                boardObserver.modelChanged(this);
            }
        }
	}

    /**
     * Notifies this board, that tiles have been changed, so call the boardObservers to update.
     */
    @Override
    public void onChange() throws RemoteException {
        notifyObservers();
    }
}
