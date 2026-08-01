package model;

import java.sql.Date;

public class Expense {
    private int id;
    private double amount;
    private Date date;
    private String description;
    private int category_id;

    public Expense(int id, double amount, Date date, String description, int category_id) {
        this.id = id;
        this.category_id = category_id;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return category_id;
    }
}
