package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import dao.MonthlySummaryDAO;
import model.MonthlySummary;
import java.sql.Date;
import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDate;

public class MonthlySummaryPanel extends JPanel {

    private JTextField monthField, salaryField;
    private JLabel salaryDisplay, expensesDisplay, balanceDisplay, statusDisplay;
    private JPanel balanceBar;
    private MonthlySummaryDAO summaryDAO = new MonthlySummaryDAO();

    private double currentSalary = 0;
    private double currentExpenses = 0;
    private Date monthDate;

    public MonthlySummaryPanel() {

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate firstOfMonth = today.withDayOfMonth(1);
        monthDate = Date.valueOf(firstOfMonth);

        setBackground(AppTheme.BG_MAIN);
        setLayout(new BorderLayout(0, 24));
        setBorder(new EmptyBorder(32, 36, 32, 36));

        // ── Header ──
        JLabel heading = new JLabel("MONTHLY SUMMARY");
        heading.setFont(AppTheme.FONT_BIG_TITLE);
        heading.setForeground(Color.BLACK);

        JLabel sub = new JLabel("SET YOUR SALARY AND TRACK MONTHLY BALANCE");
        sub.setFont(AppTheme.FONT_SUBTITLE);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(heading);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);

        // ── Set Salary form ──
        AppTheme.BrutalistPanel formCard = new AppTheme.BrutalistPanel(AppTheme.BG_CARD);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(24, 28, 30, 34));

        JLabel formTitle = AppTheme.sectionLabel("Set Monthly Salary");
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fieldsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        fieldsRow.setOpaque(false);
        fieldsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        fieldsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        monthField = AppTheme.styledTextField(12);
        monthField.setText("2026-08-01");
        salaryField = AppTheme.styledTextField(12);
        salaryField.setText("60000");

        fieldsRow.add(createFieldGroup("Month (YYYY-MM-DD)", monthField));
        fieldsRow.add(createFieldGroup("Monthly Salary (Rs.)", salaryField));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton setBtn = AppTheme.primaryButton("Set Salary");
        setBtn.setToolTipText("Update your monthly salary to track savings");
        setBtn.addActionListener(e -> handleSetSalary());
        btnRow.add(setBtn);

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

        // ── Summary display ──
        JPanel summarySection = new JPanel();
        summarySection.setOpaque(false);
        summarySection.setLayout(new BoxLayout(summarySection, BoxLayout.Y_AXIS));

        JLabel summaryTitle = AppTheme.sectionLabel("August 2026 — Overview");
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        summarySection.add(summaryTitle);
        summarySection.add(Box.createVerticalStrut(20));

        // Three info cards in a row
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardsRow.add(createInfoCard("Salary", salaryDisplay = styledValueLabel("Rs. 60,000", AppTheme.ACCENT)));
        cardsRow.add(createInfoCard("Total Expenses",
                expensesDisplay = styledValueLabel("Rs. 24,850", AppTheme.ACCENT_ORANGE)));
        cardsRow.add(createInfoCard("Balance", balanceDisplay = styledValueLabel("Rs. 35,150", AppTheme.ACCENT_GREEN)));

        summarySection.add(cardsRow);
        summarySection.add(Box.createVerticalStrut(28));

        // ── Progress bar ──
        JLabel barTitle = AppTheme.sectionLabel("Budget Usage");
        barTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        summarySection.add(barTitle);
        summarySection.add(Box.createVerticalStrut(12));

        JPanel barContainer = new JPanel(new BorderLayout(0, 8));
        barContainer.setOpaque(false);
        barContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        barContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        balanceBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                int shadowOffset = 6;
                int borderWidth = 3;
                int w = getWidth();
                int h = getHeight();
                
                // Shadow
                g2.setColor(Color.BLACK);
                g2.fillRect(shadowOffset, shadowOffset, w - shadowOffset, h - shadowOffset);

                // Background track
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w - shadowOffset, h - shadowOffset);

                // Filled portion
                double ratio = currentSalary > 0 ? Math.min(currentExpenses / currentSalary, 1.0) : 0;
                int fillW = (int) ((w - shadowOffset) * ratio);

                Color barColor = ratio > 0.8 ? AppTheme.ACCENT_RED
                        : ratio > 0.5 ? AppTheme.ACCENT_ORANGE
                        : AppTheme.ACCENT_GREEN;
                g2.setColor(barColor);
                g2.fillRect(0, 0, fillW, h - shadowOffset);
                
                // Border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(borderWidth));
                g2.drawRect(borderWidth/2, borderWidth/2, w - shadowOffset - borderWidth, h - shadowOffset - borderWidth);
                // Separator line
                if (fillW > 0 && fillW < w - shadowOffset) {
                    g2.drawLine(fillW, 0, fillW, h - shadowOffset);
                }

                // Percentage text
                String pct = String.format("%.1f%%", ratio * 100);
                g2.setFont(AppTheme.FONT_TABLE_H);
                g2.setColor(Color.BLACK);
                FontMetrics fm = g2.getFontMetrics();
                int textX = Math.max(fillW - fm.stringWidth(pct) - 12, 8);
                int textY = (h - shadowOffset + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(pct, textX, textY);

                g2.dispose();
            }
        };
        balanceBar.setPreferredSize(new Dimension(0, 32));
        balanceBar.setOpaque(false);

        statusDisplay = new JLabel("YOU'VE USED 41.4% OF YOUR SALARY. LOOKING GOOD! 👍");
        statusDisplay.setFont(AppTheme.FONT_SUBTITLE);
        statusDisplay.setForeground(AppTheme.TEXT_SECONDARY);

        barContainer.add(balanceBar, BorderLayout.CENTER);
        barContainer.add(statusDisplay, BorderLayout.SOUTH);

        summarySection.add(barContainer);

        try {
            MonthlySummary summary = summaryDAO.getMonthlySummary(monthDate);
            if (summary != null) {
                currentSalary = summary.getSalary();
                salaryField.setText(String.valueOf(summary.getSalary()));
            }
            currentExpenses = summaryDAO.getTotalExpensesForMonth(monthDate);
            handleSetSalary();
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load summary: " + e.getMessage());
        }

        add(topSection, BorderLayout.NORTH);
        add(summarySection, BorderLayout.CENTER);
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

    private JPanel createInfoCard(String title, JLabel valueLabel) {
        AppTheme.BrutalistPanel card = new AppTheme.BrutalistPanel(AppTheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 24, 26, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(AppTheme.FONT_CARD_LBL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    private JLabel styledValueLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_CARD_NUM);
        lbl.setForeground(color);
        return lbl;
    }

    private void handleSetSalary() {
        String salaryText = salaryField.getText().trim();
        if (salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a salary.", "Missing Field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            currentSalary = Double.parseDouble(salaryText);
            summaryDAO.addOrUpdateMonth(monthDate, currentSalary);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid salary amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save salary: " + e.getMessage());
            return;
        }

        double balance = currentSalary - currentExpenses;
        double ratio = currentSalary > 0 ? currentExpenses / currentSalary : 0;

        salaryDisplay.setText(String.format("Rs. %,.0f", currentSalary));
        expensesDisplay.setText(String.format("Rs. %,.0f", currentExpenses));
        balanceDisplay.setText(String.format("Rs. %,.0f", balance));
        balanceDisplay.setForeground(balance >= 0 ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_RED);

        String status;
        if (ratio > 1.0) {
            status = String.format("⚠️ YOU'VE EXCEEDED YOUR SALARY BY RS. %,.0f!", Math.abs(balance));
        } else if (ratio > 0.8) {
            status = String.format("⚡ CAREFUL! YOU'VE USED %.1f%% OF YOUR SALARY.", ratio * 100);
        } else {
            status = String.format("YOU'VE USED %.1f%% OF YOUR SALARY. LOOKING GOOD! 👍", ratio * 100);
        }
        statusDisplay.setText(status);
        balanceBar.repaint();
    }
}
