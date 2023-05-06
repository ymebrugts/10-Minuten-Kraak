package nl.hsl.heist.controllers;

import nl.hsl.heist.models.*;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteGameState;
import nl.hsl.heist.shared.RemotePlayer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * All actions that a client can call.
 *
 * @author Wesley, Yme
 */
public class GameController implements GameAction, Serializable {

    private static final long serialVersionUID = 2277978020464604497L;

    private List<RegisteredPlayer> registeredPlayers = Collections.synchronizedList(new LinkedList<RegisteredPlayer>());
    private int maxPlayerId = 1;
    private boolean started;

    private ClickController clickController = new ClickController();
    private LoadGameController loadGameController = new LoadGameController();
    private SaveGameController saveGameController;
    private GameState gameState = new GameState();

    protected Game game;

    public GameController() throws RemoteException {
    }

    public ClickController getClickController() {
        return clickController;
    }

    public int registerClient(RemotePlayer remotePlayer, String name) throws RemoteException {
        int id = 0;
        if (registeredPlayers.size() < 6) {
            id = maxPlayerId++;
            registeredPlayers.add(new RegisteredPlayer(remotePlayer, id, name));
            System.out.println("[+] player registered: " + id);
        } else {
            System.out.println("[!] max number of players");
            throw new RemoteException("maxium players.");
        }
        updateClients(registeredPlayers);
        return id;
    }

    public List<RegisteredPlayer> getRegisteredPlayers() throws RemoteException {
        return registeredPlayers;
    }

    public void saveGame() throws RemoteException {
        saveGameController = new SaveGameController(game);
        saveGameController.saveGame();
    }

    public void loadGame(File file) throws RemoteException {
        game = loadGameController.loadGame(file);
        game.getGameState().gameHasStarted();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void handoutAmulet(String name, int id) {
        RegisteredPlayer registerdPlayer = getRegisterdPlayer(id);
        Player currentPlayer = game.getPlayer(registerdPlayer.getName());

        Player opponent = game.getPlayer(name);

        if (!game.playerTurn(currentPlayer)) {
            System.out.println("[+] its not your turn");
            return;
        }

        if (!opponent.getName().equals(currentPlayer.getName())) {
            clickController.playerClickedCard(opponent);
            game.moveTurn(currentPlayer);
        }

        return;
    }

    public void zandDerIllusie(Card card, int id) {
        RegisteredPlayer registerdPlayer = getRegisterdPlayer(id);
        Player currentPlayer = game.getPlayer(registerdPlayer.getName());

        if (!game.playerTurn(currentPlayer)) {
            System.out.println("[+] its not your turn");
            return;
        }

        clickController.setNormal();
        currentPlayer.addCard(card);
        game.moveTurn(currentPlayer);
    }

    public void registerClick(int id, int row, int col) throws RemoteException {
        RegisteredPlayer registerdPlayer = getRegisterdPlayer(id);
        Player currentPlayer = game.getPlayer(registerdPlayer.getName());

        if (!game.playerTurn(currentPlayer)) {
            System.out.println("[+] it's not your turn!");
            return;
        }

        Board theBoard = game.getBoard();
        boolean passTurn = false;
        passTurn = clickController.registerClick(currentPlayer, theBoard, row, col);
        if (passTurn)
            game.moveTurn(currentPlayer);
    }

    public Player returnPlayerNow() {
        String playerNow = clickController.getPlayerNow();
        Player theplayer = game.getPlayer(playerNow);
        return theplayer;
    }

    public void HandleMythCardHandClick(Card cardClicked, int id) {
        RegisteredPlayer registerdPlayer = getRegisterdPlayer(id);
        Player currentPlayer = game.getPlayer(registerdPlayer.getName());

        if (!game.playerTurn(currentPlayer)) {
            System.out.println("[+] it's not your turn!");
            return;
        }

        if (clickController.discardCard(currentPlayer, cardClicked)) {
            game.moveTurn(currentPlayer);
        }

        System.out.println("ClickedCard in GameController ---------->" + cardClicked);
        System.out.println("HandleMythCardHand in GameController ---------->" + cardClicked.getName());
        for (int i = 0; i < currentPlayer.getCards().size() ; i++) {
            System.out.println("HandleMythCardHand in GameController ---------->" + currentPlayer.getCards().get(i));
        }
        for (int i = 0; i < currentPlayer.getCards().size() ; i++) {
            System.out.println("HandleMythCardHand in GameController ---------->" + currentPlayer.getCards().get(i).getName());
        }
    }


    public void drawBlindCard(int id) throws RemoteException {
        RegisteredPlayer registerdPlayer = getRegisterdPlayer(id);
        game.drawBlindCard(registerdPlayer.getName());
    }

    public boolean startGame(int id) throws RemoteException {
        System.out.println("[+] trying to start a new game..");
        if (registeredPlayers.size() > 0) {
            if (game == null) {
                game = new Game(gameState);
            }
            for (RegisteredPlayer registeredPlayer : registeredPlayers) {
                game.addPlayer(registeredPlayer.getName());
            }
            game.baseTurn();
            gameState.gameHasStarted();
            return true;
        } else
            return false;
    }

    private void updateClients(List<RegisteredPlayer> registeredPlayers) {
        for (RegisteredPlayer registeredPlayer : registeredPlayers) {
            try {
                RemotePlayer rmp = registeredPlayer.getRemotePlayer();
                rmp.updateClients(registeredPlayers);
            } catch (Exception e) {
                // note system does not halt in such situations!
                System.out.println("[!] could not update player " + registeredPlayer.toString());
                e.printStackTrace();
            }
        }
    }

    public void updateName(int id, String name) {
        RegisteredPlayer player = getRegisterdPlayer(id);
        player.setName(name);
    }

    @Override
    public GameState getGameState() throws RemoteException {
        return game.getGameState();
    }

    private RegisteredPlayer getRegisterdPlayer(int id) {
        RegisteredPlayer player = null;
        for (RegisteredPlayer registeredPlayer : registeredPlayers) {
            if (registeredPlayer.getId() == id)
                player = registeredPlayer;
        }
        return player;
    }

    public boolean finish(int gottenId) throws IOException {
        RegisteredPlayer registeredPlayer = null;
        for (RegisteredPlayer rp : registeredPlayers)
            if (gottenId == rp.getId()) {
                registeredPlayer = rp;
                break;
            }
        game.finish(registeredPlayer.getName());
//        updateRemotePlayers();
        return true;
    }

    public void broadcastGameState() throws RemoteException {
        System.out.println("gamestate " + gameState);
        RemoteGameState gameStateSkeleton = (RemoteGameState) UnicastRemoteObject.exportObject(gameState, 0);
        Registry registry = LocateRegistry.getRegistry(GameConfig.getPort());
        registry.rebind("GameState", gameStateSkeleton);
        System.out.println("gamestate " + gameStateSkeleton);
    }
}

