package nl.hsl.heist.shared;

import nl.hsl.heist.models.Player;
import nl.hsl.heist.observers.GamestateObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface RemoteGameState extends Remote {
    public void addObserver(GamestateObserver gamestateObserver) throws RemoteException;
    public List<Player> getPlayers() throws RemoteException;
    public boolean isGameStarted() throws RemoteException;
    public void gameHasStarted() throws RemoteException;

    public void setObservers(ArrayList<GamestateObserver> observers) throws RemoteException;
    public void setPlayers(List<Player> players) throws RemoteException;
    public ArrayList<GamestateObserver> getObservers() throws RemoteException;
    public void emptyObservers() throws RemoteException;
}
