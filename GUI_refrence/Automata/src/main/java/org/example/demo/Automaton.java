package org.example.demo;

import java.util.HashSet;
import java.util.Set;

public abstract class Automaton {
    protected Set<State> states;
    protected State startState;
    protected Set<State> acceptingStates;
    protected String alphabet;

    public Automaton() {
        this.states = new HashSet<>();
        this.acceptingStates = new HashSet<>();
    }

    public void addState(State state) {
        states.add(state);
    }

    public void setStartState(State state) {
        this.startState = state;
        states.add(state);
    }

    public void addAcceptingState(State state) {
        acceptingStates.add(state);
        states.add(state);
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    public Set<State> getStates() {
        return new HashSet<>(states);
    }

    public State getStartState() {
        return startState;
    }

    public Set<State> getAcceptingStates() {
        return new HashSet<>(acceptingStates);
    }

    public String getAlphabet() {
        return alphabet;
    }

    /**
     * Abstract method to check if input is accepted
     */
    public abstract boolean accepts(String input);
}

