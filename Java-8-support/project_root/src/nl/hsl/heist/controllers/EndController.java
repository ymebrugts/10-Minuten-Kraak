package nl.hsl.heist.controllers;

import nl.hsl.heist.models.Card;
import nl.hsl.heist.models.Player;
import nl.hsl.heist.models.Symbol;
import nl.hsl.heist.models.prestige.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * End the game and count points and give the correct prestiges to the players.
 *
 * @author Jordi, Joorden
 */
public class EndController implements Serializable {

    /**
     * Function to be called from outside, starts the entire counting and sorting of the finished list.
     *
     * @param finished List of all finished players in the game.
     * @param prestiges List all prestiges in the game.
     * @author Jordi
     */
    public void endIt(List<Player> finished, List<Prestige> prestiges) {

        if (finished.size() == 1) {
            System.out.println("[+] only 1 player playing - ending");
            return;
        }

        cardCount(finished);
        System.out.println("[+] got through cardcount");
        handoutPrestige(finished, prestiges);
        System.out.println("[+] got through handing out prestiges");
        calcAndSortByPrestigePoints(finished);
        System.out.println("[+] got through calcing and sorting");
        for (int i = 0; i < finished.size(); i++) {
            System.out.println("player: " + finished.get(i).getName() + " is number " + (i+1) + " met " + finished.get(i).getPrestigePoints() + " punten!");
            System.out.println("player had prestiges: ");
            for (Prestige p : finished.get(i).getPrestiges())
                System.out.println("\t" + p.getClass().getName());
        }
    }

    /**
     * Hands out the prestiges together with checkHandout
     * @see #checkHandout(List, Symbol, Prestige)
     *
     * @param finished List of players who finished playing.
     * @param prestiges List of all prestiges in the game.
     * @author Jordi
     */
    private void handoutPrestige(List<Player> finished, List<Prestige> prestiges) {

        List<Player> tomeWinners = new ArrayList<Player>();
        List<Player> jewelWinners = new ArrayList<Player>();
        List<Player> artifactWinners = new ArrayList<Player>();
        List<Player> potionWinners = new ArrayList<Player>();
        List<Player> fossilWinners = new ArrayList<Player>();
        List<Player> threeWinners = new ArrayList<Player>();
        List<Player> fourWinners = new ArrayList<Player>();
        List<Player> fiveWinners = new ArrayList<Player>();
        List<Player> curseHolders = new ArrayList<Player>();

        for (Player finishedPlayer : finished) {
            tomeWinners.add(finishedPlayer);
            jewelWinners.add(finishedPlayer);
            artifactWinners.add(finishedPlayer);
            potionWinners.add(finishedPlayer);
            fossilWinners.add(finishedPlayer);
            threeWinners.add(finishedPlayer);
            fourWinners.add(finishedPlayer);
            fiveWinners.add(finishedPlayer);
            curseHolders.add(finishedPlayer);
        }

        System.out.println("[+] got through adding players");

        sortWinners(tomeWinners, Symbol.TOME);
        sortWinners(jewelWinners, Symbol.JEWEL);
        sortWinners(artifactWinners, Symbol.ARTIFACT);
        sortWinners(potionWinners, Symbol.POTION);
        sortWinners(fossilWinners, Symbol.FOSSIL);
        sortWinners(threeWinners, Symbol.THREE);
        sortWinners(fourWinners, Symbol.FOUR);
        sortWinners(fiveWinners, Symbol.FIVE);
        sortWinners(curseHolders, Symbol.CURSE);

        System.out.println("[+] got through sorting");

        for (Prestige p : prestiges) {
            if (p instanceof MostTome)
                checkHandout(tomeWinners, Symbol.TOME, p);
            else if (p instanceof MostJewel)
                checkHandout(jewelWinners, Symbol.JEWEL, p);
            else if (p instanceof MostArtifact)
                checkHandout(artifactWinners, Symbol.ARTIFACT, p);
            else if (p instanceof MostPotion)
                checkHandout(potionWinners, Symbol.POTION, p);
            else if (p instanceof MostFossil)
                checkHandout(fossilWinners, Symbol.FOSSIL, p);
            else if (p instanceof MostThree)
                checkHandout(threeWinners, Symbol.THREE, p);
            else if (p instanceof MostFour)
                checkHandout(fourWinners, Symbol.FOUR, p);
            else if (p instanceof MostFive)
                checkHandout(fiveWinners, Symbol.FIVE, p);
            else if (p instanceof MostCurse)
                checkHandout(curseHolders, Symbol.CURSE, p);
            else if (p instanceof LeastCurse)
                checkHandout(curseHolders, Symbol.CURSE, p);
        }
    }

    /**
     * Checks wether the first and second winners are or are not the same.
     * Hands out prestige if they are not the same.
     *
     * @param players List of players of one type.
     * @param symbol Symbol to check the players on.
     * @param prestige Prestige to hand out.
     * @author Jordi
     */
    private void checkHandout(List<Player> players, Symbol symbol, Prestige prestige) {
        int firstPlace = 0;
        int secondPlace = 0;
        if (prestige instanceof LeastCurse) {
            firstPlace = players.get(players.size()-1).getPoints().getSymbol(symbol);
            secondPlace = players.get(players.size()-2).getPoints().getSymbol(symbol);
        } else {
            firstPlace = players.get(0).getPoints().getSymbol(symbol);
            secondPlace = players.get(1).getPoints().getSymbol(symbol);
        }

        if (firstPlace != secondPlace) {
            if (prestige instanceof LeastCurse)
                players.get(players.size()-1).getPrestiges().add(prestige);
            else
                players.get(0).getPrestiges().add(prestige);
        }
    }

