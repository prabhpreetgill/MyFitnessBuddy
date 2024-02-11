package eecs.project.gui;
import eecs.project.Database;
import eecs.project.Profile;
import eecs.project.ProfileManager;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class ProfileCreate extends JFrame {
    private JTextField nameField;
    private JTextField sexField;
    private JTextField dateOfBirthField;
    private JTextField heightField;
    private JTextField weightField;
    JComboBox<String> unitComboBox;

    boolean creating = false;

    public ProfileCreate(boolean create) {
        super("Create/Edit Profile");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        creating = create;


        nameField = new JTextField(20);
        sexField = new JTextField(20);
        dateOfBirthField = new JTextField(20);
        heightField = new JTextField(10);
        weightField = new JTextField(10);
        String[] units = {"Metric", "Imperial"};
        unitComboBox = new JComboBox<>(units);

        if (!creating) {
            Profile profile = ProfileManager.getProfile();
            nameField.setText(profile.getName());
            sexField.setText(profile.getSex());
            dateOfBirthField.setText(profile.getDateOfBirth().toString());
            heightField.setText(String.valueOf((int) profile.getHeight()));
            weightField.setText(String.valueOf((int) profile.getWeight()));
            unitComboBox.setSelectedIndex(profile.isMetric() ? 0 : 1);
        }

        JButton createProfile = new JButton("Create Profile");
        JButton editProfile = new JButton("Save Profile");

        panel.add(createRow("Name", nameField));
        panel.add(createRow("Sex", sexField));
        panel.add(createRow("Date of Birth (YYYY-MM-DD)", dateOfBirthField));
        panel.add(createRow("Unit", unitComboBox));
        panel.add(createRow("Weight", weightField));
        panel.add(createRow("Height", heightField));

        if (creating)
            panel.add(createRow("", createProfile));
        else
            panel.add(createRow("", editProfile));

        getContentPane().add(panel);

        createProfile.addActionListener(e -> createProfile());
        editProfile.addActionListener(e -> editProfile());

        setSize(900, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createRow(String label, JComponent component) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        if (!label.isEmpty())
            rowPanel.add(new JLabel(label));
        rowPanel.add(component);
        return rowPanel;
    }

    private void createProfile() {
        String sex = sexField.getText();
        int height = Integer.parseInt(heightField.getText());
        int weight = Integer.parseInt(weightField.getText());
        try {
            PreparedStatement statement = Database.connect.prepareStatement("insert into profiles (Sex, DateOfBirth, Height, Weight, UnitPreference, Name)\n" +
                    "values (?, ?, ?, ?, ?, ?)");
            statement.setString(1, sex);
            statement.setDate(2, java.sql.Date.valueOf(dateOfBirthField.getText()));
            statement.setInt(3, height);
            statement.setInt(4, weight);
            statement.setString(5, unitComboBox.getSelectedItem().toString());
            statement.setString(6, nameField.getText());
            statement.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ProfileCreate.this, "Invalid data input", "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void editProfile() {
        String sex = sexField.getText();
        try {
            int height = Integer.parseInt(heightField.getText());
            int weight = Integer.parseInt(weightField.getText());

            PreparedStatement statement = Database.connect.prepareStatement("UPDATE profiles SET Sex = ?, DateOfBirth = ?, Height = ?, Weight = ?, UnitPreference = ?, Name = ? WHERE Id = ? ");
            statement.setString(1, sex);
            statement.setDate(2, java.sql.Date.valueOf(dateOfBirthField.getText()));
            statement.setInt(3, height);
            statement.setInt(4, weight);
            statement.setString(5, unitComboBox.getSelectedItem().toString());
            statement.setString(6, nameField.getText());
            statement.setInt(7, ProfileManager.getProfile().getId());
            statement.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ProfileCreate.this, "Invalid data input", "Error", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

}
