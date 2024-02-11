package eecs.project.gui;

import eecs.project.CalorieIntake;
import eecs.project.Database;
import eecs.project.ProfileManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DietAlignmentWithCFG extends JFrame {
    private JPanel chartPanel, guidePanel;

    public DietAlignmentWithCFG() {
        super("Diet Alignment with Canada Food Guide");
        setSize(1500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        chartPanel = new JPanel();
        generateChart();

        guidePanel = new JPanel();
        generateGuideChart();

        this.add(chartPanel, BorderLayout.WEST);
        this.add(guidePanel, BorderLayout.EAST);
    }

    public static HashMap<String, Double> getFoodGroups() {
        HashMap<String, Double> groups = new HashMap<>();
        try {
            PreparedStatement statement = Database.connect.prepareStatement("        select\n" +
                    "                sum(amount * Nutrient* cf.ConversionFactionValue) as calories,\n" +
                    "                fg.FoodGroupName as food_group\n" +
                    "\n" +
                    "        from meals\n" +
                    "        inner join main.food_name fn on meals.food_description = fn.FoodDescription\n" +
                    "        inner join main.conversion_fac cf on fn.FoodID = cf.FoodID\n" +
                    "        inner join main.nutrient_amount na on cf.FoodID = na.FoodID\n" +
                    "        inner join main.nutrient_name nn on nn.NutrientID = na.NutrientNameID\n" +
                    "        inner join main.measure_name mn on mn.MeasureUD = cf.MeasureID\n" +
                    "        inner join main.food_group fg on fg.FoodGroupID = fn.FoodGroupID" +
                    "        where\n" +
                    "        profile_id = ?\n" +
                    "        and nn.NutrientID = 208\n" +
                    "        and cf.MeasureID = measure_id\n" +
                    "        group by food_group ;\n");
            statement.setInt(1, ProfileManager.getProfile().getId());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groups.put(resultSet.getString("food_group"), resultSet.getDouble("calories"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        double total = groups.values().stream().reduce((double) 0, Double::sum);
        groups.replaceAll((group, cals) -> cals * 100 / total);
        return groups;
    }

    private void generateChart() {
            HashMap<String, Double> groups = getFoodGroups();
            DefaultPieDataset dataset = new DefaultPieDataset();
            groups.forEach(dataset::setValue);

            JFreeChart chart = ChartFactory.createPieChart(
                    "My Plate",
                    dataset,
                    true,
                    true,
                    false
            );

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setSectionPaint("Vegetables & Fruits", Color.green);
            plot.setSectionPaint("Whole Grain Foods", Color.yellow);
            plot.setSectionPaint("Protein Foods", Color.red);

            // Replace existing chart
            chartPanel.removeAll();
            chartPanel.add(new ChartPanel(chart));
            chartPanel.validate();

    }

    private void generateGuideChart() {
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Vegetables & Fruits", 50);
            dataset.setValue("Whole Grain Foods", 25);
            dataset.setValue("Protein Foods", 25);

            JFreeChart chart = ChartFactory.createPieChart(
                    "CFG Plate Guide",
                    dataset,
                    true,
                    true,
                    false
            );

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setSectionPaint("Vegetables & Fruits", Color.green);
            plot.setSectionPaint("Whole Grain Foods", Color.yellow);
            plot.setSectionPaint("Protein Foods", Color.red);
            plot.setSectionPaint("Fruits", Color.orange);

            // Replace existing chart
            guidePanel.add(new ChartPanel(chart));

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DietAlignmentWithCFG frame = new DietAlignmentWithCFG();
            frame.setVisible(true);
        });
    }
}
