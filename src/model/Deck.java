package model;

import java.util.LinkedList;
import java.util.List;

/**
 * A Deck object represents card piles of the game.
 */
public class Deck {

    private final List<Card> drawPile;

    private final List<Card> discardPile;

    public static final int NUMBER_OF_TOTAL_CARDS = 108;

    public static final int NUMBER_OF_WILD_CARDS = 4;
    /**
     * fixed number of most type cards with same color except ZERO.
     */
    public static final int NUMBER_OF_MOST_CARDS = 2;

    public static final int NUMBER_OF_ZERO_CARDS = 1;

    /**
     * deck constructor.
     * Notice we need to use initializeDrawPile to actually initialize the draw pile.
     */
    public Deck() {
        drawPile = new LinkedList<>();
        discardPile = new LinkedList<>();
    }

    /**
     * initialize the draw pile and discard pile\n
     * this function does not shuffle or put one card into discard pile
     */
    public void initializeDrawPile() {

        //push four Drawfour and Wild card into draw pile
        pushNNewCards(NUMBER_OF_WILD_CARDS, Card.Color.NONE, Card.CardType.WILD, drawPile);
        pushNNewCards(NUMBER_OF_WILD_CARDS, Card.Color.NONE, Card.CardType.DRAWFOUR, drawPile);
        // push other cards
        for (Card.Color color : Card.Color.values()) {
            //we have already created all cards with no color
            if (color != Card.Color.NONE) {
                for (Card.CardType cardType : Card.CardType.values()) {
                    // we have already created all wild/drawfour cards
                    if (cardType == Card.CardType.WILD || cardType == Card.CardType.DRAWFOUR) {
                        continue;
                    }

                    int numberOfPushedCards = NUMBER_OF_MOST_CARDS;
                    // only two zero cards should exist
                    if (cardType == Card.CardType.ZERO) {
                        numberOfPushedCards = NUMBER_OF_ZERO_CARDS;
                    }
                    pushNNewCards(numberOfPushedCards, color, cardType, drawPile);
                }
            }
        }

    }

    /**
     * push specific number of new cards(used in initializeDrawPile) into the pile
     * @param numberOfPushedCards number of cards created
     * @param color card color
     * @param cardType card type
     * @param pile destination pile
     */
    private void pushNNewCards(int numberOfPushedCards, Card.Color color, Card.CardType cardType, List<Card> pile) {
        for (int i = 0; i < numberOfPushedCards; ++i) {
            Card card = new Card(color, cardType);
            pile.add(card);
        }
    }

    public List<Card> getDrawPile() {
        return drawPile;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public int drawPileSize() {
        return drawPile.size();
    }


}
