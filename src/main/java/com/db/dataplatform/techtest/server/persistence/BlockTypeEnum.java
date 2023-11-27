package com.db.dataplatform.techtest.server.persistence;

import java.util.Arrays;

public enum BlockTypeEnum {
    BLOCKTYPEA("blocktypea"),
    BLOCKTYPEB("blocktypeb");

    private final String type;

    BlockTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static BlockTypeEnum fromValue(String value) {
        for (BlockTypeEnum blockTypeEnum : values()) {
            if (blockTypeEnum.type.equalsIgnoreCase(value)) {
                return blockTypeEnum;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
