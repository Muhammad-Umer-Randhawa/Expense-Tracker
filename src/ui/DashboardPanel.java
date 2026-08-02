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

public class DashboardPanel extends JPanel {

    private MonthlySummaryDAO summaryDAO = new MonthlySummaryDAO();

    public DashboardPanel() {
        
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        Date monthDate = Date.valueOf(firstOfMonth);

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

        cardsRow.add(createSummaryCard("TOTAL EXPENSES", "Rs. 24,850", "THIS MONTH",
                AppTheme.ACCENT_BLUE));
        cardsRow.add(createSummaryCard("MONTHLY SALARY", "Rs. 60,000", "AUGUST 2026",
                AppTheme.ACCENT_GREEN));
        cardsRow.add(createSummaryCard("SAVINGS", "Rs. 35,150", "REMAINING BALANCE",
                AppTheme.ACCENT_PURPLE));

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
        Object[][] data = {
                { "2026-08-01", "Uber ride to office", "Transport", "Rs. 350" },
                { "2026-07-31", "Grocery shopping", "Food", "Rs. 2,200" },
                { "2026-07-30", "Netflix subscription", "Entertainment", "Rs. 649" },
                { "2026-07-29", "Electricity bill", "Utilities", "Rs. 1,450" },
                { "2026-07-28", "Coffee with friends", "Food", "Rs. 480" },
                { "2026-07-27", "Gym membership", "Health", "Rs. 1,500" },
                { "2026-07-26", "Stationery supplies", "Education", "Rs. 320" },
                { "2026-07-25", "Petrol fill-up", "Transport", "Rs. 2,000" },
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
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
    }

    private JPanel createSummaryCard(String title, String value, String subtitle, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(20, 24, 26, 30));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_CARD_LBL);
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(AppTheme.FONT_CARD_NUM);
        lblValue.setForeground(Color.BLACK);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblSub.setForeground(new Color(40, 40, 40));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);
        card.add(Box.createVerticalGlue());
        card.add(lblSub);

        return card;
    }
}
