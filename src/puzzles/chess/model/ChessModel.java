package puzzles.chess.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.*;
import java.util.*;

/**
 * Holds the game state and performs the game logic
 *
 * @author Max Rimlinger mwr9727@rit.edu
 */
public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();
    /** the current configuration */
    private ChessConfig currentConfig;
    /** the current puzzle file. saved in case of reset. */
    private File file;
    /** the selected piece's row. -1 if no piece is currently selected. */
    private int selectedPieceRow = -1;
    /** the selected piece's column. -1 if no piece is currently selected. */
    private int selectedPieceCol = -1;
    /** the number of rows in the chessboard */
    private int rows;
    /** the number of columns in the chessboard */
    private int cols;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * Simply initializes the model. The initial file is expected to be loaded right after.
     * The reason this is not done in the constructor is because after loading, it alerts the observers
     * but because the model is still being created, the observer cannot register itself in time.
     */
    public ChessModel() {
    }

    /**
     * Reads a chess puzzle file and loads it into the model. Alerts the observers once complete.
     * Called by the Controller.
     *
     * @param file the file to load
     */
    public void load(File file) {
        try {
            currentConfig = new ChessConfig(file.getPath());
            rows = currentConfig.getRows();
            cols = currentConfig.getCols();
            this.file = file;
            alertObservers("Loaded: " + file.getName());
        } catch (FileNotFoundException e) {
           alertObservers("Failed to load: " + file.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Selects a cell on the grid. This performs 2 possible actions:
     *  1. If this is the first time selecting a cell, it will save that as the selected cell
     *  2. If this is the second time selecting a cell, it will capture that piece with the selected piece
     *  Called by the Controller.
     *
     * @param row the row of the cell to select
     * @param col the column of the cell to select
     */
    public void select(int row, int col) {
        if (row >= 0 & row < rows & col >= 0 & col < cols) {
            if (selectedPieceRow == -1 & selectedPieceCol == -1) {
                if (currentConfig.getCell(row, col) != '.') {
                    selectedPieceRow = row;
                    selectedPieceCol = col;
                    alertObservers("Selected (" + row + ", " + col + ")");
                } else {
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;
                    alertObservers("Invalid selection (" + row + ", " + col + ")");
                }
            } else {
                ChessConfig proposedMove = new ChessConfig(currentConfig);
                if (proposedMove.isValidMove(selectedPieceRow, selectedPieceCol, row, col)) {
                    proposedMove.makeMove(selectedPieceRow, selectedPieceCol, row, col);
                    currentConfig = proposedMove;
                    alertObservers("Captured from (" + selectedPieceRow + ", " + selectedPieceCol + ") to ("
                            + row + ", " + col + ")");
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;
                } else {
                    alertObservers("Can't capture from (" + selectedPieceRow + ", " + selectedPieceCol + ") to ("
                            + row + ", " + col + ")");
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;
                }
            }
        } else {
            selectedPieceRow = -1;
            selectedPieceCol = -1;
            alertObservers("Invalid selection (" + row + ", " + col + ")");
        }
    }

    /**
     * Resets the chessboard by reloading the last filename that was loaded.
     * Called by the Controller.
     */
    public void reset() {
        selectedPieceRow = -1;
        selectedPieceCol = -1;
        load(file);
        alertObservers("Puzzle reset!");
    }

    /**
     * If the current board can be solved, finds the next, best move. Performs the move free of charge.
     * Called by the Controller.
     */
    public void hint() {
        selectedPieceRow = -1;
        selectedPieceCol = -1;

        Solver solver = new Solver();
        ArrayList<Configuration> shortestPath = solver.solve(currentConfig);
        if (shortestPath != null) {
            if (shortestPath.size() != 1) {
                currentConfig = (ChessConfig) shortestPath.get(shortestPath.size() - 2);
                alertObservers("Next step!");
            } else {
                alertObservers("Already Solved!");
            }
        } else {
            alertObservers("No solution!");
        }
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

    /**
     * Gets the piece at the given cell. Piece is represented as the character.
     *
     * @param row the row of the desired cell
     * @param col the column of the desired cell
     * @return
     */
    public char getCell(int row, int col) {
        return currentConfig.getCell(row, col);
    }

    /**
     * Generates a string representation of the board with axis numbers for ease of selection
     *
     * @return string representation
     */
    @Override
    public String toString() {
        String result = "";
        int rows = currentConfig.getRows();
        int cols = currentConfig.getCols();
        int numWidth = rows / 10 + 1; // # of spaces the row number markers will take up
        for (int i = 0; i < 1 + numWidth; i++) {
            result += " ";
        }
        for (int i = 0; i < cols; i++) {
            result += " " + i;
        }
        result += "\n";
        for (int i = 0; i < 1 + numWidth; i++) {
            result += " ";
        }
        for (int i = 0; i < cols; i++) {
            result += "--";
        }
        result += "\n";
        for (int i = 0; i < rows; i++) {
            result += i + "|";
            for (int j = 0; j < cols; j++) {
                result += " " + currentConfig.getCell(i, j);
            }
            result += "\n";
        }
        return result;
    }
}
