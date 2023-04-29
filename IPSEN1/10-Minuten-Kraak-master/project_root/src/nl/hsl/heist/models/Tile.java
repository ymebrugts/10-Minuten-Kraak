package nl.hsl.heist.models;

import nl.hsl.heist.observers.TileObserver;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
/**
 * Tile that belong to the board
 *
 * @author Wesley
 */
public class Tile implements Serializable {

	private static final long serialVersionUID = -5006569303208825707L;

	private Card card = null;
	private Player player = null;
	private ArrayList<TileObserver> observers = new ArrayList<TileObserver>();

    /**
     * Add a TileObserver
     *
	 * @param observer : TileObserver
	 * @author Wesley
     */
    public void addObsever(TileObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all the Observers (Only board, in this case) that a tile has changed.
     *
     */
    private void notifyObservers() {
        for (TileObserver observer : observers) {
            try {
                observer.onChange();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tile has a card object, and nothing else.
     *
	 * @param card : Card
     * @author Wesley
     */
	public void setCard(Card card) {
		this.card = card;
		this.player = null;
        notifyObservers();
	}

    /**
     * Tile has no objects!
     *
     * @author Wesley
     */
	public void setEmpty() {
		this.card = null;
		this.player = null;
        notifyObservers();
    }


	public Card getCard() {
		return this.card;
	}

    /**
     * Tile has a Player object, and nothing else.
     *
     * @param player : Player
     * @author Wesley
     */
	public void setPlayer(Player player) {
		this.player = player;
		this.card = null;
        notifyObservers();
    }

	public void removePlayer() {
		this.player = null;
        notifyObservers();
	}

	public Player getPlayer() {
		return this.player;
	}

    /**
     * Has this a card or player object?
     *
     * @see Player
     * @see Card
     *
     * @author Wesley
     * @return object Player, Card or null
     */
	public Object getObject() {
		if (player != null)
			return player;
		else if (card != null) 
			return card;

		return null;
	}


}
