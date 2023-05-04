package nl.hsl.heist.models;

import nl.hsl.heist.models.avatar.Avatar;
import nl.hsl.heist.models.prestige.Prestige;
import nl.hsl.heist.observers.PlayerObserver;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
/**
 * Player that belongs to a game.
 *
 * @author Wesley, Yme
 */
public class Player implements Serializable {

    private static final long serialVersionUID = -6762055625952328348L;

    private String name;
    private InetAddress ip;
    private Avatar avatar;
    private List<Card> cards; /* Rest of the open cards */
    private PlayerState playerState; /* is NOTSTARTED, PLAYING or FINISHED */
    private ArrayList<PlayerObserver> observers = new ArrayList<PlayerObserver>();
    private Points points;
    private List<Prestige> prestiges;
    private int prestigePoints;


    public Player(String name) {
        this.name = name;
        this.cards = new ArrayList<Card>();
        this.points = new Points();
        this.prestiges = new ArrayList<Prestige>();
        this.prestigePoints = 0;

    }

    /**
     * Add a TileObserver
     * @param observer : PlayerObserver
     * @author Wesley
     */
    public void addObsever(PlayerObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all the Observers (Only GameState, in this case) that a player has changed.
     *
     */
    private void notifyObservers() {
        for (PlayerObserver observer : observers) {
            try {
                observer.onChange();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notifies all the Observers (Only GameState, in this case) that a player has changed.
     *
     * @param cardIndex : index of the card that a player has
     */
    private synchronized void notifyObserversCardRemove(int cardIndex) {
        for (PlayerObserver observer : observers) {
            try {
                observer.onCardRemove(cardIndex, this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public List<Prestige> getPrestiges() {
        return prestiges;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card) {
        cards.add(card);
        notifyObservers();
    }

    public void notifyClick() {
        notifyObservers();
    }

    public void removeCard(Card card) {
        System.out.println("yo called");
        int index = 0;
        for (int i = 0; i < this.getCards().size() ; i++) {
            if (card.getName().equals(this.getCards().get(i).getName())) {
                card = this.getCards().get(i);
                index = i;
                break;
            }
        }
        cards.remove(card);
        notifyObserversCardRemove(index);
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        notifyObservers();
    }
}
