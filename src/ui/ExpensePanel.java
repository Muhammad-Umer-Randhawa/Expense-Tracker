package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Expense management screen — add expense form + full expense list table.
 * All data is dummy / hardcoded. No backend connection.
 */
public class ExpensePanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTextField amountField, descField, dateField;
    private JComboBox<String> categoryCombo;

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

        String[] categories = { "Food", "Transport", "Entertainment", "Utilities", "Health", "Education" };
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
        Object[][] data = {
                { "1", "2026-08-01", "Uber ride to office", "Transport", "Rs. 350", "Delete" },
                { "2", "2026-07-31", "Grocery shopping", "Food", "Rs. 2,200", "Delete" },
                { "3", "2026-07-30", "Netflix subscription", "Entertainment", "Rs. 649", "Delete" },
                { "4", "2026-07-29", "Electricity bill", "Utilities", "Rs. 1,450", "Delete" },
                { "5", "2026-07-28", "Coffee with friends", "Food", "Rs. 480", "Delete" },
                { "6", "2026-07-27", "Gym membership", "Health", "Rs. 1,500", "Delete" },
        };

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
        String amount = amountField.getText().trim();
        String desc = descField.getText().trim();
        String date = dateField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (amount.isEmpty() || desc.isEmpty() || date.isEmpty()) {
            // Show a styled warning
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add to table (dummy — no backend)
        int nextId = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[] { String.valueOf(nextId), date, desc, category, "Rs. " + amount, "Delete" });

        // Clear fields
        amountField.setText("");
        descField.setText("");
        dateField.setText("");
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
                    model.removeRow(row);
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
