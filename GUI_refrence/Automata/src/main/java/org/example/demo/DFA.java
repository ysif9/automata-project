package org.example.demo;

/**
 * Deterministic Finite Automaton
 * Accepts binary strings where the number represented is divisible by 3 and ends with 0
 */
public class DFA extends Automaton {

    @Override
    public boolean accepts(String input) {
        if (!input.matches("[01]*")) {
            return false;
        }

        int state = 0; // remainder mod 3

        for (char c : input.toCharArray()) {
            if (c == '1') {
                state = (state + 1) % 3;
            } else if (c != '0') {
                return false;
            }
        }

        return state == 0 && input.endsWith("0");
    }
}
