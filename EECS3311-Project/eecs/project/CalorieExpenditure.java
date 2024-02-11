package eecs.project;

import java.time.LocalDate;

public class CalorieExpenditure {
    private LocalDate date;
    private double calories;

    public CalorieExpenditure(LocalDate date, double calories) {
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
