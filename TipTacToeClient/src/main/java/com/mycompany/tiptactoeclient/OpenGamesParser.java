/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiptactoeclient;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author super
 */
public class OpenGamesParser {
    
    public static void populateTable(String result, DefaultTableModel tableModel){
    tableModel.setRowCount(0);
    
    if(result == null||result.isBlank()||result.equals("error no games")){return;}
    if(result.startsWith("error")){throw new IllegalStateException(result);}
    
    String[] games = result.split("\\R");
    
    for(String game:games){
    
        String[] details = game.split(",",3);
        
        if (details.length == 3){
        tableModel.addRow(details);
        }
    }
    
    }
}
