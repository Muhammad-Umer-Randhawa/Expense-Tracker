package model;
import java.sql.Date;
public class MonthlySummary {
    private int id;
    private Date monthDate;
    private double salary;
    public MonthlySummary(int id, Date monthDate, double salary) {
        this.id = id;
        this.monthDate = monthDate;
        this.salary = salary;
    }
    public int getId() {
        return id;
    }
    public Date getMonthDate() {
        return monthDate;
    }
    public double getSalary() {
        return salary;
    }
}
