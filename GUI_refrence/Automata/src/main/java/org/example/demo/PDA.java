package org.example.demo;

import java.util.Stack;

/**
 * Pushdown Automaton
 * Accepts strings of the form a^n b^n
 */
public class PDA extends Automaton {

    @Override
    public boolean accepts(String input) {
        Stack<Character> stack = new Stack<>();
        int i = 0;

        // Phase 1: Push all 'a's
        while (i < input.length() && input.charAt(i) == 'a') {
            stack.push('A');
            i++;
        }

        // Phase 2: Pop for 'b's
        while (i < input.length() && input.charAt(i) == 'b') {
            if (stack.isEmpty()) return false;
            stack.pop();
            i++;
        }

        return stack.isEmpty() && i == input.length();
    }
}
