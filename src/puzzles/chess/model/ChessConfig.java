package puzzles.chess.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * A configuration of a chess puzzle.
 *
 * @author Max Rimlinger mwr9727@rit.edu
 */
public class ChessConfig implements Configuration {
    /** number of rows in the grid */
    private int rows;
    /** number of columns in the grid */
    private int cols;
    /** chess board of pieces */
    private char[][] grid;

    /* Chess Piece Constants */
    /** represents a pawn */
    private final char PAWN = 'P';
    /** represents a king */
    private final char KING = 'K';
    /** represents a knight */
    private final char KNIGHT = 'N';
    /** represents a bishop */
    private final char BISHOP = 'B';
    /** represents a rook */
    private final char ROOK = 'R';
    /** represents a queen */
    private final char QUEEN = 'Q';
    /** represents an empty space */
    private final char EMPTY = '.';

    /**
     * creates an initial configuration from a file
     *
     * @param filename name of chess puzzle text file to read from
     * @throws IOException error with file
     */
    public ChessConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String[] dimensions = in.readLine().split("\\s+");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);

            grid = new char[rows][cols];
            for (int row = 0; row < rows; row++) {
                String[] gridLineChars = in.readLine().split("\\s+");
                for (int col = 0; col < cols; col++) {
                    grid[row][col] = gridLineChars[col].charAt(0);
                }
            }
        }
    }

    /**
     * creates a copy of another ChessConfig
     *
     * @param other ChessConfig to copy
     */
    public ChessConfig(ChessConfig other) {
        this.cols = other.cols;
        this.rows = other.rows;
        this.grid = new char[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                this.grid[row][col] = other.grid[row][col];
            }
        }
    }

    /**
     * is this configuration solved? AKA is there only 1 piece left on the board?
     *
     * @return true if solved, false otherwise
     */
    @Override
    public boolean isSolution() {
        int numPiecesOnBoard = 0;
        for (char[] row : grid) {
            for (char ch : row) {
                if (ch != EMPTY) {
                    numPiecesOnBoard++;
                }
            }
        }
        return numPiecesOnBoard == 1;
    }

    /**
     * get all possible valid moves for each piece from this board
     *
     * @return all possible valid configurations
     */
    @Override
    public HashSet<Configuration> getNeighbors() {
        HashSet<Configuration> neighborList = new HashSet<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                switch (grid[row][col]) {
                    case PAWN -> neighborList.addAll(getValidPawnMoves(row, col));
                    case KING -> neighborList.addAll(getValidKingMoves(row, col));
                    case KNIGHT -> neighborList.addAll(getValidKnightMoves(row, col));
                    case BISHOP -> neighborList.addAll(getValidBishopMoves(row, col));
                    case ROOK -> neighborList.addAll(getValidRookMoves(row, col));
                    case QUEEN -> neighborList.addAll(getValidQueenMoves(row, col));
                }
            }
        }
        return neighborList;
    }

    /**
     * check if a move with a given piece from one board space to another will capture a piece.
     * if so, "make the move" by creating a configuration with the capturing piece replacing the captured piece.
     *
     * @param sourceRow row the capturing piece is moving from
     * @param sourceCol col the capturing piece is moving from
     * @param destRow row the capturing piece is moving to
     * @param destCol col the capturing piece is moving to
     * @return ArrayList with the new configuration with the move made if it was successful,
     *      otherwise an empty ArrayList
     */
    public HashSet<Configuration> attemptMove(int sourceRow, int sourceCol, int destRow, int destCol) {
        HashSet<Configuration> configList = new HashSet<>();
        if (grid[destRow][destCol] != EMPTY) {
            ChessConfig config = new ChessConfig(this);
            char piece = config.grid[sourceRow][sourceCol];
            config.grid[sourceRow][sourceCol] = EMPTY;
            config.grid[destRow][destCol] = piece;
            configList.add(config);
        }
        return configList;
    }

    /**
     * Directly make a move on the configuration. Used by the model to make a proposed move that may or may not be right.
     * Doesn't make any checks to see its possible.
     *
     * @param sourceRow row the capturing piece is moving from
     * @param sourceCol col the capturing piece is moving from
     * @param destRow row the capturing piece is moving to
     * @param destCol col the capturing piece is moving to
     */
    public void makeMove(int sourceRow, int sourceCol, int destRow, int destCol) {
        grid[destRow][destCol] = grid[sourceRow][sourceCol];
        grid[sourceRow][sourceCol] = EMPTY;
    }

    /**
     * Checks if a move is a valid movement of the given piece. Does not actually check if a capture is being made.
     *
     * @param sourceRow row the capturing piece is moving from
     * @param sourceCol col the capturing piece is moving from
     * @param destRow row the capturing piece is moving to
     * @param destCol col the capturing piece is moving to
     * @return if the move is valid or not
     */
    public boolean isValidMove(int sourceRow, int sourceCol, int destRow, int destCol) {
        if (sourceRow == destRow & sourceCol == destCol) {
            return false;
        }
        if (grid[destRow][destCol] != EMPTY) {
            char piece = grid[sourceRow][sourceCol];
            switch (piece) {
                case PAWN:
                    if (destRow == sourceRow - 1) {
                       return destCol == sourceCol + 1 | destCol == sourceCol - 1;
                    }
                    return false;
                case KING:
                    if (destRow == sourceRow - 1 | destRow == sourceRow + 1) {
                        return destCol == sourceCol - 1 | destCol == sourceCol | destCol == sourceCol + 1;
                    } else if (destRow == sourceRow) {
                        return destCol == sourceCol - 1 | destCol == sourceCol + 1;
                    }
                    return false;
                case KNIGHT:
                    if (destRow == sourceRow - 2 | destRow == sourceRow + 2) {
                        return destCol == sourceCol - 1 | destCol == sourceCol + 1;
                    } else if (destRow == sourceRow - 1 | destRow == sourceRow + 1) {
                        return destCol == sourceCol - 2 | destCol == sourceCol + 2;
                    }
                    return false;
                case BISHOP:
                    return isValidBishopMove(sourceRow, sourceCol, destRow, destCol);
                case ROOK:
                    return isValidRookMove(sourceRow, sourceCol, destRow, destCol);
                case QUEEN:
                    return isValidBishopMove(sourceRow, sourceCol, destRow, destCol)
                            | isValidRookMove(sourceRow, sourceCol, destRow, destCol);
            }
        }
        return false;
    }

    /**
     * Part of isValidMove(). Checks if it is a valid diagonal bishop movement and that other pieces are
     * not blocking the capture.
     *
     * @param sourceRow row the capturing piece is moving from
     * @param sourceCol col the capturing piece is moving from
     * @param destRow row the capturing piece is moving to
     * @param destCol col the capturing piece is moving to
     * @return if the move is valid or not
     */
    private boolean isValidBishopMove(int sourceRow, int sourceCol, int destRow, int destCol) {
        if (sourceRow - destRow == sourceCol - destCol) {
            if (destRow < sourceRow) { // northwest
                for (int deltaRow = 1, deltaCol = 1;
                     sourceRow - deltaRow > destRow & sourceCol - deltaCol > destCol;
                     deltaRow++, deltaCol++) {
                    if (grid[sourceRow - deltaRow][sourceCol - deltaCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            } else { // southeast
                for (int deltaRow = 1, deltaCol = 1;
                     sourceRow + deltaRow < destRow & sourceCol + deltaCol < destCol;
                     deltaRow++, deltaCol++) {
                    if (grid[sourceRow + deltaRow][sourceCol + deltaCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            }
        } else if (sourceRow - destRow == destCol - sourceCol) {
            if (destRow < sourceRow) { // northeast
                for (int deltaRow = 1, deltaCol = 1;
                     sourceRow - deltaRow > destRow & sourceCol + deltaCol < destCol;
                     deltaRow++, deltaCol++) {
                    if (grid[sourceRow - deltaRow][sourceCol + deltaCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            } else { // southwest
                for (int deltaRow = 1, deltaCol = 1;
                     sourceRow + deltaRow < destRow & sourceCol - deltaCol > destCol;
                     deltaRow++, deltaCol++) {
                    if (grid[sourceRow + deltaRow][sourceCol - deltaCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Part of isValidMove(). Checks if it is a valid straight line rook movement and that other pieces are
     * not blocking the capture.
     *
     * @param sourceRow row the capturing piece is moving from
     * @param sourceCol col the capturing piece is moving from
     * @param destRow row the capturing piece is moving to
     * @param destCol col the capturing piece is moving to
     * @return if the move is valid or not
     */
    public boolean isValidRookMove(int sourceRow, int sourceCol, int destRow, int destCol) {
        if (destCol == sourceCol) {
            if (destRow < sourceRow) { // north
                for (int deltaRow = 1; sourceRow - deltaRow > destRow; deltaRow++) {
                    if (grid[sourceRow - deltaRow][destCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            } else { // south
                for (int deltaRow = 1; sourceRow + deltaRow < destRow; deltaRow++) {
                    if (grid[sourceRow + deltaRow][destCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            }
        } else if (destRow == sourceRow) {
            if (destCol < sourceCol) { // west
                for (int deltaCol = 1; sourceCol - deltaCol > destCol; deltaCol++) {
                    if (grid[destRow][sourceCol - deltaCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            } else { // east
                for (int deltaCol = 1; sourceCol + deltaCol < destCol; deltaCol++) {
                    if (grid[destRow][sourceCol + deltaCol] != EMPTY) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * finds all possible moves that a pawn in a given position could make
     *
     * @param row row that the piece is currently on
     * @param col column that the piece is currently on
     * @return all possible neighboring configurations
     */
    private HashSet<Configuration> getValidPawnMoves(int row, int col) {
        HashSet<Configuration> pawnNeighborList = new HashSet<>();
        if (row != 0) {
            if (col > 0) {
                // check northwest
                pawnNeighborList.addAll(attemptMove(row, col, row - 1, col - 1));
            }
            if (col < cols - 1) {
                // check northeast
                pawnNeighborList.addAll(attemptMove(row, col, row - 1, col + 1));
            }
        }
        return pawnNeighborList;
    }

    /**
     * finds all possible moves that a king in a given position could make
     *
     * @param row row that the piece is currently on
     * @param col column that the piece is currently on
     * @return all possible neighboring configurations
     */
    private HashSet<Configuration> getValidKingMoves(int row, int col) {
        HashSet<Configuration> kingNeighborList = new HashSet<>();
        // reuse pawn moves
        kingNeighborList.addAll(getValidPawnMoves(row, col));
        if (row > 0) {
            // check north
            kingNeighborList.addAll(attemptMove(row, col, row - 1, col));
        }
        if (col > 0) {
            // check west
            kingNeighborList.addAll(attemptMove(row, col, row, col - 1));
        }
        if (col < cols - 1) {
            // check east
            kingNeighborList.addAll(attemptMove(row, col, row, col + 1));
        }
        if (row < rows - 1) {
            if (col > 0) {
                // check southwest
                kingNeighborList.addAll(attemptMove(row, col, row + 1, col - 1));
            }
            // check south
            kingNeighborList.addAll(attemptMove(row, col, row + 1, col));
            if (col < cols - 1) {
                // check southeast
                kingNeighborList.addAll(attemptMove(row, col, row + 1, col + 1));
            }
        }
        return kingNeighborList;
    }

    /**
     * finds all possible moves that a knight in a given position could make
     *
     * @param row row that the piece is currently on
     * @param col column that the piece is currently on
     * @return all possible neighboring configurations
     */
    private HashSet<Configuration> getValidKnightMoves(int row, int col) {
        HashSet<Configuration> knightNeighborList = new HashSet<>();
        if (row > 1) {
            if (col > 0) {
                // check north 2 west 1
                knightNeighborList.addAll(attemptMove(row, col, row - 2, col - 1));
            }
            if (col < cols - 1) {
                // check north 2 east 1
                knightNeighborList.addAll(attemptMove(row, col, row - 2, col + 1));
            }
        }
        if (row > 0) {
            if (col > 1) {
                // check north 1 west 2
                knightNeighborList.addAll(attemptMove(row, col, row - 1, col - 2));
            }
            if (col < cols - 2) {
                // check north 1 east 2
                knightNeighborList.addAll(attemptMove(row, col, row - 1, col + 2));
            }
        }
        if (row < rows - 1) {
            if (col > 1) {
                // check south 1 west 2
                knightNeighborList.addAll(attemptMove(row, col, row + 1, col - 2));
            }
            if (col < cols - 2) {
                // check south 1 east 2
                knightNeighborList.addAll(attemptMove(row, col, row + 1, col + 2));
            }
        }
        if (row < rows - 2) {
            if (col > 0) {
                // check south 2 west 1
                knightNeighborList.addAll(attemptMove(row, col, row + 2, col - 1));
            }
            if (col < cols - 1) {
                // check south
                knightNeighborList.addAll(attemptMove(row, col, row + 2, col + 1));
            }
        }
        return knightNeighborList;
    }

    /**
     * finds all possible moves that a bishop in a given position could make
     *
     * @param row row that the piece is currently on
     * @param col column that the piece is currently on
     * @return all possible neighboring configurations
     */
    private HashSet<Configuration> getValidBishopMoves(int row, int col) {
        HashSet<Configuration> bishopNeighborList = new HashSet<>();
        for (int deltaRow = 1, deltaCol = 1;
             (row - deltaRow) >= 0 && (col - deltaCol) >= 0;
             deltaRow++, deltaCol++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row - deltaRow, col - deltaCol);
            if (!neighbor.isEmpty()) {
                bishopNeighborList.addAll(neighbor);
                break;
            }
        }
        for (int deltaRow = 1, deltaCol = 1;
             (row - deltaRow) >= 0 && (col + deltaCol) < cols;
             deltaRow++, deltaCol++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row - deltaRow, col + deltaCol);
            if (!neighbor.isEmpty()) {
                bishopNeighborList.addAll(neighbor);
                break;
            }
        }
        for (int deltaRow = 1, deltaCol = 1;
             (row + deltaRow) < rows && (col - deltaCol) >= 0;
             deltaRow++, deltaCol++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row + deltaRow, col - deltaCol);
            if (!neighbor.isEmpty()) {
                bishopNeighborList.addAll(neighbor);
                break;
            }
        }
        for (int deltaRow = 1, deltaCol = 1;
             (row + deltaRow) < rows && (col + deltaCol) < cols;
             deltaRow++, deltaCol++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row + deltaRow, col + deltaCol);
            if (!neighbor.isEmpty()) {
                bishopNeighborList.addAll(neighbor);
                break;
            }
        }
        return bishopNeighborList;
    }

    /**
     * finds all possible moves that a rook in a given position could make
     *
     * @param row row that the piece is currently on
     * @param col column that the piece is currently on
     * @return all possible neighboring configurations
     */
    private HashSet<Configuration> getValidRookMoves(int row, int col) {
        HashSet<Configuration> rookNeighborList = new HashSet<>();
        for (int deltaRow = 1; (row - deltaRow) >= 0; deltaRow++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row - deltaRow, col);
            if (!neighbor.isEmpty()) {
                rookNeighborList.addAll(neighbor);
                break;
            }
        }
        for (int deltaCol = 1; (col + deltaCol) < cols; deltaCol++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row, col + deltaCol);
            if (!neighbor.isEmpty()) {
                rookNeighborList.addAll(neighbor);
                break;
            }
        }
        for (int deltaRow = 1; (row + deltaRow) < rows; deltaRow++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row + deltaRow, col);
            if (!neighbor.isEmpty()) {
                rookNeighborList.addAll(neighbor);
                break;
            }
        }
        for (int deltaCol = 1; (col - deltaCol) >= 0; deltaCol++) {
            HashSet<Configuration> neighbor = attemptMove(row, col, row, col - deltaCol);
            if (!neighbor.isEmpty()) {
                rookNeighborList.addAll(neighbor);
                break;
            }
        }
        return rookNeighborList;
    }

    /**
     * finds all possible moves that a queen in a given position could make
     *
     * @param row row that the piece is currently on
     * @param col column that the piece is currently on
     * @return all possible neighboring configurations
     */
    private HashSet<Configuration> getValidQueenMoves(int row, int col) {
        HashSet<Configuration> queenNeighborList = new HashSet<>();
        queenNeighborList.addAll(getValidRookMoves(row, col));
        queenNeighborList.addAll(getValidBishopMoves(row, col));
        return queenNeighborList;
    }

    /**
     * generate a hashcode based on the board state
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    /**
     * check whether two ChessConfigs are identical
     *
     * @param other ChessConfig too check against
     * @return true if the two are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ChessConfig otherChess) {
            return Arrays.deepEquals(this.grid, otherChess.grid);
        }
        return false;
    }

    /**
     * create a string representation of the chess board.
     * pieces are denoted by letters and empty spaces are periods
     *
     * @return string representation of chess board
     */
    @Override
    public String toString() {
        String result = "";
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                result += grid[row][col] + " ";
            }
            result += "\n";
        }
        return result;
    }

    /**
     * Gets the piece at a certain row and column
     *
     * @param row the row of the desired cell
     * @param col the column of the desired cell
     * @return the piece represented by a character
     */
    public char getCell(int row, int col) {
        return grid[row][col];
    }

    /**
     * Gets the total number of rows in the chessboard
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the total number of columns in the chessboard
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }
}
