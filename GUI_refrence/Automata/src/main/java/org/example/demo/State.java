package org.example.demo;
public class State {
    private final String name;
    private boolean isAccepting;


    public State(String name, boolean isAccepting) {
        this.name = name;
        this.isAccepting = isAccepting;
    }

    public State(String name) {
        this(name, false);
    }

    public String getName() {
        return name;
    }

    public boolean isAccepting() {
        return isAccepting;
    }

    public void setAccepting(boolean accepting) {
        isAccepting = accepting;
    }

    @Override

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State state = (State) obj;
        return name.equals(state.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
