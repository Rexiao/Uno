package controler;

import model.Card;
import model.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DeckController is a class to hold all methods related to modification to Deck class.
 */
public class DeckController {

    private Deck deck;

    private List<Card> drawPile;

    private List<Card> discardPile;

    /**
     * current color for the pile\n
     * It exists because when the top card in discard pile is wild/wild draw four card,\n
     * we need some way to keep track of current color
     */
    private Card.Color currentColor;

//    private Card.CardType currentCardType;


    public DeckController(Deck deck) {
        this.deck = deck;
        this.drawPile = deck.getDrawPile();
        this.discardPile = deck.getDiscardPile();
    }

    /**
     * shuffke the draw pile.
     */
    public void shuffleDrawPile() {
        Collections.shuffle(drawPile);
    }

    /**
     * grab all cards from discard pile to draw pile and reshuffle.\n
     * You should use this function only if draw pile is empty.\n
     * In other words, it won't check if draw pile is empty.
     */
    public void dumpCardFromDiscardToDraw() {
        int discardPileSize = discardPile.size();
        while (discardPileSize > 1) {
            Card card = discardPile.remove(discardPileSize - 1);
            drawPile.add(card);
            --discardPileSize;
        }
        shuffleDrawPile();
    }

    /**
     * push a removed card into discard pile
     * @param card removed card
     * @param chosenColor if the card is wild, we need to specify the chosen color
     */
    public void pushCardIntoDiscardPile(Card card, Card.Color chosenColor) {
        discardPile.add(0,card);

        //update color
        if (chosenColor == Card.Color.NONE) {
            currentColor = card.getColor();
        } else {
            currentColor = chosenColor;
        }
    }

    /**
     * pop top card from draw pile
     * @return the removed card
     */
    public Card popTopCardFromDrawPile() {
        if (drawPile.size() == 0) {
            dumpCardFromDiscardToDraw();
        }
        return drawPile.remove(0);
    }


    public Card topCardInDiscardPile(){
        return discardPile.get(0);
    }

    public Card.Color getCurrentColor() {
        return currentColor;
    }

    public Card.CardType getCurrentCardType() {
        return topCardInDiscardPile().getType();
    }
}
