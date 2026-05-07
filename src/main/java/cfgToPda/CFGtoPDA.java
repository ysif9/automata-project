package cfgToPda;

import java.util.*;

public class CFGtoPDA {

    // --- 1. Classes to represent the CFG ---

    public static class Production {
        public String lhs; // Left-hand side (Non-terminal)
        public List<String> rhs; // Right-hand side (Terminals and Non-terminals)

        public Production(String lhs, List<String> rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    public static class CFG {
        public Set<String> nonTerminals = new HashSet<>();
        public Set<String> terminals = new HashSet<>();
        public String startSymbol;
        public List<Production> productions = new ArrayList<>();

        public void addProduction(String lhs, String... rhsSymbols) {
            nonTerminals.add(lhs);
            List<String> rhs = new ArrayList<>();
            for (String symbol : rhsSymbols) {
                if (!symbol.equals("ε") && !symbol.trim().isEmpty()) { // "ε" means empty string
                    rhs.add(symbol.trim());
                }
            }
            productions.add(new Production(lhs, rhs));
        }
    }

    // --- 2. Classes to represent the PDA ---

    public static class Transition {
        public String state;
        public String inputSymbol;
        public String popSymbol;
        public String nextState;
        public List<String> pushSymbols;

        public Transition(String state, String inputSymbol, String popSymbol, String nextState, List<String> pushSymbols) {
            this.state = state;
            this.inputSymbol = inputSymbol;
            this.popSymbol = popSymbol;
            this.nextState = nextState;
            this.pushSymbols = pushSymbols;
        }

        @Override
        public String toString() {
            String pushStr = pushSymbols.isEmpty() ? "ε" : String.join(" ", pushSymbols);
            return String.format("δ(%s, %s, %s) -> (%s, [%s])",
                    state, inputSymbol, popSymbol, nextState, pushStr);
        }
    }

    public static class PDA {
        public String startState = "q";
        public List<Transition> transitions = new ArrayList<>();

        public void printPDA() {
            System.out.println("PDA Transitions:");
            for (Transition t : transitions) {
                System.out.println(t.toString());
            }
        }

        public String getPDATransitionsString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PDA Transitions:\n");
            for (Transition t : transitions) {
                sb.append(t.toString()).append("\n");
            }
            return sb.toString();
        }
    }

    // --- 3. The Conversion Logic ---

    public static PDA convert(CFG cfg) {
        PDA pda = new PDA();
        String state = pda.startState;

        // Rule 1: Expand Non-Terminals
        // For each rule A -> X Y Z, add transition: read ε, pop A, push X Y Z
        for (Production p : cfg.productions) {
            pda.transitions.add(new Transition(
                    state,
                    "ε",
                    p.lhs,
                    state,
                    p.rhs
            ));
        }

        // Rule 2: Match Terminals
        // For each terminal 'a', add transition: read 'a', pop 'a', push ε
        for (String terminal : cfg.terminals) {
            pda.transitions.add(new Transition(
                    state,
                    terminal,
                    terminal,
                    state,
                    Collections.emptyList() // Push nothing (ε)
            ));
        }

        return pda;
    }

    // --- 4. Main Method / Example Usage ---

    public static void main(String[] args) {
        CFG cfg = new CFG();

        // Define Terminals
        cfg.terminals.addAll(Arrays.asList("a", "b", "+", "*", "(", ")"));
        cfg.startSymbol = "E";

        // Define Productions for a simple math grammar: 
        // E -> E + T | T
        // T -> T * F | F
        // F -> ( E ) | a | b

        cfg.addProduction("E", "E", "+", "T");
        cfg.addProduction("E", "T");

        cfg.addProduction("T", "T", "*", "F");
        cfg.addProduction("T", "F");

        cfg.addProduction("F", "(", "E", ")");
        cfg.addProduction("F", "a");
        cfg.addProduction("F", "b");

        System.out.println("Converting CFG to PDA...\n");
        PDA pda = convert(cfg);

        pda.printPDA();
    }
}