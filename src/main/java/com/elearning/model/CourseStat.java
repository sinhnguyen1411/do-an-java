package com.elearning.model;

public class CourseStat {
    private final String label;
    private final int value;

    public CourseStat(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
