package dev.pott.sucks.cleaner;

public enum Component {
    SIDE_BRUSH,
    MAIN_BRUSH,
    FILTER;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
