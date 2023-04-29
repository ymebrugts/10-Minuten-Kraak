package nl.hsl.heist.models;

import nl.hsl.heist.observers.LoadGameObserver;
import nl.hsl.heist.shared.RemoteLoadGame;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * LoadGame
 * @author wesley
 */
public class LoadGame implements RemoteLoadGame {
    private List<Player> playersTaken  = new ArrayList<Player>();
    private int totalPlayers;
    private ArrayList<LoadGameObserver> observers = new ArrayList<LoadGameObserver>();

    /**
     * Remove player from the list where clients can pick from.
     *
     * @see #notifyObserversToRemovePlayer(Player)
     * @param player : player that has been picked by a client
     * @author Wesley
     */
    public void addPlayersTaken(Player player) {
        System.out.println("Current players in list: " + totalPlayers);
        playersTaken.add(player);
        allPlayersTaken();
        try {
            notifyObserversToRemovePlayer(player);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * all the players the client can choose from.
     *
     * @param totalPlayers totalPlayers
     * @author Wesley
     */
    public void setOpenPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    /**
     * If all players are taken, notify the observers.
     * @see #notifyObserversToStart()
     * @author Wesley
     */
    private void allPlayersTaken() {
        System.out.println("players taken: " + playersTaken.size());
        if (totalPlayers == playersTaken.size() ) {
            // notify that all players are taken, so start the game.
            try {
                notifyObserversToStart();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notify all observers to load the game
     *
     * @throws RemoteException : when the connection between RMI client and server is compromised.
     * @throws IOException :
     * @throws NotBoundException : There are no observers.
     * @author Wesley
     */
    private void notifyObserversToStart() throws IOException, NotBoundException {
        for (LoadGameObserver loadGameObserver : observers) {
            loadGameObserver.loadGame();
        }
    }

    /**
     * add an observer to the list.
     *
     * @throws RemoteException : when the connection between RMI client and server is compromised.
     */
    @Override
    public void addObserver(LoadGameObserver loadGameObserver) throws RemoteException {
        observers.add(loadGameObserver);
    }

    /**
     * Notifies all the Observers to remove a player from the list.
     *
     * @throws RemoteException : when the connection between RMI client and server is compromised.
     * @param player : player that has the card that needs to be removed
     */
    private void notifyObserversToRemovePlayer(Player player) throws RemoteException {
        for (LoadGameObserver loadGameObserver : observers) {
            loadGameObserver.removePlayer(player.getName());
        }
    }
}
