package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

import dao.CategoryDAO;
import model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Category management screen — add category form + categories table.
 * All data is dummy / hardcoded. No backend connection.
 */
public class CategoryPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTextField nameField, budgetField;
    private CategoryDAO categoryDAO = new CategoryDAO();

    public CategoryPanel() {
        setBackground(AppTheme.BG_MAIN);
        setLayout(new BorderLayout(0, 24));
        setBorder(new EmptyBorder(32, 36, 32, 36));

        // ── Header ──
        JLabel heading = new JLabel("CATEGORIES");
        heading.setFont(AppTheme.FONT_BIG_TITLE);
        heading.setForeground(Color.BLACK);

        JLabel sub = new JLabel("MANAGE YOUR SPENDING CATEGORIES AND BUDGETS");
        sub.setFont(AppTheme.FONT_SUBTITLE);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);

        // ── Add Category form ──
        AppTheme.BrutalistPanel formCard = new AppTheme.BrutalistPanel(AppTheme.BG_CARD);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(24, 28, 30, 34));

        JLabel formTitle = AppTheme.sectionLabel("Add New Category");
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fieldsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        fieldsRow.setOpaque(false);
        fieldsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        fieldsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = AppTheme.styledTextField(15);
        budgetField = AppTheme.styledTextField(15);
        fieldsRow.add(createFieldGroup("Category Name", nameField));
        fieldsRow.add(createFieldGroup("Monthly Budget (Rs.)", budgetField));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton addBtn = AppTheme.primaryButton("+ Add Category");
        addBtn.setToolTipText("Click to create a new category");
        addBtn.addActionListener(e -> handleAddCategory());
        btnRow.add(addBtn);

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(fieldsRow);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(btnRow);

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(headerPanel);
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(formCard);

        // ── Categories table ──
        JPanel tableSection = new JPanel(new BorderLayout(0, 12));
        tableSection.setOpaque(false);

        JLabel tableLbl = AppTheme.sectionLabel("All Categories");
        tableSection.add(tableLbl, BorderLayout.NORTH);

        String[] columns = { "ID", "Name", "Budget", "Action" };
        List<Category> categories;
        try {
            categories = categoryDAO.getAllCategories();
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
            categories = new ArrayList<>();
        }

        Object[][] data = new Object[categories.size()][4];
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            data[i][0] = String.valueOf(c.getId());
            data[i][1] = c.getName();
            data[i][2] = "Rs. " + c.getBudget();
            data[i][3] = "Delete";
        }

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        JTable table = new JTable(tableModel);
        AppTheme.styleTable(table);

        // Budget column — right aligned, green
        DefaultTableCellRenderer budgetRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!isSelected) c.setBackground(row % 2 == 0 ? AppTheme.BG_CARD : AppTheme.BG_ROW_ALT);
                c.setForeground(Color.BLACK);
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 16));
                setFont(AppTheme.FONT_TABLE_H);
                return c;
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(budgetRenderer);

        // Delete button
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(table, tableModel));

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        tableSection.add(AppTheme.styledScrollPane(table), BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);
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

    private void handleAddCategory() {
        String name = nameField.getText().trim();
        String budgetText = budgetField.getText().trim();

        if (name.isEmpty() || budgetText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double budget = Double.parseDouble(budgetText);
            Category c = categoryDAO.addCategory(name, budget);
            tableModel.addRow(new Object[] { String.valueOf(c.getId()), c.getName(), "Rs. " + c.getBudget(), "Delete" });
            nameField.setText("");
            budgetField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Budget must be a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add category: " + ex.getMessage());
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
                        new CategoryDAO().deleteCategory(id);
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
