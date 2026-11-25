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
    
    private JTable gamesTable;
    private DefaultTableModel tableModel;
    
    private JButton createButton;
    private JButton joinButton;
    private JButton scoresButton;
    private JButton leaderboardButton;
    private JButton refreshButton;
    
    public MainFrame (WebServiceClient client, int userId, String username){
    this.client = client;
    this.userId = userId;
    this.username = username;
    this.gameController = new GameController(client);
    
    setTitle("Tic Tac Toe - Main Menu");
    setSize(750,450);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(10,10));
    
    JLabel welcomeLabel = new JLabel("Welcome "+username,SwingConstants.CENTER);
    welcomeLabel.setFont(new Font("Arial",Font.BOLD,22));
    add(welcomeLabel,BorderLayout.NORTH);
    
    String[] columns = {
    "Game ID","Created By","Created At"
    };
    
    tableModel = new DefaultTableModel(columns,0){
    @Override
    public boolean isCellEditable(int row, int column){
    return false;
    }
    };
    gamesTable = new JTable(tableModel);
    gamesTable.setRowHeight(25);
    gamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(gamesTable);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Available Games"));
    add(scrollPane,BorderLayout.CENTER);
    
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
    
    add(buttonPanel,BorderLayout.SOUTH);
    
    refreshButton.addActionListener(e->refreshGames());
    createButton.addActionListener(e -> createGame());
    joinButton.addActionListener(e -> joinGame());
    refreshGames();
    
    
    }
    
    private void refreshGames(){
    try{
    String result = client.showOpenGames();
    OpenGamesParser.populateTable(result, tableModel);
    }catch(Exception e){
    JOptionPane.showMessageDialog(this,"could not load available games", 
            "error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void createGame(){
    try{
    int gameId = gameController.createGame(userId);
    refreshGames();
    JOptionPane.showMessageDialog(this, "Game created successfully!\nGame ID: "+gameId,
            "game created",JOptionPane.INFORMATION_MESSAGE);
    
    }catch (Exception e){
    JOptionPane.showMessageDialog(this,"could not create game: "+e.getMessage(),
            "error",JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void joinGame(){
        
    int selectedRow = gamesTable.getSelectedRow();
    
    if(selectedRow==-1){
    JOptionPane.showMessageDialog(this,"please select a game first"); return;
    }
    try{
    int gameId = Integer.parseInt(gamesTable.getValueAt(selectedRow,0).toString());
    
    String creator = gamesTable.getValueAt(selectedRow,1).toString();
    
    if(creator.equalsIgnoreCase(username)){
    JOptionPane.showMessageDialog(this, "tou cannot join your own game");return;
    }
    gameController.joinGame(userId, gameId);
    
    refreshGames();
    
    JOptionPane.showMessageDialog(this, "successfully joinged game "+gameId);
    
    }catch(Exception e){
    JOptionPane.showMessageDialog(this, e.getMessage(),"error",JOptionPane.ERROR_MESSAGE);
    }
    
    }
    
    
public static void main(String[] args) {

    WebServiceClient client = new WebServiceClient();

    // Login as Player 1
    int player1 = client.login("testuser", "testpassword");

    // Create a second test account
    client.register(
            "testuser2",
            "testpassword2",
            "Test",
            "Player2"
    );

    // Login as Player 2
    int player2 = client.login("testuser2", "testpassword2");

    if (player1 <= 0 || player2 <= 0) {
        System.out.println("Login failed.");
        return;
    }

    // Player 1 creates a game
    GameController controller = new GameController(client);

    int gameId = controller.createGame(player1);

    // Player 1 makes the first move
    String move = client.takeSquare(0, 0, gameId, player1);

    if (!"1".equals(move.trim())) {
        System.out.println("First move failed: " + move);
        return;
    }

    System.out.println("Test game ID: " + gameId);

    // Open the main menu as Player 2
    SwingUtilities.invokeLater(() -> {

        MainFrame frame = new MainFrame(
                client, player2, "testuser2"
        );

        frame.setVisible(true);
    });
}

    }