    /**
     * Uses the bubblesort algorithm to sort a player list based on the symbol parameter
     *
     * @param finished List of players.
     * @param symbol Symbol to do the sorting on.
     * @author Jordi
     */
    private void sortWinners(List<Player> finished, Symbol symbol) {
        boolean sorting = true;
        while (sorting) {
            sorting = false;
            for (int i = 0; i < finished.size()-1; i++)
                if (finished.get(i).getPoints().getSymbol(symbol) /*left*/
                        < finished.get(i+1).getPoints().getSymbol(symbol) /*right*/) {
                    Player temp = finished.get(i);
                    finished.set(i, finished.get(i+1));
                    finished.set(i+1, temp);
                    temp = null;
                    sorting = true;
                        }
        }
    }

    /**
     * Calculates the prestigepoints of each player by grabbing their prestiges.
     * Bubble sorts the finished list based on that.
     *
     * @param finished List of finished players with prestiges already handed out.
     * @author Jordi
     */
    private void calcAndSortByPrestigePoints(List<Player> finished) {
        for (Player finishedPlayer : finished) {
            int points = 0;
            List<Prestige> playerPrestiges = finishedPlayer.getPrestiges();
            for (Prestige p : playerPrestiges)
                points += p.getPoints();
            finishedPlayer.setPrestigePoints(points + finishedPlayer.getPrestigePoints());
        }

        boolean sorting = true;
        while (sorting) {
            sorting = false;
            for (int i = 0; i < finished.size()-1; i++) {
                if (finished.get(i).getPrestigePoints() /*left*/
                        < finished.get(i+1).getPrestigePoints() /*right*/) {
                    Player temp = finished.get(i);
                    finished.set(i, finished.get(i+1));
                    finished.set(i+1, temp);
                    temp = null;
                    sorting = true;
                        }
            }
        }
    }

    /**
     * Counts the card symbols, numbers and curses of the finished players.
     * checks if the player has less than zero curses, which is not allowed
     * sets it to 0 if that is the case.
     *
     * @param finished List of finished players.
     * @author Jordi, Joorden
     */
    private void cardCount(List<Player> finished) {

        for (Player finishedPlayer : finished) {
            System.out.println("[+] counting cards of player: "+ finishedPlayer);
            List<Card> playerCards = finishedPlayer.getCards();
            System.out.println("[+] the playes has cardnr: " + playerCards.size());
            if (!playerCards.isEmpty()) {
                for (Card playerCard : playerCards) {
                    symbolCount(playerCard, finishedPlayer);
                    numberCount(playerCard, finishedPlayer);
                    curseCount(playerCard, finishedPlayer);
                    if (finishedPlayer.getPoints().getSymbol(Symbol.CURSE) < 0)
                        finishedPlayer.getPoints().zeroCurse();
                }
            }
        }
    }

    /**
     * Counts the symbols of the card and adds them to the player's points.
     *
     * @param playerCard Card to count the symbols from.
     * @param finishedPlayer Player to add those points to.
     * @author Joorden
     */
    private void symbolCount(Card playerCard, Player finishedPlayer) {
        List<Symbol> cardSymbols = playerCard.getSymbols();

        for (Symbol cardSymbol : cardSymbols) {
            switch (cardSymbol) {
                case POTION:
                    finishedPlayer.getPoints().addToPotion(playerCard.getPoints().get(0));
                    break;
                case FOSSIL:
                    finishedPlayer.getPoints().addToFossil(playerCard.getPoints().get(0));
                    break;
                case ARTIFACT:
                    finishedPlayer.getPoints().addToArtifact(playerCard.getPoints().get(0));
                    break;
                case TOME:
                    finishedPlayer.getPoints().addToTome(playerCard.getPoints().get(0));
                    break;
                case JEWEL:
                    finishedPlayer.getPoints().addToJewel(playerCard.getPoints().get(0));
                    break;
                case WILD:
                    finishedPlayer.getPoints().addToAll(playerCard.getPoints().get(0));
                    break;
                default:
                    System.out.println("[!] found unkown symbol");
                    break;
            }
        }
    }


    /**
     * Counts the numbers of the card and adds them to the player's points.
     *
     * @param playerCard Card to count the numbers from.
     * @param finishedPlayer Player to add those points to.
     * @author Joorden
     */
    private void numberCount(Card playerCard, Player finishedPlayer){
        List<Integer> cardPoints = playerCard.getPoints();
        for (Integer cardSymbol : cardPoints) {
            switch (cardSymbol) {
                case 3:
                    finishedPlayer.getPoints().addToThree(1);
                    break;
                case 4:
                    finishedPlayer.getPoints().addToFour(1);
                    break;
                case 5:
                    finishedPlayer.getPoints().addToFive(1);
                    break;
                default:
                    System.out.println("[+] found non 3-4-5 number on card");
                    break;
            }
        }
    }

    /**
     * Counts the curses of the card and adds them to the player's points.
     *
     * @param playerCard Card to count the curses from.
     * @param finishedPlayer Player to add those points to.
     * @author Joorden
     */
    private void curseCount(Card playerCard, Player finishedPlayer){
        finishedPlayer.getPoints().addToCurse(playerCard.getCurses());
    }
}
