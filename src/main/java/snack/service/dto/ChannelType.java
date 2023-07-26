package snack.service.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChannelType {
    USER,
    GROUP;

    @JsonValue
    public int getValue() {
        return ordinal();
    }
}
