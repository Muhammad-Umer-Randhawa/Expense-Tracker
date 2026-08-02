package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import dao.MonthlySummaryDAO;
import model.MonthlySummary;
import java.sql.Date;
import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDate;
import dao.ExpenseDAO;
import dao.CategoryDAO;
import model.Expense;
import model.Category;
import java.util.List;
import java.util.ArrayList;

public class DashboardPanel extends JPanel {

    private MonthlySummaryDAO summaryDAO = new MonthlySummaryDAO();
    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private JLabel totalExpensesLabel, monthlySalaryLabel, savingsLabel;
    private DefaultTableModel tableModel;

    public DashboardPanel() {

        setBackground(AppTheme.BG_MAIN);
        setLayout(new BorderLayout(0, 24));
        setBorder(new EmptyBorder(32, 36, 32, 36));

        // ── Header ──
        JLabel heading = new JLabel("DASHBOARD");
        heading.setFont(AppTheme.FONT_BIG_TITLE);
        heading.setForeground(Color.BLACK);

        JLabel sub = new JLabel("OVERVIEW OF YOUR FINANCES");
        sub.setFont(AppTheme.FONT_SUBTITLE);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);

        // ── Summary cards ──
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsRow.setOpaque(false);
        cardsRow.setPreferredSize(new Dimension(0, 130));

        totalExpensesLabel = new JLabel("Rs. 0");
        monthlySalaryLabel = new JLabel("Rs. 0");
        savingsLabel = new JLabel("Rs. 0");

        cardsRow.add(createSummaryCard("TOTAL EXPENSES", totalExpensesLabel, "THIS MONTH", AppTheme.ACCENT_BLUE));
        cardsRow.add(createSummaryCard("MONTHLY SALARY", monthlySalaryLabel, "THIS MONTH", AppTheme.ACCENT_GREEN));
        cardsRow.add(createSummaryCard("SAVINGS", savingsLabel, "REMAINING BALANCE", AppTheme.ACCENT_PURPLE));

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(24));
        topSection.add(cardsRow);

        // ── Recent Expenses table ──
        JPanel tableSection = new JPanel(new BorderLayout(0, 12));
        tableSection.setOpaque(false);

        JLabel tableLbl = AppTheme.sectionLabel("Recent Expenses");
        tableSection.add(tableLbl, BorderLayout.NORTH);

        String[] columns = { "Date", "Description", "Category", "Amount" };

        Object[][] data = new Object[0][4];

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        AppTheme.styleTable(table);

        // Right-align the amount column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_ROW_ALT);
                }
                c.setForeground(Color.BLACK);
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 16));
                setFont(AppTheme.FONT_TABLE_H);
                return c;
            }
        };
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        tableSection.add(AppTheme.styledScrollPane(table), BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);

        refreshData();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });
    }
    
    private String findCategoryName(int categoryId, List<Category> categories) {
    for (Category c : categories) {
        if (c.getId() == categoryId) return c.getName();
    }
    return "Unknown";
}

    private JPanel createSummaryCard(String title, JLabel valueLabel, String subtitle, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(20, 24, 26, 30));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_CARD_LBL);
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(AppTheme.FONT_CARD_NUM);
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblSub.setForeground(new Color(40, 40, 40));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());
        card.add(lblSub);

        return card;
    }

    public void refreshData() {
    LocalDate today = LocalDate.now();
    LocalDate firstOfMonth = today.withDayOfMonth(1);
    Date monthDate = Date.valueOf(firstOfMonth);
    double salary = 0;
    double totalExpenses = 0;

    try {
        MonthlySummary summary = summaryDAO.getMonthlySummary(monthDate);
        if (summary != null) {
            salary = summary.getSalary();
        }
        totalExpenses = summaryDAO.getTotalExpensesForMonth(monthDate);
    } catch (SQLException | IOException e) {
        JOptionPane.showMessageDialog(this, "Failed to load summary: " + e.getMessage());
    }

    double savings = salary - totalExpenses;

    totalExpensesLabel.setText(String.format("Rs. %,.0f", totalExpenses));
    monthlySalaryLabel.setText(String.format("Rs. %,.0f", salary));
    savingsLabel.setText(String.format("Rs. %,.0f", savings));

    List<Expense> expenses = new ArrayList<>();
    try {
        expenses = expenseDAO.getAllExpenses();
    } catch (SQLException | IOException e) {
        JOptionPane.showMessageDialog(this, "Failed to load expenses: " + e.getMessage());
    }

    expenses.sort((a, b) -> b.getDate().compareTo(a.getDate()));
    if (expenses.size() > 8) {
        expenses = expenses.subList(0, 8);
    }
    List<Category> categories = new ArrayList<>();
    try {
        categories = new CategoryDAO().getAllCategories();
    } catch (SQLException | IOException e) {
        e.printStackTrace();
    }
    tableModel.setRowCount(0);
    for (Expense ex : expenses) {
        tableModel.addRow(new Object[] {
            ex.getDate().toString(),
            ex.getDescription(),
            findCategoryName(ex.getCategoryId(), categories),
            String.format("Rs. %,.0f", ex.getAmount())
        });
    }
}
}