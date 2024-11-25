/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;

/**
 * Clase MinMaxBot que implementa un jugador automático utilizando el algoritmo Minimax 
 * con poda alfa-beta y una heurística personalizada para evaluar las posiciones en el tablero.
 * 
 * Implementa las interfaces IAuto y Jugador.
 */
public class MinMaxBot implements IAuto, Jugador {

    private Heuristica heuristica; // Instancia de la clase Heuristica para evaluar posiciones en el tablero
    private int maxDepth; // Profundidad máxima de la búsqueda Minimax
    private int COLUMN_SIZE;  // Tamaño de la columna de la tabla
    private int jugadas;

    /**
     * Constructor de MinMaxBot.
     *
     * @param size Tamaño del tablero.
     * @param depth Profundidad máxima de la búsqueda Minmax.
     */
    public MinMaxBot(int size, int depth) {
        this.COLUMN_SIZE = size;
        this.setMaxDepth(depth); // Establece la profundidad máxima
        heuristica = new Heuristica(size); // Inicializa la heurística con el tamaño del tablero
    }

    /**
     * Método para establecer la profundidad máxima de recorrido Minmax.
     *
     * @param maxDepth Profundidad máxima a establecer.
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Calcula el movimiento óptimo utilizando el algoritmo Minmax con poda alfa-beta.
     *
     * @param t Tablero actual.
     * @param color Color del jugador actual.
     * @return Columna óptima para realizar el movimiento.
     */
    @Override
    public int moviment(Tauler t, int color) {
        // Inicializa la cantidad de jugadas exploradas a 0.
        jugadas = 0;
        // Llama a minimax para determinar el mejor movimiento
        int bestMove = minimax(convertToBoardArray(t), maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, color)[0];
        System.out.printf("## Jugadas exploradas: %d\n", jugadas);
        return bestMove; // Devuelve la mejor columna
    }

    /**
     * Devuelve el nombre del jugador.
     *
     * @return Nombre del jugador ("MinMaxBot").
     */
    @Override
    public String nom() {
        return "MinMaxBot";
    }

    /**
     * Implementación del algoritmo Minimax con poda alfa-beta.
     *
     * @param board Tablero actual.
     * @param depth Profundidad restante de la búsqueda.
     * @param alpha Valor alfa para la poda.
     * @param beta Valor beta para la poda.
     * @param maximizingPlayer Indicador de si es el turno del jugador maximizador.
     * @param color Color del jugador actual.
     * @return Array con la columna óptima y la puntuación asociada, respectivamente.
     */
    public int[] minimax(int[][] board, int depth, int alpha, int beta, boolean maximizingPlayer, int color) {
        // Caso base: si se alcanza la profundidad máxima o no hay movimientos posibles
        boolean finished = heuristica.finished(board);
        if (depth == 0 || finished) {
            if (finished) {
                if (heuristica.winningMove(board, heuristica.PLAYER_PIECE)) {
                    // System.out.println("Player has winning move");
                    return new int[] { 0 , Integer.MAX_VALUE };
                }
                else if (heuristica.winningMove(board, heuristica.BOT_PIECE)) {
                    // System.out.println("Bot has winning move");
                    return new int[] { 0 , Integer.MIN_VALUE };
                }
                else return new int[] { 0, 0 };
            }
            int score = heuristica.scorePosition(board, heuristica.PLAYER_PIECE); // Evalúa el tablero
            return new int[] { 0, score }; // Devuelve la puntuación sin movimiento
        }

        ArrayList<Integer> colList = heuristica.getValidPlays(board);

        int bestColumn = colList.get((int)Math.floor(Math.random()*colList.size())); // Inicializa la mejor columna
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Inicializa la mejor puntuación
        // Itera por todas las columnas posibles
        for (int col: colList) {
            if (heuristica.validLocation(board, col)) { // Verifica si el movimiento en la columna es válido

                int[][] boardCopy = new int[COLUMN_SIZE][COLUMN_SIZE]; // Crea una copia del tablero
                for (int i = 0; i < COLUMN_SIZE; ++i){
                    System.arraycopy(board[i], 0, boardCopy[i], 0, COLUMN_SIZE);
                }

                heuristica.play(boardCopy, col, color); // Simula una jugada en la tabla copia
                ++jugadas;

                int nextPlayerColor = (color == heuristica.PLAYER_PIECE) ? // Alterna el jugador
                                       heuristica.BOT_PIECE : heuristica.PLAYER_PIECE;

                // Llama recursivamente a minimax
                int score = minimax(boardCopy, depth - 1, alpha, beta, !maximizingPlayer, nextPlayerColor)[1];
                
                // Actualiza la mejor puntuación y columna para el jugador maximizador
                if (maximizingPlayer) {
                    if (score > bestScore) {
                        bestScore = score;
                        bestColumn = col;
                    }
                    alpha = Math.max(alpha, bestScore); // Actualiza alfa
                }
                // Actualiza la mejor puntuación y columna para el jugador minimizador
                else {
                    if (score < bestScore) {
                        bestScore = score;
                        bestColumn = col;
                    }
                    beta = Math.min(beta, bestScore); // Actualiza beta
                }

                // Realiza la poda si es posible
                if (alpha >= beta) {
                    break;
                }
            }
        }

        return new int[] { bestColumn, bestScore }; // Devuelve la mejor columna y puntuación
    }

    /**
     * Convierte un objeto Tauler en una matriz bidimensional para la evaluación de la heurística.
     *
     * @param t Tablero actual.
     * @return Matriz bidimensional representando el tablero.
     */
    public int[][] convertToBoardArray(Tauler t) {
        int[][] board = new int[t.getMida()][t.getMida()]; // Inicializa la matriz del tamaño del tablero

        // Rellena la matriz con los valores del tablero
        for (int r = 0; r < t.getMida(); r++) {
            for (int c = 0; c < t.getMida(); c++) {
                board[r][c] = t.getColor(r, c); // Obtiene el color de cada posición
            }
        }
        return board; // Devuelve la matriz convertida
    }
}
