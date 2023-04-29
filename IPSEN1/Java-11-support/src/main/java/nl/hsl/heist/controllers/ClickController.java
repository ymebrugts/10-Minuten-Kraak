package nl.hsl.heist.controllers;

import nl.hsl.heist.models.*;

import java.io.Serializable;
/**
 * ClickController, handles the incomming clicks from higher up.
 *
 * @author Wesley, Jordi, Joorden, Yme
 */
public class ClickController implements Serializable {

    final static long serialVersionUID = 38923789L;

    private Command commandMode = Command.NORMAL;

    public Card cardCatched;
    private Player playerClicked;
    public Player playerNow;
    private boolean playerHasClicked = false;
    private Card cardClickedInHand;
    private ClientController clientcontroller;


    public ClickController() {
    }

    public Command getCommandMode() {
        return commandMode;
    }

    /**
     * @param player : player
     * @param board : board
     * @param row : int row
     * @param col : int col
     * @return Returns wether the click that's been made higher is actually handled correctly.
     * Handles the click from higherup.
     */
    public boolean registerClick(Player player, Board board, int row, int col) {

        boolean succesfullTurn = false;

        switch (commandMode) {
            case NORMAL:
                succesfullTurn = stealCard(player, board, row, col);
                break;
            case EXTRATURN:
                succesfullTurn = stealCard(player, board, row, col);
                break;
            case YANKCARD:
                succesfullTurn = yankCard(player, board, row, col);
                break;
            case GIVEAMULET:
                succesfullTurn = amuletCard(player, board, row, col);
                break;
        }
        
        if (!succesfullTurn) {
            System.out.println("[+] unsuccesful turn");
            return false;
        } else {
            System.out.println("[+] succesful turn");
            return (handleEndTurn(player));
        }
    }

    /**
     * @param player : player
     * @return Gives registerClick the info to see if the turn has been handled correctly
     * @see #registerClick(Player, Board, int, int) ()
     */
    private boolean handleEndTurn(Player player) {

        if (commandMode == Command.GETPRESTIGE) {
            System.out.println("[+] giving player 2 prestige");
            player.setPrestigePoints(player.getPrestigePoints() + 2);
            commandMode = Command.NORMAL;
        }
        
        // check commandMode again to see if we need to pass the turn
        if (commandMode != Command.EXTRATURN 
                && commandMode != Command.YANKCARD 
                && commandMode != Command.GIVEAMULET) {
            System.out.println("[+] passing turn\n");
            return true;
        } else {
            System.out.println("[+] granting extra turn/click because of mode: "
                    + commandMode.name() + "\n");
            return false;
        }
    }

    /**
     * @param player : player
     * @param board : board
     * @param row : int row
     * @param col : int col
     * @return Returns if the yank has been handles succesfully
     *
     * Allows the player to steal a card
     * anywhere on the board after
     * stealing the card that
     * allows him to do so.
     */
    private boolean yankCard(Player player, Board board, int row, int col) {
        System.out.println("[+] stealing card...");
        System.out.println("[+] board now: " + board);

        Tile tile = board.getTile(row, col);
        Object obj = tile.getObject();

        if (obj instanceof Card) {
            System.out.println("[+] yes it's a card");

            Tile playerTile = LocationController.locatePlayerOnBoard(board, player);

            if (isDiscardCard((Card) obj) && playerHasNoCurseCard(player)) {
                return false;
            }

            commandMode = ((Card)obj).getCommand();
            playerNow = player;
            tile.setEmpty();

            if (commandMode == Command.DISCARD) {
                player.addCard((Card) obj);
                return false;
            }

            if (commandMode != Command.GIVEAMULET && commandMode != Command.COPYCARD) {
                System.out.println("[+] debug: " + ((Card) obj).getCommand());
                player.addCard((Card) obj);

            } else {
                cardCatched = (Card) obj;
                player.notifyClick();
                return false;
            }

            System.out.println("[+] board after: " + board);
            return true;
        } else
            return false;
    }

    public void playerClickedCard(Player playerClicked) {
        playerClicked.addCard(cardCatched);
        playerHasClicked = true;
        commandMode = Command.NORMAL;
    }

    private boolean isDiscardCard(Card card) {
        if (card.getCommand() == Command.DISCARD) {
            return true;
        }
        return false;
    }

    private boolean playerHasNoCurseCard(Player player) {
        for (Card c : player.getCards()) {
            if(c.getCurses() > 0)
                return false;
        }
        return true;
    }

    public Card getClickedCardInHand(Card card) {
        return cardClickedInHand = card;
    }

    /**
     * @param player : player
     * @param cardClicked : card
     * @return Returns if the card has been discarded succesfully
     *
     * After having grabbed the card that allows him
     * to discard one of his own cards, this function allows
     * him to actually do the discarding.
     */
    public boolean discardCard(Player player, Card cardClicked) {
        player.removeCard(cardClicked);
        for (int i = 0; i < player.getCards().size() ; i++) {
            System.out.println("[+] disCardCard in ClickController ---------->" + player.getCards().get(i));
        }
        commandMode = Command.NORMAL;
        return true;
    }

    public void setNormal() {
        commandMode = Command.NORMAL;
    }



    /**
     * @param player : player
     * @param board : board
     * @param row : int row
     * @param col : int col
     * @return Returns if the amuletCard has been succesfully clicked.
     *
     * Removes a card from the board, after the player
     * has been allowed to do so.
     */
    private boolean amuletCard(Player player, Board board, int row, int col) {
        System.out.println("[+] removing card, because of Amulet 1");

        Tile tile = board.getTile(row, col);
        Object obj = tile.getObject();
        if (obj instanceof Card) {
            System.out.println("[+] yes it's a card");
            tile.setEmpty();

            commandMode = Command.GIVEAMULET2;
            player.notifyClick();
        }
        return false;
    }

    /**
     * @param player : player
     * @param board : board
     * @param row : int row
     * @param col : int col
     * @return Returns if the steal has been handles succesfully
     *
     * Allows the player to steal a card.
     */
    private boolean stealCard(Player player, Board board, int row, int col) {
        System.out.println("[+] stealing card...");
        System.out.println("[+] board now: " + board);

        Tile tile = board.getTile(row, col);
        Object obj = tile.getObject();

        if (obj instanceof Card) {
            System.out.println("[+] yes it's a card");

            Tile playerTile = LocationController.locatePlayerOnBoard(board, player);

            if (isDiscardCard((Card) obj) && playerHasNoCurseCard(player)) {
                return false;
            }
            if (row < LocationController.locateTileRowOnBoard(board, playerTile)) {
                System.out.println("[?] not allowed to go up!");
                return false;
            }

            tile.setPlayer(player);

            if (playerTile != null) {
                System.out.println("[+] we have a current title." + playerTile);
                playerTile.removePlayer();
            }

            tile.setPlayer(player);

            commandMode = ((Card)obj).getCommand();
            playerNow = player;

            if (commandMode == Command.DISCARD) {
                player.addCard((Card) obj);
                return false;
            }

            if (commandMode != Command.GIVEAMULET && commandMode != Command.COPYCARD) {
                System.out.println("[+] debug: " + ((Card) obj).getCommand());
                player.addCard((Card) obj);
            } else {
                cardCatched = (Card) obj;
                player.notifyClick();
                return false;
            }

            System.out.println("[+] board after: " + board);
            return true;
        } else
            return false;
    }

    public String getPlayerNow() {
        return playerNow.getName();
    }
}
