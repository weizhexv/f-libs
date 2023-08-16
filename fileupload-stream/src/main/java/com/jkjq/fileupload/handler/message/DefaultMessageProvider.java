package com.jkjq.fileupload.handler.message;

public class DefaultMessageProvider implements MessageProvider {

    @Override
    public String provideMessage(String url, String message) {

        JsonMessageResult jsonMessage = new JsonMessageResult();
        jsonMessage.setSuccess(false);
        jsonMessage.setMessage("上传的文件超过上限");
        jsonMessage.setDetail(message);

        return JsonHelper.toJson(jsonMessage);
    }

    @Override
    public String messageContentType() {
        return "application/json; charset=utf-8";
    }
}
