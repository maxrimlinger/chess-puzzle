package puzzles.common;

import java.util.*;

/**
 * A simple implementation of two-dimensional integer-based coordinates.
 * Note that this class is a record, which means
 * <ul>
 *     <li>These objects are immutable.</li>
 *     <li>
 *         Many methods are provided that do not appear in the source.
 *         <ul>
 *             <li>constructor</li>
 *             <li>{@link #equals(Object)}</li>
 *             <li>{@link #hashCode()}</li>
 *             <li>accessors</li>
 *         </ul>
 *     </li>
 * </ul>
 * Java records also supply a toString method, but this is being overridden
 * here in favor of a more concise format.
 *
 * @author RIT CS
 */
public record Coordinates( int row, int col )
        implements Comparable<Coordinates> {

    /**
     * Initialize this object with strings representing the integer values.
     * {@link Integer#parseInt(String)} is used to parse the strings.
     * @see Coordinates#Coordinates(int, int)
     * @param rowStr row number as a string
     * @param colStr column number as a string
     */
    public Coordinates(String rowStr, String colStr) {
        this(Integer.parseInt(rowStr), Integer.parseInt(colStr));
    }

    /**
     * Initialize this object with a string that represents both
     * integer values, separated by a command with no space,
     * e.g. "10,32" -> Coordinate(10, 32).
     * @see Coordinates#Coordinates(int, int)
     * @param coordsStr the string coordinates
     */
    public Coordinates(String coordsStr) {
        this(
            Integer.parseInt(coordsStr.substring(0, coordsStr.indexOf(","))),
            Integer.parseInt(coordsStr.substring(coordsStr.indexOf(",")+1))
        );
    }

    /**
     * Returns a string representation of the coordinate in the format
     * "(row,col)".
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "(" + this.row + ',' + this.col + ')';
    }

    /**
     * Determine natural order of two coordinate pairs.
     * This ordering is useful because if you sort a list of coordinates
     * they come out in row-major order.
     * @param other the second pair of coordinates
     * @return negative if this row is less than the other row, or if
     *         the rows are the same, if this column is less than the
     *         other column; 0 if rows <u>and</u> columns are equal;
     *         positive otherwise
     */
    @Override
    public int compareTo(Coordinates other) {
        int result = this.row - other.row;
        if ( result == 0 ) result = this.col - other.col;
        return result;
    }

    /**
     * The main method demonstrates the various ways you can
     * use the Coordinate record.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        // construct with integer row and column
        Coordinates coord1 = new Coordinates(1, 2);
        System.out.println(coord1);
        System.out.println("coord1 row: " + coord1.row());
        System.out.println("coord1 col: " + coord1.col());

        // construct with integer and row strings
        System.out.println(new Coordinates("30", "40"));
        // construct with a comma separated row and column string
        System.out.println(new Coordinates("50,60"));

        // when put into a TreeSet the order is by row then column
        Set<Coordinates> treeSet = new TreeSet<>();
        treeSet.add(new Coordinates(10, 20));
        treeSet.add(new Coordinates(20, 10));
        treeSet.add(new Coordinates(10, 15));
        treeSet.add(new Coordinates(5, 10));
        System.out.println("TreeSet: " + treeSet);

        // when put into a LinkedHashMap the insertion order is preserved
        // but still maintains O(1) access time
        Map<Coordinates, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(new Coordinates(10, 20), "A");
        linkedHashMap.put(new Coordinates(20, 10), "B");
        linkedHashMap.put(new Coordinates(10, 15), "C");
        linkedHashMap.put(new Coordinates(10, 20), "X");
        System.out.println("LinkedHashMap: ");
        for (Map.Entry<Coordinates, String> entry : linkedHashMap.entrySet()) {
            System.out.println(entry);
        }
    }
}
