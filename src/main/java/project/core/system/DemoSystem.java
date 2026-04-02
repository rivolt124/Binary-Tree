package project.core.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import project.core.data.LocationData;
import project.core.data.PatientData;
import project.core.data.PcrTestData;
import project.core.data.SortedData;
import project.core.data.WorkplaceData;
import project.core.tree.BalancedTree;
import project.core.tree.BinaryTree;

/**
 * The {@link #DemoSystem()} is the central in-memory management system for patients, PCR tests, 
 * workplaces, districts, and regions. It provides functionality to store, retrieve, 
 * and delete entities while maintaining required order using {@link #BinaryTree()} (BST) 
 * and {@link #BalancedTree()} (AVL). This system also supports range queries, sorting, and 
 * aggregation operations on test and patient data.
 */
public class DemoSystem {
    private final BalancedTree<PatientData>   patients;
    private final BalancedTree<PcrTestData>   records;
    private final BalancedTree<PcrTestData>   recordsByDate;
    private final BalancedTree<PcrTestData>   positiveRecordsByDate;
    private final BinaryTree<LocationData>    regions;
    private final BinaryTree<LocationData>    districts;
    private final BalancedTree<WorkplaceData> workplaces;

    public DemoSystem() {
        patients = new BalancedTree<>();
        records = new BalancedTree<>();
        recordsByDate = new BalancedTree<>(
            Comparator.comparing(PcrTestData::getDateTime).thenComparing(PcrTestData::getKey));
        positiveRecordsByDate = new BalancedTree<>(
            Comparator.comparing(PcrTestData::getDateTime).thenComparing(PcrTestData::getKey));
        regions = new BinaryTree<>();
        districts = new BinaryTree<>();
        workplaces = new BalancedTree<>();
    }

    // Search

