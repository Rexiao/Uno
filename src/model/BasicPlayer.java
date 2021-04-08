package model;

import controler.GameEngine;

import java.util.*;

/**
 * AI player who plays random valid cards.
 */
public class BasicPlayer extends Player implements AIPlayer {

    private Random random;
    public BasicPlayer(int playerID, List<Card> hand) {
        super(playerID, hand);
        random = new Random();

    }

    /**
     * choose a random card based on game engine
     */
    public AIPlayer.CardAndChosenColor pickCard(GameEngine gameEngine) {
        List<Card> availableCards = new LinkedList<>();
        for (Card card : getHand()) {
            if (gameEngine.checkValidCard(card)) {
                availableCards.add(card);
            }
        }
        if (availableCards.size() == 0) {
            return null;
        }
        int idx = random.nextInt(availableCards.size());
        Card returnCard = availableCards.get(idx);
        Card.Color returnColor = Card.Color.NONE;
        if (returnCard.getType() == Card.CardType.WILD || returnCard.getType() == Card.CardType.DRAWFOUR) {
            returnColor = Card.Color.values()[random.nextInt(4)];
        }
        return new CardAndChosenColor(returnCard,returnColor);
    }
}
