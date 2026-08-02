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
    private DashboardPanel dashboardPanel;
    private ExpensePanel expensePanel;
    private CategoryPanel categoryPanel;
    private MonthlySummaryPanel summaryPanel;

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

        dashboardPanel = new DashboardPanel();
        expensePanel = new ExpensePanel();
        categoryPanel = new CategoryPanel();
        summaryPanel = new MonthlySummaryPanel();

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(expensePanel, "Expenses");
        contentPanel.add(categoryPanel, "Categories");
        contentPanel.add(summaryPanel, "Summary");

        // Sidebar — navigates the CardLayout
        sidebar = new SidebarPanel(() -> {
            String page = sidebar.getActivePage();
            cardLayout.show(contentPanel, page);

            if (page.equals("Dashboard")) {
                dashboardPanel.refreshData();
            } else if (page.equals("Expenses")) {   
                expensePanel.refreshData();
            } else if (page.equals("Summary")) {
                summaryPanel.refreshData();
            }
        });

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
