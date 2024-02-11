package eecs.project;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalorieData {
    public static List<CalorieExpenditure> getCalorieExpenditure(Date startDate, Date endDate) {
        List<CalorieExpenditure> list = new ArrayList<>();
        HashMap<LocalDate, Double> exp = new HashMap<LocalDate, Double>();
        try {
            String selectQuery = startDate != null ? "SELECT date, type, intensity, duration FROM exercises WHERE profile_id = ? AND date >= ? AND date <= ?"
                    : "SELECT date, type, intensity, duration FROM exercises WHERE profile_id = ?";

            PreparedStatement preparedStatement = Database.connect.prepareStatement(selectQuery);
            preparedStatement.setInt(1, ProfileManager.getProfile().getId());
            if (startDate != null) {
                preparedStatement.setDate(2, startDate);
                preparedStatement.setDate(3, endDate);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LocalDate exerciseDate = resultSet.getDate("date").toLocalDate();
                String exerciseType = resultSet.getString("type");
                String exerciseIntensity = resultSet.getString("intensity");
                int exerciseDuration = resultSet.getInt("duration");

                double totalCaloriesBurnt = CalorieEstimation.estimateCaloriesBurnt(exerciseType, exerciseIntensity, exerciseDuration, ProfileManager.getProfile().getBMR());
                exp.put(exerciseDate, exp.getOrDefault(exerciseDate, (double) 0) + totalCaloriesBurnt);
            }
            exp.forEach((k, v) -> {
                list.add(new CalorieExpenditure(k, v));
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public static List<CalorieIntake> getCalorieIntake(Date startDate, Date endDate) {
        List<CalorieIntake> list = new ArrayList<>();
        try {
            PreparedStatement statement = Database.connect.prepareStatement("        select\n" +
                    "        meals.meal_date as meal_date,\n" +
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
                    "        and meal_date >= ?\n" +
                    "        and meal_date <= ?\n" +
                    "        group by meals.meal_date;\n");
            statement.setInt(1, ProfileManager.getProfile().getId());
            statement.setDate(2, startDate == null ? new Date(0) : startDate);
            statement.setDate(3, endDate == null ? Date.valueOf("2099-01-01") : endDate);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(new CalorieIntake(
                    resultSet.getDate("meal_date").toLocalDate(),
                    resultSet.getDouble("calories"),
                    resultSet.getString("food_group")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
