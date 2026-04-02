package project.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;

import project.core.system.DemoSystem;

/**
 * The main graphical user interface for the PCR System.
 *
 * This class extends {@link JFrame} and implements {@link ActionListener} to provide
 * a windowed interface for interacting with the {@link DemoSystem}. It includes menus
 * for managing patients and tests, performing searches, and accessing help information.
 *
 * The content panel displays dynamic information based on user actions.
 */
public final class MainFrame extends JFrame implements ActionListener {
    private final DemoSystem system;

    private JMenuBar menuBar;
    private JMenuItem loadItem;
    private JMenuItem saveItem;
    private JMenuItem generateItem;
    private JMenuItem insertPatientItem;
    private JMenuItem findPatientItem;
    private JMenuItem deletePatientItem;
    private JMenuItem insertTestItem;
    private JMenuItem findTestItem;
    private JMenuItem deleteTestItem;
    private JMenuItem advancedSearchItem;
    private JMenuItem helpPatientItem;
    private JMenuItem helpTestItem;

    private final JPanel contentPanel;

    public MainFrame() {
        this.setTitle("PCR System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new FlowLayout());
        this.setResizable(false);

        // Initialize the system
        system = new DemoSystem();

        this.setJMenuBar(createMenuBar());
        contentPanel = new JPanel(new FlowLayout());
        this.add(contentPanel, BorderLayout.CENTER);
        showHomePage();
    }

    private JMenuBar createMenuBar() {
        menuBar = new JMenuBar();

        // System
        JMenu systemMenu = new JMenu("System");
        loadItem = new JMenuItem("Load");
        saveItem = new JMenuItem("Save");
        generateItem = new JMenuItem("Generate");
        loadItem.addActionListener(this);
        saveItem.addActionListener(this);
        generateItem.addActionListener(this);
        systemMenu.add(loadItem);
        systemMenu.add(saveItem);
        systemMenu.add(generateItem);

        // Patients
        JMenu patientMenu = new JMenu("Patients");
        insertPatientItem = new JMenuItem("New patient");
        findPatientItem = new JMenuItem("Find patient");
        deletePatientItem = new JMenuItem("Delete patient");
        insertPatientItem.addActionListener(this);
        findPatientItem.addActionListener(this);
        deletePatientItem.addActionListener(this);
        patientMenu.add(insertPatientItem);
        patientMenu.add(findPatientItem);
        patientMenu.add(deletePatientItem);

        // Tests
        JMenu testMenu = new JMenu("Tests");
        insertTestItem = new JMenuItem("New test");
        findTestItem = new JMenuItem("Find test");
        deleteTestItem = new JMenuItem("Delete test");
        insertTestItem.addActionListener(this);
        findTestItem.addActionListener(this);
        deleteTestItem.addActionListener(this);
        testMenu.add(insertTestItem);
        testMenu.add(findTestItem);
        testMenu.add(deleteTestItem);

        // Search
        JMenu searchMenu = new JMenu("Search");
        advancedSearchItem = new JMenuItem("Advanced search");
        advancedSearchItem.addActionListener(this);
        searchMenu.add(advancedSearchItem);

        // Help
        JMenu helpMenu = new JMenu("Help");
        helpPatientItem = new JMenuItem("Patient data info");
        helpTestItem = new JMenuItem("Test data info");
        helpPatientItem.addActionListener(this);
        helpTestItem.addActionListener(this);
        helpMenu.add(helpPatientItem);
        helpMenu.add(helpTestItem);

        menuBar.add(systemMenu);
        menuBar.add(patientMenu);
        menuBar.add(testMenu);
        menuBar.add(searchMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == loadItem) {
                handleLoad();
            } else if (src == saveItem) {
                handleSave();
            } else if (src == generateItem) {
                promptGenerate();
            } else if (src == insertPatientItem) {
                showInsertPatientForm();
            } else if (src == findPatientItem) {
                promptFindPatient();
            } else if (src == deletePatientItem) {
                promptDeletePatient();
            } else if (src == insertTestItem) {
                showInsertTestForm();
            } else if (src == findTestItem) {
                promptFindTest();
            } else if (src == deleteTestItem) {
                promptDeleteTest();
            } else if (src == advancedSearchItem) {
                promptAdvancedSearch();
            } else if (src == helpPatientItem) {
                showPatientDataInfo();
            } else if (src == helpTestItem) {
                showTestDataInfo();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "IO error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date/time parse error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoad() throws IOException {
        String[] options = {"Patients", "Tests"};
        String choice = (String) JOptionPane.showInputDialog(this, "What do you want to load?", "Load", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return;

        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (choice.equals("Patients")) {
                system.loadPatients(f.getAbsolutePath());
            } else {
                system.loadTests(f.getAbsolutePath());
            }
            JOptionPane.showMessageDialog(this, "Loaded " + choice + " from " + f.getName(), "Loaded", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSave() throws IOException {
        String[] options = {"Patients", "Tests"};
        String choice = (String) JOptionPane.showInputDialog(this, "What do you want to save?", "Save", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return;

        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (choice.equals("Patients")) {
                system.savePatients(f.getAbsolutePath());
            } else {
                system.saveTests(f.getAbsolutePath());
            }
            JOptionPane.showMessageDialog(this, "Saved " + choice + " to " + f.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void promptFindPatient() {
        String id = JOptionPane.showInputDialog(this, "Enter patient ID (birth number):");
        if (id == null || id.trim().isEmpty()) return;
        String res = system.findPatient(id.trim());
        showQueryResult("Patient " + id, res);
    }

    private void promptDeletePatient() {
        String id = JOptionPane.showInputDialog(this, "Enter patient ID to delete (birth number):");
        if (id != null && !id.trim().isEmpty()) {
            boolean ok = system.deletePatient(id.trim());
            JOptionPane.showMessageDialog(this, ok ? "Patient deleted" : "Delete failed (not found)", "Delete Patient", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        }
    }

    private void promptFindTest() {
        String idStr = JOptionPane.showInputDialog(this, "Enter test id (int):");
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr.trim());
            String res = system.findTest(id);
            showQueryResult("Test " + id, res);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid id", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void promptDeleteTest() {
        String idStr = JOptionPane.showInputDialog(this, "Enter test id to delete (int):");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean ok = system.deleteTest(id);
                JOptionPane.showMessageDialog(this, ok ? "Test deleted" : "Delete failed (not found)", "Delete Test", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid id", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void promptAdvancedSearch() {
        String[] options = {
            "All tests",
            "All positive tests",
            "Tests for district",
            "Tests for region",
            "Tests for workplace",
            "All positive tests for district",
            "All positive tests for region",
            "Sick patients for district",
            "Sorted sick patients for district",
            "Sick patients for region",
            "All sick patients",
            "Sorted districts by sick patients",
            "Sorted regions by sick patients",
            "Most sick patient per district"
        };
        String choice = (String) JOptionPane.showInputDialog(this, "Select search", "Advanced Search", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return;

        try {
            switch (choice) {
                case "All tests" -> {
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("All tests", system.findAllTests(min, max));
                }
                case "All positive tests" -> {
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("Positive tests", system.findAllPositiveTests(min, max));
                }
                case "Tests for district" -> {
                    int district = Integer.parseInt(JOptionPane.showInputDialog(this, "District code:"));
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("Tests for district " + district, system.findAllTestsForDistrict(district, min, max));
                }
                case "Tests for region" -> {
                    int region = Integer.parseInt(JOptionPane.showInputDialog(this, "Region code:"));
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("Tests for region " + region, system.findAllTestsForRegion(region, min, max));
                }
                case "Tests for workplace" -> {
                    int wp = Integer.parseInt(JOptionPane.showInputDialog(this, "Workplace code:"));
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("Workplace tests", system.findAllTestsForWorkplace(wp, min, max));
                }
                case "All positive tests for district" -> {
                    int district = Integer.parseInt(JOptionPane.showInputDialog(this, "District code:"));
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("Positive tests for district " + district, system.findAllPositiveTestsForDistrict(district, min, max));
                }
                case "All positive tests for region" -> {
                    int region = Integer.parseInt(JOptionPane.showInputDialog(this, "Region code:"));
                    LocalDate min = promptForDate("Min date:");
                    if (min == null) return;
                    LocalDate max = promptForDate("Max date:");
                    if (max == null) return;
                    showQueryResult("Positive tests for region " + region, system.findAllPositiveTestsForRegion(region, min, max));
                }
                case "Sick patients for district" -> {
                    int district = Integer.parseInt(JOptionPane.showInputDialog(this, "District code:"));
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("Sick patients", system.findAllSickPatientsForDistrict(district, date, days));
                }
                case "Sorted sick patients for district" -> {
                    int district = Integer.parseInt(JOptionPane.showInputDialog(this, "District code:"));
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("Sorted sick patients", system.findAllSortedSickPatientsForDistrict(district, date, days));
                }
                case "Sick patients for region" -> {
                    int region = Integer.parseInt(JOptionPane.showInputDialog(this, "Region code:"));
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("Sick patients for region", system.findAllSickPatientsForRegion(region, date, days));
                }
                case "All sick patients" -> {
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("All sick patients", system.findAllSickPatients(date, days));
                }
                case "Sorted districts by sick patients" -> {
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("Sorted districts", system.findSortedDistrictsBySickPatients(date, days));
                }
                case "Sorted regions by sick patients" -> {
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("Sorted regions", system.findSortedRegionsBySickPatients(date, days));
                }
                case "Most sick patient per district" -> {
                    LocalDate date = promptForDate("Reference date (end):");
                    if (date == null) return;
                    int days = Integer.parseInt(JOptionPane.showInputDialog(this, "Days back:"));
                    showQueryResult("Most sick patients", system.findMostSickPatientForDistrict(date, days));
                }
                default -> JOptionPane.showMessageDialog(this, "Not implemented", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // helper: show a date picker dialog using JSpinner.DateEditor, returns null on cancel
    private java.time.LocalDate promptForDate(String title) {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(220, 30));

        int res = JOptionPane.showConfirmDialog(this, spinner, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) {
            return null;
        }
        Date selected = (Date) spinner.getValue();
        return Instant.ofEpochMilli(selected.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void showHomePage() {
        contentPanel.removeAll();
        
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));

        JPanel homePage = new JPanel();
        homePage.setLayout(new BoxLayout(homePage, BoxLayout.Y_AXIS));
        homePage.setBackground(new Color(250, 250, 250));
        homePage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel rightTitle = new JLabel("Quick Search");
        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField patientSearch = new JTextField();
        patientSearch.setMaximumSize(new Dimension(200, 30));
        patientSearch.setBorder(BorderFactory.createTitledBorder("Patient ID"));

        JButton patientSearchBtn = new JButton("Find patient tests");
        patientSearchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        patientSearchBtn.addActionListener(e -> {
            String id = patientSearch.getText().trim();
            if (!id.isEmpty()) {
                showQueryResult("Patient tests for " + id, system.findAllTestsForPatient(id));
                // patientSearch.setText("");
            }
        });

        JTextField testSearch = new JTextField();
        testSearch.setMaximumSize(new Dimension(200, 30));
        testSearch.setBorder(BorderFactory.createTitledBorder("Test ID"));

        JButton testSearchBtn = new JButton("Find test");
        testSearchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        testSearchBtn.addActionListener(e -> {
            String s = testSearch.getText().trim();
            if (!s.isEmpty()) {
                try {
                    int id = Integer.parseInt(s);
                    showQueryResult("Test " + id, system.findTest(id));
                    // testSearch.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid test id", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton findTestForPatientBtn = new JButton("Find patient's test");
        findTestForPatientBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        findTestForPatientBtn.addActionListener(e -> {
            String pid = patientSearch.getText().trim();
            String tid = testSearch.getText().trim();
            if (pid.isEmpty() || tid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter both Patient ID and Test ID", "Missing input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int testId = Integer.parseInt(tid);
                String res = system.findTestForPatient(testId, pid);
                showQueryResult("Test " + testId + " for " + pid, res);
                // patientSearch.setText("");
                // testSearch.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid test id", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel statusTitle = new JLabel("Info / Tips");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextArea statusArea = new JTextArea(8, 30);
        statusArea.setLineWrap(false);
        statusArea.setWrapStyleWord(true);
        statusArea.setEditable(false);
        statusArea.setText("""
            - Use 'New patient' to add patients
            - Use 'New test' to add tests (ensure patient exists first)
            - Load system data via System -> Load
            - Save system data into files via System -> Save
            - Populate system with random data via System -> Generate
            - Use Search -> Advanced search for specialized queries

            """
                       );
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        statusScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        homePage.add(rightTitle);
        homePage.add(patientSearch);
        homePage.add(patientSearchBtn);
        homePage.add(Box.createVerticalStrut(6));
        homePage.add(testSearch);
        homePage.add(testSearchBtn);
        homePage.add(Box.createVerticalStrut(6));
        homePage.add(findTestForPatientBtn);
        homePage.add(Box.createVerticalStrut(8));
        homePage.add(statusTitle);
        homePage.add(statusScroll);

        contentPanel.add(homePage, BorderLayout.EAST);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPatientDataInfo() {
        contentPanel.removeAll();

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        infoArea.setText("""
        
            PATIENT DATA STRUCTURE:  
            -----------------------
            id          : Birth Number (String)
            name        : First name (String)
            surname     : Last name (String)
            birthday    : Date of birth (LocalDate)\t

            DATA PREVIEW:
            -------------
            id          : 071022/6978
            name        : John
            surname     : Doe
            birthday    : 22.10.2007
        """);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTestDataInfo() {
        contentPanel.removeAll();

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        infoArea.setText("""
                
            PCR TEST DATA STRUCTURE:
            -------------------------
            id          : Unique test ID (int)
            dateTime    : Date and time of test (LocalDateTime)
            patientId   : ID of the tested patient (String)
            workplaceId : Workplace identifier (int)
            district    : District code (int)
            region      : Region code (int) 
            result      : Test result (boolean, true = positive)\t
            value       : Numerical test value (double)
            note        : Optional comment (String)

            DATA PREVIEW:
            -------------
            id          : 120231
            dateTime    : 2025-11-01T09:42
            patientId   : 071022/6978
            workplaceId : 24231
            district    : 13
            region      : 2
            result      : true
            value       : 34.42
            note        : "Slight symptoms, retest recommended."
        """);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showQueryResult(String title, String text) {
        JTextArea area = new JTextArea(20, 60);
        area.setEditable(false);
        area.setText(text);
        JScrollPane sp = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, sp, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInsertPatientForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 200, 20, 200));
        form.setBackground(new Color(245,245,245));

        JLabel title = new JLabel("Insert New Patient", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField idField = new JTextField();
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        idField.setBorder(BorderFactory.createTitledBorder("Birth Number (id)"));
        
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setBorder(BorderFactory.createTitledBorder("First name"));
        
        JTextField surnameField = new JTextField();
        surnameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        surnameField.setBorder(BorderFactory.createTitledBorder("Last name"));

        SpinnerDateModel birthdayModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner birthdaySpinner = new JSpinner(birthdayModel);
        JSpinner.DateEditor birthdayEditor = new JSpinner.DateEditor(birthdaySpinner, "yyyy-MM-dd");
        birthdaySpinner.setEditor(birthdayEditor);
        birthdaySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        birthdaySpinner.setBorder(BorderFactory.createTitledBorder("Birthday (YYYY-MM-DD)"));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton insertBtn = new JButton("Insert");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(insertBtn);
        buttons.add(cancelBtn);

        form.add(title);
        form.add(idField);
        form.add(nameField);
        form.add(surnameField);
        form.add(birthdaySpinner);
        form.add(buttons);

        insertBtn.addActionListener(ae -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            Date bdDate = (Date) birthdaySpinner.getValue();
            if (id.isEmpty() || name.isEmpty() || surname.isEmpty() || bdDate == null) {
                JOptionPane.showMessageDialog(this, "Fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                LocalDate birthday = Instant.ofEpochMilli(bdDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                // validate ID format: YYMMDD/NNNN where YYMMDD derived from birthday (year%100, month, day)
                String expectedPrefix = String.format("%02d%02d%02d", birthday.getYear() % 100, birthday.getMonthValue(), birthday.getDayOfMonth());
                if (!id.matches("\\d{6}/\\d{4}")) {
                    JOptionPane.showMessageDialog(this, "Invalid ID format. Expected pattern: YYMMDD/NNNN (e.g. 071022/6719)", "Invalid ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String actualPrefix = id.substring(0, 6);
                if (!actualPrefix.equals(expectedPrefix)) {
                    JOptionPane.showMessageDialog(this, "ID prefix does not match birthday. Expected prefix: " + expectedPrefix, "Invalid ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean ok = system.insertPatient(id, name, surname, birthday);
                JOptionPane.showMessageDialog(this, ok ? "Patient inserted" : "Insert failed (duplicate id or invalid data)", "Insert result:", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException | DateTimeParseException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ae -> showHomePage());

        Runnable updateId = () -> {
            Date bdDate = birthdayModel.getDate();
            LocalDate birthday = Instant.ofEpochMilli(bdDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            String prefix = String.format("%02d%02d%02d/", 
                    birthday.getYear() % 100, 
                    birthday.getMonthValue(), 
                    birthday.getDayOfMonth());

            int suffix = ThreadLocalRandom.current().nextInt(0, 10_000);
            String id = prefix + String.format("%04d", suffix);

            idField.setText(id);
        };

        updateId.run();
        birthdayModel.addChangeListener(e -> updateId.run());

        contentPanel.add(form, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showInsertTestForm() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 200, 10, 200));
        form.setBackground(new Color(245,245,245));

        JLabel title = new JLabel("      Insert New Test      ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField idField = new JTextField(); idField.setBorder(BorderFactory.createTitledBorder("Test id (int)")); idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        idField.setText(String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE)));
        JTextField patientField = new JTextField(); patientField.setBorder(BorderFactory.createTitledBorder("Patient ID (birth number)")); patientField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        SpinnerDateModel dtModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        JSpinner dtSpinner = new JSpinner(dtModel);
        JSpinner.DateEditor dtEditor = new JSpinner.DateEditor(dtSpinner, "yyyy-MM-dd HH:mm");
        dtSpinner.setEditor(dtEditor);
        dtSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dtSpinner.setBorder(BorderFactory.createTitledBorder("DateTime (yyyy-MM-dd HH:mm)"));

        JTextField workplaceField = new JTextField(); workplaceField.setBorder(BorderFactory.createTitledBorder("Workplace id (int)")); workplaceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JTextField districtField = new JTextField(); districtField.setBorder(BorderFactory.createTitledBorder("District code (int)")); districtField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JTextField regionField = new JTextField(); regionField.setBorder(BorderFactory.createTitledBorder("Region code (int)")); regionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JTextField resultField = new JTextField(); resultField.setBorder(BorderFactory.createTitledBorder("Result (true/false)")); resultField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JTextField valueField = new JTextField(); valueField.setBorder(BorderFactory.createTitledBorder("Value (double)")); valueField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JTextField noteField = new JTextField(); noteField.setBorder(BorderFactory.createTitledBorder("Note (optional)")); noteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton insertBtn = new JButton("Insert");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(insertBtn);
        buttons.add(cancelBtn);

        form.add(title);
        form.add(idField);
        form.add(patientField);
        form.add(dtSpinner);
        form.add(workplaceField);
        form.add(districtField);
        form.add(regionField);
        form.add(resultField);
        form.add(valueField);
        form.add(noteField);
        form.add(buttons);

        insertBtn.addActionListener(ae -> {
            try {
                if (idField.getText().isBlank() || patientField.getText().isBlank() || workplaceField.getText().isBlank() ||
                    districtField.getText().isBlank() || regionField.getText().isBlank() || resultField.getText().isBlank() || valueField.getText().isBlank()) {
                    JOptionPane.showMessageDialog(this, "Fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int id = Integer.parseInt(idField.getText().trim());
                String patientId = patientField.getText().trim();
                Date dtDate = (Date) dtSpinner.getValue();
                LocalDateTime dt = Instant.ofEpochMilli(dtDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                int workplace = Integer.parseInt(workplaceField.getText().trim());
                int district = Integer.parseInt(districtField.getText().trim());
                int region = Integer.parseInt(regionField.getText().trim());
                boolean result = Boolean.parseBoolean(resultField.getText().trim());
                double value = Double.parseDouble(valueField.getText().trim());
                String note = noteField.getText() == null ? "" : noteField.getText().trim();

                int expectedMinDistrict = (region - 1) * 10 + 1;
                int expectedMaxDistrict = region * 10;

                if (district < expectedMinDistrict || district > expectedMaxDistrict) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid district for region " + region + ".\n" +
                        "Valid districts: " + expectedMinDistrict + "–" + expectedMaxDistrict,
                        "Invalid Region/District",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean ok = system.insertTest(id, patientId, dt, workplace, district, region, result, value, note);
                JOptionPane.showMessageDialog(this, ok ? "Test inserted" : "Insert failed (duplicate id or missing patient)", "Insert result:", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException | DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ae -> showHomePage());

        contentPanel.add(form, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void promptGenerate() {
        String pStr = JOptionPane.showInputDialog(this, "Number of patients to generate:");
        if (pStr == null) return;
        String tStr = JOptionPane.showInputDialog(this, "Number of tests to generate:");
        if (tStr == null) return;

        int patientsToGen;
        int testsToGen;
        try {
            patientsToGen = Integer.parseInt(pStr.trim());
            testsToGen = Integer.parseInt(tStr.trim());
            if (patientsToGen < 0 || testsToGen < 0) throw new NumberFormatException("Negative values not allowed");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid numbers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();

            // generate patients
            List<String> createdPatientIds = new ArrayList<>();
            Set<String> patientIdSet = new HashSet<>();

            long startDay = LocalDate.of(1950, 1, 1).toEpochDay();
            long endDay = LocalDate.of(2020, 12, 31).toEpochDay();

            int createdPatients = 0;
            int patientAttempts = 0;
            while (createdPatients < patientsToGen && patientAttempts < patientsToGen * 10 + 1000) {
                long randomDay = rnd.nextLong(startDay, endDay + 1);
                LocalDate birthday = LocalDate.ofEpochDay(randomDay);
                String prefix = String.format("%02d%02d%02d", birthday.getYear() % 100, birthday.getMonthValue(), birthday.getDayOfMonth());

                String id;
                int suffix = rnd.nextInt(0, 10_000);
                id = prefix + "/" + String.format("%04d", suffix);

                if (patientIdSet.contains(id)) {
                    patientAttempts++;
                    continue;
                }

                String name = "Name" + (createdPatients + 1);
                String surname = "Surname" + (createdPatients + 1);

                boolean ok = system.insertPatient(id, name, surname, birthday);
                patientAttempts++;
                if (ok) {
                    patientIdSet.add(id);
                    createdPatientIds.add(id);
                    createdPatients++;
                }
            }

            // generate tests
            Set<Integer> usedTestIds = new HashSet<>();
            int createdTests = 0;
            int testAttempts = 0;

            long startEpoch = LocalDate.of(2000, 1, 1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            long endEpoch = Instant.now().getEpochSecond();

            if (createdPatientIds.isEmpty() && testsToGen > 0) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "No patients created; tests skipped.", "Generate result", JOptionPane.INFORMATION_MESSAGE)
                );
                return;
            }

            while (createdTests < testsToGen && testAttempts < testsToGen * 20 + 1000) {
                int testId = rnd.nextInt(1, Integer.MAX_VALUE);
                if (usedTestIds.contains(testId)) {
                    testAttempts++;
                    continue;
                }

                long randomSec = rnd.nextLong(startEpoch, endEpoch + 1);
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(randomSec), ZoneId.systemDefault());

                int workplaceId = rnd.nextInt(1, 1000);
                int region = rnd.nextInt(1, 11); // 1..10
                int district = rnd.nextInt((region - 1) * 10 + 1, region * 10 + 1); // region = 1 -> district 1-10, region = 2 -> district 11-20, ...
                boolean result = rnd.nextBoolean();
                double value = Math.round((1.0 + rnd.nextDouble() * 99.0) * 100.0) / 100.0;

                String patientId = createdPatientIds.get(rnd.nextInt(createdPatientIds.size()));

                boolean ok = system.insertTest(testId, patientId, dateTime, workplaceId, district, region, result, value, "");
                testAttempts++;
                if (ok) {
                    usedTestIds.add(testId);
                    createdTests++;
                }
            }

            final int pDone = createdPatients;
            final int tDone = createdTests;
            javax.swing.SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, "Generation completed:\nPatients created: " + pDone + "\nTests created: " + tDone, "Generate result", JOptionPane.INFORMATION_MESSAGE)
            );
        }).start();
    }
}