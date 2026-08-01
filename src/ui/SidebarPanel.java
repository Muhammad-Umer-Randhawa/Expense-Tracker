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
        JPanel navPanel = new JPanel();
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
        JPanel btn = new JPanel(new BorderLayout()) {
            private boolean hovering = false;
            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        activePage = label;
                        onNavigate.run();
                        // repaint the entire sidebar so all buttons update
                        SidebarPanel.this.repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                boolean active = label.equals(activePage);

                if (active || hovering) {
                    int offset = active ? 4 : 2;
                    int border = 3;
                    // Shadow
                    g2.setColor(Color.BLACK);
                    g2.fillRect(8 + offset, offset, getWidth() - 24 - offset, getHeight() - 8 - offset);
                    // Background
                    g2.setColor(Color.WHITE);
                    g2.fillRect(8, 0, getWidth() - 24 - offset, getHeight() - 8 - offset);
                    // Border
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(border));
                    g2.drawRect(8 + border/2, border/2, getWidth() - 24 - offset - border, getHeight() - 8 - offset - border);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setMaximumSize(new Dimension(WIDTH, 56));
        btn.setPreferredSize(new Dimension(WIDTH, 56));
        btn.setBorder(new EmptyBorder(0, 24, 8, 16));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLbl.setForeground(Color.BLACK);
        iconLbl.setPreferredSize(new Dimension(30, 30));

        JLabel textLbl = new JLabel(label.toUpperCase());
        textLbl.setFont(label.equals(activePage) ? AppTheme.FONT_NAV_ACTIVE : AppTheme.FONT_NAV);
        textLbl.setForeground(Color.BLACK);

        btn.add(iconLbl, BorderLayout.WEST);
        btn.add(textLbl, BorderLayout.CENTER);

        return btn;
    }

    /** Repaint redraws all children to reflect active state changes. */
    @Override
    public void repaint() {
        super.repaint();
        // Force children to repaint for active state
        if (getComponentCount() > 0) {
            for (Component c : getComponents()) {
                c.repaint();
            }
        }
    }
}
