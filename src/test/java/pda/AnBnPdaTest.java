package pda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnBnPdaTest {

    private final AnBnPda pda = new AnBnPda();

    @Test
    void acceptsValidExamples() {
        assertTrue(pda.accepts(""));
        assertTrue(pda.accepts("ab"));
        assertTrue(pda.accepts("aabb"));
        assertTrue(pda.accepts("aaabbb"));
        assertTrue(pda.accepts("aaaaabbbbb"));
    }

    @Test
    void rejectsInvalidExamples() {
        assertFalse(pda.accepts("a"));
        assertFalse(pda.accepts("b"));
        assertFalse(pda.accepts("ba"));
        assertFalse(pda.accepts("abb"));
        assertFalse(pda.accepts("aab"));
        assertFalse(pda.accepts("aaabb"));
        assertFalse(pda.accepts("aabbb"));
        assertFalse(pda.accepts("abab"));
        assertFalse(pda.accepts("abba"));
        assertFalse(pda.accepts("abc"));
    }

    @Test
    void rejectsNullInput() {
        assertFalse(pda.accepts(null));
    }

    @Test
    void acceptsExactlyWhenCountsMatchInCanonicalForm() {
        for (int aCount = 0; aCount <= 8; aCount++) {
            for (int bCount = 0; bCount <= 8; bCount++) {
                String input = "a".repeat(aCount) + "b".repeat(bCount);
                boolean expected = aCount == bCount;
                if (expected) {
                    assertTrue(pda.accepts(input), () -> "Expected accept for: " + input);
                } else {
                    assertFalse(pda.accepts(input), () -> "Expected reject for: " + input);
                }
            }
        }
    }

    @Test
    void rejectsAnyStringWhereBStartsThenAReappears() {
        assertFalse(pda.accepts("aba"));
        assertFalse(pda.accepts("aabbaa"));
        assertFalse(pda.accepts("abbab"));
    }
}
