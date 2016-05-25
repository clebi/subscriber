package org.clebi.subscribers.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ErrorResponse {

    private String status;
    private String message;

}
