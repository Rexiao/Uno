package view;

import model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * main screen for the game.\n
 * Whan game ends, it will close automatically.
 */
public class InGameView extends JFrame {
    private static final int NUMBER_OF_DISPLAYED_CARDS = 5;
    private static final Dimension cardDimension = new Dimension(200,290);
    private static final Dimension cardPanelDimension = new Dimension(1000, 300);
    private static final Dimension moveFiveCardsButtonDimension = new Dimension(60, 110);
    private static final Dimension currentCardDimension = new Dimension(1000,100);
    private static final Dimension drawDeckLabelDimension = new Dimension(1000,400);
    private static final Dimension drawButtonDimension = new Dimension(10,100);
    private static final Dimension northPanelDimension = new Dimension(1000, 200);
    private static final Dimension playerPanelDimension = new Dimension(300,500);

    private JButton prevFiveCardsButton;
    private JButton nextFiveCardsButton;
    private List<JButton> cardButtonList;
    private JButton drawButton;

    private JPanel cardPanel;
    private JPanel prevPlayerPanel;
    private JPanel nextPlayerPanel;
    private JPanel deckStatusPanel;
    private JPanel northPanel;

    private JLabel prevPlayerLabel;
    private JLabel nextPlayerLabel;
    private JLabel drawDeckLabel;
    private JLabel currentCardLabel;
    private JLabel currentColorLabel;
    private JLabel fixedCurrentCardLabel;
    private JLabel currentPlayerLabel;


    private int cardOffset;
    private int NumberOfCardInHand;

    public InGameView() {
        //set up frame
        setSize(1600,1100);
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeButtions();
        initializeLabels();
        initializePanels();

        //create GridBagConstraints for deckStatusPanel and northPanel
        GridBagConstraints gbc = new GridBagConstraints ();
        gbc.gridy = 1;
        gbc.insets = new Insets (20, 0, 0, 0);

        addToCardPanel();

        prevPlayerPanel.add(prevPlayerLabel);
        nextPlayerPanel.add(nextPlayerLabel);

        deckStatusPanel.add(drawDeckLabel);
        deckStatusPanel.add(drawButton, gbc);

        northPanel.add(currentColorLabel);
        northPanel.add(fixedCurrentCardLabel, gbc);
        gbc.gridy = 2;
        gbc.insets.set(5,0,0,0);
        northPanel.add(currentCardLabel,gbc);

        addPanelsToContentPane();

        //set up cardIndex related variables
        cardOffset = 0;
    }

    /*
     * helper method to initialize buttons
     */
    private void initializeButtions() {
        cardButtonList = new ArrayList<>();
        //create buttons in array
        for (int i = 0; i < NUMBER_OF_DISPLAYED_CARDS; ++i) {
            JButton button = new JButton();
            button.setText("<html>red<br/>five</html>");
            button.setPreferredSize(cardDimension);
            cardButtonList.add(button);
        }

        //set up buttons
        prevFiveCardsButton = new JButton("<");
        prevFiveCardsButton.setPreferredSize(moveFiveCardsButtonDimension);
        nextFiveCardsButton = new JButton(">");
        nextFiveCardsButton.setPreferredSize(moveFiveCardsButtonDimension);
        drawButton = new JButton("Draw");
        drawButton.setHorizontalAlignment(JButton.CENTER);
        drawButton.setPreferredSize(drawButtonDimension);
    }

    /*
     * helper method to set up labels
     */
    private void initializeLabels() {
        prevPlayerLabel = new JLabel();
        prevPlayerLabel.setText("<html>Previous Player:<br/>Player 1" + "</html>");

        nextPlayerLabel = new JLabel();
        nextPlayerLabel.setText("<html>Next Player:<br/>Player 1" + "</html>");

        currentCardLabel = new JLabel();
        currentCardLabel.setText("<html>" + "color" + "<br/>" + "type" + "</html>");
        currentCardLabel.setPreferredSize(currentCardDimension);

        drawDeckLabel = new JLabel("There are 108 cards left in the deck.");
        drawDeckLabel.setPreferredSize(drawDeckLabelDimension);

        currentColorLabel = new JLabel("Current color of the game: " + "color");
        currentColorLabel.setPreferredSize(currentCardDimension);

        fixedCurrentCardLabel = new JLabel("Current card");

        currentPlayerLabel = new JLabel("Player 1's hand");
    }

