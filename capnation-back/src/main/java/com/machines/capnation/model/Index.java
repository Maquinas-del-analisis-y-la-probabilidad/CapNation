package com.machines.capnation.model;

public  class Index {
    private Long key;
    private int direction;

    private static final String separator = ",";

    public Index(Long key, int direction) {
        this.key = key;
        this.direction = direction;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String toLine() {
        return String.format("%d%s%d", key, separator, direction);
    }

    public static Index fromLine(String line) {
        var arguments = line.split(separator);
        if (arguments.length == 2) {
            return new Index(Long.parseLong(arguments[0]), Integer.parseInt(arguments[1]));
        }
        else throw new RuntimeException("Invalid string to parse");
    }
}