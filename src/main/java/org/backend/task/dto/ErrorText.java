package org.backend.task.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorText {
    final String error;

    @JsonCreator
    public ErrorText(@JsonProperty("error") String error) {
        this.error = error;
    }
}
