    package nl.hsl.heist.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.input.MouseButton;

import nl.hsl.heist.controllers.ClickController;
import nl.hsl.heist.controllers.ZoomController;
import nl.hsl.heist.models.Card;
import nl.hsl.heist.models.Command;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.observers.GamestateObserver;
import nl.hsl.heist.shared.GameAction;
import nl.hsl.heist.shared.GameConfig;
import nl.hsl.heist.shared.RemoteGameState;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Houses the functions that control the HandView
 *
 * @author Yme, Wesley, Jordi
 */

public class HandView extends UnicastRemoteObject implements GamestateObserver {

    static final long serialVersionUID = 345879922458L;

    private Command commandMode = null;
    private ClickController clickController = null;
    private int id;
    private boolean placedCardWrong = false;

    @FXML private javafx.scene.layout.VBox playerPane;
    @FXML private javafx.scene.layout.VBox allCardsPane;
    @FXML private javafx.scene.control.ScrollPane handContainer;
    @FXML private javafx.scene.layout.BorderPane handBorderPane;

    private GameAction remoteGameController;
    private List<Card> cards;
    private FXView fxcontroller;
    private RemoteGameState remoteGameState;

    private StackPane stackPaneTotal;
    private int playerLoop;
    private List<Integer> saveCardList = new ArrayList<Integer>();
    private List<StackPane> stackPaneList = new ArrayList<StackPane>();
    private List<ImageView> cardPanes = new ArrayList<ImageView>();

    /**
     * Constructor for this class
     * Is empty but needed because of Remote class super()
     * @see GamestateObserver
     * @throws RemoteException : RMI connection error.
     * @author Yme
     */
    public HandView() throws RemoteException {
    }

    /**
     * Get the FXView.
     * After getting the FXView it will return the FXView as a local variable
     * @see FXView#showHand()
     *
     * @param fxView : This is a FXView object
     * @author Yme
     */
    public void getFXController(FXView fxView) {
            this.fxcontroller = fxView;
    }

    public void setId(int id) {
            this.id = id;
        }

