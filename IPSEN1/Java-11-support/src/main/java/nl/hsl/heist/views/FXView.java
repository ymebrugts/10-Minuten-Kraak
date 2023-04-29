package nl.hsl.heist.views;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import nl.hsl.heist.controllers.ClientController;
import nl.hsl.heist.controllers.ManualController;
import nl.hsl.heist.controllers.TurnPopupController;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.models.RegisteredPlayer;
import nl.hsl.heist.server.GameServer;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;

/**
 * FX class for a client.
 *
 * @author Wesley, Yme, Samir, Joorden
 */
public class FXView {

    private GameAction remoteGameController;

    private int id;
    @FXML public javafx.scene.layout.BorderPane top;
    @FXML public javafx.scene.layout.StackPane views;
    @FXML public javafx.scene.layout.HBox hboxJoinGame;
    @FXML public javafx.scene.control.Button joingameButton;
    @FXML private javafx.scene.layout.StackPane container;
    @FXML private javafx.scene.control.TextField nameField;
    @FXML public javafx.scene.image.ImageView cardsIcon;
    @FXML public javafx.scene.image.ImageView manualIcon;
    @FXML public javafx.scene.image.ImageView boardIcon;
    @FXML public javafx.scene.image.ImageView playerIcon;
    @FXML public javafx.scene.control.TextField HostIp;

    private ClientController clientController;
    private LobbyView lobbycontroller;
    private HandView handcontroller;
    private StackPane stackPaneHand;
    private Parent hand;
    private ManualController manual;
    private int indexSameName;
    private int indexManual;


    public enum ViewMode {
        HANDMODE, BOARDMODE, ENDGAME, MANUAL
    }

    private ViewMode viewmode = ViewMode.BOARDMODE;

    //Check voor first draw van de handview
    private int DraworUpdate = 0;