    /**
     * Finds a specific PCR test for a given patient and returns a formatted string 
     * containing the test details followed by the patient details.
     *
     * @param testId    The unique ID of the PCR test to find.
     * @param patientId The unique ID of the patient whose test is being queried.
     * @return A formatted string containing the test and patient information, 
     *         or a message indicating if the patient or test was not found.
     */
    public String findTestForPatient(int testId, String patientId) {
        PatientData patient = patients.find(PatientData.lookup(patientId));
        if (patient == null) {
            return "Patient not found";
        }
        PcrTestData test = patient.getRecords().find(PcrTestData.lookup(testId));
        if (test == null) {
            return "Test not found";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(test.formatData()).append("\n\n");
        sb.append(patient.formatData()).append("\n");
        return sb.toString();
    }

    /**
     * Retrieves all PCR tests for a given patient, sorted by date, 
     * and returns a formatted string containing the patient details 
     * followed by each test's details.
     *
     * @param patientId The unique ID of the patient whose tests are being queried.
     * @return A formatted string with the patient's information and all their tests, 
     *         including a total count, or a message indicating if the patient 
     *         or tests were not found.
     */
    public String findAllTestsForPatient(String patientId) {
        PatientData patient = patients.find(PatientData.lookup(patientId));
        if (patient == null) {
            return "Patient not found";
        }
        if (patient.getRecords().isEmpty()) {
            return "No tests found for this patient";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(patient.formatData()).append("\n#################\n\n");
        int count = 0;

        for (PcrTestData test : patient.getRecordsByDate().inOrder()) {
            sb.append(test.formatData()).append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No tests found for this patient";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all positive PCR tests for a specific district within a given date range,
     * and returns a formatted string containing each test's details along with the 
     * corresponding patient information.
     *
     * @param districtCode The unique code of the district to query.
     * @param min          The start date of the range (inclusive).
     * @param max          The end date of the range (inclusive).
     * @return A formatted string containing all positive tests and patient details,
     *         including a total count, or a message indicating if no positive tests were found
     *         for the district in the specified timeframe.
     */
    public String findAllPositiveTestsForDistrict(int districtCode, LocalDate min, LocalDate max) {
        LocationData district = districts.find(LocationData.lookup(districtCode));
        if (district == null || district.getPositiveRecordByDate().isEmpty()) {
            return "No positive tests found for this district";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : district.getPositiveRecordByDate().rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No positive tests found for this district in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all PCR tests for a specific district within a given date range,
     * and returns a formatted string containing each test's details along with 
     * the corresponding patient information.
     *
     * @param districtCode The unique code of the district to query.
     * @param min          The start date of the range (inclusive).
     * @param max          The end date of the range (inclusive).
     * @return A formatted string containing all tests and patient details,
     *         including a total count, or a message indicating if no tests were found
     *         for the district in the specified timeframe.
     */
    public String findAllTestsForDistrict(int districtCode, LocalDate min, LocalDate max) {
        LocationData district = districts.find(LocationData.lookup(districtCode));
        if (district == null || district.getRecordByDate().isEmpty()) {
            return "No tests found for this district";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : district.getRecordByDate().rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No tests found for this district in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all positive PCR tests for a specific region within a given date range,
     * and returns a formatted string containing each test's details along with the 
     * corresponding patient information.
     *
     * @param regionCode The unique code of the region to query.
     * @param min        The start date of the range (inclusive).
     * @param max        The end date of the range (inclusive).
     * @return A formatted string containing all positive tests and patient details,
     *         including a total count, or a message indicating if no positive tests were found
     *         for the region in the specified timeframe.
     */
    public String findAllPositiveTestsForRegion(int regionCode, LocalDate min, LocalDate max) {
        LocationData region = regions.find(LocationData.lookup(regionCode));
        if (region == null || region.getPositiveRecordByDate().isEmpty()) {
            return "No positive tests found for this region";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : region.getPositiveRecordByDate().rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No positive tests found for this region in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all PCR tests for a specific region within a given date range,
     * and returns a formatted string containing each test's details along with 
     * the corresponding patient information.
     *
     * @param regionCode The unique code of the region to query.
     * @param min        The start date of the range (inclusive).
     * @param max        The end date of the range (inclusive).
     * @return A formatted string containing all tests and patient details,
     *         including a total count, or a message indicating if no tests were found
     *         for the region in the specified timeframe.
     */
    public String findAllTestsForRegion(int regionCode, LocalDate min, LocalDate max) {
        LocationData region = regions.find(LocationData.lookup(regionCode));
        if (region == null || region.getRecordByDate().isEmpty()) {
            return "No tests found for this region";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : region.getRecordByDate().rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No tests found for this region in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all positive PCR tests within a specified date range,
     * and returns a formatted string containing each test's details 
     * along with the corresponding patient information.
     *
     * @param min The start date of the range (inclusive).
     * @param max The end date of the range (inclusive).
     * @return A formatted string containing all positive tests and patient details,
     *         including a total count, or a message indicating if no positive tests were found
     *         in the given timeframe.
     */
    public String findAllPositiveTests(LocalDate min, LocalDate max) {
        if (positiveRecordsByDate.isEmpty()) {
            return "No positive tests found";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : positiveRecordsByDate.rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No positive tests found in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all PCR tests within a specified date range,
     * and returns a formatted string containing each test's details 
     * along with the corresponding patient information.
     *
     * @param min The start date of the range (inclusive).
     * @param max The end date of the range (inclusive).
     * @return A formatted string containing all tests and patient details,
     *         including a total count, or a message indicating if no tests were found
     *         in the given timeframe.
     */
    public String findAllTests(LocalDate min, LocalDate max) {
        if (recordsByDate.isEmpty()) {
            return "No tests found";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : recordsByDate.rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No tests found in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves all unique sick patients for a specific district within a given number of days 
     * leading up to the specified date, and returns a formatted string containing each patient's details.
     *
     * @param districtCode The unique code of the district to query.
     * @param date         The reference date for the range calculation.
     * @param days         The number of days before the reference date to include in the search.
     * @return A formatted string containing all unique sick patients in the district within the timeframe,
     *         including a total count, or a message indicating if no sick patients were found.
     */
    public String findAllSickPatientsForDistrict(int districtCode, LocalDate date, int days) {
        LocationData district = districts.find(LocationData.lookup(districtCode));
        if (district == null || district.getPositiveRecordByDate().isEmpty()) {
            return "No sick patients found for this district";
        }
        PcrTestData[] range = PcrTestData.dateRange(date, days);
        StringBuilder sb = new StringBuilder();
        BinaryTree<PatientData> uniquePatients = new BinaryTree<>();
        int uniquePatientCount = 0;

        for (PcrTestData test : district.getPositiveRecordByDate().rangeFind(range[0], range[1])) {
            if (uniquePatients.insert(PatientData.lookup(test.getPatientId())) != null) {
                sb.append(findPatient(test.getPatientId())).append("\n-----------------\n");
                uniquePatientCount++;
            }
        }
        if (uniquePatientCount == 0) {
            return "No sick patients found for this district in the given timeframe";
        }
        sb.append("Total items: ").append(uniquePatientCount);
        return sb.toString();
    } 

    /**
     * Retrieves all unique sick patients for a specific district within a given number of days 
     * leading up to the specified date, sorted by the value of their positive PCR tests in descending order,
     * and returns a formatted string containing each patient's details.
     *
     * @param districtCode The unique code of the district to query.
     * @param date         The reference date for the range calculation.
     * @param days         The number of days before the reference date to include in the search.
     * @return A formatted string containing all unique sick patients in the district within the timeframe, sorted by test value,
     *         including a total count, or a message indicating if no sick patients were found
     *         in the given timeframe.
     */
    public String findAllSortedSickPatientsForDistrict(int districtCode, LocalDate date, int days) {
        LocationData district = districts.find(LocationData.lookup(districtCode));
        if (district == null || district.getPositiveRecordByDate().isEmpty()) {
            return "No sick patients found for this district";
        }
        PcrTestData[] range = PcrTestData.dateRange(date, days);
        StringBuilder sb = new StringBuilder();

        BinaryTree<PcrTestData> sortedTests = new BinaryTree<>(
            Comparator.comparingDouble(PcrTestData::getValue).reversed().thenComparing(PcrTestData::getKey));
        for (PcrTestData test : district.getPositiveRecordByDate().rangeFind(range[0], range[1])) {
            sortedTests.insert(test);
        }
        BinaryTree<PatientData> uniquePatients = new BinaryTree<>();
        int uniquePatientCount = 0;

        for (PcrTestData test : sortedTests.inOrder()) {
            if (uniquePatients.insert(PatientData.lookup(test.getPatientId())) != null) {
                sb.append(findPatient(test.getPatientId())).append("\n-----------------\n");
                uniquePatientCount++;
            }
        }
        if (uniquePatientCount == 0) {
            return "No sick patients found for this district in the given timeframe";
        }
        sb.append("Total items: ").append(uniquePatientCount);
        return sb.toString();
    } 

    /**
     * Retrieves all unique sick patients for a specific region within a given number of days 
     * leading up to the specified date, and returns a formatted string containing each patient's details.
     *
     * @param regionCode The unique code of the region to query.
     * @param date       The reference date for the range calculation.
     * @param days       The number of days before the reference date to include in the search.
     * @return A formatted string containing all unique sick patients in the region within the timeframe,
     *         including a total count, or a message indicating if no sick patients were found.
     */
    public String findAllSickPatientsForRegion(int regionCode, LocalDate date, int days) {
        LocationData region = regions.find(LocationData.lookup(regionCode));
        if (region == null || region.getPositiveRecordByDate().isEmpty()) {
            return "No sick patients found for this region";
        }
        PcrTestData[] range = PcrTestData.dateRange(date, days);
        StringBuilder sb = new StringBuilder();
        BinaryTree<PatientData> uniquePatients = new BinaryTree<>();
        int uniquePatientCount = 0;

        for (PcrTestData test : region.getPositiveRecordByDate().rangeFind(range[0], range[1])) {
            if (uniquePatients.insert(PatientData.lookup(test.getPatientId())) != null) {
                sb.append(findPatient(test.getPatientId())).append("\n-----------------\n");
                uniquePatientCount++;
            }
        }
        if (uniquePatientCount == 0) {
            return "No sick patients found for this region in the given timeframe";
        }
        sb.append("Total items: ").append(uniquePatientCount);
        return sb.toString();
    }

    /**
     * Retrieves all unique sick patients within a given number 
     * of days leading up to the specified date and returning their 
     * details in a formatted string.
     *
     * @param date The reference date for the range calculation.
     * @param days The number of days before the reference date to include in the search.
     * @return A formatted string containing all unique sick patients within the timeframe,
     *         including a total count, or a message indicating that no sick patients were found.
     */
    public String findAllSickPatients(LocalDate date, int days) {
        if (positiveRecordsByDate.isEmpty()) {
            return "No sick patients found";
        }
        PcrTestData[] range = PcrTestData.dateRange(date, days);
        StringBuilder sb = new StringBuilder();
        BinaryTree<PatientData> uniquePatients = new BinaryTree<>();
        int uniquePatientCount = 0;

        for (PcrTestData test : positiveRecordsByDate.rangeFind(range[0], range[1])) {
            if (uniquePatients.insert(PatientData.lookup(test.getPatientId())) != null) {
                sb.append(findPatient(test.getPatientId())).append("\n-----------------\n");
                uniquePatientCount++;
            }
        }
        if (uniquePatientCount == 0) {
            return "No sick patients found in the given timeframe";
        }
        sb.append("Total items: ").append(uniquePatientCount);
        return sb.toString();
    } 

    /**
     * Finds the patient with the highest positive test value in each district within the specified 
     * number of days leading up to the given date and returns their details formatted as a string.
     *
     * @param date The reference date for the range calculation.
     * @param days The number of days before the reference date to include in the search.
     * @return A formatted string containing the district, the patient with the highest test value
     *         in that district, and a total count of districts included, or a message if no 
     *         sick patients were found in the given timeframe.
     */
    public String findMostSickPatientForDistrict(LocalDate date, int days) {
        if (districts.isEmpty()) {
            return "There are no sick patients anywhere";
        }
        PcrTestData[] range = PcrTestData.dateRange(date, days);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (LocationData district : districts.inOrder()) {
            if (district.getPositiveRecordByDate().isEmpty()) {
                continue;
            }
            List<PcrTestData> testsInRange = district.getPositiveRecordByDate().rangeFind(range[0], range[1]);
            if (testsInRange.isEmpty()) {
                continue;
            }
            PcrTestData maxValueTest = testsInRange.get(0);

            for (PcrTestData test : testsInRange) {
                if (test.getValue() > maxValueTest.getValue()) {
                    maxValueTest = test;
                }
            }
            sb.append("District: ").append(district.getKey()).append('\n');
            sb.append(findPatient(maxValueTest.getPatientId())).append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "There are no sick patients anywhere in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Retrieves districts sorted by the number of sick patients within the specified number 
     * of days leading up to the given date. The result is formatted as a string listing each 
     * district and its sick patient count in descending order.
     *
     * @param date The reference date for calculating the range.
     * @param days The number of days before the reference date to include in the count.
     * @return A formatted string of districts and their sick patient counts, or a message if 
     *         there are no sick patients in the given timeframe.
     */
    public String findSortedDistrictsBySickPatients(LocalDate date, int days) {
        if (districts.isEmpty()) {
            return "There are no sick patients anywhere";
        }
        BinaryTree<SortedData> sortedDistricts = helperForLocationSorting(date, days, districts);

        StringBuilder sb = new StringBuilder();
        for (SortedData sd : sortedDistricts.inOrder()) {
            sb.append("District: ").append(sd.getSecondary())
            .append(" | Sick patients: ").append(sd.getPrimary())
            .append("\n");
        }
        return sb.isEmpty() ? "There are no sick patients anywhere in the given timeframe" : sb.toString();
    }

    /**
     * Returns a formatted string listing regions sorted by the number of unique sick 
     * patients within a specified date range.
     *
     * @param date The reference date for calculating the range.
     * @param days The number of days before the reference date to include in the count.
     * @return A string listing each region and its corresponding number of sick patients,
     *         sorted in descending order. If no regions have sick patients, an appropriate
     *         message is returned.
     */
    public String findSortedRegionsBySickPatients(LocalDate date, int days) {
        if (regions.isEmpty()) {
            return "There are no sick patients anywhere";
        }
        BinaryTree<SortedData> sortedRegions = helperForLocationSorting(date, days, regions);

        StringBuilder sb = new StringBuilder();
        for (SortedData sd : sortedRegions.inOrder()) {
            sb.append("Region: ").append(sd.getSecondary())
            .append(" | Sick patients: ").append(sd.getPrimary())
            .append("\n");
        }
        return sb.isEmpty() ? "There are no sick patients anywhere in the given timeframe" : sb.toString();
    }

    /**
     * Helper method to compute and sort locations (districts or regions) by the number 
     * of unique sick patients within a given date range.
     *
     * @param date The reference date for calculating the range.
     * @param days The number of days before the reference date to include in the count.
     * @param locations A BinaryTree containing LocationData objects to process.
     * @return A BinaryTree of SortedData objects representing locations sorted by the 
     *         number of unique sick patients in descending order.
     */
    private BinaryTree<SortedData> helperForLocationSorting (LocalDate date, int days, BinaryTree<LocationData> locations) {
        PcrTestData[] range = PcrTestData.dateRange(date, days);
        BinaryTree<SortedData> sortedLocations = new BinaryTree<>();

        for (LocationData location : locations.inOrder()) {
            if (location.getPositiveRecordByDate().isEmpty()) {
                continue;
            }
            List<PcrTestData> testsInRange = location.getPositiveRecordByDate().rangeFind(range[0], range[1]);
            if (testsInRange.isEmpty()) {
                continue;
            }
            BinaryTree<PatientData> uniquePatients = new BinaryTree<>();
            int uniquePatientCount = 0;

            for (PcrTestData test : testsInRange) {
                if (uniquePatients.insert(PatientData.lookup(test.getPatientId())) != null) {
                    uniquePatientCount++;
                }
            }
            sortedLocations.insert(new SortedData(uniquePatientCount, location.getKey()));
        }
        return sortedLocations;
    }

    /**
     * Retrieves all PCR tests for a specific workplace within a given date range.
     *
     * @param workplaceCode The unique identifier of the workplace.
     * @param min The start date of the range (inclusive).
     * @param max The end date of the range (inclusive).
     * @return A formatted string listing each test and its associated patient information.
     *         If no tests are found for the workplace or in the given timeframe, an
     *         appropriate message is returned.
     */
    public String findAllTestsForWorkplace(int workplaceCode, LocalDate min, LocalDate max) {
        WorkplaceData workplace = workplaces.find(WorkplaceData.lookup(workplaceCode));
        if (workplace == null || workplace.getRecordByDate().isEmpty()) {
            return "No tests found for this workplace";
        }
        PcrTestData[] range = PcrTestData.dateRange(min, max);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (PcrTestData test : workplace.getRecordByDate().rangeFind(range[0], range[1])) {
            sb.append(test.formatData()).append("\n");
            sb.append(findPatient(test.getPatientId()));
            sb.append("\n-----------------\n");
            count++;
        }
        if (count == 0) {
            return "No tests found for this workplace in the given timeframe";
        }
        sb.append("Total items: ").append(count);
        return sb.toString();
    }

    /**
     * Finds a PCR test by its unique identifier.
     *
     * @param tetsId The unique ID of the PCR test to search for.
     * @return A formatted string containing the test information if found;
     *         otherwise, returns "Test not found".
     */
    public String findTest(int tetsId) {
        PcrTestData test = records.find(PcrTestData.lookup(tetsId));
        return test != null ? test.formatData() : "Test not found";
    }

    /**
     * Finds a patient by their unique identifier.
     *
     * @param patientId The unique ID of the patient to search for.
     * @return A formatted string containing the patient's information if found;
     *         otherwise, returns "Patient not found".
     */
    public String findPatient(String patientId) {
        PatientData patient = patients.find(PatientData.lookup(patientId));
        return patient != null ? patient.formatData() : "Patient not found";
    }

    // Insert and Delete

    /**
     * Inserts a new patient into the system.
     *
     * @param id       The unique identifier of the patient.
     * @param name     The first name of the patient.
     * @param surname  The last name of the patient.
     * @param birthday The birth date of the patient.
     * @return {@code true} if the patient was successfully inserted; 
     *         {@code false} if a patient with the same ID already exists.
     */
    public boolean insertPatient(String id, String name, String surname, LocalDate birthday) {
        PatientData patient = new PatientData(id, name, surname, birthday);
        patient = patients.insert(patient);
        return patient != null;
    }

    /**
     * Deletes a patient and all associated test records from the system.
     *
     * @param id The unique identifier of the patient to delete.
     * @return {@code true} if the patient existed and was successfully deleted; 
     *         {@code false} if no patient with the given ID was found.
     */
    public boolean deletePatient(String id) {
        PatientData patient = patients.delete(PatientData.lookup(id));
        if (patient == null) {
            return false;
        }
        for (PcrTestData test : patient.getRecords().inOrder()) {
            records.delete(test);
            recordsByDate.delete(test);
            if (test.getResult()) {
                positiveRecordsByDate.delete(test);
            }
            regions.find(LocationData.lookup(test.getRegion())).removeTest(test);
            districts.find(LocationData.lookup(test.getDistrict())).removeTest(test);
            workplaces.find(WorkplaceData.lookup(test.getWorkplace())).removeTest(test);
        }
        return true;
    }

    /**
     * Inserts a new PCR test record into the system and associates it with
     * the corresponding patient, workplace, district, and region.
     *
     * @param id The unique identifier of the test.
     * @param patientId The ID of the patient who took the test.
     * @param dateTime The date and time when the test was performed.
     * @param workplaceId The ID of the workplace where the test was performed.
     * @param districtCode The district code for the location of the test.
     * @param regionCode The region code for the location of the test.
     * @param result {@code true} if the test result is positive; {@code false} otherwise.
     * @param value The numerical value associated with the test.
     * @param note Any additional notes or comments related to the test.
     * @return {@code true} if the test was successfully inserted; {@code false} if the patient does not exist 
     *         or the test ID is already present in the system.
     */
    public boolean insertTest(int id, String patientId, LocalDateTime dateTime, int workplaceId, int districtCode, int regionCode, boolean result, double value, String note) {
        PcrTestData test = new PcrTestData(id, patientId, dateTime, workplaceId, districtCode, regionCode, result, value, note);
        PatientData patient = patients.find(PatientData.lookup(patientId));
        if (patient == null) {
            return false;
        }
        test = records.insert(test);
        if (test == null) {
            return false;
        }
        recordsByDate.insert(test);
        if (result) {
           positiveRecordsByDate.insert(test); 
        }
        patient.addTest(test);
        regions.getOrInsert(LocationData.lookup(regionCode)).addTest(test);
        districts.getOrInsert(LocationData.lookup(districtCode)).addTest(test);
        workplaces.getOrInsert(WorkplaceData.lookup(workplaceId)).addTest(test);

        return true;
    }

    /**
     * Deletes a PCR test from the system and removes it from all associated records.
     *
     * @param id The unique identifier of the test to be deleted.
     * @return {@code true} if the test was successfully found and deleted; {@code false} if the test does not exist.
     */
    public boolean deleteTest(int id) {
        PcrTestData test = records.delete(PcrTestData.lookup(id));
        if (test == null) {
            return false;
        }
        recordsByDate.delete(test);
        if (test.getResult()) {
           positiveRecordsByDate.delete(test);
        }
        regions.find(LocationData.lookup(test.getRegion())).removeTest(test);
        districts.find(LocationData.lookup(test.getDistrict())).removeTest(test);
        workplaces.find(WorkplaceData.lookup(test.getWorkplace())).removeTest(test);
        patients.find(PatientData.lookup(test.getPatientId())).removeTest(test);

        return true;
    }

    // Import and Export

    /**
     * Loads patient data from a CSV file and inserts each patient into the system.
     *
     * @param file The path to the CSV file containing patient data.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public void loadPatients(String file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                insertPatient(fields[0], fields[1], fields[2], LocalDate.parse(fields[3]));
            }
        }
    }

    /**
     * Saves all patients in the system to a CSV file in level-order traversal.
     *
     * @param file The path to the CSV file where patient data will be saved.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void savePatients(String file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (PatientData patient : patients.levelOrder()) {
                writer.write(patient.formatToCsv());
                writer.newLine();
            }
        }
    }

    /**
     * Loads PCR test records from a CSV file and inserts them into the system.
     *
     * @param file The path to the CSV file to read tests from.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public void loadTests(String file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";", -1);
                insertTest(
                    Integer.parseInt(fields[0]),
                    fields[1],
                    LocalDateTime.parse(fields[2]),
                    Integer.parseInt(fields[3]),
                    Integer.parseInt(fields[4]),
                    Integer.parseInt(fields[5]),
                    Boolean.parseBoolean(fields[6]),
                    Double.parseDouble(fields[7]),
                    fields[8]
                );
            }
        }
    }

    /**
     * Saves all PCR test records in the system to a CSV file.
     *
     * @param file The path to the CSV file to write tests to.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void saveTests(String file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (PcrTestData test : records.levelOrder()) {
                writer.write(test.formatToCsv());
                writer.newLine();
            }
        }
    }
}
