package project.core.data;

import java.util.Comparator;

import project.core.tree.BalancedTree;

/**
 *  Represents a workplace and stores all PCR test records associated with it.
 *  Each {@link #WorkplaceData()} instance is uniquely identified by its {@code id}
 */
public class WorkplaceData extends Data<WorkplaceData> {
    private final   int                       id;
    private final   BalancedTree<PcrTestData> recordByDate;

    public WorkplaceData(int workplaceId) {
        id = workplaceId;
        recordByDate = new BalancedTree<>(Comparator.comparing(PcrTestData::getDateTime).thenComparing(PcrTestData::getKey));
    }

    public static WorkplaceData lookup(int workplaceId) {
        return new WorkplaceData(workplaceId);
    }

    // ---------------------------------------------------------------
    // Core methods
    // ---------------------------------------------------------------

    public void addTest(PcrTestData test) {
        recordByDate.insert(test);
    }

    public void removeTest(PcrTestData test) {
        recordByDate.delete(test);
    }

    public BalancedTree<PcrTestData> getRecordByDate() {
        return recordByDate;
    }

    @Override
    public int compare(WorkplaceData other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        return Integer.compare(id, other.id);
    }

    @Override
    public String getKey() {
        return String.valueOf(id);
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
