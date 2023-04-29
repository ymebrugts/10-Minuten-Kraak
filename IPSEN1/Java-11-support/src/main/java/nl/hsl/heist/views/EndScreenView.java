package nl.hsl.heist.views;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nl.hsl.heist.models.Player;

import java.util.List;
/**
 *
 * @author Wesley
 */
public class EndScreenView {

    @FXML private javafx.scene.layout.VBox winnerContainer;
    private List<Player> players;

    /**
     * For each player, show their name and score on the end screen.
     */
    public void postScore() {
        int place = 1;
        for (Player somePlayer : players) {
            somePlayer.getName();
            Label playerName = new Label(place + ". " + somePlayer.getName());
            playerName.setAlignment(Pos.CENTER_LEFT);
            playerName.setStyle("-fx-font-family: Felt; -fx-fill: #dfebf9; -fx-font-size: 28px");
            playerName.setMaxWidth(300);
            playerName.setMinWidth(300);
            playerName.setTextFill(Color.valueOf("#dfebf9"));
            playerName.setPadding(new Insets(20, 50, 0, 20));

            Label prestiges = new Label(String.valueOf(somePlayer.getPrestigePoints()));
            prestiges.setAlignment(Pos.CENTER_RIGHT);
            prestiges.setStyle("-fx-font-family: Felt; -fx-fill: #dfebf9; -fx-font-size: 28px");
            prestiges.setTextFill(Color.valueOf("#dfebf9"));
            prestiges.setPadding(new Insets(20, 50, 0, 20));

            HBox hBox = new HBox();
            hBox.getChildren().addAll(playerName, prestiges);
            winnerContainer.getChildren().addAll(hBox);
            place++;
        }
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
