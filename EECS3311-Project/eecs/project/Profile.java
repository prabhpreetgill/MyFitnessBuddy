package eecs.project;

import java.time.LocalDate;

public class Profile {
	private final int id; // "Male" or "Female"
	private final String name; // "Male" or "Female"
	private final String sex; // "Male" or "Female"
	private final LocalDate dateOfBirth;
	private final double height; // in centimeters (if metric) or inches (if imperial)
	private final double weight; // in kilograms (if metric) or pounds (if imperial)
	private final boolean isMetric;

	public Profile(int id, String name, String sex, LocalDate dateOfBirth, double height, double weight, boolean isMetric) {
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.dateOfBirth = dateOfBirth;
		this.height = height;
		this.weight = weight;
		this.isMetric = isMetric;
	}

	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public double getHeight() {
		return height;
	}

	public double getWeight() {
		return weight;
	}

	public double getBMR() {
		double bmr;

		if (sex.equalsIgnoreCase("Male")) {
			bmr = isMetric
					? 66.5 + (13.75 * weight) + (5.003 * height) - (6.75 * calculateAge())
					: 66 + (6.23 * weight) + (12.7 * height) - (6.8 * calculateAge());
		} else {
			bmr = isMetric
					? 655.1 + (9.563 * weight) + (1.85 * height) - (4.676 * calculateAge())
					: 655 + (4.35 * weight) + (4.7 * height) - (4.7 * calculateAge());
		}

		return bmr;
	}

	public int calculateAge() {
		LocalDate currentDate = LocalDate.now();
		return currentDate.getYear() - dateOfBirth.getYear();
	}

	@Override
	public String toString() {
		return "Sex: " + sex + "\n" +
				"Date of Birth: " + dateOfBirth + "\n" +
				"Height: " + height + (isMetric ? " cm" : " inches") + "\n" +
				"Weight: " + weight + (isMetric ? " kg" : " pounds");
	}

	public int getId() {
		return id;
	}

	public boolean isMetric() {
		return isMetric;
	}
}
