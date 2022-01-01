package dev.pott.sucks.cleaner;

public enum CleanMode {
    AUTO("auto"),
    EDGE("border"),
    SPOT("spot"),
    SINGLE_ROOM("singleroom"),
    STOP("stop"),
    RETURNING("going");

    private final String id;

    CleanMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
