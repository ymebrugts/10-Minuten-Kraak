package nl.hsl.heist.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
/**
 * Handles the settings for the popup when it's a player's turn.
 *
 * @author Wesley
 */
public class TurnPopupController {

    @FXML private javafx.scene.image.ImageView playerAvatar;
    @FXML private javafx.scene.control.Label playerName;
    @FXML private javafx.scene.control.Label textBox;

    public void setPlayerAvatar(String avatarPath) {
        Image avatar = new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/" + avatarPath));
        playerAvatar.setImage(avatar);
    }

    public void setPlayerName(String name) {
        playerName.setText(name);
    }

    public void setTextBox(String text) {
        textBox.setText(text);
    }


}
