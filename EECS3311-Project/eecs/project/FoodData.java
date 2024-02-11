package eecs.project;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodData {

    private int foodId;
    private int foodGroupId;
    private String foodDescription;

    public FoodData(int foodId, int foodGroupId, String foodDescription) {
        this.foodId = foodId;
        this.foodGroupId = foodGroupId;
        this.foodDescription = foodDescription;
    }

    public int getFoodId() {
        return foodId;
    }

    public int getFoodGroupId() {
        return foodGroupId;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public static List<FoodData> queryFoodData() {
        List<FoodData> foodList = new ArrayList<>();

        try {
            Statement statement = Database.connect.createStatement();

        String query = "SELECT FoodID, FoodGroupID, FoodDescription FROM food_name ORDER BY FoodDescription";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            int foodId = resultSet.getInt("FoodID");
            int foodGroupId = resultSet.getInt("FoodGroupID");
            String foodDescription = resultSet.getString("FoodDescription");

            FoodData foodData = new FoodData(foodId, foodGroupId, foodDescription);
            foodList.add(foodData);
        }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return foodList;
    }
}

