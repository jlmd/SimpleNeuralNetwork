package com.jlmd.simpleneuralnetwork.app;

public enum Mode {
    TRAIN("train"), TEST("test"), BATCH("batch");

    private final String name;

    Mode(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    /**
     * @param str Value of the enum item
     * @return enum item or null if not found in enum
     */
    public static Mode getModeFromString(String str) {
        for (Mode m : values()) {
            if (m.getValue().equals(str)) {
                return m;
            }
        }
        return null;
    }
}
