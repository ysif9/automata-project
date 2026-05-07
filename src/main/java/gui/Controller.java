package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import cfgToPda.CFGtoPDA;
import java.util.*;

public class Controller {

    @FXML
    private TextField dfaInputField;

    @FXML
    private TextField pdaInputField;

    @FXML
    private Label dfaResultLabel;

    @FXML
    private Label pdaResultLabel;

    @FXML
    private TextArea cfgInputArea;

    @FXML
    private TextArea pdaTransitionsArea;

    @FXML
    public void handleDFA() {
        String input = dfaInputField.getText().trim();
        if (input.isEmpty()) {
            dfaResultLabel.setText("✗ Please enter input");
            dfaResultLabel.setStyle("-fx-text-fill: #DD0000; -fx-font-weight: bold;");
            return;
        }
        DFA dfa = new DFA();
        boolean accepted = dfa.accepts(input);
        dfaResultLabel.setText(accepted ? "✓ ACCEPTED" : "✗ REJECTED");
        dfaResultLabel.setStyle("-fx-text-fill: " + (accepted ? "#00AA00" : "#DD0000") + "; -fx-font-weight: bold;");
    }

    @FXML
    public void handlePDA() {
        String input = pdaInputField.getText().trim();
        if (input.isEmpty()) {
            pdaResultLabel.setText("✗ Please enter input");
            pdaResultLabel.setStyle("-fx-text-fill: #DD0000; -fx-font-weight: bold;");
            return;
        }
        PDA pda = new PDA();
        boolean accepted = pda.accepts(input);
        pdaResultLabel.setText(accepted ? "✓ ACCEPTED a^n b^n" : "✗ REJECTED");
        pdaResultLabel.setStyle("-fx-text-fill: " + (accepted ? "#00AA00" : "#DD0000") + "; -fx-font-weight: bold;");
    }

    @FXML
    public void handleCFGToPDA() {
        String input = cfgInputArea.getText();
        if (input == null || input.trim().isEmpty()) {
            pdaTransitionsArea.setText("Please enter CFG rules.");
            return;
        }

        try {
            CFGtoPDA.CFG cfg = new CFGtoPDA.CFG();
            String[] lines = input.split("\\r?\\n");
            boolean first = true;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                // Expecting format: LHS -> RHS1 RHS2 | RHS3
                String[] parts = line.split("->");
                if (parts.length != 2) {
                    pdaTransitionsArea.setText("Error on line: " + line + "\nFormat should be: LHS -> RHS");
                    return;
                }
                
                String lhs = parts[0].trim();
                if (first) {
                    cfg.startSymbol = lhs;
                    first = false;
                }
                
                String rhsPart = parts[1].trim();
                String[] alternatives = rhsPart.split("\\|");
                for (String alt : alternatives) {
                    String[] symbols = alt.trim().split("\\s+");
                    cfg.addProduction(lhs, symbols);
                }
            }

            // Infer terminals (any symbol in RHS that is not in nonTerminals)
            Set<String> rhsSymbols = new HashSet<>();
            for (CFGtoPDA.Production p : cfg.productions) {
                rhsSymbols.addAll(p.rhs);
            }
            for (String sym : rhsSymbols) {
                if (!cfg.nonTerminals.contains(sym) && !sym.equals("ε")) {
                    cfg.terminals.add(sym);
                }
            }

            CFGtoPDA.PDA pda = CFGtoPDA.convert(cfg);
            pdaTransitionsArea.setText(pda.getPDATransitionsString());

        } catch (Exception e) {
            pdaTransitionsArea.setText("Error parsing CFG: " + e.getMessage());
        }
    }

    @FXML
    public void handleClearDFA() {
        dfaInputField.clear();
        dfaResultLabel.setText("Status: Waiting for input");
        dfaResultLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #666; -fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-min-height: 40; -fx-wrap-text: true;");
    }

    @FXML
    public void handleClearPDA() {
        pdaInputField.clear();
        pdaResultLabel.setText("Status: Waiting for input");
        pdaResultLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #666; -fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-min-height: 40; -fx-wrap-text: true;");
    }
}

