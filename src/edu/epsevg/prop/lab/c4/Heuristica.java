package edu.epsevg.prop.lab.c4;

/**
 * Clase Heuristica que implementa métodos para evaluar posiciones en un tablero de juego
 * y determinar jugadas óptimas basadas en una puntuación heurística.
 */
public class Heuristica {

    // Variables estáticas para definir dimensiones y constantes del tablero
    private static int ROW_COUNT; // Número de filas del tablero
    private static int COLUMN_COUNT; // Número de columnas del tablero
    private static final int WINDOW_LENGTH = 4; // Longitud de la ventana (4 fichas en línea)
    private static final int PLAYER_PIECE = 1; // Representación del jugador
    private static final int BOT_PIECE = 2; // Representación del bot
    private static final int EMPTY = 0; // Representación de un espacio vacío

    /**
     * Constructor de la clase Heuristica.
     * Inicializa el tamaño del tablero.
     *
     * @param lado Tamaño del lado del tablero (tablero cuadrado).
     */
    public Heuristica(int lado) {
        setROW_COUNT(lado); // Establece el número de filas
        setCOLUMN_COUNT(Heuristica.ROW_COUNT); // Establece el número de columnas (igual a las filas)
    }

    /**
     * Establece el número de filas del tablero.
     *
     * @param ROW_COUNT Número de filas.
     */
    public static void setROW_COUNT(int ROW_COUNT) {
        Heuristica.ROW_COUNT = ROW_COUNT;
    }

    /**
     * Establece el número de columnas del tablero.
     *
     * @param COLUMN_COUNT Número de columnas.
     */
    public static void setCOLUMN_COUNT(int COLUMN_COUNT) {
        Heuristica.COLUMN_COUNT = COLUMN_COUNT;
    }

    /**
     * Calcula la puntuación de una posición en el tablero en función de una heurística.
     *
     * @param board Matriz bidimensional que representa el tablero.
     * @param piece Pieza del jugador actual (1 para jugador, 2 para bot).
     * @return Puntuación calculada para el tablero.
     */
    public static int scorePosition(int[][] board, int piece) {
        int score = 0; // Inicializa la puntuación

        // Evalúa la columna central del tablero
        int[] centreArray = new int[ROW_COUNT];
        for (int i = 0; i < ROW_COUNT; i++) {
            centreArray[i] = board[i][COLUMN_COUNT / 2]; // Obtiene los valores de la columna central
        }
        int centreCount = 0; // Cuenta las piezas del jugador en la columna central
        for (int i : centreArray) {
            if (i == piece) centreCount++;
        }
        score += centreCount * 3; // Aumenta la puntuación por piezas en la columna central

        // Evalúa las posiciones horizontales
        for (int r = 0; r < ROW_COUNT; r++) { // Itera por filas
            for (int c = 0; c < COLUMN_COUNT - 3; c++) { // Itera por columnas
                int[] window = new int[WINDOW_LENGTH];
                System.arraycopy(board[r], c, window, 0, WINDOW_LENGTH); // Obtiene una ventana de 4 espacios
                score += evaluateWindow(window, piece); // Evalúa la ventana y actualiza la puntuación
            }
        }

        // Evalúa las posiciones verticales
        for (int c = 0; c < COLUMN_COUNT; c++) { // Itera por columnas
            for (int r = 0; r < ROW_COUNT - 3; r++) { // Itera por filas
                int[] window = new int[WINDOW_LENGTH];
                for (int i = 0; i < WINDOW_LENGTH; i++) {
                    window[i] = board[r + i][c]; // Crea una ventana vertical de 4 espacios
                }
                score += evaluateWindow(window, piece); // Evalúa la ventana y actualiza la puntuación
            }
        }

        // Evalúa las diagonales positivas (\)
        for (int r = 0; r < ROW_COUNT - 3; r++) {
            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                int[] window = new int[WINDOW_LENGTH];
                for (int i = 0; i < WINDOW_LENGTH; i++) {
                    window[i] = board[r + i][c + i]; // Crea una ventana diagonal positiva
                }
                score += evaluateWindow(window, piece); // Evalúa la ventana y actualiza la puntuación
            }
        }

