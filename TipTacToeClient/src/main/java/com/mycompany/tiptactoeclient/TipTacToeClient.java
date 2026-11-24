/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tiptactoeclient;



public class TipTacToeClient{
    public static void main(String[] args){
    
WebServiceClient client = new WebServiceClient();
int userId = client.login("testuser", "testpassword");
System.out.println("login result: "+ userId);

if(userId <= 0){
System.out.print("login failed");
return;
}

String result = client.newGame(userId);

System.out.println("New game result: "+result);
System.out.println("Open games: ");
System.out.println(client.showOpenGames());

try{
int gameId = Integer.parseInt(result.trim());
System.out.println("Delete result: "+client.deleteGame(gameId, userId));
}catch(NumberFormatException e){
System.out.println("Game creation failed");
}


}
}