    /*
     * set up panels
     */
    private void initializePanels() {
        cardPanel = new JPanel();
        cardPanel.setPreferredSize(cardPanelDimension);

        deckStatusPanel = new JPanel(new GridBagLayout ());

        northPanel = new JPanel(new GridBagLayout ());
        northPanel.setPreferredSize(northPanelDimension);

        prevPlayerPanel = new JPanel(new GridBagLayout());
        prevPlayerPanel.setPreferredSize(playerPanelDimension);

        nextPlayerPanel = new JPanel(new GridBagLayout());
        nextPlayerPanel.setPreferredSize(playerPanelDimension);
    }

    /*
     * helper method to add componets to card panel
     */
    private void addToCardPanel() {
        cardPanel.add(currentPlayerLabel);
        cardPanel.add(prevFiveCardsButton);
        for (int i = 0; i < NUMBER_OF_DISPLAYED_CARDS; ++i) {
            cardPanel.add(cardButtonList.get(i));
        }
        cardPanel.add(nextFiveCardsButton);
    }

    /*
     * add panels to main container
     */
    private void addPanelsToContentPane() {
        getContentPane().add(cardPanel, "South");
        getContentPane().add(deckStatusPanel, "Center");
        getContentPane().add(prevPlayerPanel, "West");
        getContentPane().add(nextPlayerPanel, "East");
        getContentPane().add(northPanel, "North");
    }

    public void addPrevFiveCardsButtonListener(ActionListener a) {
        prevFiveCardsButton.addActionListener(a);
    }

    public void addNextFiveCardsButtonListener(ActionListener a) {
        nextFiveCardsButton.addActionListener(a);
    }

    /**
     * add actionListener for each card button in cardButtonList.
     * @param array array of listeners. Its length must be less or equal than five.
     */
    public void addCardButtonListListeners(ArrayList<ActionListener> array) {
        int i = 0;
        for (; i < array.size(); ++i) {
            cardButtonList.get(i).addActionListener(array.get(i));
        }
        //remove invalid actionListener
        for (; i < NUMBER_OF_DISPLAYED_CARDS; ++i) {
            for (ActionListener a : cardButtonList.get(i).getActionListeners()) {
                cardButtonList.get(i).removeActionListener(a);
            }
        }
    }

    public void addDrawButtonListener(ActionListener a) {
        drawButton.addActionListener(a);
    }

    public void setTextDrawDeckNumber(int n) {
        drawDeckLabel.setText("There are " + Integer.toString(n) + " cards left in the deck.");
    }

    public void setTextPrevPlayer(int playerID) {
        prevPlayerLabel.setText("<html>Previous Player:<br/>Player " + Integer.toString(playerID) +"</html>");
    }

    public void setTextNextPlayer(int playerID) {
        nextPlayerLabel.setText("<html>Next Player:<br/>Player " + Integer.toString(playerID) + "</html>");
    }

    public void setTextCurrentCard(String color, String type) {
        if (color.equals(Card.Color.NONE.toString())) {
            currentCardLabel.setText(type);
            return;
        }
        currentCardLabel.setText("<html>" + color + "<br/>" + type + "</html>");
    }

    public void setTextCurrentColor(String color) {
        currentColorLabel.setText("Current color of the game: " + color);
    }

