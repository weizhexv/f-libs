package com.jkjq.fileupload.stream;

import com.jkjq.fileupload.stream.limit.FileSizeLimitProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
public class StreamServletFileUpload extends ServletFileUpload {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final FileSizeLimitProvider fileSizeLimitProvider;

    public StreamServletFileUpload(FileSizeLimitProvider fileSizeLimitProvider) {
        super();
        this.fileSizeLimitProvider = fileSizeLimitProvider;
    }

    /**
     * Constructs an instance of this class which uses the supplied factory to
     * create <code>FileItem</code> instances.
     *
     * @param fileItemFactory The factory to use for creating file items.
     * @see FileUpload#FileUpload()
     */
    public StreamServletFileUpload(FileItemFactory fileItemFactory, FileSizeLimitProvider fileSizeLimitProvider) {
        super(fileItemFactory);
        this.fileSizeLimitProvider = fileSizeLimitProvider;
    }

    private String getFileNameFromFileItemStream(FileItemStream item) {
        try {
            return (String) FieldUtils.readField(item, "name", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = new ArrayList<>();
        boolean successful = false;
        HttpServletRequest request = getHttpServletRequest(ctx);
        String url = request.getRequestURI();
        long maxLimit = getMaxLimit(url);

        try {
            FileItemIterator iter = getItemIterator(ctx);
            FileItemFactory fac = getFileItemFactory();
            if (fac == null) {
                throw new NullPointerException("No FileItemFactory has been set.");
            }

            while (iter.hasNext()) {
                final FileItemStream item = iter.next();
                // Don't use getName() here to prevent an InvalidFileNameException.
                final String fileName = getFileNameFromFileItemStream(item);
                FileItem fileItem = fac.createItem(item.getFieldName(), item.getContentType(),
                        item.isFormField(), fileName);
                items.add(fileItem);
                long requestAllSize = 0;
                try {
                    requestAllSize += copyFileData(item.openStream(), fileItem.getOutputStream(), url, maxLimit);
                    checkMaxLimit(requestAllSize, maxLimit, request);
                } catch (FileUploadIOException e) {
                    throw (FileUploadException) e.getCause();
                } catch (IOException e) {
                    throw new IOFileUploadException(format("Processing of %s request failed. %s",
                            MULTIPART_FORM_DATA, e.getMessage()), e);
                }
                final FileItemHeaders fih = item.getHeaders();
                fileItem.setHeaders(fih);
            }
            successful = true;
            return items;
        } catch (FileUploadIOException e) {
            throw (FileUploadException) e.getCause();
        } catch (IOException e) {
            throw new FileUploadException(e.getMessage(), e);
        } finally {
            if (!successful) {
                for (FileItem fileItem : items) {
                    try {
                        fileItem.delete();
                    } catch (Exception ignored) {
                        // ignored TODO perhaps add to tracker delete failure list somehow?
                    }
                }
            }
        }
    }

    private HttpServletRequest getHttpServletRequest(RequestContext ctx) throws IOFileUploadException {
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) FieldUtils.readField(ctx, "request", true);
        } catch (IllegalAccessException e) {
            log.warn("Processing request failed,get [request] form HttpServletRequest error.", e);
            throw new IOFileUploadException("Processing request failed", null);
        }
        return request;
    }

    private long getMaxLimit(String url) {
        long maxLimit = Long.MAX_VALUE;

        if (fileSizeLimitProvider != null) {
            maxLimit = fileSizeLimitProvider.getLimit(url).orElse(maxLimit);
        }
        return maxLimit;
    }

    private void checkMaxLimit(long allSize, long maxLimit, HttpServletRequest request) throws FileUploadIOException {
        String url = request.getRequestURI();
        if (allSize > maxLimit) {
            try {
                IOUtils.closeQuietly(request.getInputStream());
            } catch (IOException e) {
                log.warn("get inputStream error, for {}", url, e);
            }
            throw new FileUploadIOException(new FileSizeLimitExceededException(String.format("%s upload exceed limit", url), allSize, maxLimit));
        }
    }

    private long copyFileData(InputStream inputStream, OutputStream outputStream, String url, long maxLimit) throws IOException {
        byte[] buff = new byte[DEFAULT_BUFFER_SIZE];
        long size = 0;
        try {
            while (true) {
                int len = inputStream.read(buff);
                if (len == -1) {
                    break;
                }
                if (len > 0) {
                    size += len;
                }
                if (maxLimit > 0 && size > maxLimit) {
                    log.info("upload url is {},exceed size of {}.", url, maxLimit);
                    throw new FileUploadIOException(new FileSizeLimitExceededException(String.format("upload exceed limit,url is %s", url), size, maxLimit));
                }
                outputStream.write(buff, 0, len);
            }
        } finally {
            forceClose(inputStream, url);
            IOUtils.closeQuietly(outputStream);
        }
        return size;
    }

    private void forceClose(InputStream inputStream, String url) {
        if (inputStream instanceof MultipartStream.ItemInputStream) {
            try {
                ((MultipartStream.ItemInputStream) inputStream).close(true);
            } catch (IOException e) {
                log.warn("close under ItemInputStream error, {}.", url, e);
            }
            return;
        }
        IOUtils.closeQuietly(inputStream);
    }
}
