# Automata Project

This repository is for a group automata project covering both theoretical and practical work on **Turing Machines**,
**Pushdown Automata (PDA)**, and **Finite Automata**.

## Features

1. **DFA Validator**: A deterministic finite automaton that validates binary strings divisible by 3 ending with 0.
2. **PDA Validator ($a^n b^n$)**: A pushdown automaton validator that accepts exact strings in the form:
   - zero or more `a` characters,
   - followed by the same number of `b` characters,
   - with no other symbols and no reappearance of `a` after `b` starts.
   - Examples Accepted: `""`, `"ab"`, `"aabb"`, `"aaabbb"`
   - Examples Rejected: `"a"`, `"b"`, `"abb"`, `"aab"`, `"abab"`, `"abc"`
3. **CFG to PDA Converter**: A utility that converts an arbitrary Context-Free Grammar (CFG) into a Pushdown Automaton (PDA).

## Tech Stack

- Java 17
- JavaFX (for the UI)
- Maven
- JUnit 5

## Running the Application

To launch the graphical user interface, navigate to the project root and run:

```bash
mvn clean javafx:run
```
Alternatively, on Windows you can use the included wrapper:
```bash
.\mvnw.cmd clean javafx:run
```

## Running Tests

To run the unit tests:

```bash
.\mvnw.cmd test
```

## Documentation

For a detailed explanation of the logic behind the Context-Free Grammar to Pushdown Automaton converter, see [documentation.md](documentation.md).