    private void updatePlayerIcon(Player player) throws RemoteException {
        playerIcon.setImage(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/" + player.getAvatar().getMiniImgPath())));
    }

    public GameAction getRemoteGameController() {
        return this.remoteGameController;
    }

    @FXML
    public void hostGame() throws UnknownHostException {
        GameServer.main(null);
        GameConfig.setHostName(InetAddress.getLocalHost().getHostAddress());
        try {
            joinGame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void joinGame() throws RemoteException {
        try {
            setHostIp();
            clientController = new ClientController(this);
            try {
                // Get remote GamePlay object (the stub) from registry
                Registry registry = LocateRegistry.getRegistry(GameConfig.getHostName(), GameConfig.getPort());
                remoteGameController = (GameAction) registry.lookup(GameConfig.getGameName());
                this.id = remoteGameController.registerClient(clientController, playerName());
                lobby();
            } catch (NotBoundException | RemoteException e) {
                showNoConnectionErrorMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNoConnectionErrorMessage() {
        StackPane errorPopup = new StackPane();
        errorPopup.setAlignment(Pos.CENTER);
        errorPopup.setMaxSize(500, 300);
        errorPopup.setStyle("-fx-background-color: dodgerblue");
        Label errorMessage = new Label("Failed to connect to the host: " + GameConfig.getHostName() + "!");
        errorMessage.setFont(new Font("Felt", 21));
        errorMessage.setTextFill(Paint.valueOf("#fff"));
        errorPopup.getChildren().addAll(errorMessage);
        container.getChildren().add(errorPopup);
        doFadeOut(errorPopup);
    }

    /**
     * Created the lobby, also loads the LobbyMenu fxml file and show's it to the client.
     * @throws IOException :
     */
    public void lobby() throws IOException {
        top.setLeft(null);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("lobbyMenu.fxml"));
        Parent root = fxmlLoader.load();
        LobbyView lobbyView = fxmlLoader.getController();
        lobbyView.setId(id);
        lobbyView.setRemoteGameController(remoteGameController);
        lobbyView.setClientController(clientController);
        lobbyView.createLobby();
        views.getChildren().add(root);
        views.getChildren().remove(nameField);
    }


    private void setHostIp() throws UnknownHostException {
        if (!HostIp.getText().trim().isEmpty()) {
            GameConfig.setHostName(HostIp.getText());
        } else {
            GameConfig.setHostName(InetAddress.getLocalHost().getHostAddress());
        }
    }

    /**
     * get playername from textfield.
     * If empty fill in name with player;
     * @throws RemoteException :  RMI connection compromised
     * @return name : String Containing player name.
     * @author Samir, Wesley
     */
    private String playerName() throws RemoteException {
        String name = "Player";
        if (!nameField.getText().trim().isEmpty()) {
            name = nameField.getText().trim();
        }
        System.out.println(name);
        if (sameName(name)) {
            name = name + " " + String.format("%04d", new Random().nextInt(10000));
        }
        return name;
    }

    /**
     * Registered player may not have the same name as another
     *
     * @see RegisteredPlayer
     * @param name : Registered player name
     * @return boolean : checks if the name is the same from another player
     * @throws RemoteException : RMI connection compromised
     * @author Samir
     */
    private boolean sameName(String name) throws RemoteException {
        boolean sameName = false;
        System.out.println(remoteGameController.getRegisteredPlayers());
        try {
            for (RegisteredPlayer player: remoteGameController.getRegisteredPlayers()) {
                if ( player.getName().equals(name)) {
                    sameName = true;
                    break;
                }
            }
        } catch (Exception e) {

        }
        return sameName;
    }


    /**
     * Shows the popup when the turn switches from player to player.
     * @param player Get's the avatar and name to show during popup.
     * @param textBox Text to show in the popup itself.
     * @throws IOException :
     */
    public void showPlayerTurn(Player player, String textBox) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "turnPopup.fxml"));
        Parent root = fxmlLoader.load();
        TurnPopupController TurnPopupController = fxmlLoader.getController();
        TurnPopupController.setPlayerName(player.getName());
        TurnPopupController.setTextBox(textBox);
        TurnPopupController.setPlayerAvatar(player.getAvatar().getImgPath());
        container.getChildren().add(root);
        root.prefWidth(1000);
        root.prefHeight(400);
        container.setAlignment(Pos.CENTER);
        root.toFront();
        doFadeOut(root);
        updatePlayerIcon(player);
    }

    /**
     * @param root JavaFX node to fade out
     * Fades out the node.
     */
    private void doFadeOut(Node root) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDelay(Duration.seconds(2));
        fadeTransition.setDuration(Duration.seconds(0.3));
        fadeTransition.setNode(root);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        fadeTransition.setOnFinished( e -> container.getChildren().remove(root));
    }

    /**
     * Updates and shows the hand view for the client
     * @throws IOException :
     * @throws NotBoundException :
     */
    @FXML public void showHand() throws IOException, NotBoundException {
        if (DraworUpdate == 0) {
            FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("Hand.fxml"));
            hand = fxmlLoader2.load();
            handcontroller = fxmlLoader2.getController();
            handcontroller.setObservable();
            handcontroller.setGameaction(remoteGameController);
            handcontroller.getFXController(this);
            handcontroller.setId(id);
            handcontroller.setPlayers(handcontroller.getPlayers());
            stackPaneHand = new StackPane(hand);
            stackPaneHand.setAlignment(Pos.CENTER);
            views.getChildren().add(stackPaneHand);
            stackPaneHand.toFront();
            DraworUpdate = 1;
            cardsIcon.setStyle("-fx-opacity: 1;");
            manualIcon.setStyle("-fx-opacity: 0.6;");
            boardIcon.setStyle("-fx-opacity: 0.6");
        } else if (DraworUpdate == 1) {
            System.out.println("[+] update HANDMODE " + DraworUpdate);
            stackPaneHand.toFront();
            cardsIcon.setStyle("-fx-opacity: 1;");
            manualIcon.setStyle("-fx-opacity: 0.6;");
            boardIcon.setStyle("-fx-opacity: 0.6");
        }
    }

    void setManual(ManualController manual) {
        this.manual = manual;
        System.out.println(manual);
    }

    public void setIndexManual(int indexManual){
        this.indexManual = indexManual;
    }

    /**
     * show manual, if pressed again manual will close, the sound will also stop if
     * it was playing.
     *
     * @author Samir
     */

    public void showManual() {
        manual.show();
        manualIcon.setStyle("-fx-opacity: 1;");
        indexManual++;
        if(indexManual == 2){
            manual.hide();
            indexManual = 0;
        }
    }

    /**
     * Switches view mode to board view.
     * @throws IOException :
     * @throws NotBoundException :
     */
    public void showPlayer() throws IOException, NotBoundException {
        stackPaneHand.toBack();
        viewmode = ViewMode.BOARDMODE;
        cardsIcon.setStyle("-fx-opacity: 0.6;");
        manualIcon.setStyle("-fx-opacity: 0.6;");
        boardIcon.setStyle("-fx-opacity: 1;");
    }

    /**
     * Creates and shows the end screen view.
     * @param players : List with players
     * @throws IOException :
     */
    public void showEndScreen(List<Player> players) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("endScreen.fxml"));
        Parent root = fxmlLoader.load();
        EndScreenView endScreenView = fxmlLoader.getController();
        endScreenView.setPlayers(players);
        endScreenView.postScore();
        container.getChildren().add(root);
    }
}
