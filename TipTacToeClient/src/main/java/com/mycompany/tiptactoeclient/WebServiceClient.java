/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiptactoeclient;

import com.tttws.TicTacToeWS;
import com.tttws.TicTacToeWebService;

public class WebServiceClient{
    
private static TicTacToeWebService service;
private static TicTacToeWS proxy;

public WebServiceClient(){
service = new TicTacToeWebService();
proxy = service.getTicTacToeWSPort();
}

public synchronized int login(String username, String password){
    return proxy.login(username, password);
}

public synchronized String register(String username, String password, String name, String surname){
    return proxy.register(username, password, name, surname);
}
public synchronized String newGame(int userId){return proxy.newGame(userId);}
public synchronized String joinGame(int userId, int gameId){return proxy.joinGame(userId, gameId);}
public synchronized String showOpenGames(){return proxy.showOpenGames();}
public synchronized String showMyOpenGames(int userId){return proxy.showMyOpenGames(userId);}
public synchronized String showAllMyGames(int userId){return proxy.showAllMyGames(userId);}
public synchronized String getBoard(int gameId){return proxy.getBoard(gameId);}
public synchronized String getGameState(int gameId){return proxy.getGameState(gameId);}
public synchronized String setGameState(int gameId, int  gameState){return proxy.setGameState(gameId, gameState);}
public synchronized String checkSquare(int x, int y, int gameId){return proxy.checkSquare(x,y,gameId);}
public synchronized String takeSquare(int x, int y, int gameId, int userId){return proxy.takeSquare(x,y,gameId,userId);}
public synchronized String checkWin(int gameId){return proxy.checkWin(gameId);}
public synchronized String leagueTable(){return proxy.leagueTable();}
public synchronized String deleteGame(int gameId, int userId){return proxy.deleteGame(gameId,userId);}
}
