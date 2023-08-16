package com.jkjq.fileupload.handler;

import com.jkjq.fileupload.handler.message.DefaultMessageProvider;
import com.jkjq.fileupload.handler.message.MessageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ExceedMaxLimitHandlerExceptionResolver implements HandlerExceptionResolver {
    private static final MessageProvider DEFAULT_MESSAGE_PROVIDER = new DefaultMessageProvider();

    private final MessageProvider messageProvider;

    public ExceedMaxLimitHandlerExceptionResolver(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    public ExceedMaxLimitHandlerExceptionResolver() {
        this.messageProvider = null;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(ex instanceof MaxUploadSizeExceededException)) {
            return null;
        }
        MaxUploadSizeExceededException me = (MaxUploadSizeExceededException) ex;
        MessageProvider provider = getCurrentProvider();
        String msg = provider.provideMessage(request.getRequestURI(), me.getCause().getMessage());
        try {
            response.setContentType(provider.messageContentType());
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.info("resolveException error.", e);
        }
        return new ModelAndView();
    }

    private MessageProvider getCurrentProvider() {
        return messageProvider == null ? DEFAULT_MESSAGE_PROVIDER : messageProvider;
    }
}
