package cfgToPda;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class CFGtoPDATest {

    @Test
    public void testConvertSimpleCFG() {
        CFGtoPDA.CFG cfg = new CFGtoPDA.CFG();
        cfg.terminals.addAll(Arrays.asList("a", "b"));
        cfg.startSymbol = "S";
        
        // S -> a S b
        cfg.addProduction("S", "a", "S", "b");
        // S -> ε
        cfg.addProduction("S", "ε");

        CFGtoPDA.PDA pda = CFGtoPDA.convert(cfg);

        assertNotNull(pda);
        assertEquals("q", pda.startState);
        
        // Transitions expected:
        // 1. S -> a S b : δ(q, ε, S) -> (q, [a, S, b])
        // 2. S -> ε : δ(q, ε, S) -> (q, [ε])
        // 3. terminal 'a' : δ(q, a, a) -> (q, [ε])
        // 4. terminal 'b' : δ(q, b, b) -> (q, [ε])
        
        assertEquals(4, pda.transitions.size());
        
        boolean hasExpansion1 = false;
        boolean hasExpansion2 = false;
        boolean hasMatchA = false;
        boolean hasMatchB = false;

        for (CFGtoPDA.Transition t : pda.transitions) {
            if (t.inputSymbol.equals("ε") && t.popSymbol.equals("S") && t.pushSymbols.equals(Arrays.asList("a", "S", "b"))) {
                hasExpansion1 = true;
            }
            if (t.inputSymbol.equals("ε") && t.popSymbol.equals("S") && t.pushSymbols.isEmpty()) {
                hasExpansion2 = true;
            }
            if (t.inputSymbol.equals("a") && t.popSymbol.equals("a") && t.pushSymbols.isEmpty()) {
                hasMatchA = true;
            }
            if (t.inputSymbol.equals("b") && t.popSymbol.equals("b") && t.pushSymbols.isEmpty()) {
                hasMatchB = true;
            }
        }

        assertTrue(hasExpansion1, "Missing expansion for S -> a S b");
        assertTrue(hasExpansion2, "Missing expansion for S -> ε");
        assertTrue(hasMatchA, "Missing match transition for 'a'");
        assertTrue(hasMatchB, "Missing match transition for 'b'");
    }

    @Test
    public void testGetPDATransitionsString() {
        CFGtoPDA.CFG cfg = new CFGtoPDA.CFG();
        cfg.terminals.addAll(Arrays.asList("x"));
        cfg.startSymbol = "A";
        cfg.addProduction("A", "x");

        CFGtoPDA.PDA pda = CFGtoPDA.convert(cfg);
        String output = pda.getPDATransitionsString();

        assertTrue(output.contains("PDA Transitions:"));
        assertTrue(output.contains("δ(q, ε, A) -> (q, [x])"));
        assertTrue(output.contains("δ(q, x, x) -> (q, [ε])"));
    }
}
