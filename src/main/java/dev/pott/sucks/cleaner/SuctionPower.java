package dev.pott.sucks.cleaner;

public enum SuctionPower {
    NORMAL("standard"),
    HIGH("strong");

    private final String id;

    SuctionPower(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
