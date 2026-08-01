package dao;

import java.sql.*;
import db.DBConnection;
import java.io.*;
import model.MonthlySummary;

public class MonthlySummaryDAO {
    public MonthlySummary addOrUpdateMonth(Date monthDate, double salary) throws SQLException, IOException {
        DBConnection db = new DBConnection();
        Connection conn = db.connectDatabase();
        String insertOrUpdate = "INSERT INTO monthly_summary (month_date, salary) VALUES (?, ?) ON DUPLICATE KEY UPDATE salary = ?";
        PreparedStatement stmt = conn.prepareStatement(insertOrUpdate, Statement.RETURN_GENERATED_KEYS);

        stmt.setDate(1, monthDate);
        stmt.setDouble(2, salary);
        stmt.setDouble(3, salary);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        int id;
        if (keys.next()) {
            id = keys.getInt(1);
        } else {
            // If no new row was inserted, retrieve the existing ID
            String selectId = "SELECT id FROM monthly_summary WHERE month_date = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectId);
            selectStmt.setDate(1, monthDate);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            } else {
                throw new SQLException("Failed to retrieve ID for the given month date.");
            }
        }

        return new MonthlySummary(id, monthDate, salary);
    }

    public double getTotalExpensesForMonth(Date monthDate) throws SQLException, IOException {
        DBConnection db = new DBConnection();
        Connection conn = db.connectDatabase();
        String select = "SELECT SUM(amount) AS total_expenses FROM expenses WHERE MONTH(date) = MONTH(?) AND YEAR(date) = YEAR(?)";
        PreparedStatement stmt = conn.prepareStatement(select);
        stmt.setDate(1, monthDate);
        stmt.setDate(2, monthDate);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("total_expenses");
        } else {
            return 0.0;
        }
    }

    public MonthlySummary getMonthlySummary(Date monthDate) throws SQLException, IOException {
        DBConnection db = new DBConnection();
        Connection conn = db.connectDatabase();
        String select = "SELECT * FROM monthly_summary WHERE month_date = ?";
        PreparedStatement stmt = conn.prepareStatement(select);
        stmt.setDate(1, monthDate);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            double salary = rs.getDouble("salary");
            return new MonthlySummary(id, monthDate, salary);
        } else {
            return null;
        }
    }
}
