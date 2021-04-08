package controler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import model.*;

import javax.swing.*;

/**
 * GameEngine is the core class which sets up all components of the game
 */
public class GameEngine {
    /**
     * initial number of cards for each player
     */
    private final int INITIAL_CARDS_NUMBER = 7;

    private final ArrayList<Player> players;

    private static Random random;

    private int currentPlayerID;

    /**
     * order of player array.
     * 1 means moving forward
     * -1 means moving backward
     */
    private int playerMovingOrder;

    /**
     * step size of moving to next player
     * The default value will be 1.
     */
    private int movingStepSize;

    private int numberOfMandatoryDrawingCards;

    private final Deck deck;

    private final DeckController deckController;

    /**
     * constructor for GameEngine.\n
     * It initializes variables related to game state.
     * @param numberOfPlayers number of players
     */
    public GameEngine(int numberOfPlayers) {
        //push 108 cards into draw pile
        this.deck = new Deck();
        this.deckController = new DeckController(deck);
        //initialize random generator
        random = new Random();

        //initialize all players
        this.players = new ArrayList<Player>();
        for (int i = 0; i < numberOfPlayers; ++i) {
            if (i == numberOfPlayers - 2) {
                players.add(new BasicPlayer(i, new LinkedList<Card>()));
            } else if (i == numberOfPlayers - 1) {
                players.add(new SuperPlayer(i, new LinkedList<Card>()));
            }else {
                players.add(new Player(i, new LinkedList<Card>()));
            }
        }

        //initialize variables
        currentPlayerID = 0;
        movingStepSize = 1;
        numberOfMandatoryDrawingCards = 0;
        playerMovingOrder = random.nextBoolean() ? 1 : -1;

    }

    /**
     * initialized all related fields of game engine.\n
     * It is separated from constructor since this function contains instance method
     */
    public void initializeAll() {
        deck.initializeDrawPile();
        //shuffle draw pile
        deckController.shuffleDrawPile();
        //push one random card into discard pile
        List<Card> drawPile = deck.getDrawPile();
        Card removedCard = null;
        do {
            if (removedCard != null) {
                drawPile.add(removedCard);
            }
            removedCard = deckController.popTopCardFromDrawPile();
        } while (removedCard.getColor() == Card.Color.NONE || !removedCard.isNumberCard());
        deckController.pushCardIntoDiscardPile(removedCard, Card.Color.NONE);

        //send seven cards to each player
        initializePlayersHand();


    }

    /**
     * send cards to all players
     */
    private void initializePlayersHand() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_CARDS_NUMBER; ++i) {
                Card card = deckController.popTopCardFromDrawPile();
                sendCardToPlayer(card, player);
            }
        }
    }
    
    private void sendCardToPlayer(Card card, Player destinationPlayer) {
        List<Card> hand = destinationPlayer.getHand();
        hand.add(card);
    }

    /**
     * current player play card.\n
     * It will check playerID match currentPlayer when GUI is ready by using checkValidPlayer.\n
     * You should check if the card is valid to play before using this method.\n
     * @param playerID player who plays the card
     * @param card card played by player
     * @param chosenColor for wild card only. Others will be None.
     */
    public void playerPlayCard(int playerID, Card card, Card.Color chosenColor) {
        Player player = players.get(playerID);
        List<Card> hand = player.getHand();

        //card effect
        hand.remove(card);
        Card.Color colorToSend = Card.Color.NONE;
        if (card.getType() == Card.CardType.DRAWFOUR || card.getType() == Card.CardType.WILD) {
            colorToSend = chosenColor;
        }
        //handle variables
        if (card.getType() == Card.CardType.SKIP) {
            movingStepSize = 2;
        } else if (card.getType() == Card.CardType.DRAWFOUR) {
            numberOfMandatoryDrawingCards += 4;
        } else if (card.getType() == Card.CardType.DRAWTWO) {
            numberOfMandatoryDrawingCards += 2;
        } else if (card.getType() == Card.CardType.REVERSE) {
            playerMovingOrder *= -1;
            if (deckController.getCurrentCardType() == Card.CardType.REVERSE) {
                switchHand();
            }
        }
        deckController.pushCardIntoDiscardPile(card, colorToSend); //send card to discard pile
    }

    /*
     * helper method for perform double reverse.
     * It switches each players' hand in current moving order;
     */
    private void switchHand() {
        int numberOfPlayers = players.size();
        ArrayList<List<Card>> handArray = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; ++i) {
            int trueIdx = (i + currentPlayerID) % numberOfPlayers;
            handArray.add(players.get(trueIdx).getHand());
        }
        for (int i = 0; i < numberOfPlayers; ++i) {
            int trueIdxForPlayer = (i + currentPlayerID + playerMovingOrder) % numberOfPlayers;
            if (trueIdxForPlayer < 0) {
                trueIdxForPlayer += numberOfPlayers;
            }
            players.get(trueIdxForPlayer).setHand(handArray.get(i));
        }
    }

    public boolean checkValidPlayer(int playerID) {
        return playerID == currentPlayerID;
    }

    /**
     * check if the card is valid to play
     */
    public boolean checkValidCard(Card card) {
        //custom rule:stacking with black is king(if previous card is Wild/WildFour, return false)
        if (deckController.getCurrentColor() == Card.Color.NONE) {
            return false;
        }
        boolean stackingHasStarted = numberOfMandatoryDrawingCards > 0;
        if (stackingHasStarted) {
            if (card.getType() == Card.CardType.DRAWTWO) {
                //continue stacking
                return true;
            } else {
                //cannot play the card since stacking start and the current card is not DRAWTWO
                return false;
            }
        }
        Card.Color currentColor = deckController.getCurrentColor();
        //special case for wild four
        if (card.getType() == Card.CardType.DRAWFOUR) {
            Player currentPlayer = players.get(currentPlayerID);
            List<Card> hand = currentPlayer.getHand();
            //check if there is matched card
            boolean hasMatched = false;
            for (Card testCard: hand) {
                if (testCard.getColor() == currentColor) {
                    hasMatched = true;
                    break;
                }
            }
            return !hasMatched;
        }

        if (card.getColor() == Card.Color.NONE || (card.getColor() == currentColor)
                || card.getType() == deckController.getCurrentCardType()) {
            return true;
        }
        return false;
    }

    //FIXME:deal with double swap by (if handsize == 1 && hasPlayable() return True) )

    /**
     * check if game finishes.
     */
    public boolean isGameEnd() {
        for (Player player : players) {
            if (player.getHand().size() == 0) {
                return true;
            }
        }
        return false;
    }

    public int getCurrentPlayerID() {
        return currentPlayerID;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }

