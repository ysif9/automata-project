# Context-Free Grammar (CFG) to Pushdown Automaton (PDA) Conversion

This document explains the logic used to convert an arbitrary Context-Free Grammar into an equivalent Pushdown Automaton within the Automata Project.

## Overview

A Context-Free Grammar $G = (V, \Sigma, R, S)$ can be algorithmically converted into a Pushdown Automaton $M$ that accepts the same language by empty stack. We use a standardized "top-down" parsing approach to simulate the leftmost derivations of the CFG.

The resulting PDA has a single state $q$ and utilizes the stack to keep track of the remaining symbols to be generated or matched.

## Input Format

When using the application GUI, you provide your grammar rules in the format:
```text
LHS -> RHS1 | RHS2
```

- **LHS**: A single non-terminal symbol.
- **RHS**: A space-separated list of symbols (terminals and non-terminals).
- Multiple productions for the same LHS can be separated by the pipe character `|`.
- To denote an empty string (epsilon), use the symbol `ε`.
- The first symbol on the LHS of the first rule provided is automatically assumed to be the start symbol $S$.

### Example Input
```text
E -> E + T | T
T -> T * F | F
F -> ( E ) | a | b
```

## Conversion Logic

The implementation relies on two core rules to generate the PDA transitions:

### Rule 1: Expand Non-Terminals
For every production rule in the grammar of the form $A \rightarrow X_1 X_2 \dots X_n$, we add a transition to the PDA that:
1. Reads no input (`ε`).
2. Pops the non-terminal $A$ from the top of the stack.
3. Pushes the string $X_1 X_2 \dots X_n$ onto the stack.

**Formal Transition:**
$\delta(q, \epsilon, A) \rightarrow (q, [X_1, X_2, \dots, X_n])$

*If the production is $A \rightarrow \epsilon$, the PDA pops $A$ and pushes nothing.*

### Rule 2: Match Terminals
For every terminal symbol $a \in \Sigma$ found in the grammar, we add a transition that:
1. Reads the terminal $a$ from the input string.
2. Pops the terminal $a$ from the top of the stack.
3. Pushes nothing (`ε`).

**Formal Transition:**
$\delta(q, a, a) \rightarrow (q, [\epsilon])$

## Example Execution

Given the simple grammar:
```text
S -> a S b
S -> ε
```
The algorithm identifies the Non-Terminals `{S}` and Terminals `{a, b}`.

Applying **Rule 1**, we get the expansion transitions:
- $\delta(q, \epsilon, S) \rightarrow (q, [a, S, b])$
- $\delta(q, \epsilon, S) \rightarrow (q, [\epsilon])$

Applying **Rule 2**, we get the matching transitions for each terminal:
- $\delta(q, a, a) \rightarrow (q, [\epsilon])$
- $\delta(q, b, b) \rightarrow (q, [\epsilon])$

This set of four transitions is what the application will output, effectively defining the PDA that accepts strings of the form $a^n b^n$.