    /**
     * SetHandView only runs the first time a client opens the handview
     * gets the players from RemoteGameController.
     * Creates all the panes and spacing for the HandView.
     * Loops through all the players to do this.
     * Adds a clickListener for the special Card 'Amulet of Nae"
     *
     * @see ClickController
     * @see #createCardsHandView(List, int)
     * @see #drawIndividualCard(List, int, int)
     * @see GameAction
     * @see FXView#showHand()
     *
     * @param player  :
     * @author Yme
     */
    public void setPlayers(List<Player> player) {
        if (clickController == null) {
            try {
                clickController = remoteGameController.getClickController();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        playerPane.setPadding(new Insets(10,10,0,10));
        playerPane.setSpacing(0);


        for (playerLoop = 0; playerLoop < player.size(); playerLoop++) {
            //Creating rectangle so it falls behind the player avatar
            Rectangle naamrectangle = new Rectangle();
            naamrectangle.setFill(Color.web("#0b4775"));
            //creating player avatar so it goes over the rectangle
            System.out.println(player.get(playerLoop).getAvatar().getImgPath());
            Image avatarImage = new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/" + player.get(playerLoop).getAvatar().getImgPath()));
            ImageView imageview = new ImageView(avatarImage);
            //Event listener playerClicked to select the player that clicked on the player icon
            Player playerClicked = player.get(playerLoop);
            imageview.setFitHeight(150);
            imageview.setPreserveRatio(true);
            //StackPane for label and player avatar
            StackPane stackpaneHand = new StackPane();
            //Top pane for spacing in between players
            Pane paneSpacing = new Pane();
            /*
            Pane for the cards in their entirety.
            This stackpane contains all the stackpanes of each individual card
            */
            stackPaneTotal = new StackPane();
            String stackpaneID = "stackPaneTotal" + playerLoop;
            stackPaneTotal.setId(stackpaneID);
            //We use this to refer to specific players later on
            stackPaneList.add(stackPaneTotal);
            //Click Listener on player Icon
            System.out.println("[+] muh clickController = " + clickController);
            imageview.setOnMouseClicked(e -> {
                System.out.println("[+] event listener: " + playerClicked.getName());
                commandMode = clickController.getCommandMode();
                System.out.println("[+] commandMode in HandView = " + commandMode);
                if (commandMode == Command.GIVEAMULET2) {
                    try {
                        remoteGameController.handoutAmulet(playerClicked.getName(), id);
                        System.out.println(playerClicked);
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }

                    //FX Reset after select
                    System.out.println("[+] event listener: " + playerClicked.getName());
                    fxcontroller.boardIcon.setVisible(true);
                }
            });
            //Creating list of the amount of cards every player had on their first look in the HandView
            saveCardList.add(player.get(playerLoop).getCards().size());
            //create the borderpane Center handview with the cards inside
            createCardsHandView(player, playerLoop);
            naamrectangle.setWidth(400);
            naamrectangle.setHeight(50);
            //variables needed
            double heightImage = 14;
            int translateConstantX = 62;
            System.out.println("[+] dit is de hoogte:" + heightImage);
            naamrectangle.setTranslateY(heightImage);
            naamrectangle.setTranslateX(translateConstantX);
            Label label = new Label(player.get(playerLoop).getName());
            label.setMaxWidth(300);
            label.paddingProperty().set(new Insets(0,0,0,50));
            label.setTranslateY(heightImage);
            label.setTranslateX(translateConstantX);
            label.setFont(new Font(32));
            label.setTextFill(Color.WHITE);
            //Full cardPanes with all the cards in them. We use this one for padding and placement
            allCardsPane.getChildren().add(stackPaneTotal);
            allCardsPane.setTranslateX(-100);
            allCardsPane.setSpacing(110);
            allCardsPane.setPadding(new Insets(100,0,0,0));
            stackpaneHand.getChildren().addAll(naamrectangle, label);
            paneSpacing.getChildren().addAll(stackpaneHand,imageview);
            playerPane.getChildren().addAll(paneSpacing);
            playerPane.setPrefWidth(150);
            playerPane.setMaxWidth(150);
            playerPane.setMinWidth(150);
            playerPane.setSpacing(260);
        }
    }

    /**
     * Creates a new List of cards to make sure it is always synced
     * This function is mostly redundant now but stays in for stability.
     *
     * @see #updateCardsHandView(List)
     * @see #createCardsHandView(List, int)
     *
     * @param player : List of cards that need to be updated for a player in the list
     * @param number : The specific player (index) that needs to be updated
     * @author Yme
     */
    private void updateCardList(List<Player> player, int number) {
        // For updating/creating the card lists so they show the newest version
        cards = new ArrayList<Card>();
        cards.addAll(player.get(number).getCards());
    }

    /**
     * Create the cardview for the fist time.
     *
     *
     * @see #updateCardsHandView(List)
     *
     * @param player : List of cards that need to be updated for a player in the list
     * @param spelerLoop : The specific player (index) that needs to be created
     * @author Yme
     */
    private void createCardsHandView(List<Player> player, int spelerLoop) {
        //updateCardList
        updateCardList(player, spelerLoop);
        //for each card run one time
        for (int j = 0; j < cards.size(); j++) {
            //for each card that a player has run one time to draw them one by one.
            drawIndividualCard(cards, j, spelerLoop);
        }
    }

    /**
     * Drawing individual cards.
     * This function is used to draw the individual cards so that updating may become possible one by one.
     * Creates ImageView inside StackPane inside StackPane
     * On Hover is detected and onHover called
     * Click Listeners for the Special Cards in the Game COPYCARD and
     *
     *
     * @see #onHover(Node, int)
     * @see #updateCardsHandView(List)
     * @see #setPlayers(List)
     * @see GameAction#zandDerIllusie(Card, int)
     * @see GameAction#handoutAmulet(String, int)
     *
     * @param cards : List of cards that need to be drawn on the handview
     * @param cardLoop : The specific cardList (index) that needs to be used
     * @param playerLoopI : The specific player (index) that needs to be created
     * @author Yme
     */
    private void drawIndividualCard(List<Card> cards, int cardLoop, int playerLoopI) {
        //Get imagepatch of the card that we have to draw
        String imagePath = cards.get(cardLoop).getImagePath();
        //Create new card by first creating and image and putting it into an imageview
        Image cardimg = new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/" + imagePath));
        ImageView cardimage = new ImageView(cardimg);
        //Add a class dynamically so we can style or call all the cards later
        cardimage.getStyleClass().add("imageClass");
        //stackpane to house all of the imageviews in
        StackPane stackpane = new StackPane();
        stackpane.getChildren().add(cardimage);
        //width and height of the cards
        cardimage.setFitWidth(300);
        cardimage.setFitHeight(300);
        //position of the cards
        stackpane.setAlignment(Pos.CENTER_LEFT);
        //class of the stackpane that houses the imageview that houses the card
        stackpane.getStyleClass().add("stackCard");

        //adding cardimages to a list for later reference
        cardPanes.add(cardimage);
        //adding to stackPaneList so they will be shown in the correct players hand (see setPlayers()#stackPaneTotal)
        stackPaneList.get(playerLoopI).getChildren().add(stackpane);
        //set Unique id on the card stackpanes
        stackpane.setId(Integer.toString(playerLoopI));
        //set Unique id on the card imageviews
        cardimage.setId(Integer.toString(playerLoopI));


        //translate set for the cards on hover effect. -2 is for normal drawing
        int LastCardIndex = stackPaneList.get(playerLoopI).getChildren().size() - 2;
        //-1 is for the first card in the index
        if (LastCardIndex == -1) {
            LastCardIndex = stackPaneList.get(playerLoopI).getChildren().size() - 1;
        }
        //translate based on the last second to last stackpane(card) drawn in a previous iteration of this function
        StackPane stackLast = (StackPane) stackPaneList.get(playerLoopI).getChildren().get(LastCardIndex);
        stackpane.setTranslateX(stackLast.getTranslateX() + 110);

        //onHover effect for when mouse enters en exits
        cardimage.setOnMouseEntered (e -> {
            int translateA = 186;
            onHover(cardimage, translateA);
        });
        cardimage.setOnMouseExited(e -> {
            int translateA = -186;
            onHover(cardimage, translateA);
        });
        //Zoom function bound to the right mouse click
        cardimage.setOnMouseClicked( e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                StackPane top = (StackPane) handContainer.getParent().getParent();
                if (isBlindCard(cardLoop, playerLoopI)) {
                    ZoomController.zoomLightCard(top);
                } else {
                    ZoomController.zoomCard(cards.get(cardLoop), top);
                }
            }
        });
        //replace the first card with a blind card for only the other clients not yourself. (see: isBlindCard)
        if (isBlindCard(cardLoop, playerLoopI))
            cardimage.setImage(new Image(this.getClass().getResourceAsStream("/nl/hsl/heist/resources/images/cards/light_back.png")));
        /*
        This listener is for listening for mouse clicks on the cards. This is needed when a player picks up specific
        cards.
        */
        stackpane.setOnMouseClicked(e -> {
            System.out.println(clickController.getCommandMode());
            /*
            This listener is for the special command card that allows a player to remove one card from his own hand.
            See the ClickController for more information about the CommandMode and GameController for the handling of
            this click
            */
            if (clickController.getCommandMode() == Command.DISCARD) {
                try {
                    Player playerNowObjectAmulet = remoteGameController.returnPlayerNow();
                    boolean foundCard = false;
                    for (Card c: playerNowObjectAmulet.getCards()) {
                        if (c.getName().equals(cards.get(cardLoop).getName())) {
                            foundCard = true;
                            break;
                        }
                    }
                    if (!foundCard || cards.get(cardLoop).getCurses() < 1) {
                        return;
                    }
                    remoteGameController.HandleMythCardHandClick(cards.get(cardLoop), id);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

            /*
            This listener is for the special command card that allows a player to copy one card from his own hand.
            See the ClickController for more information about the CommandMode and GameController for the handling of
            this click
            */
            }
            else if (clickController.getCommandMode() == Command.COPYCARD) {
                Player playerNowObject = null;
                try {
                    //returns the player that has a turn right now so we can check on which hand the client can click
                    playerNowObject = remoteGameController.returnPlayerNow();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                boolean foundCard = false;
                for (Card c: playerNowObject.getCards()) {
                    if (c.getName().equals(cards.get(cardLoop).getName())) {
                        foundCard = true;
                        break;
                    }
                }
                //This is to check for all of the conditions of this card before handling click
                //It failed to pass the conditions if it returns false
                boolean voldoetVoorwaarde = CheckZandDerIllusieVoorwaarde(cards.get(cardLoop));
                //This returns if it does not meet the conditions set.
                if (!foundCard || !voldoetVoorwaarde) {
                    return;
                }
                try {
                    //handles click and all the actions associated with this card
                    remoteGameController.zandDerIllusie(cards.get(cardLoop), id);
                    if (cards.get(cardLoop).equals(cards.get(cards.size()-1)) && !placedCardWrong) {
                        placedCardWrong = true;
        }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * Checking Command card conditions
     * If true then it has passed the conditions
     *
     *
     * @see #drawIndividualCard(List, int, int)
     *
     * @param card : The card that needs to be checked for conditions
     * @return boolean : returns true if check was passed.
     * @author Yme
     */
    private boolean CheckZandDerIllusieVoorwaarde(Card card) {
        boolean checkCard = false;
        if (card.getPoints().get(0) < 6 && card.getPoints().get(0) > 2) {
            checkCard = true;
        }
        return checkCard;
    }

    /**
     * Checks if BlindCard
     *
     *
     * @see #drawIndividualCard(List, int, int)
     *
     * @param cardLoop : The specific cardList (index) that needs to be used
     * @param playerLoopI : The specific player (index) that needs to be created
     * @return boolean : the first card of the player is his blind card.
     * @author Jordi
     */
    private boolean isBlindCard(int cardLoop, int playerLoopI) {
        System.out.println("ID ===== " + id);
        System.out.println("playerLoopI ===== " + playerLoopI);
        return (cardLoop == 0) && (playerLoopI != id-1);
    }

    /**
     * Remove the card for the CommandCard that removes a players card
     *
     * @see #drawIndividualCard(List, int, int)
     *
     * @param cardIndex : card index
     * @param player : the player that needs their card removed
     * @throws RemoteException : RMI connection error.
     * @author Yme
     */
    public void removeCardView(int cardIndex, Player player) throws RemoteException {
        System.out.println("PLAYER IN REMOVECARDVIEW  ------>    " + player.getName());
        System.out.println("CARDINDEX IN REMOVECARDVIEW  ------>    " + cardIndex);
        int playerIndex = 0;
        //breaks once found on which index the player exists out of all the players in the game
        for (int i = 0; i < getPlayers().size(); i++) {
            if (getPlayers().get(i).getName().equals(player.getName())) {
                playerIndex = i;
                break;
            }
        }
        //find the stackpane that we need to remove
        StackPane cardStack = (StackPane) stackPaneList.get(playerIndex).getChildren().get(cardIndex);
        //move all of the stackpanes that are to the right of the removed stackpane 110 pixels to the left.
        //This is needed to make sure that the handview looks natural after removal of the card
        for (int i = cardIndex; i < cardStack.getParent().getChildrenUnmodifiable().size(); i++) {
            System.out.println("RUNNAN THROUGH LOOP");
            System.out.println("cardStack.getChildren.size ===== " + cardStack.getParent().getChildrenUnmodifiable().size());
            cardStack.getParent().getChildrenUnmodifiable().get(i).setTranslateX(cardStack.getParent().getChildrenUnmodifiable().get(i).getTranslateX() - 110);
        }
        //Remove the stackpane that holds the card that was chosen for removal
        (((StackPane) cardStack.getParent()).getChildren()).remove(cardIndex);

    }

    /**
     * onHover Effect for the cards drawn in the handview
     *
     * @see #drawIndividualCard(List, int, int)
     *
     * @param img : The Node that needs to be translated
     * @param translateA : the amount to move the card for the onHover
     * @author Yme
     */
    private void onHover(Node img, int translateA) {
        StackPane imgPane = (StackPane) img.getParent();
        StackPane stackList = (StackPane) imgPane.getParent();
        int stackListSize = stackList.getChildren().size();
        if (imgPane.equals(stackList.getChildren().get(stackListSize-2)) && placedCardWrong) {
            placedCardWrong = false;
            System.out.println("[!] RETURNING ONHOVER");
            return;
        }
        int index = 0;
        //find the stackpane that holds the card
        StackPane stackCard = (StackPane) img.getParent();
        StackPane playerStack = (StackPane) stackCard.getParent();
        //find the index that holds the card and then break
        for (int i = 0; i < playerStack.getChildren().size(); i++) {
            if (playerStack.getChildren().get(i) == stackCard) {
                index = i + 1;
                break;
            }
        }
        //move everything that is to the right of this to right or left by 'translateA' pixels
        for (int i = index; i < playerStack.getChildren().size(); i++) {
            playerStack.getChildren().get(i).setTranslateX(playerStack.getChildren().get(i).getTranslateX() + translateA);
        }

    }


    /**
     * updates the handCard view by getting the old amount of cards shown vs the new amount.
     * Starts the loop to draw from old amount so it will only draws the new cards.
     *
     * @see FXView#showHand()
     * @see #drawIndividualCard(List, int, int)
     *
     * @param player : The list of players that need to be updated
     * @throws RemoteException : RMI connection error
     * @author Yme
     */
    private void updateCardsHandView(List<Player> player) throws RemoteException {
        //Get the commandMode triggered by card click in case it is not updated yet
        clickController = remoteGameController.getClickController();
        commandMode = clickController.getCommandMode();
        System.out.println("[+] updateCardsHandView is called");
        //For each player draw cards
        for (int i = 0; i < player.size(); i++) {
            //get amount of cards already displayed for that player
            int oldAmountJ = saveCardList.get(i);
            System.out.println("[+] player: "+ i + " Size oldamount: " + cards.size());
            //updateCardList
            updateCardList(player, i);
            System.out.println("[+] speler: "+ i + " Size new Amount: " + cards.size());
            //Get number of cards that the player has right now
            int newAmount = player.get(i).getCards().size();
            //compare oldAmount and newAmount so we can check if a new card has to be drawn
            for (int oldAmount = oldAmountJ; oldAmount < newAmount; oldAmount++) {
                drawIndividualCard(cards, oldAmount, i);
            }
            //update the amount of cards this player has so it can be the 'oldAmount' the next time.
            saveCardList.set(i, player.get(i).getCards().size());
        }
    }

    /**
     * Gets the players from the GameController (gameAction)
     *
     * @see GameAction
     *
     * @return : returns the List of players in the game
     * @throws RemoteException : RMI connection error.
     * @author Yme
     */
    public List<Player> getPlayers() throws RemoteException {
        if (remoteGameState.getPlayers() != null) {
            return remoteGameState.getPlayers();
        }
        return remoteGameController.getGameState().getPlayers();
    }

    /**
     * Sets the GameAction for handling clicks
     *
     * @see GameAction
     * @param remoteGameController : GamaAction (stub for GameController)
     * @author Yme
     */
    public void setGameaction(GameAction remoteGameController) {
        this.remoteGameController = remoteGameController;
    }

    /**
     * Sets the GameAction for handling clicks
     *
     * @see #updateCardsHandView(List)
     *
     * @param players : list of players that need to be updated
     * @throws RemoteException :  RMI connection error.
     * @author Wesley
     */
    @Override
    public void onCardPick(List<Player> players) throws RemoteException {
        Platform.runLater( () -> {
            try {
                updateCardsHandView(players);
            } catch (RemoteException e) {
                //
            }
        });
    }

    /**
     * OnGameEnd detection comes from the observer
     *
     * @see GamestateObserver#onGameEnd(List)
     *
     * @param player : list of players that need to be changed to GameEnd
     * @throws RemoteException :  RMI connection error.
     * @author Wesley
     */
    @Override
    public void onGameEnd(List<Player> player) throws RemoteException {

    }

    @Override
    public void onCardRemove(int index, Player player) throws RemoteException {
        // remove card
        Platform.runLater( () -> {
            try {
                removeCardView(index, player);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the Observer for the HandView
     *
     * @throws RemoteException :  RMI connection error.
     * @throws NotBoundException : GameState stub doesn't exist.
     * @author Wesley
     */
    public  void setObservable() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(GameConfig.getHostName()); // if server on another machine: provide that machine's IP address. Default port  1099
        remoteGameState = (RemoteGameState) registry.lookup("GameState"); // get remote Calculator object from registry
        remoteGameState.addObserver(this);
    }

}

