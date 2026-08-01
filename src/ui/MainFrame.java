package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level JFrame — holds the sidebar and swaps content panels via CardLayout.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private SidebarPanel sidebar;

    public MainFrame() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_MAIN);

        // ── Layout ──
        setLayout(new BorderLayout());

        // Content area — CardLayout to swap between panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.BG_MAIN);

        contentPanel.add(new DashboardPanel(), "Dashboard");
        contentPanel.add(new ExpensePanel(), "Expenses");
        contentPanel.add(new CategoryPanel(), "Categories");
        contentPanel.add(new MonthlySummaryPanel(), "Summary");

        // Sidebar — navigates the CardLayout
        sidebar = new SidebarPanel(() -> {
            String page = sidebar.getActivePage();
            cardLayout.show(contentPanel, page);
        });

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
