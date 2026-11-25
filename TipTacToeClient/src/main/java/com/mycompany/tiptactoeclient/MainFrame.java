/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author super
 */
public class MainFrame extends JFrame {

    private final WebServiceClient client;
    private final int userId;
    private final String username;
    private final GameController gameController;
    private final GameCleanup gameCleanup;
    private boolean refreshing = false;
    private boolean gameActionRunning = false;

    private JTable gamesTable;
    private DefaultTableModel tableModel;

    private JButton createButton;
    private JButton joinButton;
    private JButton scoresButton;
    private JButton leaderboardButton;
    private JButton refreshButton;

    public MainFrame(WebServiceClient client, int userId, String username) {
        this.client = client;
        this.userId = userId;
        this.username = username;
        this.gameController = new GameController(client);
        this.gameCleanup = new GameCleanup(client);

        setTitle("Tic Tac Toe - Main Menu");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(welcomeLabel, BorderLayout.NORTH);

        String[] columns = {
            "Game ID", "Created By", "Created At"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gamesTable = new JTable(tableModel);
        gamesTable.setRowHeight(25);
        gamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(gamesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Games"));
        add(scrollPane, BorderLayout.CENTER);

        createButton = new JButton("Create Game");
        joinButton = new JButton("Join Game");
        scoresButton = new JButton("My Scores");
        leaderboardButton = new JButton("Leaderboard");
        refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(createButton);
        buttonPanel.add(joinButton);
        buttonPanel.add(scoresButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshGames());
        createButton.addActionListener(e -> createGame());
        joinButton.addActionListener(e -> joinGame());
        refreshGames();

        Timer cleanupTimer = new Timer(15000, e -> refreshGames());
        cleanupTimer.start();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cleanupTimer.stop();
            }
        });

    }

    private void refreshGames() {
        if (refreshing || gameActionRunning) {
            return;
        }

        refreshing = true;
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                gameCleanup.deleteExpiredGames(userId);

                return client.showOpenGames();
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    OpenGamesParser.populateTable(result, tableModel);
                } catch (Exception e) {
                    System.err.println("Could not refresh games: " + e.getMessage());
                } finally {
                    refreshing = false;
                }
            }
        };
        worker.execute();
    }

    private void setGameActionRunning(boolean running) {

        gameActionRunning = running;
        createButton.setEnabled(!running);
        joinButton.setEnabled(!running);
        refreshButton.setEnabled(!running);

    }

    private void createGame() {

        if (gameActionRunning) {
            return;
        }

        setGameActionRunning(true);

        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {

                return gameController.createGame(userId);
            }

            @Override
            protected void done() {

                try {
                    int gameId = get();

                    JOptionPane.showMessageDialog(MainFrame.this, "Game created succesfully!\nGame Id: " + gameId);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Could not create game:" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
                } finally {

                    setGameActionRunning(false);
                    refreshGames();
                }
            }
        };
        worker.execute();
    }

    private void joinGame() {

        if (gameActionRunning) {
            return;
        }

        int selectedRow = gamesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "please select a game first");
            return;
        }

        int gameId = Integer.parseInt(gamesTable.getValueAt(selectedRow, 0).toString());

        String creator = gamesTable.getValueAt(selectedRow, 1).toString();

        if (creator.equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "you cannot join your own game");
            return;
        }
        setGameActionRunning(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {

                gameController.joinGame(userId, gameId);
                return null;
            }

            @Override
            protected void done() {

                try {
                    get();

                    JOptionPane.showMessageDialog(MainFrame.this,
                            "succesfully joined game: " + gameId);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(),
                            "error", JOptionPane.ERROR_MESSAGE);
                } finally {

                    setGameActionRunning(false);
                    refreshGames();
                }
            }
        };
        worker.execute();

    }

    public static void main(String[] args) {

        WebServiceClient client = new WebServiceClient();

        int userId = client.login("testuser", "testpassword");

        if (userId <= 0) {
            System.out.println("Login failed.");
            return;
        }

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame(
                    client, userId, "testuser"
            );

            frame.setVisible(true);
        });
    }

}
