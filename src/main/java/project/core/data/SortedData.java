package project.core.data;

/**
 * Represents a simple data structure used for sorting and displaying aggregated results,
 * such as the number of sick patients per district or region.
 */
public class SortedData extends Data<SortedData> {
    private final int primary;
    private final String secondary;

    public SortedData(int pri, String sec) {
        primary = pri;
        secondary = sec;
    }

    // ---------------------------------------------------------------
    // Core methods
    // ---------------------------------------------------------------

    public int getPrimary() {
        return primary;
    }

    public String getSecondary() {
        return secondary;
    }

    @Override
    public int compare(SortedData other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        int cmp = Integer.compare(other.primary, primary);
        if (cmp != 0) {
            return cmp;
        }
        return secondary.compareTo(other.secondary);
    }

    @Override
    public String getKey() {
        return String.valueOf(primary);
    }

    @Override
    public String formatToCsv() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String formatData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
