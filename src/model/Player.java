package model;

import controler.GameEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * A Player object represents a single player in the game.
 */
public class Player {

    private List<Card> hand;

    /**
     * unique id for player
     */
    private final int playerID;


    public Player(int playerID, List<Card> hand) {
        this.playerID = playerID;
        this.hand = hand;
    }

    /**
     * pop a card from hand. It should be used in GameEngine.playerPlayCard.
     * @param handArrayIndex index of chosen card
     */
    public Card popCardFromHand(int handArrayIndex) {
        return hand.remove(handArrayIndex);
    }

    public List<Card> getHand() {
        return hand;
    }

    /**
     * used mainly for double swap(swap playes' hand)
     * @param hand players' hand
     */
    public void setHand(List<Card> hand) {
        this.hand = hand;
    }
}
