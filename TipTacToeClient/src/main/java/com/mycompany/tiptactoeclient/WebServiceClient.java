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

public int login(String username, String password){
    return proxy.login(username, password);
}
}