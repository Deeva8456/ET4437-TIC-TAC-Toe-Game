
package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LeaderboardFrame extends JFrame {

    // Table for displaying player statistics
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;

    public LeaderboardFrame() {

        // Set up the window
        setTitle("Tic Tac Toe - Leaderboard");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create heading
        JLabel title = new JLabel(
            "Player Leaderboard",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // Column names
        String[] columns = {
            "Username",
            "Wins",
            "Losses",
            "Draws"
        };

        // Create table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create leaderboard table
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setAutoCreateRowSorter(true);

        // Add table inside scroll pane
        JScrollPane scrollPane =
            new JScrollPane(leaderboardTable);

        // Create Close button
        JButton closeButton = new JButton("Close");

        closeButton.addActionListener(e -> {
            dispose();
        });

        // Add components
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        add(panel);
    }

    // Add a player's statistics to the table
    public void addPlayerStats(
            String username,
            int wins,
            int losses,
            int draws) {

        tableModel.addRow(new Object[]{
            username, wins, losses, draws
        });
    }

    // Remove existing results before refreshing
    public void clearLeaderboard() {
        tableModel.setRowCount(0);
    }

    // Temporary test method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LeaderboardFrame frame =
                new LeaderboardFrame();

            // Example players for testing only
            frame.addPlayerStats("Player1", 5, 2, 1);
            frame.addPlayerStats("Player2", 3, 3, 2);
            frame.addPlayerStats("Player3", 2, 4, 1);

            frame.setVisible(true);
        });
    }
}
