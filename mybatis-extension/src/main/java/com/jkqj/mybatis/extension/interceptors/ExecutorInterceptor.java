package com.jkqj.mybatis.extension.interceptors;

import com.jkqj.mybatis.extension.annotations.AutoFillBackstage;
import com.jkqj.mybatis.extension.backstage.AuoBackstageProvider;
import com.jkqj.mybatis.extension.consts.MybatisConsts;
import com.jkqj.mybatis.extension.helper.BackstageContext;
import com.jkqj.mybatis.extension.helper.MappedStatementHelper;
import com.jkqj.mybatis.extension.helper.MySqlParserHelper;
import com.jkqj.mybatis.extension.sharding.annotations.TableSharding;
import com.jkqj.mybatis.extension.sharding.helper.ShardingTableHelper;
import com.jkqj.mybatis.extension.trace.TraceSQLProvider;
import com.jkqj.sql.exec.context.SQLExecutorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * mybatis 拦截器，用于支持分表、sql trace id 打标
 *
 * @author rolandhe
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ExecutorInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorInterceptor.class);

    private static final String TRACE_SQL_FORMATTER = "/* %s */ %s";


    private final TraceSQLProvider traceSQLProvider;

    private final AuoBackstageProvider auoBackstageProvider;

    private boolean beautify = false;

    private int logSqlMax = Integer.MAX_VALUE;


    public ExecutorInterceptor() {
        this(null);
    }

    public ExecutorInterceptor(TraceSQLProvider traceSQLProvider) {
        this(traceSQLProvider, null);
    }

    public ExecutorInterceptor(TraceSQLProvider traceSQLProvider, AuoBackstageProvider auoBackstageProvider) {
        this.traceSQLProvider = traceSQLProvider;
        this.auoBackstageProvider = auoBackstageProvider;
    }


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        String traceId = getTraceSqlId();
        boolean[] needLogHolder = {true};
        try {
            return interceptCore(invocation, traceId, needLogHolder);
        } finally {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
            if (needLogHolder[0]) {
                logger.warn("call {} cost {}ms,{}.", mappedStatement.getId(), System.currentTimeMillis() - start, shortSql(boundSql.getSql()));
            }
            SQLExecutorContext.OUTPUT.remove();
        }
    }

    private String shortSql(String sql) {
        if (sql.length() < logSqlMax) {
            return sql;
        }
        if (logSqlMax <= 0) {
            return "";
        }
        return sql.substring(0, (logSqlMax - 3)) + "...";
    }

    private Object interceptCore(Invocation invocation, String traceId, boolean[] needLogHolder) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object params = args[1];
        MapperMethod.ParamMap paramMap = null;
        if (params instanceof MapperMethod.ParamMap) {
            paramMap = (MapperMethod.ParamMap) params;
        } else {
            // 单个参数时不会壹map呈现，则自行创建map
            paramMap = new MapperMethod.ParamMap();
            paramMap.put(ShardingTableHelper.FIRST_PARAM_NAME_MYBATIS, params);
        }

        long start = System.currentTimeMillis();
        String mapperStmtId = mappedStatement.getId();

        TableSharding tableSharding = ShardingTableHelper.findMapperShardingConfig(mapperStmtId);
        ShardingTableHelper.MethodConfig methodConfig = ShardingTableHelper.findMethodConfig(mapperStmtId, tableSharding != null);
        // 是否需要输出log
        needLogHolder[0] = methodConfig.getIgnoreLog() == null;
        SQLExecutorContext.OUTPUT.set(needLogHolder[0]);

        int changeStatus = needChangeSql(tableSharding, methodConfig, mappedStatement.getSqlCommandType(), traceId);

        ShardingTableHelper.TableNameShardingToolbox tableNameShardingToolbox = null;
        if ((changeStatus & MybatisConsts.TABLE_SHARD) == MybatisConsts.TABLE_SHARD) {
            tableNameShardingToolbox = ShardingTableHelper.buildTableNameShardingToolbox(paramMap,
                    methodConfig.getTableShardingKeyInfo(), tableSharding, mapperStmtId);
            if (tableNameShardingToolbox == ShardingTableHelper.TableNameShardingToolbox.NONE) {
                changeStatus &= (~MybatisConsts.TABLE_SHARD);
            }
        }
        if (MybatisConsts.NONE == changeStatus) {
            return invocation.proceed();
        }
        ChangeContext changeContext = new ChangeContext(args,tableSharding,methodConfig.getAutoFillBackstage(),traceId);
        String newSql = changeSQL(changeContext, tableNameShardingToolbox, changeStatus);
        if (needLogHolder[0]) {
            logger.info("change sql cost {}ms: {}.", System.currentTimeMillis() - start, newSql);
        }
        return invocation.proceed();
    }

    private static class ChangeContext{
        final  Object[] args;
        final TableSharding tableSharding;
        final AutoFillBackstage autoFillBackstage;
        final String traceId;

        private ChangeContext(Object[] args, TableSharding tableSharding, AutoFillBackstage autoFillBackstage, String traceId) {
            this.args = args;
            this.tableSharding = tableSharding;
            this.autoFillBackstage = autoFillBackstage;
            this.traceId = traceId;
        }
    }


    private int needChangeSql(TableSharding tableSharding, ShardingTableHelper.MethodConfig methodConfig, SqlCommandType commandType, String traceId) {
        int result = MybatisConsts.NONE;
        if (tableSharding != null) {
            result |= MybatisConsts.TABLE_SHARD;
        }
        if (methodConfig != null && methodConfig.getSelective() != null && (commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UPDATE)) {
            result |= MybatisConsts.SELECTIVE;
        }
        if (!StringUtils.isEmpty(traceId)) {
            result |= MybatisConsts.TRACE;
        }

        if (methodConfig != null && methodConfig.getAutoFillBackstage() != null
                && (commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UPDATE)
                && auoBackstageProvider != null) {
            result |= MybatisConsts.AUTO_BACKSTAGE;
        }

        if (beautify) {
            result |= MybatisConsts.BEAUTIFY;
        }
        return result;
    }

    /**
     * copy from DefaultParameterHandler::setParameters.
     * <p>
     * 获取所有null参数
     *
     * @param mappedStatement
     * @param parameterObject
     * @return
     */
    private Set<Integer> getNullParameterIndexesCore(MappedStatement mappedStatement, Object parameterObject) {
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Set<Integer> nullParams = new TreeSet<>();
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                Object value;
                String propertyName = parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                if (value == null) {
                    nullParams.add(i);
                }
            }

        }
        return nullParams;
    }

    /**
     * 分表支持及trace id 打标，修改表名称
     *
     * @param tableNameShardingToolbox
     */
    private String changeSQL(ChangeContext changeContext, final ShardingTableHelper.TableNameShardingToolbox tableNameShardingToolbox,  int changeStatus) {
        MappedStatement mappedStatement = (MappedStatement) changeContext.args[0];
        BoundSql boundSql = mappedStatement.getBoundSql(changeContext.args[1]);

        String oldSql = boundSql.getSql();
        logger.debug("original input sql:{}", oldSql);

        String newSql = oldSql;
        Set<Integer> nullParamIndex = null;
        if ((changeStatus & MybatisConsts.SELECTIVE) == MybatisConsts.SELECTIVE) {
            nullParamIndex = getNullParameterIndexes(changeStatus, mappedStatement, changeContext.args[1]);
        }

        boolean parseAndChangeSql = (changeStatus & MybatisConsts.SELECTIVE) == MybatisConsts.SELECTIVE
                || (changeStatus & MybatisConsts.TABLE_SHARD) == MybatisConsts.TABLE_SHARD
                || (changeStatus & MybatisConsts.AUTO_BACKSTAGE) == MybatisConsts.AUTO_BACKSTAGE;

        if (parseAndChangeSql) {
            BackstageContext backstageContext = new BackstageContext(changeContext.autoFillBackstage,auoBackstageProvider);
            newSql = MySqlParserHelper.parseAndConvertSql(oldSql, buildTableNameChanger(changeStatus, tableNameShardingToolbox, changeContext.tableSharding),
                    nullParamIndex, backstageContext);
        }

        if ((changeStatus & MybatisConsts.BEAUTIFY) == MybatisConsts.BEAUTIFY && !parseAndChangeSql) {
            newSql = MappedStatementHelper.beautySQL(newSql);
        }

        if ((changeStatus & MybatisConsts.TRACE) == MybatisConsts.TRACE) {
            newSql = String.format(TRACE_SQL_FORMATTER, changeContext.traceId, newSql);
        }


        MappedStatement newMappedStmt = MappedStatementHelper.buildMappedStatementFromOriginal(mappedStatement, boundSql, newSql, nullParamIndex);
        changeContext.args[0] = newMappedStmt;


        return newSql;
    }


    private String getTraceSqlId() {
        if (this.traceSQLProvider == null) {
            return null;
        }
        return traceSQLProvider.provideTraceId();
    }

    private MySqlParserHelper.TableNameChanger buildTableNameChanger(int changeStatus, ShardingTableHelper.TableNameShardingToolbox tableNameShardingToolbox, TableSharding tableSharding) {
        if ((changeStatus & MybatisConsts.TABLE_SHARD) != MybatisConsts.TABLE_SHARD) {
            return null;
        }
        return oldName -> tableNameShardingToolbox.shardingTablePolicy.shardTableName(tableNameShardingToolbox.shardingKeyValue, oldName, tableSharding.sliceCount());
    }

    private Set<Integer> getNullParameterIndexes(int changeStatus, MappedStatement mappedStatement, Object params) {
        if ((changeStatus & MybatisConsts.SELECTIVE) != MybatisConsts.SELECTIVE) {
            return null;
        }
        return getNullParameterIndexesCore(mappedStatement, params);
    }

    public boolean isBeautify() {
        return beautify;
    }

    public void setBeautify(boolean beautify) {
        this.beautify = beautify;
    }

    public int getLogSqlMax() {
        return logSqlMax;
    }

    public void setLogSqlMax(int logSqlMax) {
        this.logSqlMax = logSqlMax;
    }
}
