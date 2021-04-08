import controler.DeckController;
import controler.GameEngine;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class UnoUnitTest {
    /**
     * check if 2 players work
     */
    private final int PLAYERS_COUNT = 3;
    private static GameEngine gameEngine;

    /**
     * set up game engine before each test
     */
    @BeforeEach
    private void setUp() {
        boolean isTest = true;
        gameEngine = new GameEngine(PLAYERS_COUNT);
        gameEngine.initializeAll();
    }

    /**
     * test for card class
     */
    @Test
    public void checkCardClass() {
        Card card = new Card(Card.Color.NONE, Card.CardType.FIVE);
        Card.Color color = card.getColor();
        assertEquals(color, Card.Color.NONE);
        Card.CardType cardType = card.getType();
        assertEquals(cardType, Card.CardType.FIVE);
        Card specialCard = new Card(Card.Color.NONE, Card.CardType.WILD);
        assertEquals(false, specialCard.isNumberCard());
    }

    /**
     * test for player class
     */
    @Test
    public void checkPlayer() {
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player currentPlayer = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> hand = currentPlayer.getHand();
        Card firstCard = hand.get(0);
        assertEquals(firstCard,currentPlayer.popCardFromHand(0));
    }
    /**
     * test for deck class.
     * check number of initial draw deck
     */
    @Test
    public void checkDeckClass() {
        Deck deck = new Deck();
        deck.initializeDrawPile();
        assertEquals(Deck.NUMBER_OF_TOTAL_CARDS, deck.getDrawPile().size());
    }

    /**
     * check deck controller class
     */
    @Test
    public void checkDeckController() {
        Deck deck = gameEngine.getDeck();
        List<Card> drawPile = deck.getDrawPile();
        List<Card> discardPile = deck.getDiscardPile();
        int originalDrawPileSize = drawPile.size();
        for (int i = originalDrawPileSize - 1; i >= 0; --i) {
            discardPile.add(drawPile.remove(i));
        }
        assertEquals(0, drawPile.size());
        DeckController deckController = gameEngine.getDeckController();
        deckController.dumpCardFromDiscardToDraw();
        assertEquals(originalDrawPileSize, drawPile.size());
    }
    /**
     * check the start stage of game.
     * Each player have 7 cards and discard pile has one card.
     */
    @Test
    public void checkInitialization() {
//        Logger log = Logger.getLogger(UnoUnitTest.class.getName());
        ArrayList<Player> players = gameEngine.getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            List<Card> hand = players.get(i).getHand();
            assertEquals(7, hand.size());
        }
        assertEquals(108 - PLAYERS_COUNT * 7 - 1, gameEngine.getCardNumberInDrawPile());
        assertEquals(true,gameEngine.getCurrentPlayer()
                == gameEngine.getPlayers().get(gameEngine.getCurrentPlayerID()));
    }

    /**
     * helper method for inserting fake card into discard pile and current player hand
     * @param color color of fake card
     * @param cardType type of fake card
     * @return
     */
    private Card insertFakeCards(Card.Color color, Card.CardType cardType, Card.Color chosenColor) {
        //insert fake card into discard pile
        insertFakeCardsInDiscardPile(color, cardType, chosenColor);

        //insert another fake card into player's hand(same color, same type)
        Card fake = insertFakeCardsInHand(color, cardType, chosenColor);
        return fake;
    }

    private Card insertFakeCardsInDiscardPile(Card.Color color, Card.CardType cardType, Card.Color chosenColor) {
        Deck deck = gameEngine.getDeck();
        DeckController deckController = gameEngine.getDeckController();
//        List<Card> discardPile = deck.getDiscardPile();
        Card additionalCardInDiscardPile = new Card(color, cardType);
        deckController.pushCardIntoDiscardPile(additionalCardInDiscardPile, chosenColor);
        return additionalCardInDiscardPile;
    }

    private Card insertFakeCardsInHand(Card.Color color, Card.CardType cardType, Card.Color chosenColor) {
        //insert another fake card into player's hand(same color, same type)
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        Card fake = new Card(color,cardType);
        currentPlayerHand.add(fake);
        return fake;
    }

    /**
     * check normal card(with number and color)
     */
    @Test
    public void checkNormalCardEffect() {
        Card fake = insertFakeCardsInDiscardPile(Card.Color.GREEN, Card.CardType.FIVE, Card.Color.NONE);
        Card fakeHandCard = insertFakeCardsInDiscardPile(Card.Color.GREEN, Card.CardType.NINE, Card.Color.NONE);
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();

        gameEngine.playerPlayCard(currentPlayerID, fakeHandCard, Card.Color.NONE);
        Card newTopDiscard = deckController.topCardInDiscardPile();
        assertEquals(7, currentPlayerHand.size());
        assertEquals(Card.Color.GREEN, deckController.getCurrentColor());
        assertEquals(Card.CardType.NINE, deckController.getCurrentCardType());
    }

    /**
     * check skip card
     */
    @Test
    public void checkSkip() {
        Card fake = insertFakeCardsInDiscardPile(Card.Color.RED, Card.CardType.FIVE, Card.Color.NONE);
        Card fakeHandCard = insertFakeCardsInHand(Card.Color.RED, Card.CardType.SKIP, Card.Color.NONE);
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();

        gameEngine.playerPlayCard(currentPlayerID, fakeHandCard, Card.Color.NONE);
        assertEquals(Card.Color.RED, deckController.getCurrentColor());
        assertEquals(Card.CardType.SKIP, deckController.getCurrentCardType());
        gameEngine.moveToNextPlayer(null);
        int newCurrntePlayerID = gameEngine.getCurrentPlayerID();
        System.out.println(newCurrntePlayerID);
        System.out.println(currentPlayerID);
        int checknewCurrentPlayerID = currentPlayerID + gameEngine.getPlayerMovingOrder() * 2;
        if (checknewCurrentPlayerID < 0) {
            checknewCurrentPlayerID += gameEngine.getPlayers().size();
        }
        assertEquals(1,gameEngine.getMovingStepSize());
        assertEquals(checknewCurrentPlayerID, newCurrntePlayerID);
    }

    /**
     * check draw two card
     */
    @Test
    public void checkDrawTwo() {
        Card fakeDiscard = insertFakeCardsInDiscardPile(Card.Color.BLUE, Card.CardType.TWO, Card.Color.NONE);
        Card fake = insertFakeCardsInHand(Card.Color.BLUE, Card.CardType.DRAWTWO, Card.Color.NONE);
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();

        gameEngine.playerPlayCard(currentPlayerID, fake, Card.Color.NONE);
        Card newTopDiscard = deckController.topCardInDiscardPile();
        assertEquals(Card.Color.BLUE, deckController.getCurrentColor());
        assertEquals(2,gameEngine.getNumberOfMandatoryDrawingCards());
    }

    /**
     * check wild card
     */
    @Test
    public void checkWild() {
        Card fake = insertFakeCards(Card.Color.NONE, Card.CardType.WILD, Card.Color.BLUE);
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();

        gameEngine.playerPlayCard(currentPlayerID, fake, Card.Color.YELLOW);
        Card newTopDiscard = deckController.topCardInDiscardPile();
        assertEquals(Card.Color.YELLOW, deckController.getCurrentColor());
    }

    /**
     * check draw four card
     */
    @Test
    public void checkDrawFour() {
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();

        //remove match card
        List<Card> hand = player.getHand();
        hand.clear();

        Card fake = insertFakeCardsInHand(Card.Color.NONE, Card.CardType.DRAWFOUR, Card.Color.YELLOW);
        Card fakeDiscard = insertFakeCardsInDiscardPile(Card.Color.YELLOW, Card.CardType.FIVE, Card.Color.NONE);
        assertEquals(true, gameEngine.hasPlayableCard());
        gameEngine.playerPlayCard(currentPlayerID, fake, Card.Color.YELLOW);
        assertEquals(Card.Color.YELLOW, deckController.getCurrentColor());
        assertEquals(4, gameEngine.getNumberOfMandatoryDrawingCards());

        //check bonus rule for drawFour
        Card c = new Card(Card.Color.YELLOW, Card.CardType.FIVE);
        hand.add(c);
        hand.add(new Card(Card.Color.NONE, Card.CardType.DRAWFOUR));
        assertEquals(false, gameEngine.hasPlayableCard());
    }

    /**
     * check reverse card
     */
    @Test
    public void checkReverse() {
        Card fake = insertFakeCards(Card.Color.BLUE, Card.CardType.REVERSE, Card.Color.NONE);
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();
        int originalDirection = gameEngine.getPlayerMovingOrder();
        List<Card> originalHand = player.getHand();
        gameEngine.playerPlayCard(currentPlayerID, fake, Card.Color.NONE);
        Card newTopDiscard = deckController.topCardInDiscardPile();
        assertEquals(Card.Color.BLUE, deckController.getCurrentColor());
        assertEquals(-1 * originalDirection, gameEngine.getPlayerMovingOrder());
        int playerIndexOfcomparedHand = (currentPlayerID + gameEngine.getPlayerMovingOrder())
                % (gameEngine.getPlayers().size());
        if (playerIndexOfcomparedHand < 0) {
            playerIndexOfcomparedHand += gameEngine.getPlayers().size();
        }
        System.out.println(gameEngine.getPlayerMovingOrder());
        List<Card> comparedHand = gameEngine.getPlayers()
                .get(playerIndexOfcomparedHand)
                .getHand();
        assertEquals(true, originalHand.equals(comparedHand));
    }

    /**
     * check drawCard from draw pile
     */
    @Test
    public void checkDrawFromDrarPile() {
        Deck deck = gameEngine.getDeck();
        DeckController deckController = gameEngine.getDeckController();
        List<Card> drawPile = deck.getDrawPile();
        Card testCard = new Card(Card.Color.GREEN, Card.CardType.FIVE);
        drawPile.add(0, testCard);
        Card sameTestCard = deckController.popTopCardFromDrawPile();
        assertEquals(true,testCard == sameTestCard);
    }

    /**
     * check player option:
     *  when hasPlayableCard is true
     *  when hasPlayableCard is false
     *  when using drawAndPlayIfPossible
     */
    @Test
    public void checkOption() {
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        assertEquals(true,gameEngine.checkValidPlayer(currentPlayerID));
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();
        Card.Color currentColor = deckController.getCurrentColor();
        currentPlayerHand.add(new Card(currentColor, Card.CardType.FIVE));
        assertEquals(true, gameEngine.hasPlayableCard());

        for (int i = currentPlayerHand.size() - 1; i >= 0; --i) {
            if (currentPlayerHand.get(i).getColor() == Card.Color.NONE
                    || currentPlayerHand.get(i).getColor() == currentColor
                    || currentPlayerHand.get(i).getType() == deckController.getCurrentCardType()) {
                currentPlayerHand.remove(currentPlayerHand.get(i));
            }
        }
        assertEquals(false, gameEngine.hasPlayableCard());

        Deck deck = gameEngine.getDeck();
        List<Card> drawPile = deck.getDrawPile();
        drawPile.add(0, new Card(currentColor, Card.CardType.ZERO));
        gameEngine.drawAndPlayIfPossible(Card.Color.NONE, null);
        assertEquals(Card.CardType.ZERO, deckController.getCurrentCardType());
        assertEquals(currentColor, deckController.getCurrentColor());

        //with jframe
        drawPile.add(0, new Card(currentColor, Card.CardType.ZERO));
        gameEngine.drawAndPlayIfPossible(Card.Color.NONE, new JFrame());
        assertEquals(Card.CardType.ZERO, deckController.getCurrentCardType());
        assertEquals(currentColor, deckController.getCurrentColor());
    }

    /**
     * check game ends
     */
    @Test
    public void checkEnd() {
        assertEquals(false, gameEngine.isGameEnd());
        Player player = gameEngine.getPlayers().get(gameEngine.getCurrentPlayerID());
        List<Card> hand = player.getHand();
        hand.clear();
        assertEquals(true, gameEngine.isGameEnd());
    }

    /**
     * test same number card with different card will cause hasPlayableCard return true
     */
    @Test
    public void checkSameNumber() {
        Card fake = insertFakeCards(Card.Color.BLUE, Card.CardType.FIVE, Card.Color.NONE);
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        Player player = gameEngine.getPlayers().get(currentPlayerID);
        List<Card> currentPlayerHand = player.getHand();
        DeckController deckController = gameEngine.getDeckController();
        for (int i = currentPlayerHand.size() - 1; i >= 0; --i) {
            if (currentPlayerHand.get(i).getColor() == Card.Color.NONE
                    || currentPlayerHand.get(i).getColor() == deckController.getCurrentColor()
                    || currentPlayerHand.get(i).getType() == deckController.getCurrentCardType()) {
                currentPlayerHand.remove(currentPlayerHand.get(i));
            }
        }
        currentPlayerHand.add(new Card(Card.Color.YELLOW, Card.CardType.FIVE));
        assertEquals(true, gameEngine.hasPlayableCard());
    }

    @Test
    public void checkMoveToNextPlayer1() {
        Card fake = insertFakeCards(Card.Color.BLUE, Card.CardType.FIVE, Card.Color.NONE);
        int nextPlayerID = gameEngine.getCurrentPlayerID() + gameEngine.getPlayerMovingOrder();
        if (nextPlayerID < 0) {
            nextPlayerID += gameEngine.getPlayers().size();
        }
        List<Card> nextPlayerHand = gameEngine.getPlayers().get(nextPlayerID).getHand();
        nextPlayerHand.clear();
        gameEngine.moveToNextPlayer(new JFrame());
        assertEquals(nextPlayerID,gameEngine.getCurrentPlayerID());

    }

    @Test
    public void checkMoveToNextPlayer2() {
        Card fake = insertFakeCards(Card.Color.BLUE, Card.CardType.FIVE, Card.Color.NONE);
        int nextPlayerID = gameEngine.getCurrentPlayerID() + gameEngine.getPlayerMovingOrder();
        if (nextPlayerID < 0) {
            nextPlayerID += gameEngine.getPlayers().size();
        }
        gameEngine.setNumberOfMandatoryDrawingCards(2);
        List<Card> nextPlayerHand = gameEngine.getPlayers().get(nextPlayerID).getHand();
        nextPlayerHand.clear();
        gameEngine.moveToNextPlayer(new JFrame());

    }

    @Test
    public void checkAIPlayerInterface() {
        Card chosenCard = new Card(Card.Color.YELLOW, Card.CardType.FIVE);
        Card.Color chosenColor = Card.Color.NONE;
        AIPlayer.CardAndChosenColor cardAndChosenColor = new AIPlayer.CardAndChosenColor(
                chosenCard, chosenColor);
        assertEquals(true, cardAndChosenColor.getCard() == chosenCard);
        assertEquals(true, chosenColor == cardAndChosenColor.getChosenColor());
    }

    @Test
    public void checkBasicPlayer() {
        BasicPlayer player = (BasicPlayer) gameEngine.getPlayers().get(1);
        player.pickCard(gameEngine);
        player.getHand().clear();
        player.pickCard(gameEngine);
    }

    @Test
    public void checkSuperPlayer() {
        SuperPlayer player = (SuperPlayer) gameEngine.getPlayers().get(2);
        player.pickCard(gameEngine);
        player.getHand().clear();
        player.pickCard(gameEngine);
        player.getHand().add(new Card(Card.Color.NONE, Card.CardType.WILD));
        player.pickCard(gameEngine);
    }


}