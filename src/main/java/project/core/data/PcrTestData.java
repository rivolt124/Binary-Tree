package project.core.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single PCR test record containing all of it's relevant 
 * information.
 * Each record is uniquely identified by an integer {@code id}
 */
public class PcrTestData extends Data<PcrTestData> {
    private final   int             id;
    private         String          patientId;
    private         LocalDateTime   dateTime;     
    private         int             workplaceId;
    private         int             district;
    private         int             region;
    private         boolean         result;
    private         double          value;
    private         String          note;

    public PcrTestData(int tstId, String patId, LocalDateTime dttm, int wrkId, int dist, int reg, boolean res, double val, String nt) {
        id = tstId;
        patientId = patId;
        dateTime = dttm;
        workplaceId = wrkId;
        district = dist;
        region = reg;
        result = res;
        value = val;
        note = nt;
    }

    private PcrTestData(int tstId) {
        id = tstId;
    }

    private PcrTestData(int tstId, LocalDateTime date) {
        id = tstId;
        dateTime = date;
    }

    public static PcrTestData lookup(int tstId) {
        return new PcrTestData(tstId);
    }

    public static PcrTestData[] dateRange(LocalDate start, LocalDate end) {
        return new PcrTestData[] { 
            new PcrTestData(Integer.MIN_VALUE, start.atStartOfDay()), 
            new PcrTestData(Integer.MAX_VALUE, end.atTime(23,59,59)) 
        };
    }

    public static PcrTestData[] dateRange(LocalDate marker, int days) {
        LocalDate start = marker.minusDays(days);
        return dateRange(start, marker);
    }

    // ---------------------------------------------------------------
    // Core methods
    // ---------------------------------------------------------------

    public String getPatientId() {
        return patientId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getWorkplace() {
        return workplaceId;
    }

    public int getDistrict() {
        return district;
    }

    public int getRegion() {
        return region;
    }

    public boolean getResult() {
        return result;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compare(PcrTestData other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        return Integer.compare(id, other.id);
    }

    @Override
    public String formatToCsv() {
        String dateTimeStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return String.join(";",
            String.valueOf(id),
            patientId,
            dateTimeStr,
            String.valueOf(workplaceId),
            String.valueOf(district),
            String.valueOf(region),
            String.valueOf(result),
            String.valueOf(value),
            note == null ? "" : note.replace(";", ","));
    }

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public String formatData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test ID: ").append(id).append("\n");
        sb.append("Patient ID: ").append(patientId).append("\n");
        sb.append("Date/Time: ").append(dateTime
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .append("\n");
        sb.append("Workplace: ").append(workplaceId).append("\n");
        sb.append("District: ").append(district).append("\n");
        sb.append("Region: ").append(region).append("\n");
        sb.append("Result: ").append(result).append("\n");
        sb.append("Value: ").append(value).append("\n");
        sb.append("Note: ").append(note == null ? "" : note);
        return sb.toString();
    }

}
