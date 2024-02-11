package eecs.project.gui;

import eecs.project.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ExerciseManager extends JFrame {
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> intensityComboBox;
    private JTextField durationField;
    private JButton addButton;
    private JTable exerciseTable;
    private DefaultTableModel tableModel;

    public ExerciseManager() {
        super("Exercise Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2));

        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().toString());

        timeField = new JTextField(10);
        timeField.setText(LocalTime.now().toString());

        String[] exerciseTypes = {"Walking", "Running", "Biking", "Swimming", "Weightlifting", "Skiing", "Skating"};
        typeComboBox = new JComboBox<>(exerciseTypes);

        String[] exerciseIntensities = {"Low", "Medium", "High", "Very High"};
        intensityComboBox = new JComboBox<>(exerciseIntensities);

        durationField = new JTextField(10);

        addButton = new JButton("Add Exercise");

        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Time (HH:mm):"));
        formPanel.add(timeField);
        formPanel.add(new JLabel("Exercise Type:"));
        formPanel.add(typeComboBox);
        formPanel.add(new JLabel("Exercise Intensity:"));
        formPanel.add(intensityComboBox);
        formPanel.add(new JLabel("Exercise Duration (minutes):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel()); // Empty label for spacing
        formPanel.add(addButton);

        // Table Panel
        String[] columnNames = {"Date", "Time", "Type", "Intensity", "Duration", "Total Calories Burnt"};
        tableModel = new DefaultTableModel(columnNames, 0);
        exerciseTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(exerciseTable);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        addButton.addActionListener(e -> {
            addExerciseToDatabase();
            updateExerciseTable();
        });

        updateExerciseTable(); // Load initial data
    }

    private void addExerciseToDatabase() {
        String insertQuery = "INSERT INTO exercises (date, time, type, intensity, duration, profile_id) VALUES (?, ?, ?, ?, ?, ?)";

        try {
             PreparedStatement preparedStatement = Database.connect.prepareStatement(insertQuery);

            preparedStatement.setDate(1, Date.valueOf(dateField.getText()));
            preparedStatement.setTime(2, Time.valueOf(LocalTime.parse(timeField.getText())));
            preparedStatement.setString(3, Objects.requireNonNull(typeComboBox.getSelectedItem()).toString());
            preparedStatement.setString(4, Objects.requireNonNull(intensityComboBox.getSelectedItem()).toString());
            preparedStatement.setInt(5, Integer.parseInt(durationField.getText()));
            preparedStatement.setInt(6, ProfileManager.getProfile().getId()); // Replace with your actual profile name

            preparedStatement.executeUpdate();
            System.out.println("Exercise inserted successfully!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateExerciseTable() {
        String selectQuery = "SELECT date, time, type, intensity, duration FROM exercises WHERE profile_id = ?";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Database.connect.prepareStatement(selectQuery);

            preparedStatement.setInt(1, ProfileManager.getProfile().getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear existing data in the table
            tableModel.setRowCount(0);

            while (resultSet.next()) {
                LocalDate exerciseDate = resultSet.getDate("date").toLocalDate();
                LocalTime exerciseTime = resultSet.getTime("time").toLocalTime();
                String exerciseType = resultSet.getString("type");
                String exerciseIntensity = resultSet.getString("intensity");
                int exerciseDuration = resultSet.getInt("duration");

                double totalCaloriesBurnt = CalorieEstimation.estimateCaloriesBurnt(exerciseType, exerciseIntensity, exerciseDuration, ProfileManager.getProfile().getBMR());

                Object[] row = {exerciseDate, exerciseTime, exerciseType, exerciseIntensity, exerciseDuration, (int) totalCaloriesBurnt};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExerciseManager().setVisible(true);
        });
    }
}
