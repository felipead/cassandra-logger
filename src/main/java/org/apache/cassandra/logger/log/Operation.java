package org.apache.cassandra.logger.log;

public enum Operation {
    SAVE("save"),
    DELETE("del");
    
    private final String value;

    Operation(String value) {
        this.value = value;        
    }

    public String getValue() {
        return value;
    }
}