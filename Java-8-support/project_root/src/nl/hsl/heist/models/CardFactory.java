package nl.hsl.heist.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * init cards and add them to one list.
 *
 * @author Wesley, Jordi
 */
public class CardFactory {

    /**
     * Create cards by Looping through a JSON list.
     *
     * Create a card object, with the necessary information
     * Add it to the list, and return this list when done.
     * initCards() in Game.java
     *
     * @param resourcePath : Path to required JSON file.
     * @return List
     * @author Wesley, Jordi
     */
    public List<Card> initCards(InputStream resourcePath) {
        System.out.println("[+] init cards...");
        List<Card> cards = new ArrayList<Card>();

        String data = "";
        try {
            data = FileReader.readFile(resourcePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("[+] card can not be read");
        }
        System.out.println(data);

        JSONArray dataArray = new JSONArray(data);
        JSONObject singleChunk = null;

        for (int i = 0; i < dataArray.length(); i++) {
            singleChunk = new JSONObject(dataArray.get(i).toString());

            String cardName = singleChunk.getString("name");
            String cardSoundPath = singleChunk.getString("soundpath");
            String cardImage = singleChunk.getString("image");
            int cardCurses = singleChunk.getInt("curses");

            Card.Moon cardMoon = null;
            for (Card.Moon m : Card.Moon.values())
                if (m.name().toLowerCase().equals(singleChunk.getString("moon"))) {
                    cardMoon = m;
                    break;
                }

            List<Integer> cardPoints = new ArrayList<Integer>();
            List<Symbol> cardSymbols = new ArrayList<Symbol>();

            JSONArray innerList;

            innerList = singleChunk.getJSONArray("points");
            for (int j = 0; j < innerList.length(); j++) {
                cardPoints.add(Integer.parseInt(innerList.get(j).toString()));
            }

            innerList = singleChunk.getJSONArray("symbols");
            String symName = "";
            for (int j = 0; j < innerList.length(); j++) {
                symName = innerList.get(j).toString();
                for (Symbol s : Symbol.values())
                    if (s.name().toLowerCase().equals(symName)) {
                        cardSymbols.add(s);
                        break;
                    }
            }

            Command cardCommand = null;
            for (Command c : Command.values()) {
                if (c.name().toLowerCase().equals(singleChunk.getString("command"))) {
                    cardCommand = c;
                    break;
                }
            }
            cards.add(new Card(cardName, cardSoundPath, cardCurses, cardMoon, cardPoints, cardSymbols, cardImage, cardCommand));
        }
        return cards;
    }
}
