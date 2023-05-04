package nl.hsl.heist.controllers;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nl.hsl.heist.models.Card;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Class with functions that can be called from anywhere
 * and be given the top stackpane in order to create
 * a big card in the middle of the screen.
 *
 * @author Jordi, Samir
 */
public class ZoomController {

    private static final int imgSize = 950;
    public static final String PREFIX = "10minutenkraakaudio";
    public static final String SUFFIX = ".tmp";


    /**
     * Zooms in on the specific light back image of a card.
     *
     * @param top Top stackpane where the image will be placed.
     * @author Jordi
     */
    public static void zoomLightCard(StackPane top) {

        System.out.println("[+] zooming on card");

        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(imgSize,imgSize);
        imgPane.setOnMouseClicked((MouseEvent e) -> {
            top.getChildren().remove(imgPane);
        });

        InputStream fullPath = ZoomController.class.getResourceAsStream("/nl/hsl/heist/resources/images/cards/light_back.png");
        Image img = new Image(fullPath, imgSize, imgSize, true, true);
        System.out.println("[+] loading file: " + fullPath);
        ImageView imgView = new ImageView(img);

        imgPane.getChildren().add(imgView);
        top.getChildren().add(imgPane);

        imgPane.setAlignment(Pos.CENTER);
        //imgPane.setTranslateX(imgPane.getTranslateX()+ 900);
      //  imgPane.setTranslateY(imgPane.getTranslateY()+ 550);
        imgPane.toFront();

    }

    /**
     * Zooms in on the specific dark back image of a card.
     *
     * @param top Top stackpane where the image will be placed.
     * @author Jordi
     */
    public static void zoomDarkCard(StackPane top) {

        System.out.println("[+] zooming on card");

        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(imgSize,imgSize);
        imgPane.setOnMouseClicked((MouseEvent e) -> {
            top.getChildren().remove(imgPane);
        });

        InputStream fullPath = ZoomController.class.getResourceAsStream("/nl/hsl/heist/resources/images/cards/dark_back.png");
        Image img = new Image(fullPath, imgSize, imgSize, true, true);
        System.out.println("[+] loading file: " + fullPath);
        ImageView imgView = new ImageView(img);

        imgPane.getChildren().add(imgView);
        top.getChildren().add(imgPane);

        imgPane.setAlignment(Pos.CENTER);
        imgPane.toFront();

    }

    /**
     * Uses the top stackpane and an object to create a large image
     * of the given object in the middle of the screen
     * only if the object is a Card.
     *
     * @param tile Object to be zoomed in returns if not a card.
     * @param top Top stackpane where the image will be placed.
     * @author Jordi, Samir
     */
    public static void zoomCard(Object tile, StackPane top) {
        if (tile instanceof Card) {

            int imgSize = 950;
            int imgSizeSound = 125;

            System.out.println("[+] zooming on card");

            InputStream inputStream = tile.getClass().getResourceAsStream("/nl/hsl/heist/resources/" + ((Card)tile).getSoundPath());
            File soundFile = null;
            OutputStream outputStream = null;
            Media media = null;
            try {
                soundFile = File.createTempFile(PREFIX, SUFFIX);
                outputStream = new FileOutputStream(soundFile);
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
                media = new Media(soundFile.toURI().toString());
                soundFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            StackPane imgPane = new StackPane();
            StackPane imgPaneSound = new StackPane();
            imgPane.setPrefSize(imgSize,imgSize);

            InputStream imgPath = tile.getClass().getResourceAsStream("/nl/hsl/heist/resources/" + ((Card)tile).getImagePath());
            InputStream imgPathSound = tile.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/UI/sound_button.png");
            Image imgSound = new Image(imgPathSound, imgSizeSound, imgSizeSound, true, true);
            Image img = new Image(imgPath, imgSize, imgSize, true, true);

            System.out.println("[+] loading file: " + imgPath);
            System.out.println("[+] loading file: " + imgPathSound);

            ImageView imgViewSound = new ImageView(imgSound);
            ImageView imgView = new ImageView(img);

            imgPaneSound.getChildren().addAll(imgViewSound);
            imgPaneSound.setMaxWidth(imgSizeSound);
            imgPaneSound.setMaxHeight(imgSizeSound);
            imgPane.getChildren().addAll(imgView);
            imgView.mouseTransparentProperty();
            imgPaneSound.setTranslateX(-350);
            imgPaneSound.setTranslateY(399);
            imgView.setMouseTransparent(false);

            imgViewSound.setOnMouseClicked((MouseEvent e) ->{
                mediaPlayer.play();
                mediaPlayer.seek(Duration.ZERO);
            });

            imgPane.setOnMouseClicked((MouseEvent e) -> {
                top.getChildren().remove(imgPane);
                top.getChildren().remove(imgPaneSound);
                mediaPlayer.stop();
                mediaPlayer.dispose();
            });

            top.getChildren().addAll(imgPane, imgPaneSound);

            imgPane.setAlignment(Pos.CENTER);
            imgPane.toFront();
            imgPaneSound.toFront();

        } else {
            System.out.println("[!] tried to zoom on non-card");
        }
    }
}

