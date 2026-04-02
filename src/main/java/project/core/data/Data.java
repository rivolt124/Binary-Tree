package project.core.data;

/**
 * The {@code Data} class defines the base abstraction for all data types
 * stored and managed within the project.
 */
public abstract class Data<T extends Data<T>> implements Comparable<T> {
    /**
     * Compares this node's data with the specified node's data.
     *
     * @param other the node data to compare with
     * @return 0 if this data is equal to {@code other};
     *         a positive value if this data is greater than {@code other};
     *         a negative value if this data is less than {@code other}
     */
    public abstract int compare(T other);

    public abstract String getKey();

    /** 
     * Neccesary for in-order test in {@link TreeTester()}
     */ 
    @Override
    public int compareTo(T other) {
        return compare(other);
    }

    public abstract String formatToCsv();

    public abstract String formatData();
}
