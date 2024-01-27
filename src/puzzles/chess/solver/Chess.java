package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Solves a chess puzzle from a given file
 *
 * @author Max Rimlinger mwr9727@rit.edu
 */
public class Chess {
    /**
     * Solves a chess puzzle with the fewest moves and displays the resulting solution
     *
     * @param args filepath of chess puzzle text file
     * @throws IOException error with file
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        }
        ChessConfig initConfig = new ChessConfig(args[0]);
        System.out.println("File: " + args[0]);
        System.out.print(initConfig);
        Solver solver = new Solver();
        ArrayList<Configuration> shortestPath = solver.solve(initConfig);
        System.out.println("Total configs: " + solver.getConfigCount());
        System.out.println("Unique configs: " + solver.getUniqueConfigCount());
        if (shortestPath != null) {
            for (int i = 0; i < shortestPath.size(); i++) {
                System.out.println("Step " + i + ": \n" + shortestPath.get(shortestPath.size() - i - 1));
            }
        } else {
            System.out.println("No solution");
        }
    }
}
