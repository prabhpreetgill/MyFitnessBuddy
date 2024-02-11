package eecs.project.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Navigation extends JFrame {
    public Navigation() {
        setTitle("Fitness App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // Create buttons for different parts of the app
        JButton mealManagerButton = createButton("Meal Manager");
        JButton exerciseManagerButton = createButton("Exercise Manager");
        JButton dailyNutrientIntakeButton = createButton("Daily Nutrient Intake");
        JButton calorieVisualizerButton = createButton("Calorie Visualizer");
        JButton foodGuideAlignmentButton = createButton("Canada Food Guide Alignment");
        JButton weightLossPredictorButton = createButton("Weight Loss Predictor");

        // Add action listeners to the buttons
        mealManagerButton.addActionListener(new ButtonClickListener(MealManager.class));
        exerciseManagerButton.addActionListener(new ButtonClickListener(ExerciseManager.class));
        dailyNutrientIntakeButton.addActionListener(new ButtonClickListener(DailyNutrientIntakeVisualizer.class));
        calorieVisualizerButton.addActionListener(new ButtonClickListener(CalorieExerciseVisualizer.class));
        foodGuideAlignmentButton.addActionListener(new ButtonClickListener(DietAlignmentWithCFG.class));
        weightLossPredictorButton.addActionListener(new ButtonClickListener(WeightLossPredictor.class));

        // Add buttons to the frame
        add(mealManagerButton);
        add(exerciseManagerButton);
        add(dailyNutrientIntakeButton);
        add(calorieVisualizerButton);
        add(foodGuideAlignmentButton);
        add(weightLossPredictorButton);

        setSize(600, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String buttonText) {
        JButton button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(150, 50));
        return button;
    }

    private static class ButtonClickListener implements ActionListener {
        private final Class<?> targetClass;

        ButtonClickListener(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFrame frame = (JFrame) targetClass.getDeclaredConstructor().newInstance();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}