    public void setTextCurrentPlayer(int playerID) {
        currentPlayerLabel.setText("Player "+ Integer.toString(playerID) + "'s hand");
    }
    /**
     * set text for each card button
     * @param colors list of color. Its length must be less or equal than 5.
     * @param types list of card tupe. Its length must be less or equal than 5.
     */
    public void setTextButtonArray(List<String> colors, List<String> types) {
        int i = 0;
        for (; i < colors.size(); ++i) {
            if (colors.get(i).equals(Card.Color.NONE.toString())) {
                cardButtonList.get(i).setText(types.get(i));
            } else {
                cardButtonList.get(i).setText("<html>" + colors.get(i) + "<br/>" + types.get(i) + "</html>");
            }
        }
        for (; i < NUMBER_OF_DISPLAYED_CARDS; ++i) {
            cardButtonList.get(i).setText("");
        }
    }
//    public static void main(String[] args){
//        InGameView inGameView = new InGameView();
//        inGameView.setVisible(true);
//    }

    public void setCardOffset(int cardOffset) {
        this.cardOffset = cardOffset;
    }

    public void setNumberOfCardInHand(int numberOfCardInHand) {
        NumberOfCardInHand = numberOfCardInHand;
    }

    public int getCardOffset() {
        return cardOffset;
    }

    public int getCardButtonIndex(JButton jButton) {
        return cardButtonList.indexOf(jButton);
    }

    /**
     * If card is WILD/DRAWFOUR, it will pop up a window and make user to choose a color and return it.
     * @param colors color array(R Y B G)
     * @return chosen color
     */
    public Card.Color chooseColor(Card card, Card.Color[] colors) {
        if (card.getType() == Card.CardType.WILD
                || card.getType() == Card.CardType.DRAWFOUR) {
            int colorIdx = JOptionPane.showOptionDialog(null, "Please choose a color",
                    "Click a button", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, colors, colors[1]);
            return colors[colorIdx];
        }
        return Card.Color.NONE;
    }
    //initial status
    public static void main(String[] args) {
        InGameView inGameView = new InGameView();
        ArrayList<String> colors = new ArrayList<>(5);
        ArrayList<String> types = new ArrayList<>(5);
        //set card
        for (int i = 0; i < 2; ++i) {
            colors.add("RED");
            types.add("FIVE");
        }
        for (int i = 0; i < 3; ++i) {
            colors.add("NONE");
            types.add("WILD");
        }
        inGameView.setTextButtonArray(colors,types);
        //set status
        inGameView.setTextCurrentCard("GREEN","FIVE");
        inGameView.setTextCurrentColor("GREEN");
        inGameView.setTextDrawDeckNumber(86);
        inGameView.setTextPrevPlayer(2);
        inGameView.setTextNextPlayer(1);
        inGameView.setTextCurrentPlayer(0);

        //invalid card play
//        ArrayList<ActionListener> as = new ArrayList<>();
//        for (int i = 0; i <inGameView.NUMBER_OF_DISPLAYED_CARDS; ++i) {
//            as.add(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    JOptionPane.showMessageDialog(null, "This card is not valid to play");
//                }
//            });
//        }
//        inGameView.addCardButtonListListeners(as);

        //choose color
        ArrayList<ActionListener> as = new ArrayList<>();
        for (int i = 0; i <inGameView.NUMBER_OF_DISPLAYED_CARDS; ++i) {
            as.add(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Card.Color[] colors = Arrays.copyOfRange(Card.Color.values(),0,4);
                    int x = JOptionPane.showOptionDialog(null, "Please choose a color",
                            "Click a button",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, colors, colors[1]);
                }
            });
        }
        inGameView.addCardButtonListListeners(as);

//        /*message to indicate no cards to play so that current player can only donly some number of cards and jump
//        to next player*/
//        inGameView.setVisible(true);
//        JOptionPane.showMessageDialog(null, "You have no card to play. " +
//                "Please draw cards becuase previous player playe draw/draw four cards");

//        end game
//        inGameView.setTextButtonArray(new ArrayList<String>(), new ArrayList<String>());
        inGameView.addDrawButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "You win! Game ends.");
            }
        });

        inGameView.setVisible(true);
    }
}
