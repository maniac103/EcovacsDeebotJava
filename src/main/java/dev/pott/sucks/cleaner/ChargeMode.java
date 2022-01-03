package dev.pott.sucks.cleaner;

public enum ChargeMode {
    RETURN("go"),
    RETURNING("Going"),
    CHARGING("SlotCharging"),
    IDLE("Idle");

    private final String id;

    ChargeMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
