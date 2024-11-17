/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

/**
 *
 * @author kmalhal
 */
public class MinMaxBot implements Jugador, IAuto {
    
    private String name;
    
    MinMaxBot() {
        name = "MinMaxBot";
    }
    
    public int moviment(Tauler t, int color) {
        return 0;
    }
    
    public String nom() {
        return this.name;
    }
}
