package eecs.project.gui;

import eecs.project.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealManager extends JFrame {
    private final Choice foodChoice;
    private JTextField dateField;
    private JComboBox<String> mealTypeComboBox;

    private JTextField itemQuantityField;
    JLabel itemQuantityLabel;

    Integer measureId = null;

    private JTable mealTable, mealDetailsTable ;
    private DefaultTableModel mealTableModel, mealDetailsTableModel;
    private JPanel mealDetailsPanel;

    JLabel measureLabel;

    public MealManager() {
        super("Meal Manager");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2));

        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateField = new JTextField(20);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        JLabel mealTypeLabel = new JLabel("Meal Type:");
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        mealTypeComboBox = new JComboBox<>(mealTypes);
        JLabel itemNameLabel = new JLabel("Ingredient Name:");
        itemQuantityLabel = new JLabel("Ingredient Quantity:");
        measureLabel = new JLabel("");
        itemQuantityField = new JTextField(20);
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> addMealItem());
        foodChoice = new Choice();
        foodSelectBox(FoodData.queryFoodData());
        updateMeasureId();
        updateMealTable();

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(mealTypeLabel);
        panel.add(mealTypeComboBox);
        panel.add(itemNameLabel);
        panel.add(foodChoice);
        panel.add(itemQuantityLabel);
        panel.add(itemQuantityField);
        panel.add(addItemButton);

        JPanel mealPanel = new JPanel(new BorderLayout());

        JPanel tablePanel = new JPanel(new GridLayout(3, 1));
        tablePanel.add(mealTable.getTableHeader());
        tablePanel.add(mealTable);
        JButton selectMealBtn = new JButton("Select Meal");
        selectMealBtn.addActionListener(l -> {
            int rowNum = mealTable.getSelectedRow();
            String mealDate  = (String) mealTable.getModel().getValueAt(rowNum, 0);
            String mealType = (String) mealTable.getModel().getValueAt(rowNum, 1);
            displayMealDetails(mealDate, mealType);
        });
        tablePanel.add(selectMealBtn);

        mealPanel.add(tablePanel, BorderLayout.NORTH);
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));

        mealDetailsTableModel= new DefaultTableModel();
        mealDetailsTableModel.addColumn("Nutrient");
        mealDetailsTableModel.addColumn("Amount");
        mealDetailsTable = new JTable(mealDetailsTableModel);
        detailsPanel.add(mealDetailsTable.getTableHeader());
        detailsPanel.add(mealDetailsTable);


        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(mealPanel, BorderLayout.CENTER);
        getContentPane().add(detailsPanel, BorderLayout.SOUTH);


    }

    private void foodSelectBox(List<FoodData> foodList) {
        foodChoice.addItemListener(l -> {
            updateMeasureId();
        });

        for (FoodData foodData : foodList) {
            foodChoice.add(foodData.getFoodDescription());
        }
    }

    void updateMeasureId() {
        String foodDesc = foodChoice.getSelectedItem().toString();
        try {
            String query = "SELECT MeasureUD as MeasureID, MeasureName FROM measure_name INNER JOIN conversion_fac cf on cf.MeasureID = MeasureUD inner join main.food_name fn on cf.FoodID = fn.FoodID WHERE fn.FoodDescription = ? LIMIT 1";
            PreparedStatement statement = Database.connect.prepareStatement(query);
            statement.setString(1, foodDesc);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            measureId = resultSet.getInt("MeasureID");
            String measureName = resultSet.getString("MeasureName");
            itemQuantityLabel.setText("Ingredient Quantity (multiples of: " + measureName +")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMealItem() {
        double amount = Double.parseDouble(itemQuantityField.getText());
        String mealType = mealTypeComboBox.getSelectedItem().toString();
        LocalDate mealDate = LocalDate.parse(dateField.getText());
        String foodDescription = foodChoice.getSelectedItem();

        String insertQuery = "INSERT INTO meals (profile_id, amount, meal_type, meal_date, food_description, measure_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = Database.connect.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, ProfileManager.getProfile().getId());
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, mealType);
            preparedStatement.setDate(4, Date.valueOf(mealDate));
            preparedStatement.setString(5, foodDescription);
            preparedStatement.setInt(6, measureId);

            // Execute the insert statement
            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear input fields for the next entry
        itemQuantityField.setText("");
        updateMealTable();
    }

    public void displayMealDetails(String mealDate, String mealType) {
        HashMap<String, Double> mealData = MealData.getMealDetails(mealType, Date.valueOf(mealDate));
        mealDetailsTableModel.setRowCount(0);

        mealData.forEach((nutrient, amount) -> {
                Object[] row = {
                        nutrient,
                        String.valueOf(amount),
                };
                mealDetailsTableModel.addRow(row);
        });
        mealDetailsTable.setModel(mealDetailsTableModel);
        mealDetailsTableModel.fireTableDataChanged();
    }
    public void updateMealTable() {
        mealTableModel = new DefaultTableModel();
        mealTableModel.addColumn("Meal Date");
        mealTableModel.addColumn("Meal Type");
        mealTableModel.addColumn("Total Calories");

        try {
            String query = "select\n" +
                    "        meals.meal_date as mealDate,\n" +
                    "        meal_type as mealType,\n" +
                    "       sum(amount * Nutrient* cf.ConversionFactionValue) as calories\n" +
                    "\n" +
                    "from meals\n" +
                    "inner join main.food_name fn on meals.food_description = fn.FoodDescription\n" +
                    "inner join main.conversion_fac cf on fn.FoodID = cf.FoodID\n" +
                    "inner join main.nutrient_amount na on cf.FoodID = na.FoodID\n" +
                    "inner join main.nutrient_name nn on nn.NutrientID = na.NutrientNameID\n" +
                    "inner join main.measure_name mn on mn.MeasureUD = cf.MeasureID\n" +
                    "where profile_id = ? and nn.NutrientID = 208 and cf.MeasureID = measure_id\n" +
                    "group by meals.meal_date, meal_type";
            PreparedStatement statement = Database.connect.prepareStatement(query);
            statement.setInt(1, ProfileManager.getProfile().getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getDate("mealDate").toString(),
                        resultSet.getString("mealType"),
                        Double.toString(resultSet.getDouble("calories")),
                };
                mealTableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mealTable == null) {
            mealTable = new JTable(mealTableModel);
        }
        mealTable.setModel(mealTableModel);
        mealTableModel.fireTableDataChanged();

    }
}
