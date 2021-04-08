package model;

import controler.GameEngine;

/**
 * interface for AI players.
 */
public interface AIPlayer {
    /**
     * return a card and a chosen color(if the card is wild/draw four card) based on deck status
     * @param gameEngine container for deck status
     * @return a card and a chosen color
     */
    CardAndChosenColor pickCard(GameEngine gameEngine);

    /**
     * return object class
     */
    class CardAndChosenColor {
        Card card;
        Card.Color chosenColor;

        public CardAndChosenColor(Card card, Card.Color chosenColor) {
            this.card = card;
            this.chosenColor = chosenColor;
        }

        public Card getCard() {
            return card;
        }

        public Card.Color getChosenColor() {
            return chosenColor;
        }
    }
}
