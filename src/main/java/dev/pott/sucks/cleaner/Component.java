package dev.pott.sucks.cleaner;

public enum Component {
    BRUSH("Brush", "brush"),
    SIDE_BRUSH("SideBrush", "sideBrush"),
    DUST_CASE_HEAP("DustCaseHeap", "heap");

    public final String xmlValue;
    public final String jsonValue;

    private Component(String xmlValue, String jsonValue) {
        this.xmlValue = xmlValue;
        this.jsonValue = jsonValue;
    }
}
