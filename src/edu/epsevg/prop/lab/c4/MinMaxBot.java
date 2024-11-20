/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;
/**
 * Clase MinMaxBot que implementa un jugador automático utilizando el algoritmo Minimax 
 * con poda alfa-beta y una heurística personalizada para evaluar las posiciones en el tablero.
 * 
 * Implementa las interfaces IAuto y Jugador.
 */
public class MinMaxBot implements IAuto, Jugador {

    private Heuristica heuristica; // Instancia de la clase Heuristica para evaluar posiciones en el tablero
    private int maxDepth; // Profundidad máxima de la búsqueda Minimax

    /**
     * Constructor de MinMaxBot.
     * 
     * @param size Tamaño del tablero.
     * @param depth Profundidad máxima de la búsqueda Minimax.
     */
    public MinMaxBot(int size, int depth) {
        this.setMaxDepth(depth); // Establece la profundidad máxima
        heuristica = new Heuristica(size); // Inicializa la heurística con el tamaño del tablero
    }

    /**
     * Método privado para establecer la profundidad máxima.
     * 
     * @param maxDepth Profundidad máxima a establecer.
     */
    private void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Calcula el movimiento óptimo utilizando el algoritmo Minimax con poda alfa-beta.
     * 
     * @param t Tablero actual.
     * @param color Color del jugador actual (1 o 2).
     * @return Columna óptima para realizar el movimiento.
     */
    @Override
    public int moviment(Tauler t, int color) {
        // Llama a minimax para determinar el mejor movimiento
        int bestMove = minimax(t, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, color)[0];
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
     * @param t Tablero actual.
     * @param depth Profundidad restante de la búsqueda.
     * @param alpha Valor alfa para la poda.
     * @param beta Valor beta para la poda.
     * @param maximizingPlayer Indicador de si es el turno del jugador maximizador.
     * @param color Color del jugador actual.
     * @return Arreglo con la columna óptima y la puntuación asociada.
     */
    private int[] minimax(Tauler t, int depth, int alpha, int beta, boolean maximizingPlayer, int color) {
        // Caso base: si se alcanza la profundidad máxima o no hay movimientos posibles
        if (depth == 0 || t.espotmoure() == false) {
            int score = heuristica.scorePosition(convertToBoardArray(t), color); // Evalúa el tablero
            return new int[] { -1, score }; // Devuelve la puntuación sin movimiento
        }

        int bestColumn = -1; // Inicializa la mejor columna
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Inicializa la mejor puntuación

        // Itera por todas las columnas posibles
        for (int col = 0; col < t.getMida(); col++) {
            if (t.movpossible(col)) { // Verifica si el movimiento en la columna es válido
                Tauler tCopy = new Tauler(t); // Crea una copia del tablero
                tCopy.afegeix(col, color); // Simula el movimiento en la copia

                int nextPlayerColor = (color == 1) ? 2 : 1; // Alterna el jugador

                // Llama recursivamente a minimax
                int score = minimax(tCopy, depth - 1, alpha, beta, !maximizingPlayer, nextPlayerColor)[1];

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
    private int[][] convertToBoardArray(Tauler t) {
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
