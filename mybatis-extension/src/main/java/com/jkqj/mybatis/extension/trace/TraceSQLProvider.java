package com.jkqj.mybatis.extension.trace;

/**
 * 在sql中打标，把trace id打上，以方便从慢sql中追踪业务问题。提供当前的业务trace id
 *
 * @author rolandhe
 */
public interface TraceSQLProvider {
    /**
     * 提供当前业务的trace id
     *
     * @return
     */
    String provideTraceId();
}
