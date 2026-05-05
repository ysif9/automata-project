package pda;

/**
 * Validates strings against the language {@code L = { a^n b^n | n >= 0 }}.
 *
 * <p>The accepted form is zero or more {@code a} characters followed by the same number of
 * {@code b} characters. Any other symbol ordering or count mismatch is rejected.
 */
public final class AnBnPda {
    /**
     * Checks whether the given input belongs to {@code L = { a^n b^n | n >= 0 }}.
     *
     * <p>Behavior:
     * <ul>
     *   <li>Returns {@code true} for inputs like {@code ""}, {@code "ab"}, {@code "aabb"}.</li>
     *   <li>Returns {@code false} for null input, invalid symbols, mixed ordering such as
     *       {@code "abab"}, or unequal counts such as {@code "aab"}.</li>
     * </ul>
     *
     * @param input candidate string over the alphabet {@code {a, b}}
     * @return {@code true} if the input is in the language; otherwise {@code false}
     */
    public boolean accepts(String input) {
        if (input == null) {
            return false;
        }

        int balance = 0;
        int i = 0;

        // q0: read a's and push one marker per 'a'
        while (i < input.length() && input.charAt(i) == 'a') {
            balance++;
            i++;
        }

        // q1: read only b's and pop one marker per 'b'
        while (i < input.length()) {
            if (input.charAt(i) != 'b') {
                return false;
            }
            balance--;
            if (balance < 0) {
                return false;
            }
            i++;
        }
        return balance == 0;
    }
}
