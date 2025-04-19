import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsPanel extends JPanel {
    private JFrame mainFrame;
    private boolean isDarkMode = false;
    private java.util.List<Component> componentsToToggle = new ArrayList<>();

    public SettingsPanel(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JButton darkModeButton = new JButton("Toggle Dark Mode");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton generateQRButton = new JButton("Generate QR Code");

        darkModeButton.addActionListener(e -> toggleDarkMode());
        undoButton.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "Undo feature not implemented"));
        redoButton.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "Redo feature not implemented"));
        generateQRButton.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "QR Code generation not yet implemented"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(darkModeButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(redoButton);
        buttonPanel.add(generateQRButton);

        componentsToToggle.add(this);
        componentsToToggle.add(buttonPanel);
        componentsToToggle.add(darkModeButton);
        componentsToToggle.add(undoButton);
        componentsToToggle.add(redoButton);
        componentsToToggle.add(generateQRButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        Color bg = isDarkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = isDarkMode ? Color.WHITE : Color.BLACK;

        for (Component comp : componentsToToggle) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof JPanel || comp instanceof JButton) {
                for (Component child : ((Container) comp).getComponents()) {
                    child.setBackground(bg);
                    child.setForeground(fg);
                }
            }
        }

        mainFrame.getContentPane().setBackground(bg);
        mainFrame.repaint();
    }
}
