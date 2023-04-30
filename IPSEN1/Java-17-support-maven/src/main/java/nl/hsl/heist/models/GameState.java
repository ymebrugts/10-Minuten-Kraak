package nl.hsl.heist.models;

import nl.hsl.heist.observers.GamestateObserver;
import nl.hsl.heist.observers.PlayerObserver;
import nl.hsl.heist.shared.RemoteGameState;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable, RemoteGameState, PlayerObserver {

    private static final long serialVersionUID = -8456531625106330836L;

    private List<Player> players;
    private List<Player> finished;
    private ArrayList<GamestateObserver> observers = new ArrayList<GamestateObserver>();
    private boolean gameStarted = false;
    private boolean gameEnded = false;

    public List<Player> getFinished() {
        return finished;
    }

    public void gameHasEnded() throws IOException {
        gameEnded = true;
        notifyObserversOnGameEnd();
    }

    public boolean isGameStarted()  {
        return gameStarted;
    }

    public void gameHasStarted() throws RemoteException {
        this.gameStarted = true;
        notifyObservers();
    }

    public ArrayList<GamestateObserver> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<GamestateObserver> observers) {
        this.observers = observers;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void emptyObservers() {
        observers.clear();
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayers(Player player) {
        if (players == null) {
            players = new ArrayList<Player>();
        }
        players.add(player);
    }

    public void finish(Player player) throws IOException {
        if (finished == null) {
            finished = new ArrayList<Player>();
        }
        finished.add(player);
        players.remove(player);
    }

    /**
     * Add a boardObserver and notify them
     * @see #notifyObservers()
     *
     * @throws RemoteException
     * when the connection between RMI client and server is
     * compromised
     */
    @Override
    public synchronized void addObserver(GamestateObserver gamestateObserver) throws RemoteException {
        observers.add(gamestateObserver);
    }

    /**
     * Notifies all the Observers that the model is changed.
     *
     * @throws RemoteException
     *             when the connection between RMI client and server is
     *             compromised
     */
    public synchronized void notifyObservers() throws RemoteException {
        for (GamestateObserver gamestateObserver : observers) {
            gamestateObserver.onCardPick(players);
        }
    }

    /**
     * Notifies all the Observers that the model is changed.
     *
     * @throws RemoteException
     *             when the connection between RMI client and server is
     *             compromised
     */
    public synchronized void notifyObserversOnGameEnd() throws IOException {
        for (GamestateObserver gamestateObserver : observers) {
            gamestateObserver.onGameEnd(finished);
        }
    }

    public synchronized void notifyObserversCardRemoved(int index, Player player) throws RemoteException {
        for (GamestateObserver gamestateObserver : observers) {
            gamestateObserver.onCardRemove(index, player);
        }
    }

    @Override
    public void onChange() throws RemoteException {
        notifyObservers();
    }

    @Override
    public void onCardRemove(int index, Player player) throws RemoteException {
        notifyObserversCardRemoved(index, player);
    }
}
