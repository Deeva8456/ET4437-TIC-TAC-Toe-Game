
package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ScoreFrame extends JFrame {

    // Labels used to display the player's scores
    private JLabel winsLabel;
    private JLabel lossesLabel;
    private JLabel drawsLabel;

    public ScoreFrame(String username) {

        // Set up the scoreboard window
        setTitle("Tic Tac Toe - My Scores");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Create heading
        JLabel title = new JLabel(
            "My Scoreboard",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // Display the player's username
        JLabel playerLabel = new JLabel(
            "Player: " + username,
            SwingConstants.CENTER
        );

        // Create score labels
        winsLabel = new JLabel("Wins: --");
        lossesLabel = new JLabel("Losses: --");
        drawsLabel = new JLabel("Draws: --");

        // Create panel for statistics
        JPanel statsPanel = new JPanel(
            new GridLayout(4, 1, 10, 10)
        );

        statsPanel.add(playerLabel);
        statsPanel.add(winsLabel);
        statsPanel.add(lossesLabel);
        statsPanel.add(drawsLabel);

        // Create Close button
        JButton closeButton = new JButton("Close");

        closeButton.addActionListener(e -> {
            dispose();
        });

        // Add components to main panel
        panel.add(title, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        add(panel);
    }

    // Update the statistics displayed on screen
    public void setScores(int wins, int losses, int draws) {

        winsLabel.setText("Wins: " + wins);
        lossesLabel.setText("Losses: " + losses);
        drawsLabel.setText("Draws: " + draws);
    }

    // Temporary method to test the window
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ScoreFrame frame = new ScoreFrame("TestUser");

            // Example scores for testing only
            frame.setScores(4, 2, 1);

            frame.setVisible(true);
        });
    }
}
