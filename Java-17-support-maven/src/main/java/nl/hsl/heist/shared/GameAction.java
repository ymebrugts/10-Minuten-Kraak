package nl.hsl.heist.shared;


import nl.hsl.heist.controllers.ClickController;
import nl.hsl.heist.models.Card;
import nl.hsl.heist.models.GameState;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.models.RegisteredPlayer;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
 *  GameAction, get calls from the client and forwards it to the gamecontroller.
 *  @author wesley, Yme
 */
public interface GameAction extends Remote {

	// The RemoteException is an exception that can occur when a failure occurs in the RMI process.
	public int registerClient(RemotePlayer remotePlayer, String name) throws RemoteException;
	public List<RegisteredPlayer> getRegisteredPlayers() throws RemoteException;
	public void saveGame() throws RemoteException;
	public void loadGame(File file) throws RemoteException;
	public void registerClick(int id, int col, int row) throws RemoteException;
	public void drawBlindCard(int id) throws RemoteException;
    public boolean isStarted() throws RemoteException;
    public void setStarted(boolean started) throws RemoteException;
	public boolean startGame(int id) throws RemoteException;
	public boolean finish(int id) throws IOException;
    public void  updateName(int id, String name) throws RemoteException;
    public GameState getGameState() throws RemoteException;
    public ClickController getClickController() throws RemoteException;
    public void handoutAmulet(String name, int id) throws RemoteException;
    public void HandleMythCardHandClick(Card cardClicked, int id) throws RemoteException;
    public Player returnPlayerNow() throws RemoteException;
	public void zandDerIllusie(Card card, int id) throws RemoteException;
}
