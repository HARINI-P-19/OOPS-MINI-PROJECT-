import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DashboardPanel extends JPanel {
    private JLabel totalHostellersLabel;
    private JLabel totalFeesLabel;
    private JLabel occupancyLabel;
    private JLabel highestFeeLabel;
    private JButton refreshButton;
    private JButton darkModeButton;
    private JButton undoButton;
    private JButton redoButton;
    private boolean isDarkMode = false;

    private Stack<ArrayList<Hosteller>> undoStack = new Stack<>();
    private Stack<ArrayList<Hosteller>> redoStack = new Stack<>();

    public DashboardPanel(ArrayList<Hosteller> hostellerList) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        totalHostellersLabel = new JLabel();
        totalFeesLabel = new JLabel();
        occupancyLabel = new JLabel();
        highestFeeLabel = new JLabel();

        Font font = new Font("Arial", Font.BOLD, 18);
        totalHostellersLabel.setFont(font);
        totalFeesLabel.setFont(font);
        occupancyLabel.setFont(font);
        highestFeeLabel.setFont(font);

        statsPanel.add(totalHostellersLabel);
        statsPanel.add(totalFeesLabel);
        statsPanel.add(occupancyLabel);
        statsPanel.add(highestFeeLabel);

        refreshButton = new JButton("Refresh Dashboard");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 16));
        refreshButton.setToolTipText("Refresh the statistics and update the dashboard");
        refreshButton.addActionListener(e -> {
            pushState(hostellerList);
            updateDashboard(hostellerList);
        });

        darkModeButton = new JButton("Toggle Dark Mode");
        darkModeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        darkModeButton.setToolTipText("Toggle light/dark theme for this panel");
        darkModeButton.addActionListener(e -> toggleDarkMode());

        undoButton = new JButton("Undo");
        undoButton.setFont(new Font("Arial", Font.PLAIN, 16));
        undoButton.setToolTipText("Undo the last dashboard data change");
        undoButton.addActionListener(e -> undo(hostellerList));

        redoButton = new JButton("Redo");
        redoButton.setFont(new Font("Arial", Font.PLAIN, 16));
        redoButton.setToolTipText("Redo the last undone change");
        redoButton.addActionListener(e -> redo(hostellerList));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(refreshButton);
        bottomPanel.add(darkModeButton);
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);

        add(statsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateDashboard(hostellerList);
    }

    public void updateDashboard(List<Hosteller> hostellerList) {
        int totalHostellers = hostellerList.size();
        double totalFees = 0;
        double maxFee = 0;
        for (Hosteller h : hostellerList) {
            totalFees += h.getFeesPaid();
            if (h.getFeesPaid() > maxFee) maxFee = h.getFeesPaid();
        }

        totalHostellersLabel.setText("Total Hostellers: " + totalHostellers);
        totalFeesLabel.setText("Total Fees Collected: Rs. " + totalFees);
        occupancyLabel.setText("Total Rooms Occupied: " + totalHostellers);
        highestFeeLabel.setText("Highest Fee Paid: Rs. " + maxFee);
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        Color bg = isDarkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = isDarkMode ? Color.WHITE : Color.BLACK;

        setBackground(bg);
        setForeground(fg);
        applyDarkMode(this, bg, fg);
    }

    private void applyDarkMode(Component comp, Color bg, Color fg) {
        if (comp instanceof JPanel || comp instanceof JScrollPane) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            for (Component child : ((Container) comp).getComponents()) {
                applyDarkMode(child, bg, fg);
            }
        } else if (comp instanceof JLabel || comp instanceof JButton ||
                   comp instanceof JTextField || comp instanceof JTextArea) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof JTextArea) {
                ((JTextArea) comp).setCaretColor(fg);
            }
        }
    }

    private void pushState(ArrayList<Hosteller> hostellerList) {
        ArrayList<Hosteller> stateCopy = new ArrayList<>();
        for (Hosteller h : hostellerList) {
            stateCopy.add(new Hosteller(h.getId(), h.getName(), h.getAge(),
                                        h.getRoomNo(), h.getFeesPaid(),
                                        h.getDepartment(), h.getYear()));
        }
        undoStack.push(stateCopy);
        redoStack.clear();
    }

    private void undo(ArrayList<Hosteller> hostellerList) {
        if (!undoStack.isEmpty()) {
            ArrayList<Hosteller> stateCopy = new ArrayList<>();
            for (Hosteller h : hostellerList) {
                stateCopy.add(new Hosteller(h.getId(), h.getName(), h.getAge(),
                                            h.getRoomNo(), h.getFeesPaid(),
                                            h.getDepartment(), h.getYear()));
            }
            redoStack.push(stateCopy);

            ArrayList<Hosteller> prevState = undoStack.pop();
            hostellerList.clear();
            hostellerList.addAll(prevState);
            updateDashboard(hostellerList);
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
        }
    }

    private void redo(ArrayList<Hosteller> hostellerList) {
        if (!redoStack.isEmpty()) {
            ArrayList<Hosteller> stateCopy = new ArrayList<>();
            for (Hosteller h : hostellerList) {
                stateCopy.add(new Hosteller(h.getId(), h.getName(), h.getAge(),
                                            h.getRoomNo(), h.getFeesPaid(),
                                            h.getDepartment(), h.getYear()));
            }
            undoStack.push(stateCopy);

            ArrayList<Hosteller> nextState = redoStack.pop();
            hostellerList.clear();
            hostellerList.addAll(nextState);
            updateDashboard(hostellerList);
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to redo.");
        }
    }
}
