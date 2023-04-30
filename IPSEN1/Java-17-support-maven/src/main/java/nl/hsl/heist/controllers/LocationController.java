package nl.hsl.heist.controllers;

import nl.hsl.heist.models.Board;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.models.Tile;

import static nl.hsl.heist.shared.GameConfig.COLS;
import static nl.hsl.heist.shared.GameConfig.ROWS;

/**
 *  LocationController
 * @author wesley, Jordi
 */
public class LocationController {

    /**
     * Last card is the board matrix is a blind card
     * @param col : int col
     * @param row : int row
     * @return boolean
     * @author Jordi
     */
    public static boolean isBlindCardBoard(int row, int col) {
        return (row == 6 && col == 4);
    }

    /**
     * Find the row of the tile
     * @param board : Board object
     * @param tile : Tile object
     * @return int tileRow
     * @author Wesley, Jordi
     */
    public static int locateTileRowOnBoard(Board board, Tile tile) {
        int tileRow = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board.getTile(row, col).equals(tile)) {
                    tileRow = row; break;
                }
            }
        }
        return tileRow;
    }

    /**
     * Find the tile where the player is located
     * @param board : Board object
     * @param player : Player object
     * @return Tile playerTile
     * @author Wesley, Jordi
     */
    public static Tile locatePlayerOnBoard(Board board, Player player) {
        Tile playerTile = null;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile tile = board.getTile(row, col);
                if ((tile.getPlayer() != null) && (tile.getPlayer().equals(player))) {
                    playerTile = tile; break;
                }
            }
        }     
        return playerTile;
    }    
}
