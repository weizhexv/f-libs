package com.jkjq.fileupload.stream;

import com.jkjq.fileupload.stream.limit.FileSizeLimitProvider;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletContext;

public class StreamCommonsMultipartResolver extends CommonsMultipartResolver {
    private final FileSizeLimitProvider fileSizeLimitProvider;

    public StreamCommonsMultipartResolver(FileSizeLimitProvider fileSizeLimitProvider) {
        super();
        this.fileSizeLimitProvider = fileSizeLimitProvider;
    }

    /**
     * Constructor for standalone usage. Determines the servlet container's
     * temporary directory via the given ServletContext.
     * @param servletContext the ServletContext to use
     */
    public StreamCommonsMultipartResolver(ServletContext servletContext, FileSizeLimitProvider fileSizeLimitProvider) {
        super(servletContext);
        this.fileSizeLimitProvider = fileSizeLimitProvider;
    }

    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return new StreamServletFileUpload(fileItemFactory, fileSizeLimitProvider);
    }
}
