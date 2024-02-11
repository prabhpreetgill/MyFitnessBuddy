package eecs.project.gui;

import eecs.project.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class WeightLossPredictor extends JFrame {
    private JTextField targetDateField;
    private JLabel resultLabel;
    private JButton calculateButton;

    private static final int CALORIES_PER_KG_FAT = 7700;

    public WeightLossPredictor() {
        super("Weight Loss Predictor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        targetDateField = new JTextField(10);
        resultLabel = new JLabel(" ");
        calculateButton = new JButton("Calculate");

        calculateButton.addActionListener(this::calculateWeightLoss);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Target Date (yyyy-MM-dd):"));
        panel.add(targetDateField);
        panel.add(calculateButton);
        panel.add(resultLabel);

        this.add(panel);
    }

    private void calculateWeightLoss(ActionEvent e) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date targetDate = dateFormat.parse(targetDateField.getText());
            List<CalorieIntake> intakes = CalorieData.getCalorieIntake(null, null);
            List<CalorieExpenditure> expenditureDays = CalorieData.getCalorieExpenditure(null, null);
            double totalIntake = intakes.stream().map(CalorieIntake::getCalories).reduce((double) 0, Double::sum);
            double totalExpenditure = expenditureDays.stream().map(CalorieExpenditure::getCalories).reduce((double) 0, Double::sum);
            double averageIntake = totalIntake / intakes.size();
            double averageExpenditure = totalExpenditure / expenditureDays.size();
            double averageCalorieDeficit = averageExpenditure - averageIntake;

            Date currentDate = new Date();
            long timeDifference = targetDate.getTime() - currentDate.getTime();
            long daysDifference = timeDifference / (24 * 60 * 60 * 1000);

            // Calculate weight loss in kg
            double weightLoss = (double) averageCalorieDeficit * daysDifference/ CALORIES_PER_KG_FAT;

            if (weightLoss > 0)
                resultLabel.setText(String.format("Projected weight loss: %.2f kg", weightLoss));
            else
                resultLabel.setText(String.format("Projected weight gain: %.2f kg", -weightLoss));

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in the format yyyy-MM-dd.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter numerical values for calorie intake and burn.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeightLossPredictor predictor = new WeightLossPredictor();
            predictor.setVisible(true);
        });
    }
}
