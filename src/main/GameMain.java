package main;

import controler.GameEngine;
import model.AIPlayer;
import model.Card;
import view.InGameView;
import view.StartView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * main class for the whole game.
 * It is used to start the game, set up models and initializes controllers.
 */
public class GameMain {
    private InGameView inGameView;
    private StartView startView;
    private GameEngine gameEngine;
    private int playerCounts;
    private Card.Color[] colors;
    public GameMain() {
        colors = Arrays.copyOfRange(Card.Color.values(),0,4);
        setStartView();
    }

    /*
     * private method for initializing fields about start view.
     */
    private void setStartView() {
        startView = new StartView();
        startView.addStartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerNumberStr = startView.getPlayerNumber();
                int playerNumber;
                //try to get player's input
                try {
                    playerNumber = Integer.parseInt(playerNumberStr);
                } catch (NumberFormatException error) {
                    JOptionPane.showMessageDialog(startView, "Please enter an valid number");
                    return;
                }
                // if number is too big or too small
                if (playerNumber < 3 || playerNumber > 10) {
                    JOptionPane.showMessageDialog(startView, "Please enter a number between 3 and 10");
                    return;
                }
                playerCounts = playerNumber;
                JOptionPane.showMessageDialog(startView,"go!");
                setInGameView();
            }
        });
        startView.setVisible(true);
    }

    /*
     * set all labels
     */
    private void setLabels() {
        //player labels
        int currentPlayerID = gameEngine.getCurrentPlayerID();
        inGameView.setTextCurrentPlayer(currentPlayerID);

        int prevPlayerID = gameEngine.getUpdatedPlayerIdx(-1);
        inGameView.setTextPrevPlayer(prevPlayerID);

        int nextPlayerID = gameEngine.getUpdatedPlayerIdx(1);
        inGameView.setTextNextPlayer(nextPlayerID);

        //deck status
        inGameView.setTextDrawDeckNumber(gameEngine.getCardNumberInDrawPile());
        String currentColor = gameEngine.getDeckController().getCurrentColor().toString();
        inGameView.setTextCurrentColor(currentColor);
        String type = gameEngine.getDeckController().getCurrentCardType().toString();
        inGameView.setTextCurrentCard(currentColor,type);

        //set cards labels
        setCardLabels();
    }

    /*
     * set JLabels related to cards
     */
    private void setCardLabels() {
        List<Card> hand = gameEngine.getCurrentPlayer().getHand();
        int cardOffet = inGameView.getCardOffset();
        List<String> types = new LinkedList<>();
        List<String> colors = new LinkedList<>();
        for (int i = cardOffet; (i < hand.size()) && (i < cardOffet + 5); ++i) {
            types.add(hand.get(i).getType().toString());
            colors.add(hand.get(i).getColor().toString());
        }
        inGameView.setTextButtonArray(colors,types);
    }

    /*
     * initialize labels and buttons in the InGameView.
     */
    private void setInGameView() {
        startView.dispose();
        inGameView = new InGameView();
        gameEngine = new GameEngine(playerCounts);
        gameEngine.initializeAll();

        //set jlabels in ingameview
        setLabels();
        setCardFunctionalButton();
        setCardButton();
        setDrawButton();
        inGameView.setVisible(true);
        //if first player has no valid card to play
        if (!gameEngine.hasPlayableCard()) {
            JOptionPane.showMessageDialog(inGameView, "You have no valid card to play. Please use the draw button");
        }
    }

    private void setDrawButton() {
        inGameView.addDrawButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if current player has cards to draw
                if (gameEngine.getNumberOfMandatoryDrawingCards() != 0) {
                    JOptionPane.showMessageDialog(inGameView,
                            "You cannot draw card since stacking rule(previous player plays drawTwo/drawFour)");
                    return;
                }

                //get one card
                Card chosenCard = gameEngine.drawOneCardFromDrawPile();
                JOptionPane.showMessageDialog(inGameView, "Draw " + chosenCard.getColor().toString()
                        + " " + chosenCard.getType());
                Card.Color chosenColor;
                //if chosenCard need to choose color
                chosenColor = inGameView.chooseColor(chosenCard,colors);
                if (gameEngine.checkValidCard(chosenCard)) {//if playbable
                    gameEngine.playerPlayCard(gameEngine.getCurrentPlayerID(), chosenCard,chosenColor);
                    int currentPlayerID = gameEngine.getCurrentPlayerID();
                    afterPlayingCard(currentPlayerID,chosenCard);
                } else {
                    afterPlayingCard(gameEngine.getCurrentPlayerID(),null);
                    inGameView.setCardOffset(0);
                    setLabels();
                }
            }
        });
    }

    /*
     * set card buttons' actionListener.
     */
    private void setCardButton() {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton jButton = (JButton) e.getSource();
                int cardIdx = inGameView.getCardButtonIndex(jButton);
                int cardOffset = inGameView.getCardOffset();
                int cardArraySize = gameEngine.getCurrentPlayer().getHand().size();
                if (cardOffset + cardIdx >= cardArraySize) {
                    return;
                }
                Card chosenCard = gameEngine.getCurrentPlayer().getHand().get(cardIdx + cardOffset);

                Card.Color chosenColor;
                if (gameEngine.checkValidCard(chosenCard) == false) { //if card is not playable
                    JOptionPane.showMessageDialog(inGameView, "This card is not playable.\n"
                            + "Please choose another one");
                    return;
                } else { // card is valid to play
                    chosenColor = inGameView.chooseColor(chosenCard,colors);
                }
                int currentPlayerID = gameEngine.getCurrentPlayerID();
                gameEngine.playerPlayCard(currentPlayerID, chosenCard, chosenColor);

                //check if game ends or show message of playing card
                afterPlayingCard(currentPlayerID,chosenCard);
            }
        };
        ArrayList<ActionListener> actionListenerList = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            actionListenerList.add(actionListener);
        }
        inGameView.addCardButtonListListeners(actionListenerList);
    }

    /*
     * it should be used when any player ends his or her turn.
     * It game ends, it will shut down the game.
     * chosenCard is null means that current player only draws a card.
     */
    private void afterPlayingCard(int currentPlayerID, Card chosenCard) {
        if (gameEngine.isGameEnd()) {
            JOptionPane.showMessageDialog(inGameView, "Player " + Integer.toString(currentPlayerID) +" wins!");
            inGameView.dispose();
        } else {
            inGameView.setTextButtonArray(new ArrayList<>(),new ArrayList<>());
            if (chosenCard != null) {
                JOptionPane.showMessageDialog(inGameView, "Player " + Integer.toString(currentPlayerID)
                        +" plays " +  chosenCard.getColor().toString() + " "
                        + chosenCard.getType().toString());
            }
            gameEngine.moveToNextPlayer(inGameView);
            //deal with AI player
            if (gameEngine.getCurrentPlayer() instanceof AIPlayer) {
                AIPlayer aiPlayer = (AIPlayer) gameEngine.getCurrentPlayer();
                AIPlayer.CardAndChosenColor res = aiPlayer.pickCard(gameEngine);
                int AIPlayerID = gameEngine.getCurrentPlayerID();
                if (res == null) {
                    JOptionPane.showMessageDialog(inGameView, "Player " + Integer.toString(AIPlayerID)
                            + " draws a card");
                    gameEngine.drawOneCardFromDrawPile();
                    res = aiPlayer.pickCard(gameEngine);
                }
                if (res != null) {
                    gameEngine.playerPlayCard(AIPlayerID, res.getCard(), res.getChosenColor());
                    afterPlayingCard(AIPlayerID, res.getCard());
                } else {
                    afterPlayingCard(AIPlayerID, null);
                }
            }
            inGameView.setCardOffset(0);
            setLabels();
        }
    }

    private void setCardFunctionalButton() {
        //moving right button
        inGameView.addNextFiveCardsButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cardOffset = inGameView.getCardOffset();
                int cardMax = gameEngine.getCurrentPlayer().getHand().size();
                if ((cardOffset + 5) >= cardMax) {
                    return;
                } else {
                    inGameView.setCardOffset(cardOffset + 5);
                    setCardLabels();
                    inGameView.revalidate();
                }
            }
        });

        //moving left button
        inGameView.addPrevFiveCardsButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cardOffset = inGameView.getCardOffset();
                if (cardOffset == 0) {
                    return;
                } else {
                    inGameView.setCardOffset(cardOffset - 5);
                    setCardLabels();
                    inGameView.revalidate();
                }
            }
        });
    }

    /**
     * start point for the game.
     */
    public static void main(String[] args) {
        GameMain gameMain = new GameMain();
    }
}
