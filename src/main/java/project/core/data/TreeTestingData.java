package project.core.data;

/**
 * Represents a simple data structure used only in testing the
 * implementaion of {@link BinaryTree} and {@link BalancedTree}
 * inside of {@link TreeTester}
 */
public class TreeTestingData extends Data<TreeTestingData> {
    private final int value;

    public TreeTestingData(int val) {
        value = val;
    }

    // ---------------------------------------------------------------
    // Core methods
    // ---------------------------------------------------------------

    @Override
    public int compare(TreeTestingData other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public String formatToCsv() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getKey() {
        return String.valueOf(value);
    }

    @Override
    public String formatData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
