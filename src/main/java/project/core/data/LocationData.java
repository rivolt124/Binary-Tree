package project.core.data;

import java.util.Comparator;

import project.core.tree.BalancedTree;

/**
 * Represents all PCR test data associated with a specific location.
 * Each instance stores tests in two balanced binary trees (AVL):
 * one containing all recorded tests and another containing
 * only positive test results. Both are organized chronologically
 * by their {@code dateTime} and, when equal, by their unique key.
 */
public class LocationData extends Data<LocationData> {
    private final   int                         code;
    private final   BalancedTree<PcrTestData>   recordByDate;
    private final   BalancedTree<PcrTestData>   positiveRecordByDate;

    public LocationData(int cd) {
        code = cd;
        recordByDate = new BalancedTree<>(Comparator.comparing(PcrTestData::getDateTime).thenComparing(PcrTestData::getKey));
        positiveRecordByDate = new BalancedTree<>(Comparator.comparing(PcrTestData::getDateTime).thenComparing(PcrTestData::getKey));
    }

    public static LocationData lookup(int cd) {
       return new LocationData(cd);
    }

    // ---------------------------------------------------------------
    // Core methods
    // ---------------------------------------------------------------

    public void addTest(PcrTestData test) {
        recordByDate.insert(test);
        if (test.getResult()) {
            positiveRecordByDate.insert(test);
        }
    }

    public void removeTest(PcrTestData test) {
        recordByDate.delete(test);
        if (test.getResult()) {
            positiveRecordByDate.delete(test);
        }
    }

    public BalancedTree<PcrTestData> getRecordByDate() {
        return recordByDate;
    }

    public BalancedTree<PcrTestData> getPositiveRecordByDate() {
        return positiveRecordByDate;
    }

    @Override
    public int compare(LocationData other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        return Integer.compare(code, other.code);
    }

    @Override
    public String getKey() {
        return String.valueOf(code);
    }

    @Override
    public String formatToCsv() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String formatData() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
