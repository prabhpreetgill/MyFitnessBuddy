package eecs.project.gui;
import eecs.project.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalorieExerciseVisualizer extends JFrame {
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton visualizeButton;

    ChartPanel chartPanel;


    public CalorieExerciseVisualizer() {
        super("Calorie Intake and Expenditure Chart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
        startDateField = new JTextField(10);
        startDateField.setText("2023-10-10");
        JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");
        endDateField = new JTextField(10);
        endDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        visualizeButton = new JButton("Visualize");
        JFreeChart chart = createChart();

        chartPanel = new ChartPanel(chart);

        visualizeButton.addActionListener(e -> {
            // Set the new dataset to the chart
            TimeSeriesCollection newDataset = createDataset();
            chart.getXYPlot().setDataset(newDataset);

            // Notify the chart that the dataset has changed
            chart.fireChartChanged();

            // Repaint the chartPanel
            chartPanel.repaint();
        });

        inputPanel.add(startDateLabel);
        inputPanel.add(startDateField);
        inputPanel.add(endDateLabel);
        inputPanel.add(endDateField);
        inputPanel.add(visualizeButton);


        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(chartPanel, BorderLayout.CENTER);


        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }




    private TimeSeriesCollection createDataset() {
        List<CalorieIntake> intakeData = CalorieData.getCalorieIntake(Date.valueOf(startDateField.getText()), Date.valueOf(endDateField.getText()));
        List<CalorieExpenditure> expenditureData = CalorieData.getCalorieExpenditure(Date.valueOf(startDateField.getText()), Date.valueOf(endDateField.getText()));
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries intakeSeries = new TimeSeries("Calorie Intake");
        TimeSeries expenditureSeries = new TimeSeries("Calorie Expenditure");

        for (CalorieIntake intake : intakeData) {
            intakeSeries.addOrUpdate(new Second(Date.from(intake.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())), intake.getCalories());
        }

        for (CalorieExpenditure expenditure : expenditureData) {
            expenditureSeries.addOrUpdate(new Second(Date.from(expenditure.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())), expenditure.getCalories());
        }

        dataset.addSeries(intakeSeries);
        dataset.addSeries(expenditureSeries);

        return dataset;
    }

    private JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Calorie Intake and Expenditure Over Time",
                "Date",
                "Calories",
                createDataset(),
                true,
                false,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE); // Calorie Intake
        renderer.setSeriesPaint(1, Color.RED); // Calorie Expenditure
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        return chart;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           new CalorieExerciseVisualizer().setVisible(true);
        });
    }
}
