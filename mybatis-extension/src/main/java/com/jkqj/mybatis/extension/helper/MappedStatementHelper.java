package com.jkqj.mybatis.extension.helper;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用于修改MapperStatement的工具类，用于更改MapperStatement的sql
 *
 * @author rolandhe
 */
public class MappedStatementHelper {
    private MappedStatementHelper() {
    }

    /**
     * 重新构建新的MappedStatement，其中的sql，参数都可以修改，本方法只修改sql
     *
     * @param mappedStatement
     * @param originalBoundSql
     * @param newSql
     * @return
     */
    public static MappedStatement buildMappedStatementFromOriginal(final MappedStatement mappedStatement, final BoundSql originalBoundSql, String newSql, Set<Integer> nullParamIndex) {
        SqlSource newSqlSource = new WrapperBoundSqlSqlSource(copyBoundSql(mappedStatement, originalBoundSql, newSql, nullParamIndex));
        MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), mappedStatement.getId(), newSqlSource, mappedStatement.getSqlCommandType());
        builder.resource(mappedStatement.getResource());
        builder.fetchSize(mappedStatement.getFetchSize());
        builder.statementType(mappedStatement.getStatementType());
        builder.keyGenerator(mappedStatement.getKeyGenerator());
        if (mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length > 0) {
            builder.keyProperty(mappedStatement.getKeyProperties()[0]);
        }
        builder.timeout(mappedStatement.getTimeout());
        builder.parameterMap(mappedStatement.getParameterMap());
        builder.resultMaps(mappedStatement.getResultMaps());
        builder.resultSetType(mappedStatement.getResultSetType());
        builder.cache(mappedStatement.getCache());
        builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
        builder.useCache(mappedStatement.isUseCache());
        return builder.build();
    }

    public static String beautySQL(String sql) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = sql.toCharArray();
        Character left = null;
        boolean lastSpace = false;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == ' ') {
                if (stringBuilder.length() == 0) {
                    continue;
                }
                if (left != null) {
                    stringBuilder.append(c);
                } else if (!lastSpace) {
                    stringBuilder.append(c);
                    lastSpace = true;
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                lastSpace = false;
                stringBuilder.append(c);
                if (left == null) {
                    left = c;
                } else if ((i <= 0 || chars[i - 1] != '\\') && left == c) {
                    left = null;
                }
                continue;
            }
            if (c == '\n' && left == null) {
                if(!lastSpace) {
                    stringBuilder.append(" ");
                    lastSpace = true;
                }
                continue;
            }
            lastSpace = false;
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }


    private static BoundSql copyBoundSql(MappedStatement mappedStatement, BoundSql originalBoundSql, String newSql, Set<Integer> removedParamIndex) {
        List<ParameterMapping> parameterMappings = originalBoundSql.getParameterMappings();

        // 忽略掉已经 @Selective 识别的参数
        if (null != parameterMappings && parameterMappings.size() > 0 && removedParamIndex != null && removedParamIndex.size() > 0) {
            int size = parameterMappings.size();
            parameterMappings = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if (removedParamIndex.contains(i)) {
                    continue;
                }
                parameterMappings.add(originalBoundSql.getParameterMappings().get(i));
            }
        }
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), newSql, parameterMappings, originalBoundSql.getParameterObject());
        for (ParameterMapping mapping : originalBoundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (originalBoundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, originalBoundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    private static class WrapperBoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public WrapperBoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}