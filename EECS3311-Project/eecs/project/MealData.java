package eecs.project;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealData {

    private String profileName;
    private double amount;
    private String mealType;
    private Date mealDate;
    private String foodDescription;

    public MealData(double amount, String mealType, Date mealDate, String foodDescription) {
        this.amount = amount;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.foodDescription = foodDescription;
    }

    public double getAmount() {
        return amount;
    }

    public String getMealType() {
        return mealType;
    }

    public Date getMealDate() {
        return mealDate;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public static HashMap<String, Double> getMealDetails(String mealType, Date mealDate) {
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
                    "                           and meal_date = ?\n" +
                    "                           and meal_type = ?\n" +
                    "                           and profile_id = ?\n" +
                    "                           group by nutrientName");
            statement.setInt(1, ProfileManager.getProfile().getId());
            statement.setDate(2, mealDate);
            statement.setString(3, mealType);
            statement.setInt(4, ProfileManager.getProfile().getId());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                nutrients.put(resultSet.getString("NutrientName"), resultSet.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return nutrients;
    }


}
