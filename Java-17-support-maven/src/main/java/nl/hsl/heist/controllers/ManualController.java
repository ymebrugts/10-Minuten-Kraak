package nl.hsl.heist.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nl.hsl.heist.views.FXView;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Class which houses the manual for each client.
 *
 * @author Jordi, Samir
 */

public class ManualController {

    @FXML private javafx.scene.layout.StackPane manualPane;
    @FXML private javafx.scene.image.ImageView manualImageView;
    @FXML private javafx.scene.control.Label manualLabel;
    @FXML private javafx.scene.control.ScrollPane manualScrollPane;

    private int index = 0;
    private int amountPages;
    private FXView fxcontroller;
    private static final String PREFIX = "10minutenkraakaudio";
    private static final String SUFFIX = ".tmp";

    List<Image> manualPages;
    List<MediaPlayer> mediaPlayers;

    public ManualController getManual() {
        return this;
    }

    public ManualController() {
    }

    /**
     * Loads all of the images and audio files used in the manual.
     *
     * @author Jordi
     */
    public void loadContentFiles() {

        manualPages = new ArrayList<Image>();
        List<InputStream> audioInputs = new ArrayList<InputStream>();
        mediaPlayers = new ArrayList<MediaPlayer>();

        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/frontside.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside2.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside3.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside6.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside7.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside8.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside9.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside10.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/inside11.mp3"));
        audioInputs.add(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/sounds/manual/backside.mp3"));

        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/frontside.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside2.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside3.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside6.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside7.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside8.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside9.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside10.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/inside11.jpg")));
        manualPages.add(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/manual/backside.jpg")));

        for (InputStream inputStream : audioInputs) {
            File soundFile = null;
            OutputStream outputStream = null;
            try {
                soundFile = File.createTempFile(PREFIX, SUFFIX);
                outputStream = new FileOutputStream(soundFile);
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
                mediaPlayers.add(new MediaPlayer(new Media(soundFile.toURI().toString())));
                soundFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        amountPages = manualPages.size();
        System.out.println(manualPages.size());
    }

    /**
     * Flips the page to the right in the manual.
     *
     * @author Jordi, Samir
     */
    public void nextPage() {
        mediaPlayers.get(index).stop();
        index = (index < amountPages-1) ? index+1 : 0;
        manualImageView.setImage(manualPages.get(index));
        manualLabel.setText((Integer.toString(index+1)) + "/" + Integer.toString(amountPages));
        manualScrollPane.setVvalue(0);

}

    /**
     * Flips the page to the left in manual.
     *
     * @author Jordi, Samir
     */
    public void previousPage() {
        mediaPlayers.get(index).stop();
        index = (index > 0) ? index-1 : amountPages-1;
        manualImageView.setImage(manualPages.get(index));
        manualLabel.setText((Integer.toString(index+1)) + "/" + Integer.toString(amountPages));
        manualScrollPane.setVvalue(0);
    }

    /**
     * Plays sound from the designated page.
     *
     * @author Samir
     */
    public void playSound(){
        mediaPlayers.get(index).play();
        mediaPlayers.get(index).seek(Duration.ZERO);
    }

    /**
     * Shows the manual for the client.
     *
     * @author Jordi
     */
    public void show() {
        manualPane.toFront();
    }

    /**
     * Hides the manual for the client.
     *
     * @author Jordi, Samir
     */
    public void hide() {
        manualPane.toBack();
        fxcontroller.manualIcon.setStyle("-fx-opacity: 0.6");
        mediaPlayers.get(index).stop();
        fxcontroller.setIndexManual(0);
    }

    public void setFX(FXView fxView) {
        this.fxcontroller = fxView;
    }

}

