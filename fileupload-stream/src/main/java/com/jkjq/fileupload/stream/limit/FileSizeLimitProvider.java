package com.jkjq.fileupload.stream.limit;

import java.util.Optional;

public interface FileSizeLimitProvider {
    Optional<Long> getLimit(String url);
}
