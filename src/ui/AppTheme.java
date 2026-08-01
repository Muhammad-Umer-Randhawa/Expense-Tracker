package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Neo-Brutalist design-system for the Expense Tracker GUI.
 * Stark colors, thick borders, hard shadows, and monospace fonts.
 */
public final class AppTheme {

    // ── Palette ──────────────────────────────────────────────
    public static final Color BG_DARK = new Color(245, 245, 245);
    public static final Color BG_MAIN = new Color(255, 255, 255);
    public static final Color BG_CARD = new Color(255, 255, 255);
    public static final Color BG_ROW_ALT = new Color(250, 250, 250);
    public static final Color BG_INPUT = new Color(255, 255, 255);

    // Vibrant Neo-Brutalist Accents
    public static final Color ACCENT = new Color(255, 220, 0); // Pure Yellow
    public static final Color ACCENT_GREEN = new Color(0, 230, 118); // Vibrant Green
    public static final Color ACCENT_RED = new Color(255, 51, 102); // Pinkish Red
    public static final Color ACCENT_ORANGE = new Color(255, 145, 0); // Pure Orange
    public static final Color ACCENT_BLUE = new Color(0, 229, 255); // Cyan
    public static final Color ACCENT_PURPLE = new Color(213, 128, 255); // Vibrant Purple

    public static final Color TEXT_PRIMARY = new Color(0, 0, 0); // Solid Black
    public static final Color TEXT_SECONDARY = new Color(60, 60, 60); // Dark Gray
    public static final Color BORDER = new Color(0, 0, 0); // Solid Black

    // ── Fonts (Monospaced / Grotesque style) ─────────────────
    public static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Monospaced", Font.BOLD, 14);
    public static final Font FONT_NAV = new Font("Monospaced", Font.BOLD, 15);
    public static final Font FONT_NAV_ACTIVE = new Font("Monospaced", Font.BOLD, 15);
    public static final Font FONT_CARD_NUM = new Font("Monospaced", Font.BOLD, 32);
    public static final Font FONT_CARD_LBL = new Font("Monospaced", Font.BOLD, 14);
    public static final Font FONT_TABLE_H = new Font("Monospaced", Font.BOLD, 14);
    public static final Font FONT_TABLE = new Font("Monospaced", Font.PLAIN, 14);
    public static final Font FONT_INPUT = new Font("Monospaced", Font.BOLD, 14);
    public static final Font FONT_BUTTON = new Font("Monospaced", Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font("Monospaced", Font.BOLD, 18);
    public static final Font FONT_BIG_TITLE = new Font("Monospaced", Font.BOLD, 32);

    private AppTheme() {
    } // utility class

    // ── Brutalist Panel ────────────────────────────────────────
    /**
     * A simplified panel with a flat background and thin black border.
     */
    public static class BrutalistPanel extends JPanel {
        private Color bgColor;

        public BrutalistPanel(Color bgColor) {
            super();
            this.bgColor = bgColor;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int shadowOffset = 2;

            // Soft shadow
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRect(shadowOffset, shadowOffset, getWidth() - shadowOffset, getHeight() - shadowOffset);

            // Background
            g2.setColor(bgColor);
            g2.fillRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset);

            // Thin border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(0, 0, getWidth() - shadowOffset - 1, getHeight() - shadowOffset - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Factory helpers ──────────────────────────────────────

    /**
     * Create a styled text field: stark white, thick black border, no rounded
     * corners.
     */
    public static JTextField styledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 3, false),
                new EmptyBorder(8, 12, 8, 12)));
        return field;
    }

    /** Create a brutalist combo box. */
    public static <T> JComboBox<T> styledComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(FONT_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_INPUT);
        combo.setBorder(new LineBorder(BORDER, 3, false));
        combo.setOpaque(true);

        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▼");
                arrow.setFont(new Font("Monospaced", Font.BOLD, 12));
                arrow.setBackground(ACCENT);
                arrow.setForeground(Color.BLACK);
                arrow.setBorder(new MatteBorder(0, 3, 0, 0, Color.BLACK));
                arrow.setFocusPainted(false);
                return arrow;
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? ACCENT : BG_INPUT);
                c.setForeground(TEXT_PRIMARY);
                c.setFont(FONT_INPUT);
                ((JLabel) c).setBorder(new EmptyBorder(4, 8, 4, 8));
                return c;
            }
        });
        return combo;
    }

    /**
     * Primary action button — simplified flat style.
     */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text.toUpperCase());
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.BLACK);
        btn.setBackground(ACCENT);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 1, false),
                new EmptyBorder(10, 24, 10, 24)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Danger button — simplified flat style. */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text.toUpperCase());
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(ACCENT_RED);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 1, false),
                new EmptyBorder(8, 16, 8, 16)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Delete this item");
        return btn;
    }

    /** Style a JTable with thick grid lines. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(BORDER);
        table.setRowHeight(44);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(2, 2)); // visible grid
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
        table.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_H);
        header.setForeground(Color.BLACK);
        header.setBackground(ACCENT);
        header.setBorder(new MatteBorder(0, 0, 3, 0, Color.BLACK));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                }
                c.setForeground(Color.BLACK);
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    /**
     * Wrap a table inside a brutalist scroll pane (thick border, blocky scrollbar).
     */
    public static JScrollPane styledScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new MatteBorder(3, 3, 3, 3, Color.BLACK));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);

        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.BLACK;
                this.trackColor = Color.WHITE;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createBlockyButton("▲");
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createBlockyButton("▼");
            }

            private JButton createBlockyButton(String text) {
                JButton button = new JButton(text);
                button.setBackground(ACCENT);
                button.setForeground(Color.BLACK);
                button.setFont(new Font("Monospaced", Font.BOLD, 10));
                button.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
                button.setFocusPainted(false);
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
                    return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(isDragging ? ACCENT : thumbColor);
                g2.fillRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2);
                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.setColor(Color.BLACK);
                g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width - 1, trackBounds.height - 1);
                g2.dispose();
            }
        });
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));

        return sp;
    }

    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    public static JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(Color.BLACK);
        return lbl;
    }
}
