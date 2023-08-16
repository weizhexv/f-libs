package com.jkjq.fileupload.handler.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JsonMessageResult {
    private boolean success;
    private String message;
    private String detail;
}
