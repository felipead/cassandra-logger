package org.apache.cassandra.logger;

public enum OperationType {
    SAVE("save"),
    DELETE("delete");

    private String value;
    
    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}