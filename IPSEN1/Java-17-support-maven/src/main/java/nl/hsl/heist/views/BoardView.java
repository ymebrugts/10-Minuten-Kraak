package nl.hsl.heist.views;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import nl.hsl.heist.controllers.LocationController;
import nl.hsl.heist.controllers.ZoomController;
import nl.hsl.heist.models.Card;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.observers.BoardObserver;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteBoard;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import static nl.hsl.heist.shared.GameConfig.COLS;
import static nl.hsl.heist.shared.GameConfig.ROWS;
/**
 * Houses the functions that control the board.
 *
 * @author Wesley, Jordi
 */
public class BoardView extends UnicastRemoteObject implements BoardObserver {

    static final long serialVersionUID = 345879983458L;

    @FXML
    private javafx.scene.layout.BorderPane boardBorderPane;
    private javafx.scene.control.ScrollPane scrollPane;
    private final double TILE_SIZE_WIDTH = 250;
    private final double TILE_SIZE_HEIGHT = 250;
    private int id;
    private GameAction remoteGameController;
    private boolean started = false;

    public BoardView() throws RemoteException {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRemoteGameController(GameAction remoteGameController) {
        this.remoteGameController = remoteGameController;
    }

    /**
     * Observe the board.
     * @throws RemoteException :
     * @throws NotBoundException :
     */
    public  void setObservable() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(GameConfig.getHostName()); // if server on another machine: provide that machine's IP address. Default port  1099
        RemoteBoard remoteBoard = (RemoteBoard) registry.lookup("Board"); // get remote Calculator object from registry
        remoteBoard.addObserver(this);
    }

