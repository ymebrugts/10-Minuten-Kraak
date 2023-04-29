package nl.hsl.heist.views;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.hsl.heist.controllers.ManualController;
import nl.hsl.heist.shared.GameConfig;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Main class for the client.
 *
 * @author Wesley, Yme
 */
public class FXApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static FXView fxView;

    /**
     * Starts the game for the client
     * Also sepperatly creates the manual,
     * so it may be accesed from anytime
     * and anywhere within the game.
     * @param primaryStage :
     * @throws Exception :
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("startMenu.fxml"));
        FXMLLoader manualLoader = new FXMLLoader(getClass().getResource("manual.fxml"));

        Parent root = fxmlLoader.load();
        Parent manual = manualLoader.load();

        ManualController manualObj = manualLoader.<ManualController>getController();
        manualObj.loadContentFiles();
        fxView = (FXView) fxmlLoader.getController();
        fxView.setManual(manualObj);
        fxView.playerIcon.setVisible(false);
        fxView.cardsIcon.setVisible(false);
        fxView.boardIcon.setVisible(false);
        primaryStage.setTitle("10 Minuten Kraak");
        StackPane main = new StackPane();
        main.getChildren().addAll(manual, root);
        primaryStage.setScene(new Scene(main));

        manualObj.setFX(fxView);

        root.getStylesheets().addAll(this.getClass().getResource("stylesheet.css").toExternalForm());
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    Registry reg = LocateRegistry.getRegistry(GameConfig.getPort());
                    UnicastRemoteObject.unexportObject(reg,true);
                } catch (NoSuchObjectException e) {
                    // do nothing
                } catch (RemoteException e) {
                    // do nothing
                }
                System.exit(0);
                primaryStage.close();
            }
        });
    }
}
