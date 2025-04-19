import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ManageHostellerPanel extends JPanel {
    private ArrayList<Hosteller> hostellerList;
    private int roomCounter;

    private JTextField nameField, ageField, feesField, deptField, yearField;
    private JTextArea displayArea;

    public ManageHostellerPanel(ArrayList<Hosteller> hostellerList, int roomCounter) {
        this.hostellerList = hostellerList;
        this.roomCounter = roomCounter;

        setLayout(new BorderLayout());

        // Input form
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Hosteller"));

        nameField = new JTextField();
        ageField = new JTextField();
        feesField = new JTextField();
        deptField = new JTextField();
        yearField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Fees Paid:"));
        formPanel.add(feesField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptField);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);

        JButton addButton = new JButton("Add Hosteller");
        JButton resetButton = new JButton("Reset Fields");
        JButton viewAllButton = new JButton("View All Hostellers");
        JButton deleteButton = new JButton("Delete Hosteller by ID");

        formPanel.add(addButton);
        formPanel.add(resetButton);
        formPanel.add(viewAllButton);
        formPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);

        // Display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                double fees = Double.parseDouble(feesField.getText().trim());
                String dept = deptField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());

                if (name.isEmpty() || dept.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and Department cannot be empty.");
                    return;
                }

                int id = generateNextId();
                int roomNo = getNextRoomNumber();

                Hosteller h = new Hosteller(id, name, age, roomNo, fees, dept, year);
                hostellerList.add(h);

                displayArea.append("Added Hosteller:\n" + h + "\n");

                // Clear fields
                nameField.setText("");
                ageField.setText("");
                feesField.setText("");
                deptField.setText("");
                yearField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for age, fees, and year.");
            }
        });

        resetButton.addActionListener(e -> {
            nameField.setText("");
            ageField.setText("");
            feesField.setText("");
            deptField.setText("");
            yearField.setText("");
        });

        viewAllButton.addActionListener(e -> {
            displayArea.setText("");
            if (hostellerList.isEmpty()) {
                displayArea.append("No hostellers found.\n");
            } else {
                for (Hosteller h : hostellerList) {
                    displayArea.append(h.toString() + "\n");
                }
            }
        });

        deleteButton.addActionListener(e -> {
            String inputId = JOptionPane.showInputDialog(this, "Enter Hosteller ID to delete:");
            try {
                int id = Integer.parseInt(inputId.trim());
                boolean removed = hostellerList.removeIf(h -> h.getId() == id);
                if (removed) {
                    displayArea.append("Hosteller with ID " + id + " deleted.\n");
                } else {
                    JOptionPane.showMessageDialog(this, "No hosteller found with ID: " + id);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        });
    }

    private int currentId = 1;

    private int generateNextId() {
        int max = 0;
        for (Hosteller h : hostellerList) {
            if (h.getId() > max) max = h.getId();
        }
        return max + 1;
    }

    public int getNextRoomNumber() {
        return roomCounter++;
    }
}
