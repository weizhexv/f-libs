package com.jkjq.fileupload.handler.message;

public interface MessageProvider {
    String provideMessage(String url, String message);
    String messageContentType();
}
