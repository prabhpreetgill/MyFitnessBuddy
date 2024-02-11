package eecs.project.gui;

import eecs.project.Database;
import eecs.project.Profile;
import eecs.project.ProfileManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class DailyNutrientIntakeVisualizer extends JFrame {
    private final JTextField startDateField;
    private final JTextField endDateField;
    private ChartPanel chartPanel;

    public DailyNutrientIntakeVisualizer() {
        super("Nutrient Intake Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
        startDateField = new JTextField(10);
        JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");
        endDateField = new JTextField(10);
        JButton visualizeButton = new JButton("Visualize");

        visualizeButton.addActionListener(e -> visualizeData());

        inputPanel.add(startDateLabel);
        inputPanel.add(startDateField);
        inputPanel.add(endDateLabel);
        inputPanel.add(endDateField);
        inputPanel.add(visualizeButton);

        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(chartPanel, BorderLayout.CENTER);
    }

    private HashMap<String, Double> fetchNutrientData() {
        PreparedStatement statement = null;
        HashMap<String, Double> nutrients = new HashMap<>();
        try {
            statement = Database.connect.prepareStatement("select\n" +
                    "                           NutrientName, MeasureName, sum(amount * Nutrient* cf.ConversionFactionValue) as total\n" +
                    "\n" +
                    "                           from meals\n" +
                    "                           inner join main.food_name fn on meals.food_description = fn.FoodDescription\n" +
                    "                           inner join main.conversion_fac cf on fn.FoodID = cf.FoodID\n" +
                    "                           inner join main.nutrient_amount na on cf.FoodID = na.FoodID\n" +
                    "                           inner join main.nutrient_name nn on nn.NutrientID = na.NutrientNameID\n" +
                    "                           inner join main.measure_name mn on mn.MeasureUD = cf.MeasureID\n" +
                    "                           where\n" +
                    "                           profile_id = ?\n" +
                    "                           and cf.MeasureID = measure_id\n" +
                    "                           and NutrientName IN ('CARBOHYDRATE, TOTAL (BY DIFFERENCE)', 'PROTEIN', 'FAT (TOTAL LIPIDS)', 'ENERGY (KILOCALORIES)')\n" +
                    "                           and meal_date >= ?\n" +
                    "                           and meal_date <= ?\n" +
                    "                           group by nutrientName");
            statement.setInt(1, ProfileManager.getProfile().getId());
            statement.setDate(2, java.sql.Date.valueOf(startDateField.getText()));
            statement.setDate(3, java.sql.Date.valueOf(endDateField.getText()));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nutrients.put(resultSet.getString("NutrientName"), resultSet.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return nutrients;
    }

    private void visualizeData() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        HashMap<String, Double> nutrients = fetchNutrientData();
        HashMap<String, Double> calories = new HashMap<>();
        nutrients.forEach((nutrient, amount) -> {
            switch (nutrient) {
                case "CARBOHYDRATE, TOTAL (BY DIFFERENCE)":
                    calories.put("Carbohydrates", amount * 4);
                    break;
                case "PROTEIN":
                    calories.put("Proteins", amount * 4);
                    break;
                case "FAT (TOTAL LIPIDS)":
                    calories.put("Fats", amount * 9);
                    break;
                default:
                    break;
            }
        });
        double totalCalories = nutrients.get("ENERGY (KILOCALORIES)");
        double otherCalories = totalCalories - calories.values().stream().reduce((double) 0, Double::sum);

        dataset.setValue("Proteins", calories.get("Proteins") * 100 / totalCalories);
        dataset.setValue("Carbohydrates", calories.get("Carbohydrates") * 100 / totalCalories);
        dataset.setValue("Fats", calories.get("Fats") * 100 / totalCalories);
        dataset.setValue("Other nutrients", otherCalories * 100 / totalCalories);

        // chart title
        // data
        // include legend
        JFreeChart chart = ChartFactory.createPieChart(
                "Average Daily Nutrient Intake", // chart title
                dataset, // data
                true, // include legend
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}%", new DecimalFormat("0"), new DecimalFormat("0%")));
        // You can customize the plot here, for example, setting colors or section outlines

        // Replace the existing chart panel with the new one
        getContentPane().remove(chartPanel);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        getContentPane().add(chartPanel, BorderLayout.CENTER);

        // Validate the container after adding the new component
        validate();

        // Here, you can also add a notification for the recommended daily portions.
        // For example, you can use a JTextArea or JLabel to display the message at the bottom of the window.
        // ...

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DailyNutrientIntakeVisualizer visualizer = new DailyNutrientIntakeVisualizer();
            visualizer.setVisible(true);
        });
    }
}
