import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancedHostelSystem {
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private static final String ADMIN_USER = "admin";
    private String adminPassword = "admin123";
    private static final String DATA_FILE = "hostellerData.dat";
    private static final String ROOM_COUNTER_FILE = "roomCounter.txt";

    private ArrayList<Hosteller> hostellerList = new ArrayList<>();
    private int hostellerIdCounter = 1;
    private int roomCounter = 100;
    private ManageHostellerPanel managePanel;

    public AdvancedHostelSystem() {
        loadRoomCounter();
        loadHostellerData();
        showLoginScreen();
    }

    private void loadRoomCounter() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOM_COUNTER_FILE))) {
            roomCounter = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            // Default to 100 if the room counter file doesn't exist or is corrupted
            roomCounter = 100;
        }
    }

    private void saveRoomCounter() {
        // Update the room counter from the manage panel if it exists
        if (managePanel != null) {
            roomCounter = managePanel.getNextRoomNumber();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOM_COUNTER_FILE))) {
            writer.write(String.valueOf(roomCounter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHostellerData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(hostellerList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error saving data: " + e.getMessage());
        }
    }

    private void loadHostellerData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                hostellerList = (ArrayList<Hosteller>) ois.readObject();
                if (!hostellerList.isEmpty()) {
                    hostellerIdCounter = hostellerList.get(hostellerList.size() - 1).getId() + 1;
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(mainFrame, "Error loading data: " + e.getMessage());
            }
        }
    }

    private void showLoginScreen() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (user.equals(ADMIN_USER) && pass.equals(adminPassword)) {
                initMainUI();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials. Exiting.");
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    private void initMainUI() {
        mainFrame = new JFrame("Amrita Hostel Management System - Advanced");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 600);

        tabbedPane = new JTabbedPane();

        // Create the dashboard panel
        tabbedPane.addTab("Dashboard", new DashboardPanel(hostellerList));

        // Create the manage hosteller panel
        managePanel = new ManageHostellerPanel(hostellerList, roomCounter);
        tabbedPane.addTab("Manage Hostellers", managePanel);

        // Create other panels
        tabbedPane.addTab("Search", createSearchPanel());
        tabbedPane.addTab("Import/Export", createImportExportPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());

        // Add window listener to save data on close
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveHostellerData();
                saveRoomCounter();
            }
        });

        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        
        topPanel.add(new JLabel("Search by Name/Department:"), BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);
        
        JTextArea searchResultsArea = new JTextArea();
        searchResultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(searchResultsArea);

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            searchResultsArea.setText("");

            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(searchPanel, "Please enter a search term");
                return;
            }

            boolean found = false;
            for (Hosteller h : hostellerList) {
                if (h.getName().toLowerCase().contains(searchTerm) || 
                    h.getDepartment().toLowerCase().contains(searchTerm)) {
                    searchResultsArea.append(h.toString() + "\n");
                    found = true;
                }
            }
            
            if (!found) {
                searchResultsArea.setText("No results found for: " + searchTerm);
            }
        });

        searchPanel.add(topPanel, BorderLayout.NORTH);
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        return searchPanel;
    }

    private JPanel createImportExportPanel() {
        JPanel importExportPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        importExportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton exportButton = new JButton("Export to CSV");
        JButton importButton = new JButton("Import from CSV");
        JLabel statusLabel = new JLabel("Import/Export Status: Ready");

        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            int userSelection = fileChooser.showSaveDialog(importExportPanel);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                exportToCSV(fileToSave.getAbsolutePath(), statusLabel);
            }
        });

        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open CSV File");
            int userSelection = fileChooser.showOpenDialog(importExportPanel);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();
                importFromCSV(fileToOpen.getAbsolutePath(), statusLabel);
            }
        });

        importExportPanel.add(exportButton);
        importExportPanel.add(importButton);
        importExportPanel.add(statusLabel);

        return importExportPanel;
    }

    private void exportToCSV(String filePath, JLabel statusLabel) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Name,Age,Room No,Fees Paid,Department,Year");
            writer.newLine();
            
            // Write data
            for (Hosteller h : hostellerList) {
                writer.write(String.format("%d,%s,%d,%d,%.2f,%s,%d",
                    h.getId(), h.getName(), h.getAge(), h.getRoomNo(),
                    h.getFeesPaid(), h.getDepartment(), h.getYear()));
                writer.newLine();
            }
            
            statusLabel.setText("Export Status: Successfully exported to " + filePath);
        } catch (IOException e) {
            statusLabel.setText("Export Status: Error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void importFromCSV(String filePath, JLabel statusLabel) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header
            
            if (line == null) {
                statusLabel.setText("Import Status: Error - Empty file");
                return;
            }
            
            ArrayList<Hosteller> importedList = new ArrayList<>();
            int maxId = 0;
            int maxRoomNo = roomCounter;
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;
                
                try {
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int age = Integer.parseInt(data[2]);
                    int roomNo = Integer.parseInt(data[3]);
                    double feesPaid = Double.parseDouble(data[4]);
                    String dept = data[5];
                    int year = Integer.parseInt(data[6]);
                    
                    Hosteller h = new Hosteller(id, name, age, roomNo, feesPaid, dept, year);
                    importedList.add(h);
                    
                    if (id > maxId) maxId = id;
                    if (roomNo > maxRoomNo) maxRoomNo = roomNo;
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
            
            if (!importedList.isEmpty()) {
                hostellerList.clear();
                hostellerList.addAll(importedList);
                hostellerIdCounter = maxId + 1;
                roomCounter = maxRoomNo + 1;
                
                saveHostellerData();
                saveRoomCounter();
                
                // Update the dashboard
                DashboardPanel dashboardPanel = (DashboardPanel) tabbedPane.getComponentAt(0);
                dashboardPanel.updateDashboard(hostellerList);
                
                statusLabel.setText("Import Status: Successfully imported " + importedList.size() + " records");
            } else {
                statusLabel.setText("Import Status: No valid records found");
            }
        } catch (IOException e) {
            statusLabel.setText("Import Status: Error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField newPasswordField = new JTextField(adminPassword);
        JButton changePasswordButton = new JButton("Change Password");
        
        JTextField startRoomField = new JTextField(String.valueOf(roomCounter));
        JButton updateRoomCounterButton = new JButton("Update Room Counter");
        
        JButton backupButton = new JButton("Backup Data");
        JButton restoreButton = new JButton("Restore Data");
        
        changePasswordButton.addActionListener(e -> {
            String newPass = newPasswordField.getText().trim();
            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(settingsPanel, "Password cannot be empty");
            } else {
                adminPassword = newPass;
                JOptionPane.showMessageDialog(settingsPanel, "Password changed successfully!");
            }
        });
        
        updateRoomCounterButton.addActionListener(e -> {
            try {
                int newRoomCounter = Integer.parseInt(startRoomField.getText().trim());
                if (newRoomCounter < 1) {
                    JOptionPane.showMessageDialog(settingsPanel, "Room counter must be positive");
                } else {
                    roomCounter = newRoomCounter;
                    saveRoomCounter();
                    JOptionPane.showMessageDialog(settingsPanel, "Room counter updated!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsPanel, "Please enter a valid number");
            }
        });
        
        backupButton.addActionListener(e -> {
            try {
                File backup = new File(DATA_FILE + ".backup");
                try (
                    FileInputStream in = new FileInputStream(DATA_FILE);
                    FileOutputStream out = new FileOutputStream(backup)
                ) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                JOptionPane.showMessageDialog(settingsPanel, "Backup created successfully");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(settingsPanel, "Backup failed: " + ex.getMessage());
            }
        });
        
        restoreButton.addActionListener(e -> {
            File backup = new File(DATA_FILE + ".backup");
            if (!backup.exists()) {
                JOptionPane.showMessageDialog(settingsPanel, "No backup file found");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(settingsPanel, 
                "This will overwrite current data. Continue?", 
                "Confirm Restore", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    File current = new File(DATA_FILE);
                    try (
                        FileInputStream in = new FileInputStream(backup);
                        FileOutputStream out = new FileOutputStream(current)
                    ) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                    
                    // Reload data
                    loadHostellerData();
                    // Update dashboard
                    DashboardPanel dashboardPanel = (DashboardPanel) tabbedPane.getComponentAt(0);
                    dashboardPanel.updateDashboard(hostellerList);
                    
                    JOptionPane.showMessageDialog(settingsPanel, "Data restored successfully");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(settingsPanel, "Restore failed: " + ex.getMessage());
                }
            }
        });

        settingsPanel.add(new JLabel("New Admin Password:"));
        settingsPanel.add(newPasswordField);
        settingsPanel.add(changePasswordButton);
        settingsPanel.add(new JLabel()); // Empty cell for spacing
        
        settingsPanel.add(new JLabel("Starting Room Number:"));
        settingsPanel.add(startRoomField);
        settingsPanel.add(updateRoomCounterButton);
        settingsPanel.add(new JLabel()); // Empty cell for spacing
        
        settingsPanel.add(backupButton);
        settingsPanel.add(restoreButton);

        return settingsPanel;
    }

    public static void main(String[] args) {
        try {
            // Set Look and Feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(AdvancedHostelSystem::new);
    }
}