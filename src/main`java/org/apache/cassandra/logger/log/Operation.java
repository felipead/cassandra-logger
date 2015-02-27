package org.apache.cassandra.logger.log;

public enum Operation {
    SAVE("save"),
    DELETE("delete");

    private String value;
    
    Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}