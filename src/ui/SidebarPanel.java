package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Left-side navigation sidebar with 4 nav items.
 * Emits navigation events via a callback.
 */
public class SidebarPanel extends JPanel {

    private static final int WIDTH = 230;
    private String activePage = "Dashboard";
    private Runnable onNavigate; // called after activePage changes
    private JPanel navPanel;
    // Unicode icons for nav items
    private static final String[][] NAV_ITEMS = {
        { "\u2302", "Dashboard" },       // ⌂
        { "\uD83D\uDCB8", "Expenses" },  // 💸
        { "\u2630", "Categories" },       // ☰
        { "\uD83D\uDCC5", "Summary" }    // 📅
    };

    public SidebarPanel(Runnable onNavigate) {
        this.onNavigate = onNavigate;
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(AppTheme.ACCENT); // vibrant yellow
        setBorder(new MatteBorder(0, 0, 0, 3, Color.BLACK)); // thick separator
        setLayout(new BorderLayout());

        // ── Top: brand ──
        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(32, 20, 32, 20));

        JLabel logo = new JLabel("\uD83D\uDCB0"); // 💰
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("EXPENSE");
        title.setFont(AppTheme.FONT_BIG_TITLE);
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("TRACKER");
        subtitle.setFont(AppTheme.FONT_SUBTITLE);
        subtitle.setForeground(Color.BLACK);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(logo);
        brandPanel.add(Box.createVerticalStrut(8));
        brandPanel.add(title);
        brandPanel.add(subtitle);

        // ── Nav buttons ──
        navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        for (String[] item : NAV_ITEMS) {
            navPanel.add(createNavButton(item[0], item[1]));
            navPanel.add(Box.createVerticalStrut(4));
        }

        // ── Bottom: version ──
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 24, 20, 0));
        JLabel ver = new JLabel("v1.0.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(AppTheme.TEXT_SECONDARY);
        bottomPanel.add(ver);

        add(brandPanel, BorderLayout.NORTH);
        add(navPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public String getActivePage() {
        return activePage;
    }

    private JPanel createNavButton(String icon, String label) {
        JPanel btn = new JPanel(new BorderLayout());
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(label.equals(activePage) ? new Color(255, 255, 255) : new Color(255, 244, 180));
        btn.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 1, false),
                new EmptyBorder(8, 18, 8, 14)));
        btn.setMaximumSize(new Dimension(WIDTH - 24, 46));
        btn.setPreferredSize(new Dimension(WIDTH - 24, 46));
        btn.putClientProperty("navLabel", label);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activePage = label;
                refreshNavButtons();
                onNavigate.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!label.equals(activePage)) {
                    btn.setBackground(new Color(255, 245, 160));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!label.equals(activePage)) {
                    btn.setBackground(new Color(255, 240, 120));
                }
            }
        });

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLbl.setForeground(Color.BLACK);
        iconLbl.setPreferredSize(new Dimension(28, 28));

        JLabel textLbl = new JLabel(label.toUpperCase());
        textLbl.setFont(label.equals(activePage) ? AppTheme.FONT_NAV_ACTIVE.deriveFont(14f) : AppTheme.FONT_NAV.deriveFont(14f));
        textLbl.setForeground(Color.BLACK);

        btn.add(iconLbl, BorderLayout.WEST);
        btn.add(textLbl, BorderLayout.CENTER);

        return btn;
    }

    private void refreshNavButtons() {
        for (Component comp : navPanel.getComponents()) {
            if (!(comp instanceof JPanel)) {
                continue;
            }
            JPanel btn = (JPanel) comp;
            Object value = btn.getClientProperty("navLabel");
            if (!(value instanceof String)) {
                continue;
            }
            String label = (String) value;
            btn.setBackground(label.equals(activePage) ? Color.WHITE : new Color(255, 240, 120));
            Component center = ((BorderLayout) btn.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (center instanceof JLabel) {
                ((JLabel) center).setFont(label.equals(activePage) ? AppTheme.FONT_NAV_ACTIVE : AppTheme.FONT_NAV);
            }
        }
    }
}
