# Automata Project

This repository is for a group automata project covering both theoretical and practical work on **Turing Machines**,
**Pushdown Automata (PDA)**, and **Finite Automata**.

## PDA Logic (Task 3)

The validator accepts exact strings in the form:

- zero or more `a` characters,
- followed by the same number of `b` characters,
- with no other symbols and no reappearance of `a` after `b` starts.

Examples:

- Accepted: `""`, `"ab"`, `"aabb"`, `"aaabbb"`
- Rejected: `"a"`, `"b"`, `"abb"`, `"aab"`, `"abab"`, `"abc"`

## Tech Stack

- Java 17
- Maven
- JUnit 5

## Run Tests

From the project root:

```bash
mvn test
```