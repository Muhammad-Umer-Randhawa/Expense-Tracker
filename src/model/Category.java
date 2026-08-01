package model;

public class Category {
    private int id;
    private String name;
    private double budget;

    // Constructor
    public Category(int id, String name, double budget) {
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
