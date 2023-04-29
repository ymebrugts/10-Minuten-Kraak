package nl.hsl.heist.controllers;

import nl.hsl.heist.models.Game;

import java.io.*;

/**
 *  RemoteLoadGame
 * @author wesley
 */
public class SaveGameController {

    public static Game game;

    public SaveGameController(Game game) {
        this.game = game;
    }

    public void saveGame() {
        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("10_minuten_kraak_save.data")))) {
            // no need to specify members individually
            oos.writeObject(game);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