//    /**
//     * pop the cards from origin pile and push it into destination pile
//     * @param origin origin pile of cards
//     * @param destination destination pile of cards
//     * @param card the card in origin pile
//     */
//    public void insertAndPopCard(ArrayList<Card> origin, ArrayList<Card> destination, Card card) {}

    /**
     * check if current player has playable card
     * @return if player can play any card
     */
    public boolean hasPlayableCard(){
        List<Card> hand = players.get(currentPlayerID).getHand();
        for (Card card : hand) {
            if (checkValidCard(card)) {
                return true;
            }
        }
        return false;
    }

    public Card drawOneCardFromDrawPile() {
        List<Card> hand = players.get(currentPlayerID).getHand();
        List<Card> drawPile = deck.getDrawPile();
        Card returnCard = deckController.popTopCardFromDrawPile();
        hand.add(returnCard);
        return returnCard;
    }

    public DeckController getDeckController() {
        return deckController;
    }

    public int getMovingStepSize() {
        return movingStepSize;
    }

    public int getNumberOfMandatoryDrawingCards() {
        return numberOfMandatoryDrawingCards;
    }

    public int getPlayerMovingOrder() {
        return playerMovingOrder;
    }

    /**
     * return number of remaining cards in draw pile
     */
    public int getCardNumberInDrawPile() {
        return deck.getDrawPile().size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerID);
    }

    public void setNumberOfMandatoryDrawingCards(int num) {
        numberOfMandatoryDrawingCards = num;
    }
    /**
     * draw a card and play it if possible
     */
    public void drawAndPlayIfPossible(Card.Color chosenColor, JFrame jFrame) {
        Card newCard = drawOneCardFromDrawPile();
        if (checkValidCard(newCard)) {
            if (jFrame != null) {
                JOptionPane.showMessageDialog(jFrame, "Player " + Integer.toString(currentPlayerID)
                        + " plays " + newCard.getType().toString() + " " + newCard.getColor().toString());
            }
            playerPlayCard(currentPlayerID, newCard, chosenColor);
        }
    }

    /**
     * It is used when a player finished his or her turn.
     * @param jFrame if jFrame is not null, it will show message in that frame.
     */
    public void moveToNextPlayer(JFrame jFrame) {
        //update currentPlayerID and moving step size
        currentPlayerID = getUpdatedPlayerIdx(movingStepSize);
        movingStepSize = 1;
        //check current player
        if (!hasPlayableCard()) {
            if (numberOfMandatoryDrawingCards != 0) {
                if (jFrame != null) {
                    JOptionPane.showMessageDialog(jFrame, "Player " + Integer.toString(currentPlayerID) + " has to draw "
                            + Integer.toString(numberOfMandatoryDrawingCards) + " cards");
                }
                //perform drawing cards
                for (;numberOfMandatoryDrawingCards > 0; --numberOfMandatoryDrawingCards) {
                    Card card = deckController.popTopCardFromDrawPile();
                    getCurrentPlayer().getHand().add(card);
                }
                numberOfMandatoryDrawingCards = 0;
                currentPlayerID = getUpdatedPlayerIdx(movingStepSize);
            } else {
                //if current player is not AI player, show message
                if (!(getCurrentPlayer() instanceof AIPlayer) && jFrame != null) {
                    JOptionPane.showMessageDialog(jFrame, "You have no valid card to play. Please use the draw button");
                }
            }
        }

    }

    /**
     * calculate updated player id based on current playerID and stepsize.
     */
    public int getUpdatedPlayerIdx(int stepSize) {
        int res = (currentPlayerID + playerMovingOrder * stepSize) % players.size();
        if (res < 0) {
            res += players.size();
        }
        return res;
    }

}