    /**
     * Starts the game after Start Game button has been clicked in the main-menu.
     */
    @FXML public void startGame() {
        System.out.println("[+] starting...");
        try {
            started = remoteGameController.startGame(id);
            System.out.println("[+] started..." + started);
            remoteGameController.setStarted(started);
        } catch (RemoteException e) {
            System.out.println("[!] remoteException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * When the board model has changed, paint or update the board.
     * @throws RemoteException :
     * @param board Remote version of the board.
     * @author wesley
     */
    @Override public void modelChanged(RemoteBoard board) throws RemoteException {
        System.out.println("[+] board has changed!");
        // called from another thread, so we have to use the platform.
        // Create a new CompletableFuture that runs the UI updates on the JavaFX Application Thread
        CompletableFuture<Void> uiUpdateFuture = CompletableFuture.supplyAsync(() -> {
            CompletableFuture<Void> uiUpdateCompletion = new CompletableFuture<>();
            Platform.runLater(() -> {
                try {
                    if (scrollPane == null) {
                        paintBoard(board);
                    } else {
                        updateTiles(board);
                    }
                    uiUpdateCompletion.complete(null);
                } catch (Exception e) {
                    uiUpdateCompletion.completeExceptionally(e);
                }
            });
            return uiUpdateCompletion;
        }).thenCompose(Function.identity());

        CompletableFuture<Void> saveGameFuture = uiUpdateFuture.thenRunAsync(() -> {
            try {
                remoteGameController.saveGame();
            } catch (RemoteException e) {
                System.out.println("RemoteException: Saving failed");
                System.out.println(e.getMessage());
            }
        });

    }


    /**
     * Starts the first painting of the board, creates the main scrollpane.
     * @param board Remote version of the board.
     */
    public void paintBoard(RemoteBoard board) {
        Task<Parent> loadTask = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent root = paintTiles(board);
                return root;
            }
        };

        loadTask.setOnSucceeded( e-> {
            if (scrollPane == null) {
                scrollPane = new ScrollPane();
                scrollPane.setMaxSize((TILE_SIZE_WIDTH + 5) * COLS, TILE_SIZE_HEIGHT * ROWS);
            }
            scrollPane.setContent(loadTask.getValue());
            boardBorderPane.setCenter(scrollPane);
        });

        Thread thread = new Thread(loadTask);
        thread.start();
    }

    /**
     * Updates all the tiles from the board, replaces the new tiles with the old ones.
     * @param board Remote version of board
     */
    public void updateTiles(RemoteBoard board) {
        Pane root = (Pane) scrollPane.getContent();
        int i = 0;
        for (int row = 0; row < ROWS ; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane tile = (StackPane) root.getChildren().get(i);
                try {
                    Object gameTile = board.getTile(row, col).getObject();
                    if (!(gameTile instanceof Card)) {
                        ImageView cardContainer = (ImageView) tile.getChildren().get(0);
                        cardContainer.setImage(null);
                        tile.setStyle("-fx-background-image: none");
                    }

                    if (gameTile instanceof Player) {
                        //Set the player image on the board at the correct position
                        String imgPath = ((Player)gameTile).getAvatar().getImgPath();
                        String playerImage = BoardView.class.getResource("/nl/hsl/heist/resources/" + imgPath).toExternalForm();
                        tile.setStyle("-fx-background-image: url(" + playerImage + "); -fx-background-size: contain; -fx-background-repeat: stretch; -fx-background-position: center center");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }

    /**
     * Sends the click it recieved to the GameController, with the player's ID.
     * @param row : int row
     * @param col : int col
     * @throws RemoteException :
     */
    public void registerClick(int row, int col) throws RemoteException {
        remoteGameController.registerClick(id, row, col);
    }

    /**
     * Paints the tiles on the board and assigns the MouseClicked event for the tiles on board.
     * @param board Remote version of the board.
     * @return Parent
     */
    public Parent paintTiles(RemoteBoard board) {
        try {
            Pane root = new Pane();
            root.setPrefSize((TILE_SIZE_WIDTH + 1) * COLS, (TILE_SIZE_HEIGHT +2) * ROWS);
            String backgroundUrl = BoardView.class.getResource("/nl/hsl/heist/resources/images/backgrounds/paper_bg.png").toExternalForm();
            root.setStyle("-fx-background-image: url(" + backgroundUrl + "); -fx-background-size: cover; -fx-background-repeat: stretch");
            for (int row = 0; row < ROWS ; row++) {
                for (int col = 0; col < COLS; col++) {
                    final int row_number = row;
                    final int col_number = col;
                    StackPane tile = new StackPane();
                    tile.setPrefSize(TILE_SIZE_WIDTH, TILE_SIZE_HEIGHT);
                    tile.setTranslateX(col * (TILE_SIZE_WIDTH + 2));
                    tile.setTranslateY(row * (TILE_SIZE_HEIGHT + 2));
                    tile.getStyleClass().add("TileBoard");
                    Object gameTile = board.getTile(row, col).getObject();
                    ImageView cardContainer = new ImageView();
                    if (gameTile instanceof Card) {
                        String image = "";
                        if (LocationController.isBlindCardBoard(row, col)) {
                            image = "images/cards/dark_back.png";
                        } else {
                            image = board.getTile(row, col).getCard().getImagePath();
                        }
                        // With this code, the first setup is fast, however it will reload on every update.

                        Image cardImage = new Image(BoardView.class.getResource("/nl/hsl/heist/resources/" + image).toExternalForm(), true);
                        cardContainer.setImage(cardImage);
                        cardContainer.setFitWidth(TILE_SIZE_WIDTH);
                        cardContainer.setFitHeight(TILE_SIZE_HEIGHT);
                    }
                    tile.getChildren().add(cardContainer);

                    if (gameTile instanceof Player) {
                        String imgPath = ((Player)gameTile).getAvatar().getImgPath();
                        String imageUrl = BoardView.class.getResource("/nl/hsl/heist/resources/" + imgPath).toExternalForm();
                        tile.setStyle("-fx-background-image: url(" + imageUrl + "); -fx-background-size: contain; -fx-background-repeat: stretch; -fx-background-position: center center");
                    }

                    tile.setOnMouseClicked( (MouseEvent e) -> {
                        try {
                            if (e.getButton() == MouseButton.PRIMARY)
                                registerClick(row_number, col_number);
                            else
                                zoomCardBoard(row_number, col_number, board);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    });
                    root.getChildren().add(tile);
                }
            }

            return root;
        } catch (Exception e) {
            System.out.println("[!] ik ben exception in paint Tiles");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used board together with row and col to check if the card is zoomable
     * and if so, which image should be used. Passes it over to zoomCard.
     * @param row : int row
     * @param col : int col
     * @param board : RemoteBoard board
     * @see nl.hsl.heist.controllers.ZoomController
     */
    private void zoomCardBoard(int row, int col, RemoteBoard board) {
        Object gameTile = null;
        try {
            gameTile = board.getTile(row, col).getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!(gameTile instanceof Card))
            return;

        if (LocationController.isBlindCardBoard(row, col)) {
            ZoomController.zoomDarkCard((StackPane)boardBorderPane.getParent().getParent());
        } else
            ZoomController.zoomCard(gameTile, (StackPane)boardBorderPane.getParent().getParent());
    }

    /**
     * Sends the bridge click to the GameController with the current player's id.
     * this let's the player finish
     */
    @FXML public void handleBridgeClick() {
        try {
            remoteGameController.finish(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
