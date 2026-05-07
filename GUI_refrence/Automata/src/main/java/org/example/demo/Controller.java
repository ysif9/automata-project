package org.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

@FXML
    private TextField inputField;

    @FXML
    private Label dfaResultLabel;

    @FXML
    private Label pdaResultLabel;






    @FXML
    public void handleDFA() {
        String input = inputField.getText().trim();
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
        String input = inputField.getText().trim();
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

    public void handleClear() {
        inputField.clear();
        dfaResultLabel.setText("Waiting...");
        dfaResultLabel.setStyle("-fx-font-weight: bold;");
        pdaResultLabel.setText("Waiting...");
        pdaResultLabel.setStyle("-fx-font-weight: bold;");
    }
}

