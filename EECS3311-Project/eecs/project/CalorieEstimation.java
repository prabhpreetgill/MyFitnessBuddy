package eecs.project;

import java.util.HashMap;
import java.util.Map;

public class CalorieEstimation {

    private static final Map<String, Double> exerciseMetValues = new HashMap<>();

    static {
        exerciseMetValues.put("walking", 0.65);
        exerciseMetValues.put("running", 1.33);
        exerciseMetValues.put("biking", 1.0);
        exerciseMetValues.put("swimming", 1.17);
        exerciseMetValues.put("weightlifting", 0.5);
        exerciseMetValues.put("skiing", 1.17);
        exerciseMetValues.put("skating", 0.83);
    }

    private static final Map<String, Double> intensityMultiplier = new HashMap<>();

    static {
        intensityMultiplier.put("low", 0.17);
        intensityMultiplier.put("medium", 0.21);
        intensityMultiplier.put("high", 0.25);
        intensityMultiplier.put("very high", 0.29);
    }

    public static double estimateCaloriesBurnt(String exerciseType, String intensity, int duration, double bmr) {
        double metValue = getMetabolicEquivalent(exerciseType, intensity);
        return metValue * bmr * (duration / 60.0);
    }

    private static double getMetabolicEquivalent(String exerciseType, String intensity) {
        if (exerciseMetValues.containsKey(exerciseType.toLowerCase())) {
            double baseMetValue = exerciseMetValues.get(exerciseType.toLowerCase());
            double intensityFactor = intensityMultiplier.getOrDefault(intensity.toLowerCase(), 1.0);
            return baseMetValue * intensityFactor;
        } else {
            System.out.println("Unknown exercise type");
            return 1.0;
        }
    }
}
