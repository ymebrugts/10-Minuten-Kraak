package nl.hsl.heist.models;

import nl.hsl.heist.controllers.EndController;
import nl.hsl.heist.controllers.LocationController;
import nl.hsl.heist.models.avatar.*;
import nl.hsl.heist.models.prestige.*;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteBoard;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static nl.hsl.heist.shared.GameConfig.*;
/**
 * Houses all the functions for creating, setting up and changing the game.
 *
 * @author Wesley, Jordi, Joorden, Yme
 */
public class Game implements Serializable {

    private static final long serialVersionUID = -7256308005407049420L;

    private List<Card> lightCards;
    private List<Card> darkCards;
    private List<Avatar> avatarList;
    private List<Prestige> prestiges;
    private GameState gameState;
    private Player currentPlayerTurn;
    private EndController endController;
    private Board board;
    int finishedPlayers = 0;

    /**
     * Setup the game.
     * Init all cards, and object a game needs.
     * Broadcast the board
     *
     * @see #initCards()
     * @see #initPrestige()
     * @see #initAvatars()
     * @see #setupBoard(Board)
     *
     * @throws RemoteException :
     *
     * @param gameState : GameState
     * @author Wesley
     */
    public Game(GameState gameState) throws RemoteException {
        try {
            setup(gameState);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Setup the required cards for the game.
     *
     * @author Wesley, Jordi
     */
    public void initCards() {
        InputStream lightCardsResourcePath = this.getClass().getResourceAsStream("/nl/hsl/heist/resources/light.json");
        InputStream darkCardsResourcePath = this.getClass().getResourceAsStream("/nl/hsl/heist/resources/dark.json");
        CardFactory cardFactory = new CardFactory();
        lightCards = cardFactory.initCards(lightCardsResourcePath);
        darkCards = cardFactory.initCards(darkCardsResourcePath);
        // we don't need the cardFactory anymore, so let's destroy it.
        cardFactory = null;
    }

    /**
     * Setup the avatars a player may have and make it random.
     *
     * @author Jordi
     */
    public void initAvatars() {
        if (avatarList == null)
            avatarList = new ArrayList<Avatar>();

        avatarList.add(new BlueAvatar());
        avatarList.add(new SnorAvatar());
        avatarList.add(new TravelerAvatar());
        avatarList.add(new LanternAvatar());
        avatarList.add(new KeyAvatar());

        // Make avatar handout random
        Collections.shuffle(avatarList);
    }

    /**
     * Setup the prestige's a player may acquire at the end of a game.
     *
     * @author Wesley
     */
    public void initPrestige() {
        if (prestiges == null)
            prestiges = new ArrayList<Prestige>();
        prestiges.add(new MostTome());
        prestiges.add(new MostJewel());
        prestiges.add(new MostPotion());
        prestiges.add(new MostArtifact());
        prestiges.add(new MostFossil());
        prestiges.add(new MostThree());
        prestiges.add(new MostFour());
        prestiges.add(new MostFive());
        prestiges.add(new MostCurse());
        prestiges.add(new LeastCurse());
        prestiges.add(new FirstEscape());
        prestiges.add(new SecondEscape());
        prestiges.add(new LastEscape());
    }

    public void setup(GameState gameState) throws RemoteException {
        initCards();
        initPrestige();
        initAvatars();

        board = new Board();
        this.gameState = gameState;

        RemoteBoard boardSkeleton = (RemoteBoard) UnicastRemoteObject.exportObject(board, 0);
        Registry registry = LocateRegistry.getRegistry(GameConfig.getPort());
        System.out.println("[+] got registry server");
        registry.rebind("Board", boardSkeleton);
        setupBoard(board);
    }

    /**
     * Define the matrix of the board and setup the needed tiles.
     *
     * @param board : Board object that we want to setup
     * @author Wesley
     */
    public void setupBoard(Board board) {
        board.matrix = new Tile[ROWS][COLS];
        System.out.println("[+] setting up board...");
        for (int row = 0; row < LIGHTROWS; row++) {
            setupTiles(row, lightCards);
        }

        for (int row = LIGHTROWS; row < ROWS; row++) {
			setupTiles(row, darkCards);
        }
    }

    /**
     * Setup tiles, add the observer: Board board.
     * Pick a random card from the list of cards.
     * assign tile to a free place in the board matrix.
     * @see #setupBoard(Board)
     *
     * @param currentRow : row, where the tile needs to be in the board matrix.
     * @param cards : List of cards, the tile may pick from.
     * @author Wesley
     */
    private void setupTiles(int currentRow, List<Card> cards) {
        for (int col = 0; col < COLS; col++) {
            Tile tile = new Tile();
            tile.addObsever(board);
            Card card = drawRandomCard(cards);
            tile.setCard(card);
            board.matrix[currentRow][col] = tile;
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * Draw a random card, from the list with cards.
     *
     * @param cards : List of card objects.
     * @return card : Card card.
     * @author Wesley
     */
    private Card drawRandomCard(List<Card> cards) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, cards.size());
        Card card = cards.get(randomNum);
        cards.remove(randomNum);
        return card;
    }

    /**
     * Draw a random card light card at the beginning of the game.
     * Assign the card to a user.
     *
     * @param name : Name of the player who needs to get a blind card.
     * @author Wesley
     */
    public void drawBlindCard(String name) {
        Card card = drawRandomCard(lightCards);
        System.out.println("[+] blind card drawn: " + card);
        Player player = getPlayer(name);
        player.addCard(card);
    }

    /**
     * Add a new player to the game (GameState).
     * The player starts with the state: #PlayerState.WAITING.
     * Assign a random avatar to the player.
     *
     * @throws RemoteException
     * when the connection between RMI client and server is
     * compromised
     *
     * @param name : Name for the new player.
     * @author Wesley
     */
    public void addPlayer(String name) throws RemoteException {
        System.out.println("[+] adding new player");
        Player player = new Player(name);
        player.setPlayerState(PlayerState.WAITING);
        gameState.addPlayers(player);
        drawBlindCard(name);
        player.addObsever(gameState);

        player.setAvatar(avatarList.get(0));
        avatarList.remove(0);
    }

    public Player getPlayer(String name) {
        Player player = null;
        List<Player> players = gameState.getPlayers();
        for (Player somePlayer : players) {
            if (somePlayer.getName().equalsIgnoreCase(name)) {
                player = somePlayer;
            }
        }
        return player;
    }

    /**
     * When the game starts, pick a random #Player who may start the game.
     *
     * @author Wesley
     */
    public void baseTurn() {
        List<Player> players = gameState.getPlayers();
        System.out.println(players);
        int randomNum = ThreadLocalRandom.current().nextInt(0, players.size());
        currentPlayerTurn = players.get(randomNum);
        currentPlayerTurn.setPlayerState(PlayerState.PLAYING);
        System.out.println("[+] the game starts with: " + currentPlayerTurn.getName());
    }

    /**
     * Pass the turn to the next player in the list.
     * Set the old player to the state: #PlayerState.WAITING.
     * Set the next player to the state: #PlayerState.PLAYING.
     *
     * @param player : Player who already did his turn (current player).
     * @author Wesley
     */
    public void moveTurn(Player player) {
        List<Player> players = gameState.getPlayers();
        currentPlayerTurn = players.get((players.indexOf(player)+1)%players.size());
        player.setPlayerState(PlayerState.WAITING);
        currentPlayerTurn.setPlayerState(PlayerState.PLAYING);
    }

    /**
     * Check if the player has the turn.
     *
     * @param player : Player who wants to do an action.
     * @return boolean
     * @author Wesley
     */
    public boolean playerTurn(Player player) {
        return currentPlayerTurn.getName().equals(player.getName());
    }

    /**
     * Assign the escape prestige only to the first, second and last #Player who is done.
     * @see FirstEscape
     * @see SecondEscape
     * @see LastEscape
     *
     * @param player : Player, who is done playing.
     * @author Jordi, Joorden
     */
    private void handoutEscapeNumberPrestige(Player player) {

        int totalPlayers = calculateTotalPlayers();

        if (finishedPlayers == 1) {
            for (Prestige p : prestiges) {
                if (p instanceof FirstEscape) {
                    player.getPrestiges().add(p);
                    System.out.println("[+] handing out firstescape");
                }
            }
        } else if (finishedPlayers == 2) {
            for (Prestige p : prestiges) {
                if (p instanceof SecondEscape) {
                    player.getPrestiges().add(p);
                    System.out.println("[+] handing out secondescape");
                }
            }
        }

        if (finishedPlayers == totalPlayers) {
            for (Prestige p : prestiges) {
                if (p instanceof LastEscape) {
                    player.getPrestiges().add(p);
                    System.out.println("[+] handing out lastescape");
                }
            }
        }
    }

    /**
     * Count the total players, the list is splits in finished players and playing players.
     * Needed for handing out prestiges.
     * @see #handoutEscapeNumberPrestige(Player)
     * @see GameState
     * @return int : amount of playing players plus donePlayers
     * @author Jordi
     */
    private int calculateTotalPlayers() {
        int playingPlayers = 0;
        int donePlayers = 0;
        if (gameState.getPlayers() != null)
            playingPlayers = gameState.getPlayers().size();
        if (gameState.getFinished() != null)
            donePlayers = gameState.getFinished().size();
        return playingPlayers + donePlayers;

    }

    /**
     * A player has finished, remove player from board, Player list
     * Add Player to finished Players
     * If everyone is done, end the game
     * @see GameState
     * @see EndController
     * @throws IOException :
     * @param name : name of the player that finish.
     * @return boolean : has the player finished
     * @author Jordi, Joorden
     */
    public boolean finish(String name) throws IOException {
        Player player = getPlayer(name);

        if (playerTurn(player) == false) {
            System.out.println("[?] it's not your turn! " + player.getName());
            return false;
        }

        finishedPlayers++;
        handoutEscapeNumberPrestige(player);

        Tile tile = LocationController.locatePlayerOnBoard(board, player);

        gameState.finish(player);
        tile.removePlayer();
        System.out.println("[+] player has finished");

        if (gameState.getPlayers().isEmpty()) {
            endController = new EndController();
            System.out.println("[+] fucking ending it");
            endController.endIt(gameState.getFinished(), prestiges);
            gameState.gameHasEnded();
        } else {
            moveTurn(player);
        }

        return true;

    }
}
