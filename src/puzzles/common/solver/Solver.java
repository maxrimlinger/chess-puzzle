package puzzles.common.solver;

import java.util.*;

/**
 * Generic BFS puzzle solver that can solve different types of puzzles in the shortest way.
 *
 * @author Max Rimlinger mwr9727@rit.edu
 */
public class Solver {
    /** Queue of objects to visit. */
    private LinkedList<Configuration> queue = new LinkedList<>();
    /** Map of object to its predecessor. */
    private HashMap<Configuration, Configuration> predecessorMap = new HashMap<>();
    /** Total number of configs generated. */
    private int configCount = 1;
    /** Total number of unique configs generated. */
    private int uniqueConfigCount = 1;

    /**
     * Finds the shortest path from a starting configuration to a solution.
     * In effect, shows the quickest way to solve a puzzle.
     *
     * @param config starting configuration
     * @return path from starting configuration to solution
     */
    public ArrayList<Configuration> solve(Configuration config) {
        queue.addLast(config);
        predecessorMap.put(config, null);
        while (queue.size() > 0) {
            Configuration currentConfig = queue.pop();
            if (currentConfig.isSolution()) {
                Configuration predecessor = currentConfig;
                ArrayList<Configuration> shortestPath = new ArrayList<>();
                shortestPath.add(predecessor);
                while (predecessorMap.get(predecessor) != null) {
                    predecessor = predecessorMap.get(predecessor);
                    shortestPath.add(predecessor);
                }
                return shortestPath;
            } else {
                for (Configuration neighbor : currentConfig.getNeighbors()) {
                    configCount++;
                    if (!predecessorMap.containsKey(neighbor)) {
                        uniqueConfigCount++;
                        queue.addLast(neighbor);
                        predecessorMap.put(neighbor, currentConfig);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the total number of configs generated
     *
     * @return number of configs
     */
    public int getConfigCount() {
        return configCount;
    }

    /**
     * Get the total number of unique configs generated
     *
     * @return number of configs
     */
    public int getUniqueConfigCount() {
        return uniqueConfigCount;
    }
}
