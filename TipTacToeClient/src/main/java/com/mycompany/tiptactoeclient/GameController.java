/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiptactoeclient;

/**
 *
 * @author super
 */
public class GameController {
    private final WebServiceClient client;
    
    public GameController(WebServiceClient client){
    this.client = client;
    }
    
    public int createGame(int userId){
    String result = client.newGame(userId);
    
    if (result == null || result.startsWith("error")){
    throw new IllegalStateException("Could not create game: "+result);
    }
    return Integer.parseInt(result.trim());
}
}
