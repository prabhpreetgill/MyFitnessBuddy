package eecs.project;

import java.time.LocalDate;

public class CalorieIntake {
    private LocalDate date;
    private double calories;

    private String foodGroup;

    public CalorieIntake(LocalDate date, double calories, String foodGroup) {
        this.date = date;
        this.calories = calories;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getCalories() {
        return calories;
    }
}
