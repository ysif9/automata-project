package pda;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class PdaGUI extends JFrame {

    private final AnBnPda pda = new AnBnPda();

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(0xF8F8F7);
    private static final Color CARD        = Color.WHITE;
    private static final Color BORDER_CLR  = new Color(0xDDDCD7);
    private static final Color ACCENT_BLUE = new Color(0x378ADD);
    private static final Color ACCEPT_BG   = new Color(0xEAF3DE);
    private static final Color ACCEPT_FG   = new Color(0x27500A);
    private static final Color REJECT_BG   = new Color(0xFCEBEB);
    private static final Color REJECT_FG   = new Color(0x791F1F);
    private static final Color MUTED       = new Color(0x888780);
    private static final Color TEXT        = new Color(0x1A1A1A);
    private static final Color STACK_BG    = new Color(0xE6F1FB);
    private static final Color STACK_FG    = new Color(0x0C447C);

    // ── Widgets ───────────────────────────────────────────────────────────────
    private JTextField inputField;
    private JPanel     tapePanel;
    private JPanel     stackPanel;
    private JLabel     stateLabel;
    private JLabel     stateDesc;
    private JPanel     resultBar;
    private JLabel     resultIcon;
    private JLabel     resultText;
    private JTextArea  logArea;

    // ── Step state ────────────────────────────────────────────────────────────
    private String          simInput;
    private Stack<Character> simStack;
    private int             simIndex;
    private String          simPhase;   // "q0" | "q1" | "done"
    private boolean         simDone;
    private boolean         simAccepted;

    // ── Constructor ───────────────────────────────────────────────────────────
    public PdaGUI() {
        super("PDA Simulator — L = { aⁿbⁿ | n ≥ 0 }");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 640);
        setMinimumSize(new Dimension(580, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        resetSim();
    }

    // ── Header (title + input row) ────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 24, 12, 24));

        JLabel title = new JLabel("PDA Simulator");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Language:  L = { aⁿbⁿ | n ≥ 0 }");
        sub.setFont(new Font("Monospaced", Font.PLAIN, 13));
        sub.setForeground(MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(2, 0, 14, 0));

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(BG);
        inputRow.setAlignmentX(LEFT_ALIGNMENT);
        inputRow.setBorder(new EmptyBorder(10, 0, 0, 0));

        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 15));
        inputField.setPreferredSize(new Dimension(0, 36));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        inputField.addActionListener(e -> runFull());
        inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { onInput(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { onInput(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { onInput(); }
        });

        JButton stepBtn = styledBtn("Step →", false);
        stepBtn.addActionListener(e -> doStep());

        JButton runBtn = styledBtn("Run", true);
        runBtn.addActionListener(e -> runFull());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setBackground(BG);
        btns.add(stepBtn);
        btns.add(runBtn);

        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(btns,       BorderLayout.EAST);

        panel.add(title);
        panel.add(sub);
        panel.add(inputRow);
        return panel;
    }

    // ── Centre (tape + panels) ─────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Tape
        JPanel tapeWrap = card();
        tapeWrap.setLayout(new BoxLayout(tapeWrap, BoxLayout.Y_AXIS));
        tapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        tapePanel.setBackground(CARD);
        tapeWrap.add(sectionLabel("Input tape"));
        tapeWrap.add(tapePanel);

        // Stack + State side by side
        JPanel mid = new JPanel(new GridLayout(1, 2, 12, 0));
        mid.setBackground(BG);
        mid.setAlignmentX(LEFT_ALIGNMENT);
        mid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel stackCard = card();
        stackCard.setLayout(new BoxLayout(stackCard, BoxLayout.Y_AXIS));
        stackPanel = new JPanel();
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.X_AXIS));
        stackPanel.setBackground(CARD);
        stackPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
        stackCard.add(sectionLabel("Stack  (bottom → top)"));
        stackCard.add(stackPanel);

        JPanel stateCard = card();
        stateCard.setLayout(new BoxLayout(stateCard, BoxLayout.Y_AXIS));
        stateLabel = new JLabel("—");
        stateLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        stateLabel.setForeground(TEXT);
        stateDesc = new JLabel(" ");
        stateDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stateDesc.setForeground(MUTED);
        stateDesc.setBorder(new EmptyBorder(4, 0, 0, 0));
        stateCard.add(sectionLabel("Current state"));
        stateCard.add(stateLabel);
        stateCard.add(stateDesc);

        mid.add(stackCard);
        mid.add(stateCard);

        // Result bar
        resultBar  = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        resultBar.setBackground(new Color(0xF1EFE8));
        resultBar.setBorder(new LineBorder(BORDER_CLR, 1, true));
        resultBar.setAlignmentX(LEFT_ALIGNMENT);
        resultBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        resultIcon = new JLabel("○");
        resultIcon.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultIcon.setForeground(MUTED);
        resultText = new JLabel("Enter a string and press Run or Step");
        resultText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultText.setForeground(MUTED);
        resultBar.add(resultIcon);
        resultBar.add(resultText);

        center.add(tapeWrap);
        center.add(Box.createVerticalStrut(12));
        center.add(mid);
        center.add(Box.createVerticalStrut(12));
        center.add(resultBar);
        center.add(Box.createVerticalStrut(12));
        return center;
    }

    // ── Footer (log) ──────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = card();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_CLR),
            new EmptyBorder(10, 24, 16, 24)
        ));
        footer.add(sectionLabel("Step log"));
        logArea = new JTextArea(6, 0);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setForeground(MUTED);
        logArea.setBackground(CARD);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setText("— no steps yet —");
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(new LineBorder(BORDER_CLR, 1, true));
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        footer.add(scroll);
        return footer;
    }

    // ── Simulation ────────────────────────────────────────────────────────────
    private void initSim(String input) {
        simInput    = input;
        simStack    = new Stack<>();
        simIndex    = 0;
        simPhase    = "q0";
        simDone     = false;
        simAccepted = false;
    }

    private void resetSim() {
        simInput = null;
        refreshTape("", -1);
        refreshStack(new Stack<>(), false);
        setStateDisplay(null, null);
        setResult("idle", "○", "Enter a string and press Run or Step");
        logArea.setText("— no steps yet —");
        logArea.setForeground(MUTED);
    }

    private void onInput() {
        resetSim();
        refreshTape(inputField.getText(), -1);
    }

    private void doStep() {
        String input = inputField.getText();
        if (simInput == null) {
            initSim(input);
            appendLog("→ start: input = \"" + (input.isEmpty() ? "ε" : input) + "\", stack = []");
            refreshTape(input, 0);
            refreshStack(simStack, false);
            setStateDisplay("q₀ — reading a's", "Pushing A for each a");
            setResult("idle", "○", "Stepping…");
            return;
        }
        if (simDone) return;

        if (simPhase.equals("q0")) {
            if (simIndex >= simInput.length() || simInput.charAt(simIndex) != 'a') {
                simPhase = "q1";
                doStep();
                return;
            }
            simStack.push('A');
            simIndex++;
            appendLog("q₀: read 'a' → push A  [pos " + (simIndex - 1) + "]  depth: " + simStack.size());
            refreshTape(simInput, simIndex);
            refreshStack(simStack, true);
            setStateDisplay("q₀ — reading a's", "Pushing A for each a");

        } else {
            if (simIndex >= simInput.length()) {
                finish(simStack.isEmpty());
                return;
            }
            char ch = simInput.charAt(simIndex);
            if (ch != 'b') {
                simDone = true;
                setStateDisplay("qᵣ — reject", "Invalid symbol encountered");
                setResult("reject", "✗", "\"" + simInput + "\"  rejected — invalid symbol '" + ch + "' at pos " + simIndex);
                appendLog("q₁: read '" + ch + "' — not b, invalid → REJECT");
                return;
            }
            if (simStack.isEmpty()) {
                simDone = true;
                setStateDisplay("qᵣ — reject", "More b's than a's");
                setResult("reject", "✗", "\"" + simInput + "\"  rejected — more b's than a's");
                appendLog("q₁: read 'b' but stack empty → REJECT");
                return;
            }
            simStack.pop();
            simIndex++;
            appendLog("q₁: read 'b' → pop A  [pos " + (simIndex - 1) + "]  depth: " + simStack.size());
            refreshTape(simInput, simIndex);
            refreshStack(simStack, false);
            setStateDisplay("q₁ — reading b's", "Popping A for each b");
        }
    }

    private void runFull() {
        String input = inputField.getText();
        initSim(input);
        logArea.setText("");
        appendLog("→ run: input = \"" + (input.isEmpty() ? "ε" : input) + "\"");
        int guard = 0;
        while (!simDone && guard++ < 500) runOneStep();
        refreshTape(simInput, simIndex);
        refreshStack(simStack, false);
    }

    private void runOneStep() {
        if (simPhase.equals("q0")) {
            if (simIndex >= simInput.length() || simInput.charAt(simIndex) != 'a') {
                simPhase = "q1"; return;
            }
            simStack.push('A'); simIndex++;
            appendLog("q₀: read 'a' → push A");
            setStateDisplay("q₀ — reading a's", "Pushing A for each a");
        } else {
            if (simIndex >= simInput.length()) { finish(simStack.isEmpty()); return; }
            char ch = simInput.charAt(simIndex);
            if (ch != 'b') {
                simDone = true;
                setStateDisplay("qᵣ — reject", "Invalid symbol");
                setResult("reject", "✗", "\"" + simInput + "\"  rejected — invalid symbol '" + ch + "'");
                appendLog("q₁: invalid symbol '" + ch + "' → REJECT");
                return;
            }
            if (simStack.isEmpty()) {
                simDone = true;
                setStateDisplay("qᵣ — reject", "More b's than a's");
                setResult("reject", "✗", "\"" + simInput + "\"  rejected — more b's than a's");
                appendLog("q₁: stack empty on 'b' → REJECT");
                return;
            }
            simStack.pop(); simIndex++;
            appendLog("q₁: read 'b' → pop A");
            setStateDisplay("q₁ — reading b's", "Popping A for each b");
        }
    }

    private void finish(boolean accepted) {
        simDone     = true;
        simAccepted = accepted;
        if (accepted) {
            int n = simInput.length() / 2;
            setStateDisplay("qₐ — accept", "Stack empty, all input consumed");
            setResult("accept", "✓", "\"" + (simInput.isEmpty() ? "ε" : simInput) + "\"  accepted — aⁿbⁿ  n = " + n);
            appendLog("→ stack empty → ACCEPT");
        } else {
            setStateDisplay("qᵣ — reject", "Stack not empty — unmatched a's");
            setResult("reject", "✗", "\"" + simInput + "\"  rejected — " + simStack.size() + " unmatched a's");
            appendLog("→ stack not empty → REJECT");
        }
    }

    // ── Rendering helpers ─────────────────────────────────────────────────────
    private void refreshTape(String input, int head) {
        tapePanel.removeAll();
        if (input == null || input.isEmpty()) {
            JLabel eps = new JLabel("ε");
            eps.setFont(new Font("Monospaced", Font.ITALIC, 15));
            eps.setForeground(MUTED);
            eps.setBorder(new EmptyBorder(4, 6, 4, 6));
            tapePanel.add(eps);
        } else {
            for (int i = 0; i < input.length(); i++) {
                JLabel cell = new JLabel(String.valueOf(input.charAt(i)), SwingConstants.CENTER);
                cell.setFont(new Font("Monospaced", Font.BOLD, 14));
                cell.setPreferredSize(new Dimension(32, 32));
                cell.setOpaque(true);
                if (i == head) {
                    cell.setBackground(STACK_BG);
                    cell.setForeground(STACK_FG);
                    cell.setBorder(new LineBorder(ACCENT_BLUE, 2, true));
                } else if (head >= 0 && i < head) {
                    cell.setBackground(new Color(0xF1EFE8));
                    cell.setForeground(MUTED);
                    cell.setBorder(new LineBorder(BORDER_CLR, 1, true));
                } else {
                    cell.setBackground(CARD);
                    cell.setForeground(TEXT);
                    cell.setBorder(new LineBorder(BORDER_CLR, 1, true));
                }
                tapePanel.add(cell);
            }
        }
        tapePanel.revalidate();
        tapePanel.repaint();
    }

    private void refreshStack(Stack<Character> stack, boolean highlightTop) {
        stackPanel.removeAll();
        if (stack == null || stack.isEmpty()) {
            JLabel empty = new JLabel("empty");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 12));
            empty.setForeground(MUTED);
            stackPanel.add(empty);
        } else {
            for (int i = 0; i < stack.size(); i++) {
                JLabel cell = new JLabel(String.valueOf(stack.get(i)), SwingConstants.CENTER);
                cell.setFont(new Font("Monospaced", Font.BOLD, 13));
                cell.setPreferredSize(new Dimension(32, 28));
                cell.setOpaque(true);
                boolean isTop = (i == stack.size() - 1);
                if (isTop && highlightTop) {
                    cell.setBackground(new Color(0x9FE1CB));
                    cell.setForeground(new Color(0x085041));
                } else {
                    cell.setBackground(STACK_BG);
                    cell.setForeground(STACK_FG);
                }
                cell.setBorder(new LineBorder(new Color(0xB5D4F4), 1, true));
                cell.setToolTipText("stack[" + i + "]");
                stackPanel.add(cell);
                if (i < stack.size() - 1) {
                    JLabel arrow = new JLabel(" → ");
                    arrow.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    arrow.setForeground(MUTED);
                    stackPanel.add(arrow);
                }
            }
        }
        stackPanel.revalidate();
        stackPanel.repaint();
    }

    private void setStateDisplay(String state, String desc) {
        if (state == null) {
            stateLabel.setText("—");
            stateLabel.setForeground(TEXT);
            stateDesc.setText(" ");
        } else {
            stateLabel.setText(state);
            if (state.startsWith("qₐ"))      stateLabel.setForeground(ACCEPT_FG);
            else if (state.startsWith("qᵣ")) stateLabel.setForeground(REJECT_FG);
            else if (state.startsWith("q₀")) stateLabel.setForeground(STACK_FG);
            else                              stateLabel.setForeground(new Color(0x27500A));
            stateDesc.setText(desc != null ? desc : " ");
        }
    }

    private void setResult(String type, String icon, String msg) {
        resultIcon.setText(icon);
        resultText.setText(msg);
        switch (type) {
            case "accept":
                resultBar.setBackground(ACCEPT_BG);
                resultIcon.setForeground(ACCEPT_FG);
                resultText.setForeground(ACCEPT_FG);
                break;
            case "reject":
                resultBar.setBackground(REJECT_BG);
                resultIcon.setForeground(REJECT_FG);
                resultText.setForeground(REJECT_FG);
                break;
            default:
                resultBar.setBackground(new Color(0xF1EFE8));
                resultIcon.setForeground(MUTED);
                resultText.setForeground(MUTED);
        }
        resultBar.revalidate();
        resultBar.repaint();
    }

    private void appendLog(String msg) {
        if (logArea.getText().equals("— no steps yet —")) logArea.setText("");
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        logArea.setForeground(TEXT);
    }

    // ── UI factory helpers ────────────────────────────────────────────────────
    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(MUTED);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JButton styledBtn(String text, boolean primary) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(80, 34));
        b.setBackground(CARD);
        b.setForeground(TEXT);
        b.setBorder(new LineBorder(BORDER_CLR, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0xF1EFE8)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(CARD); }
        });
        return b;
    }

    private JButton presetBtn(String label, boolean valid) {
        JButton b = new JButton(label.isEmpty() ? "ε" : label);
        b.setFont(new Font("Monospaced", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(60, 26));
        Color fg = valid ? ACCEPT_FG : REJECT_FG;
        Color bg = valid ? ACCEPT_BG : REJECT_BG;
        b.setBackground(bg);
        b.setForeground(fg);
        b.setBorder(new LineBorder(valid ? new Color(0xC0DD97) : new Color(0xF7C1C1), 1, true));
        b.addActionListener(e -> {
            inputField.setText(label);
            resetSim();
            refreshTape(label, -1);
        });
        return b;
    }

    // ── main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new PdaGUI().setVisible(true);
        });
    }
}