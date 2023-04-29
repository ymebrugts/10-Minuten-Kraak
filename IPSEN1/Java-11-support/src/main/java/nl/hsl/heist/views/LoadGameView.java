package nl.hsl.heist.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.observers.LoadGameObserver;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteLoadGame;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/**
 * Controller for loading a game from a file.
 *
 * @author Wesley
 */
public class LoadGameView extends UnicastRemoteObject implements LoadGameObserver {

    final static long serialVersionUID = 23490835049L;

    private GameAction remoteGameController;
    @FXML
    private javafx.scene.layout.BorderPane borderPane;
    @FXML
    private javafx.scene.layout.VBox lobbyContainer;
    private BoardView boardView;
    private RemoteLoadGame remoteLoadGameController;

    private int id;

    public LoadGameView() throws RemoteException {
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Observe if loadGame is broadcasted else showGame.
     * @see #showGame()
     * @throws RemoteException :
     */
    public  void setObservable() throws IOException {
        Registry registry = LocateRegistry.getRegistry(GameConfig.getHostName()); // if server on another machine: provide that machine's IP address. Default port  1099
        try {
            remoteLoadGameController = (RemoteLoadGame) registry.lookup("LoadGame"); // get remote Calculator object from registry
            remoteLoadGameController.addObserver(this);
            System.out.println("Load game exist");
        } catch (NotBoundException e) {
            showGame();
        }
    }

    /**
     * Removes the player from the lobbycontainer.
     * @param playerName : String playerName
     * @throws RemoteException :
     */
    @Override
    public void removePlayer(String playerName) throws RemoteException {
        for (Node playerNode : lobbyContainer.getChildren()) {
            Label playerLabel = (Label) playerNode;
            if (playerLabel.getText().equals(playerName)) {
                Platform.runLater( () -> {
                    lobbyContainer.getChildren().remove(playerLabel);
                });
                break;
            }
        }
    }

    /**
     * Starts loading the game.
     * @throws IOException :
     * @throws NotBoundException :
     */
    @Override
    public void loadGame() throws IOException, NotBoundException {
       showGame();
    }

    public void showGame() {
        Platform.runLater( () -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("board.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            boardView = fxmlLoader.<BoardView>getController();
            boardView.setId(id);
            boardView.setRemoteGameController(remoteGameController);
            try {
                boardView.setObservable();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
            borderPane.setLeft(null);
            borderPane.setCenter(root);
        });
    }

    public void setRemoteGameController(GameAction remoteGameController) throws RemoteException {
        this.remoteGameController = remoteGameController;
        try {
            showPlayers();
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Shows available players.
     * @throws RemoteException :
     */
    private void showPlayers() throws RemoteException {
        System.out.println("Remotegamestate players " + remoteGameController.getGameState());
        for (Player player : remoteGameController.getGameState().getPlayers()) {
            Label playerName = new Label(player.getName());
            playerName.setOnMouseClicked( e -> {
                try {
                    remoteGameController.updateName(id, player.getName());
                    remoteLoadGameController.addPlayersTaken(player);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            });
            playerName.setAlignment(Pos.CENTER);
            playerName.setStyle("-fx-font-family: Felt; -fx-fill: #dfebf9; -fx-font-size: 18px");
            playerName.setTextFill(Color.valueOf("#dfebf9"));
            lobbyContainer.getChildren().addAll(playerName);
        }
    }
}
