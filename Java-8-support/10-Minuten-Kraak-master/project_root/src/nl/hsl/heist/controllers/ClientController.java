package nl.hsl.heist.controllers;

import javafx.application.Platform;
import nl.hsl.heist.models.Command;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.models.PlayerState;
import nl.hsl.heist.models.RegisteredPlayer;
import nl.hsl.heist.observers.GamestateObserver;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteGameState;
import nl.hsl.heist.shared.RemotePlayer;
import nl.hsl.heist.views.BoardView;
import nl.hsl.heist.views.FXView;
import nl.hsl.heist.views.LobbyView;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Remote function caller for client.
 *
 * @author Wesley, Jordi, Yme
 */
public class ClientController extends UnicastRemoteObject implements RemotePlayer, GamestateObserver {
	private static final long serialVersionUID = -3844812177483416720L;
	
	private FXView controller;
	private LobbyView lobbyView;
	private boolean started = false;
	private BoardView boardView;
	private int id;
	private RemoteGameState remoteGameState;

    /**
     * Get's the registry on a server IP to connect with RMI.
     * @param controller the FXView for this ClientController
     * @throws RemoteException :
     * @throws NotBoundException :
     */
    public ClientController(FXView controller) throws RemoteException, NotBoundException {
        this.controller = controller;
        try {
            Registry registry = LocateRegistry.getRegistry(GameConfig.getHostName()); // if server on another machine: provide that machine's IP address. Default port  1099
            remoteGameState = (RemoteGameState) registry.lookup("GameState"); // get remote Calculator object from registry
            remoteGameState.addObserver(this);
        } catch (Exception e) {
            controller.showNoConnectionErrorMessage();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLobbyView(LobbyView lobbyView) {
        this.lobbyView = lobbyView;
    }

    /**
     * Updates the UI as FXThread.
     * @param registeredPlayers list of registeredPlayers currently in the game
     * @throws RemoteException :
     * @see FXView#lobby
     */
    @Override
    public void updateClients(List<RegisteredPlayer> registeredPlayers) throws RemoteException {
        Platform.runLater(
                () -> {
                    try {
                        controller.lobby();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public String getName() throws RemoteException {
        return null;
    }

    /**
     * If the game has just started, it will show the game for the clients.
     * When a card is picked, show's every player who is playing the correct popup.
     * @param players List of playing players
     * @throws RemoteException :
     */
    @Override
    public void onCardPick(List<Player> players) throws RemoteException  {
        Platform.runLater( () -> {
            try {
                if (players == null) {
                    return;
                }

                if (!lobbyView.isStarted()) {
                    lobbyView.showGame();
                }

                for (Player player : players) {
                    if (player.getPlayerState() == PlayerState.PLAYING) {
                        Command commandMode = controller.getRemoteGameController().getClickController().getCommandMode();
                        System.out.println(player.getPlayerState());
                        System.out.println("[+] showing player turn");
                        switch (commandMode) {
                            case NORMAL:
                                controller.showPlayerTurn(player, "is aan de beurt!");
                                controller.boardIcon.setVisible(true);
                                break;
                            case EXTRATURN:
                                controller.showPlayerTurn(player, "heeft een\nextra beurt!");
                                break;
                            case YANKCARD:
                                controller.showPlayerTurn(player, "mag een kaart uit\nde toren pakken!");
                                break;
                            case GIVEAMULET:
                                controller.showPlayerTurn(player, "verwijder kaart\nuit de toren");
                                break;
                            case GIVEAMULET2:
                                controller.showHand();
                                controller.showPlayerTurn(player, "Selecteer de speler die\nde Amulet-van-Nae krijgt");
                                controller.boardIcon.setVisible(false);
                                break;
                            case DISCARD:
                                controller.showPlayerTurn(player, "Verwijder cursekaart\nuit hand");
                                controller.showHand();
                                controller.boardIcon.setVisible(false);
                                break;
                            case COPYCARD:
                                controller.showPlayerTurn(player, "Kopieer een niet-curse\n" +
                                        "3, 4 of 5 kaart uit je hand");
                                controller.showHand();
                                controller.boardIcon.setVisible(false);
                        }
                    }
                }
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Calls FXView to show the end screen to the player.
     * @param players List of (finished) players.
     * @throws IOException :
     * @see FXView#showEndScreen
     */
    @Override
    public void onGameEnd(List<Player> players) throws IOException {
        Platform.runLater( () -> {
            try {
                controller.showEndScreen(players);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onCardRemove(int index, Player player) throws RemoteException {
    }
}
