package com.basbase.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {
    private int statusCode;
    private ZonedDateTime timestamp;
    private String message;
    private String description;
}
