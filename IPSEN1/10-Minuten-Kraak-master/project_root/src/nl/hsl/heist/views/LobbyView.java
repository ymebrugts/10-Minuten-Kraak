package nl.hsl.heist.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import nl.hsl.heist.controllers.ClientController;
import nl.hsl.heist.models.GameState;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.models.RegisteredPlayer;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import static nl.hsl.heist.views.FXApplication.fxView;

/**
 * Class which handles the lobby.
 *
 * @author Wesley, Yme
 */
public class LobbyView {

    @FXML
    private javafx.scene.layout.VBox lobbyContainer;
    @FXML
    private javafx.scene.image.ImageView loading;
    @FXML private javafx.scene.layout.Pane loadPane;
    @FXML private javafx.scene.layout.BorderPane borderPane;

    private GameAction remoteGameController;
    private int id;
    private List<RegisteredPlayer> players;
    private GameState gameState;
    private boolean started;
    private boolean loaded;
    private BoardView boardView;
    final FileChooser fileChooser = new FileChooser();

    private ClientController clientController;

    public LobbyView() throws IOException {
    }

    public void setRemoteGameController(GameAction remoteGameController) {
        this.remoteGameController = remoteGameController;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Creates the lobby, adds all players to the lobby.
     * @throws RemoteException : cannot connect to RMI
     * @throws UnknownHostException : might not find the ip adress of the server
     */
    public void createLobby() throws RemoteException, UnknownHostException {
        players = remoteGameController.getRegisteredPlayers();
        Label ipAddress = new Label(GameConfig.getHostName());
        ipAddress.setAlignment(Pos.CENTER);
        ipAddress.setStyle("-fx-font-family: Felt; -fx-fill: #dfebf9; -fx-font-size: 32px");
        ipAddress.setTextFill(Color.valueOf("#dfebf9"));
        ipAddress.setOnMouseClicked( e -> {
            StringSelection selection = new StringSelection(ipAddress.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });
        lobbyContainer.getChildren().add(ipAddress);
        for (RegisteredPlayer somePlayer : players) {
            somePlayer.getName();
            Label playerName = new Label(somePlayer.getName());
            playerName.setAlignment(Pos.CENTER);
            playerName.setStyle("-fx-font-family: Felt; -fx-fill: #dfebf9; -fx-font-size: 18px");
            playerName.setTextFill(Color.valueOf("#dfebf9"));
            lobbyContainer.getChildren().addAll(playerName);
        }
    }

    /**
     * Opens the file browser menu and lets the player load a game.
     *
     * @throws IOException :
     * @throws NotBoundException :
     * @see nl.hsl.heist.controllers.GameController#loadGame
     */
    public void loadGame() throws IOException, NotBoundException {
        File file = fileChooser.showOpenDialog(borderPane.getScene().getWindow());
        if (file != null) {
            remoteGameController.loadGame(file);
            loaded = true;
        }
    }

    /**
     * Loads the board, starts the game.
     * @throws IOException :
     * @throws NotBoundException :
     */
    @FXML public void startGame() throws IOException, NotBoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/board.fxml"));
        Parent root = fxmlLoader.load();
        boardView = fxmlLoader.<BoardView>getController();
        boardView.setId(id);
        boardView.setRemoteGameController(remoteGameController);
        boardView.startGame();
        boardView.setObservable();
        borderPane.setLeft(null);
        borderPane.setCenter(root);
        started = true;
        fxView.playerIcon.setVisible(true);
        fxView.cardsIcon.setVisible(true);
        fxView.boardIcon.setVisible(true);
        fxView.boardIcon.setStyle("-fx-opacity:1");
    }

    /**
     * Shows the game to the client.
     * @throws IOException :
     * @throws NotBoundException :
     */
    public void showGame() throws IOException, NotBoundException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/loadGame.fxml"));
        Parent root = fxmlLoader.load();
        LoadGameView loadGameView = fxmlLoader.<LoadGameView>getController();
        loadGameView.setId(id);
        loadGameView.setObservable();
        loadGameView.setRemoteGameController(remoteGameController);
        borderPane.setLeft(null);
        borderPane.setCenter(root);
        loaded = true;
        fxView.playerIcon.setVisible(true);
        fxView.cardsIcon.setVisible(true);
        fxView.boardIcon.setVisible(true);
        fxView.boardIcon.setStyle("-fx-opacity:1");
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
        clientController.setLobbyView(this);
    }

}
