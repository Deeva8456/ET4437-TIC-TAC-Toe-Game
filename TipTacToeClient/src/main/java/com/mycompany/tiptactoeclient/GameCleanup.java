/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiptactoeclient;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 *
 * @author super
 */
public class GameCleanup {
    private final WebServiceClient client;
    
    private static final int EXPIRY_MINUTES = 1;
    
    public GameCleanup(WebServiceClient client){
    this.client = client;
    }
    
    public void deleteExpiredGames(int userId){
    String result = client.showMyOpenGames(userId);
    
    if(result == null || result.isBlank()|| result.equals("ERROR-NOGAMES")){
    return;
    }
    
    if(result.startsWith("ERROR")){
    throw new IllegalStateException(result);
    }
    
    String[] games = result.split("\\R");
    
    for(String game : games){
    String[] details = game.split(",",3);
    
    if(details.length != 3){
    continue;
    }
    
    int gameId = Integer.parseInt(details[0].trim());
    
    LocalDateTime createdAt = Timestamp.valueOf(details[2].trim()).toLocalDateTime();
    
    long minutes = Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    
    if (minutes >= EXPIRY_MINUTES){
     String state = client.getGameState(gameId);
     
     if(!"-1".equals(state)){
         continue;
     }
     
     String deleteResult = client.deleteGame(gameId,userId);
     
     if ("1".equals(deleteResult)){
     System.out.println("Expired game deleted: "+gameId);
     }else if (!"ERROR-GAMESTARTED".equals(deleteResult)){
     System.out.println("could not delete game: "+deleteResult);
     }
    }
    }
    
    }
    }