        // Evalúa las diagonales negativas (/)
        for (int r = 0; r < ROW_COUNT - 3; r++) {
            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                int[] window = new int[WINDOW_LENGTH];
                for (int i = 0; i < WINDOW_LENGTH; i++) {
                    window[i] = board[r + 3 - i][c + i]; // Crea una ventana diagonal negativa
                }
                score += evaluateWindow(window, piece); // Evalúa la ventana y actualiza la puntuación
            }
        }

        return score; // Devuelve la puntuación total
    }

    /**
     * Evalúa una ventana de 4 espacios en el tablero y asigna una puntuación.
     *
     * @param window Arreglo que representa la ventana a evaluar.
     * @param piece Pieza del jugador actual (1 para jugador, 2 para bot).
     * @return Puntuación de la ventana.
     */
    private static int evaluateWindow(int[] window, int piece) {
        int score = 0; // Inicializa la puntuación para la ventana
        int oppPiece = (piece == PLAYER_PIECE) ? BOT_PIECE : PLAYER_PIECE; // Define la pieza del oponente

        // Priorización de ganar
        if (count(window, piece) == 4) {
            score += 100; // Puntuación alta para 4 en línea
        }
        // Priorización de 3 en línea
        else if (count(window, piece) == 3 && count(window, EMPTY) == 1) {
            score += 5;
        }
        // Priorización de 2 en línea
        else if (count(window, piece) == 2 && count(window, EMPTY) == 2) {
            score += 2;
        }
        // Priorización de bloquear 3 en línea del oponente
        if (count(window, oppPiece) == 3 && count(window, EMPTY) == 1) {
            score -= 4; // Penalización si el oponente tiene 3 en línea con posibilidad de ganar
        }

        return score; // Devuelve la puntuación para la ventana
    }

    /**
     * Verifica si un jugador tiene una jugada ganadora en el tablero.
     *
     * @param board Matriz bidimensional que representa el tablero.
     * @param piece Pieza del jugador actual (1 para jugador, 2 para bot).
     * @return true si el jugador tiene una jugada ganadora, de lo contrario false.
     */
    public static boolean winningMove(int[][] board, int piece) {
        // Verifica filas para jugadas ganadoras
        for (int c = 0; c < COLUMN_COUNT - 3; c++) {
            for (int r = 0; r < ROW_COUNT; r++) {
                if (board[r][c] == piece && board[r][c + 1] == piece &&
                    board[r][c + 2] == piece && board[r][c + 3] == piece) {
                    return true;
                }
            }
        }

        // Verifica columnas para jugadas ganadoras
        for (int c = 0; c < COLUMN_COUNT; c++) {
            for (int r = 0; r < ROW_COUNT - 3; r++) {
                if (board[r][c] == piece && board[r + 1][c] == piece &&
                    board[r + 2][c] == piece && board[r + 3][c] == piece) {
                    return true;
                }
            }
        }

        // Verifica diagonales positivas (\)
        for (int c = 0; c < COLUMN_COUNT - 3; c++) {
            for (int r = 0; r < ROW_COUNT - 3; r++) {
                if (board[r][c] == piece && board[r + 1][c + 1] == piece &&
                    board[r + 2][c + 2] == piece && board[r + 3][c + 3] == piece) {
                    return true;
                }
            }
        }

        // Verifica diagonales negativas (/)
        for (int c = 0; c < COLUMN_COUNT - 3; c++) {
            for (int r = 3; r < ROW_COUNT; r++) {
                if (board[r][c] == piece && board[r - 1][c + 1] == piece &&
                    board[r - 2][c + 2] == piece && board[r - 3][c + 3] == piece) {
                    return true;
                }
            }
        }

        return false; // Devuelve falso si no hay jugadas ganadoras
    }

    /**
     * Cuenta cuántas veces aparece un valor en una ventana.
     *
     * @param window Arreglo que representa la ventana a analizar.
     * @param value Valor a contar en la ventana.
     * @return Cantidad de ocurrencias del valor.
     */
    private static int count(int[] window, int value) {
        int count = 0;
        for (int i : window) {
            if (i == value) count++;
        }
        return count; // Devuelve la cantidad de coincidencias
    }
}
