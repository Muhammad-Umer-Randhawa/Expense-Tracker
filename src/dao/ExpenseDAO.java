    package dao;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.io.*;

    import db.DBConnection;
    import model.Expense;

    public class ExpenseDAO {

        public double getTotalByCategory(int categoryId) throws SQLException, IOException {
            DBConnection db = new DBConnection();
            Connection conn = db.connectDatabase();
            String select = "SELECT SUM(amount) FROM expenses WHERE category_id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(select);

            selectStmt.setInt(1, categoryId);

            ResultSet rs = selectStmt.executeQuery();
            rs.next();
            double total = rs.getDouble(1);
            return total; 
        }

        public Expense addExpense(int category_id, double amount, Date date, String description) throws SQLException, IOException {
            DBConnection db = new DBConnection();
            Connection conn = db.connectDatabase();
            String insert = "INSERT INTO expenses (category_id, amount, date, description) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);

            insertStmt.setInt(1, category_id);
            insertStmt.setDouble(2, amount);
            insertStmt.setDate(3, new Date(date.getTime()));
            insertStmt.setString(4, description);
            insertStmt.executeUpdate();

            ResultSet keys = insertStmt.getGeneratedKeys();
            keys.next();
            int id = keys.getInt(1);
            return new Expense(id, amount, date, description, category_id);
        }

        public int deleteExpense(int id) throws SQLException, IOException {
            DBConnection db = new DBConnection();
            Connection conn = db.connectDatabase();
            String delete = "DELETE FROM expenses WHERE id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(delete);
            deleteStmt.setInt(1, id);
            int row = deleteStmt.executeUpdate();
            return row;
        }

        public List<Expense> getAllExpenses() throws SQLException, IOException {
            DBConnection db = new DBConnection();
            Connection conn = db.connectDatabase();
            String select = "SELECT * FROM expenses";
            PreparedStatement selectStmt = conn.prepareStatement(select);
            ResultSet rs = selectStmt.executeQuery();
            List<Expense> expenses = new ArrayList<>();
            
            while(rs.next()){
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("date");
                String description = rs.getString("description");
                int category_id = rs.getInt("category_id");
                expenses.add(new Expense(id, amount, date, description, category_id));
            }
            return expenses;
        }
    }
