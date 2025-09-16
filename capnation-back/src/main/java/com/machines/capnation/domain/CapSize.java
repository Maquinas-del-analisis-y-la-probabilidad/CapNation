package com.machines.capnation.domain;

public enum CapSize {
    SMALL("S"),
    MEDIUM("M"),
    LARGE("L"),
    EXTRA_LARGE("XL"),
    ONE_SIZE_FITS_ALL("One Size Fits All");

    private final String label;

    CapSize(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

