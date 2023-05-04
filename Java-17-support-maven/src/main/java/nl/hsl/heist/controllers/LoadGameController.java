package nl.hsl.heist.controllers;

import nl.hsl.heist.models.Board;
import nl.hsl.heist.models.Game;
import nl.hsl.heist.models.GameState;
import nl.hsl.heist.models.LoadGame;
import nl.hsl.heist.observers.GamestateObserver;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteBoard;
import nl.hsl.heist.shared.RemoteGameState;
import nl.hsl.heist.shared.RemoteLoadGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *  LoadGameView
 * @author wesley
 */
public class LoadGameController {

    private static Game game;

    /**
     * Read file, and get game object from it.
     * After getting the object back, broadcast the old board, gameState.
     * Broadcast as well that the game has been loaded from a save file.
     *
     * @see #broadCastBoard()
     * @see #broadCastGameState()
     * @see #broadCastLoad()
     *
     * @param file : Save File.
     * @return game
     * @author wesley
     */
    public Game loadGame(File file)  {
        try {
            // Open file to read from, named SavedObj.sav.
            FileInputStream saveFile = new FileInputStream(file);

            // Create an ObjectInputStream to get objects from save file.
            ObjectInputStream save = new ObjectInputStream(saveFile);

            game = (Game) save.readObject();

            // Close the file.
            save.close(); // This also closes saveFile.

            // broadcast the board again, without any old observers
            broadCastBoard();
            broadCastGameState();
            broadCastLoad();
        }
        catch(Exception exc) {
            exc.printStackTrace(); // If there was an error, print the info.
        }
        return game;
    }

    private void broadCastBoard() throws RemoteException {
        Board board = game.getBoard();
        board.emptyObservers();
        RemoteBoard boardSkeleton = (RemoteBoard) UnicastRemoteObject.exportObject(board, 0);
        Registry registry = LocateRegistry.getRegistry(GameConfig.getPort());
        registry.rebind("Board", boardSkeleton);
    }

    private void broadCastLoad() throws RemoteException {
        LoadGame loadGame = new LoadGame();
        loadGame.setOpenPlayers(game.getGameState().getPlayers().size());
        RemoteLoadGame loadSkeleton = (RemoteLoadGame) UnicastRemoteObject.exportObject(loadGame, 0);
        Registry registry = LocateRegistry.getRegistry(GameConfig.getPort());
        registry.rebind("LoadGame", loadSkeleton);
        System.out.println("Load game has been broadcasted");
    }

    private void broadCastGameState() throws RemoteException, NotBoundException {
        GameState gameState = game.getGameState();
        System.out.println("Players " + gameState.getPlayers() );
        Registry registry = LocateRegistry.getRegistry(GameConfig.getPort());
        RemoteGameState remoteGameState = (RemoteGameState) registry.lookup("GameState");
        ArrayList<GamestateObserver> observers = remoteGameState.getObservers();
        remoteGameState.emptyObservers();
        remoteGameState = gameState;
        remoteGameState.setPlayers(gameState.getPlayers());
        System.out.println(remoteGameState.getPlayers());
        remoteGameState.setObservers(observers);
    }
}
