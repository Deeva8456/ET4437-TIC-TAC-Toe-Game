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
    
    public static void main(String[] args){
    WebServiceClient client = new WebServiceClient();
    
    int userId = client.login("testuser", "testpassword");
    
    if (userId <= 0){
    System.out.println("login failed");
    return;
    }
    
    
    SwingUtilities.invokeLater(()->{
    MainFrame frame = new MainFrame(client,userId,"testuser");
    frame.setVisible(true);
    });
    }
    }

