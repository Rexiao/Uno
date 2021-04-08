package model;

import controler.GameEngine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AI player who plays card strategically。
 */
public class SuperPlayer extends Player implements AIPlayer{

    public SuperPlayer(int playerID, List<Card> hand) {
        super(playerID, hand);
    }

    /*
     * get available cards and put them in a map base on their card
     */
    private Map<Card.Color, List<Card>> getAvailableMap(List<Card> availableCardList) {
        Map<Card.Color, List<Card>> colorListMap = new HashMap<>();
        //initialize map
        for (Card card : availableCardList) {
            List<Card> val = colorListMap.get(card.getColor());
            if (val == null) {
                val = new LinkedList<>();
                colorListMap.put(card.getColor(), val);
            }
            val.add(card);
        }
        return colorListMap;
    }

    /*
     * get list with most number of cards in available card map
     */
    private List<Card> getListWithMostCards(Map<Card.Color, List<Card>> colorListMap) {
        List<Card> chosenArray = null;
        int length = 0;
        for (Map.Entry<Card.Color, List<Card>> entry : colorListMap.entrySet()) {
            List<Card> temp = entry.getValue();
            if (temp.size() > length)  {
                chosenArray = temp;
                length = temp.size();
            }
        }
        return chosenArray;
    }

    /*
     * get all playable cards
     */
    private List<Card> getAvailableCardList(GameEngine gameEngine) {
        List<Card> availableCardList = new LinkedList<>();
        for (Card card : getHand()) {
            if (gameEngine.checkValidCard(card)) {
                availableCardList.add(card);
            }
        }
        return availableCardList;
    }
    /**
     * choose a card based on game engine and analysis. It will choose cards with most frequent color.
     */
    public CardAndChosenColor pickCard(GameEngine gameEngine) {
        List<Card> availableCardList = getAvailableCardList(gameEngine);
        // no card to play
        if (availableCardList.size() == 0) {
            return null;
        }

        Map<Card.Color, List<Card>> colorListMap = getAvailableMap(availableCardList);
        //get array of cards with most common color
        List<Card> chosenArray = getListWithMostCards(colorListMap);
        Card chosenCard = chosenArray.get(0);
        Card.Color chosenColor = Card.Color.NONE;

        //if chosen is a wild/wild draw four card, decide color
        if (chosenCard.getColor() == Card.Color.NONE) {
            //remove Wild/DrawFour cards and find next common list
            colorListMap.remove(Card.Color.NONE);
            List<Card> coloredList = getListWithMostCards(colorListMap);
            if (coloredList == null) { //player can only play wild/drawFour cards
                chosenColor = Card.Color.RED;
            } else {
                chosenColor = coloredList.get(0).getColor();
            }
        }

        return new CardAndChosenColor(chosenCard,chosenColor);
    }
}
