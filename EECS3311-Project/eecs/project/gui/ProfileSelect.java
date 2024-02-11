package eecs.project.gui;

import eecs.project.Database;
import eecs.project.Profile;
import eecs.project.ProfileManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileSelect extends JFrame {

    private DefaultTableModel profileTableModel;
    private JTable profileTable;

    static List<Profile> loadProfiles() {
        Statement statement = null;
        try {
            statement = Database.connect.createStatement();
            String query = "SELECT Id, Name, Sex, DateOfBirth, Weight, Height, UnitPreference FROM profiles";
            ResultSet resultSet = statement.executeQuery(query);

            // Process the result set and store in a list
            java.util.List<Profile> profiles = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String name = resultSet.getString("Name");
                String sex = resultSet.getString("Sex");
                int weight = resultSet.getInt("Weight");
                int height = resultSet.getInt("Height");
                boolean isMetric = resultSet.getString("UnitPreference").equals("Metric");
                LocalDate dateOfBirth = resultSet.getDate("DateOfBirth").toLocalDate();

                profiles.add(new Profile(id, name, sex, dateOfBirth, height, weight, isMetric));
            }
            return profiles;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ProfileSelect() {
        super("Profile Selector");

        List<Profile> profiles = loadProfiles();
        Object[][] tableData = new Object[profiles.size()][6]; // 5 fields: Name, Sex, Age, Weight, DateOfBirth

        for (int i = 0; i < profiles.size(); i++) {
            Profile profile = profiles.get(i);
            tableData[i][0] = profile.getName();
            tableData[i][1] = profile.getSex();
            tableData[i][2] = profile.calculateAge();
            tableData[i][3] = profile.getWeight();
            tableData[i][4] = profile.getHeight();
            tableData[i][5] = profile.getDateOfBirth();
        }

        // Column names
        String[] columnNames = {"Name", "Sex", "Age", "Weight", "Height", "DateOfBirth"};

        // Create a DefaultTableModel and populate it with user profiles
        profileTableModel = new DefaultTableModel(tableData, columnNames);

        // Create JTable with the DefaultTableModel
        profileTable = new JTable(profileTableModel);
        profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton selectButton = new JButton("Select Profile");
        selectButton.addActionListener(e -> {
            // Get the selected row and columns
            int selectedRow = profileTable.getSelectedRow();

            if (selectedRow != -1) {
                Profile selectedProfile = profiles.get(selectedRow);
                ProfileManager.setProfile(selectedProfile);
                MainUI.profileSelected();
            } else {
                // Display an error message if no profile is selected
                JOptionPane.showMessageDialog(ProfileSelect.this,
                        "Please select a profile.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> {
            int selectedRow = profileTable.getSelectedRow();

            if (selectedRow != -1) {
                Profile selectedProfile = profiles.get(selectedRow);
                ProfileManager.setProfile(selectedProfile);
                new ProfileCreate(false).setVisible(true);
            } else {
                // Display an error message if no profile is selected
                JOptionPane.showMessageDialog(ProfileSelect.this,
                        "Please select a profile.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        // Create a JScrollPane to hold the JTable
        JScrollPane tableScrollPane = new JScrollPane(profileTable);

        // Set layout manager for the frame
        setLayout(new BorderLayout());

        // Add components to the frame
        add(tableScrollPane, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.add(selectButton, BorderLayout.NORTH);
        btnPanel.add(editButton, BorderLayout.SOUTH);
        add(btnPanel, BorderLayout.SOUTH);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProfileSelect();
            }
        });
    }
}
