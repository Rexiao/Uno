package view;


import model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * start scene for the game.\n
 */
public class StartView extends JFrame {
    private JButton startButton;
    private JTextField playerNumberInput;
    private JLabel plyerNumberInputLabel;
    private JLabel welcomeLabel;

    public StartView() {
        //set up frame
        setSize(800, 600);
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //deal with welcomLabel
        welcomeLabel = new JLabel("Uno!", JLabel.CENTER);
        welcomeLabel.setFont(new Font(welcomeLabel.getFont().toString(), Font.PLAIN, 50));
        getContentPane().add(welcomeLabel, "North");

        //deal with components in start panel
        startButton = new JButton("start");
        playerNumberInput = new JTextField();
        playerNumberInput.setColumns(10);
        plyerNumberInputLabel = new JLabel("Please enter number of players to begin");

        //set up panel
        JPanel startPanel = new JPanel();
        startPanel.setPreferredSize(new Dimension(600, 400));

        startPanel.add(plyerNumberInputLabel);
        startPanel.add(playerNumberInput);
        startPanel.add(startButton);
        getContentPane().add(startPanel, "South");
    }

    public void addStartListener(ActionListener a) {
        startButton.addActionListener(a);
    }

    public void setPlayerNumberInput(int n) {
        playerNumberInput.setText(Integer.toString(n));
    }

    /**
     * get string of playerNumberInput
     */
    public String getPlayerNumber() {
        return playerNumberInput.getText();
    }
    //for start invalid player number
    public static void main(String[] args) {
        StartView startView = new StartView();
        startView.setVisible(true);
        startView.setPlayerNumberInput(1);
        startView.addStartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Number of player should betweeen 2-10");
            }
        });
    }
}


