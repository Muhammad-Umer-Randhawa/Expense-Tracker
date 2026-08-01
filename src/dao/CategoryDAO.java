package dao;
import model.Category;
import java.sql.*;
import java.util.*;
import db.DBConnection;
import java.io.*;
public class CategoryDAO {

    public Category addCategory(String name, double budget) throws SQLException, IOException {
        DBConnection db = new DBConnection();
        Connection conn = db.connectDatabase();
        String insert = "INSERT INTO categories (name, budget) VALUES (?, ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);

        insertStmt.setString(1, name);
        insertStmt.setDouble(2, budget);
        int row = insertStmt.executeUpdate();
        ResultSet keys = insertStmt.getGeneratedKeys();
        keys.next();
        int id = keys.getInt(1);

        return new Category(id, name, budget);
    }
    public int deleteCategory(int id) throws SQLException, IOException {
        DBConnection db = new DBConnection();
        Connection conn = db.connectDatabase();
        String delete = "DELETE FROM categories WHERE id = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(delete);
        deleteStmt.setInt(1, id);
        int row = deleteStmt.executeUpdate();
        return row;
    }
    public List<Category> getAllCategories() throws SQLException, IOException {
        DBConnection db = new DBConnection();
        Connection conn = db.connectDatabase();
        String select = "SELECT * FROM categories";
        PreparedStatement selectStmt = conn.prepareStatement(select);
        ResultSet rs = selectStmt.executeQuery();
        List<Category> categories = new ArrayList<>();
        
        while(rs.next()){
            int id = rs.getInt("id");
            String name = rs.getString("name");
            double budget = rs.getDouble("budget");
            categories.add(new Category(id, name, budget));
        }
        return categories;
    }
}