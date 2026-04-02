package project.core.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import project.core.tree.BalancedTree;
import project.core.tree.BinaryTree;

/**
 * Represents a patient's personal information and associated PCR test records.
 * Each patient is uniquely identified by an {@code id} and may have multiple
 * PCR test records stored in two binary trees:
 * one maintaining natural insertion order {@code record}, and another sorted chronologically
 * by their {@code dateTime} and, when equal, by their unique key.
 */
public class PatientData extends Data<PatientData> {
    private final   String                      id;
    private         String                      name;
    private         String                      surname;
    private         LocalDate                   birthday;
    private final   BinaryTree<PcrTestData>     record;
    private final   BalancedTree<PcrTestData>   recordByDate;

    public PatientData(String ptId, String nm, String srnm, LocalDate brth) {
        id = ptId;
        name = nm;
        surname = srnm;
        birthday = brth;
        record = new BinaryTree<>();
        recordByDate = new BalancedTree<>(Comparator.comparing(PcrTestData::getDateTime).thenComparing(PcrTestData::getKey));
    }

    private PatientData(String ptId) {
        id = ptId;
        record = null;
        recordByDate = null;
    }

    public static PatientData lookup(String patientId) {
        return new PatientData(patientId);
    }

    // ---------------------------------------------------------------
    // Core methods
    // ---------------------------------------------------------------

    public void addTest(PcrTestData test) {
        record.insert(test);
        recordByDate.insert(test);
    }

    public void removeTest(PcrTestData test) {
        record.delete(test);
        recordByDate.delete(test);
    }

    public BinaryTree<PcrTestData> getRecords() {
        return record;
    }

    public BalancedTree<PcrTestData> getRecordsByDate() {
        return recordByDate;
    }

    @Override
    public int compare(PatientData other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        return id.compareTo(other.id);
    }

    @Override
    public String formatToCsv() {
        return String.join(";", id, name, surname, birthday.toString());
    }

    @Override
    public String getKey() {
        return id;
    }

    @Override
    public String formatData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient ID: ").append(id).append("\n");
        sb.append("Name: ").append(name == null ? "" : name).append("\n");
        sb.append("Surname: ").append(surname == null ? "" : surname).append("\n");
        sb.append("Birthday: ").append(birthday == null 
                ? "" 
                : birthday.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append("\n");
        return sb.toString();
    }
}
