package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import dao.ExpenseDAO;
import dao.CategoryDAO;
import model.Expense;
import model.Category;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Date;
import java.io.IOException;
import java.awt.event.*;

/**
 * Expense management screen — add expense form + full expense list table.
 * All data is dummy / hardcoded. No backend connection.
 */
public class ExpensePanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTextField amountField, descField, dateField;
    private JComboBox<String> categoryCombo;
    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private List<Category> categoryList;

    public ExpensePanel() {
        setBackground(AppTheme.BG_MAIN);
        setLayout(new BorderLayout(0, 24));
        setBorder(new EmptyBorder(32, 36, 32, 36));

        // ── Header ──
        JLabel heading = new JLabel("EXPENSES");
        heading.setFont(AppTheme.FONT_BIG_TITLE);
        heading.setForeground(Color.BLACK);

        JLabel sub = new JLabel("ADD AND MANAGE YOUR EXPENSES");
        sub.setFont(AppTheme.FONT_SUBTITLE);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);

        // ── Add Expense form ──
        AppTheme.BrutalistPanel formCard = new AppTheme.BrutalistPanel(AppTheme.BG_CARD);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(24, 28, 30, 34)); // extra padding for shadow

        JLabel formTitle = AppTheme.sectionLabel("Add New Expense");
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields row
        JPanel fieldsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        fieldsRow.setOpaque(false);
        fieldsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        fieldsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldsRow.add(createFieldGroup("Amount (Rs.)", amountField = AppTheme.styledTextField(10)));
        fieldsRow.add(createFieldGroup("Description", descField = AppTheme.styledTextField(10)));
        fieldsRow.add(createFieldGroup("Date (YYYY-MM-DD)", dateField = AppTheme.styledTextField(10)));

        this.categoryList = new ArrayList<>();
        try {
            categoryList = categoryDAO.getAllCategories();
        } catch (SQLException | IOException e) {
            categoryList = new ArrayList<>();
        }
        String[] categories = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categories[i] = categoryList.get(i).getName();
        }
        categoryCombo = AppTheme.styledComboBox(categories);
        fieldsRow.add(createFieldGroup("Category", categoryCombo));

        // Button row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton addBtn = AppTheme.primaryButton("+ Add Expense");
        addBtn.setToolTipText("Click to add this expense to your tracker");
        addBtn.addActionListener(e -> handleAddExpense());
        btnRow.add(addBtn);

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(fieldsRow);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(btnRow);

        // ── Top section ──
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(formCard);

        // ── Expenses table ──
        JPanel tableSection = new JPanel(new BorderLayout(0, 12));
        tableSection.setOpaque(false);

        JLabel tableLbl = AppTheme.sectionLabel("All Expenses");
        tableSection.add(tableLbl, BorderLayout.NORTH);

        String[] columns = { "ID", "Date", "Description", "Category", "Amount", "Action" };
        List<Expense> expenses;
        try {
            expenses = expenseDAO.getAllExpenses();
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load expenses: " + e.getMessage());
            expenses = new ArrayList<>();
        }

        Object[][] data = new Object[expenses.size()][6];
        for (int i = 0; i < expenses.size(); i++) {
            Expense ex = expenses.get(i);
            data[i][0] = String.valueOf(ex.getId());
            data[i][1] = ex.getDate().toString();
            data[i][2] = ex.getDescription();
            data[i][3] = findCategoryName(ex.getCategoryId());
            data[i][4] = "Rs. " + ex.getAmount();
            data[i][5] = "Delete";
        }

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable table = new JTable(tableModel);
        AppTheme.styleTable(table);

        // Amount column — right aligned, accent color
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!isSelected)
                    c.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_ROW_ALT);
                c.setForeground(Color.BLACK);
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 16));
                setFont(AppTheme.FONT_TABLE_H);
                return c;
            }
        };
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Delete button in last column
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(table, tableModel));

        // Hide ID column visually but keep in model
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        tableSection.add(AppTheme.styledScrollPane(table), BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                try {
                    categoryList = categoryDAO.getAllCategories();
                } catch (SQLException | IOException ex) {
                    categoryList = new ArrayList<>();
                }
                categoryCombo.removeAllItems();
                for(Category c : categoryList) {
                    categoryCombo.addItem(c.getName());
                }
            }
        });
    }

    private String findCategoryName(int categoryId) {
            for (Category c : categoryList) {
                if (c.getId() == categoryId) return c.getName();
            }
            return "Unknown";
    }

    private JPanel createFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel();    
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        JLabel lbl = AppTheme.formLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        group.add(lbl);
        group.add(Box.createVerticalStrut(6));
        group.add(field);
        return group;
    }

    private void handleAddExpense() {
        String amountText = amountField.getText().trim();
        String desc = descField.getText().trim();
        String dateText = dateField.getText().trim();
        String categoryName = (String) categoryCombo.getSelectedItem();

        if (amountText.isEmpty() || desc.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            Date date = Date.valueOf(dateText);
            int categoryId = -1;
            for (Category c : categoryList) {
                if (c.getName().equals(categoryName)) { categoryId = c.getId(); break; }
            }
            if (categoryId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a valid category.", "Invalid Category", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Expense ex = expenseDAO.addExpense(categoryId, amount, date, desc);
            tableModel.addRow(new Object[] { String.valueOf(ex.getId()), ex.getDate().toString(), ex.getDescription(), categoryName, "Rs. " + ex.getAmount(), "Delete" });

            amountField.setText("");
            descField.setText("");
            dateField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add expense: " + ex.getMessage());
        }
    }

    // ── Delete button renderer ──
    private static class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 4));
            setOpaque(true);
            button = AppTheme.dangerButton("Delete");
            button.setPreferredSize(new Dimension(80, 28));
            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_ROW_ALT);
            return this;
        }
    }

    // ── Delete button editor ──
    private static class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton button;
        private final JTable table;
        private final DefaultTableModel model;

        public ButtonEditor(JTable table, DefaultTableModel model) {
            this.table = table;
            this.model = model;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            panel.setOpaque(true);
            button = AppTheme.dangerButton("Delete");
            button.setPreferredSize(new Dimension(80, 28));
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                fireEditingStopped();
                if (row >= 0 && row < model.getRowCount()) {
                    int id = Integer.parseInt((String) model.getValueAt(row, 0));
                    try {
                        new ExpenseDAO().deleteExpense(id);
                        model.removeRow(row);
                    } catch (SQLException | IOException ex) {
                        JOptionPane.showMessageDialog(panel, "Failed to delete: " + ex.getMessage());
                    }
                }
            });
            panel.add(button);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_ROW_ALT);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }
    }
}