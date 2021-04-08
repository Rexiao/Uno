package model;

/**
 * A Card object represents a uno card.
 */
public class Card {

    public enum Color {
        RED, YELLOW, GREEN, BLUE, NONE
    }

    public enum CardType {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP,
        REVERSE, DRAWTWO, WILD, DRAWFOUR;
    }

    private final Color color;

    private final CardType type;

    /**
     * Card only constructor
     */
    public Card(Color color, CardType type) {
        this.color = color;
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public CardType getType() {
        return type;
    }

    /**
     * check if the card has number(0-9) and valid color
     */
    public boolean isNumberCard() {
        if (type == CardType.DRAWFOUR || type == CardType.WILD || type == CardType.DRAWTWO
        || type == CardType.REVERSE || type == CardType.SKIP) {
            return false;
        }
        return true;
    }
}
