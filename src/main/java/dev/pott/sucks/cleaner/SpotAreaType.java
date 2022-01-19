package dev.pott.sucks.cleaner;

public enum SpotAreaType {
    LIVING_ROOM(1),
    DINING_ROOM(2),
    BEDROOM(3),
    OFFICE(4),
    KITCHEN(5),
    BATHROOM(6),
    LAUNDRY_ROOM(7),
    LOUNGE(8),
    STORAGE_ROOM(9),
    CHILDS_ROOM(10),
    SUN_ROOM(11),
    CORRIDOR(12),
    BALCONY(13),
    GYM(14);

    private final int type;

    private SpotAreaType(int type) {
        this.type = type;
    }

    public SpotAreaType fromApiResponse(String response) {
        int id = Integer.valueOf(response);
        for (SpotAreaType t : values()) {
            if (t.type == id) {
                return t;
            }
        }
        return null;
    }
}
