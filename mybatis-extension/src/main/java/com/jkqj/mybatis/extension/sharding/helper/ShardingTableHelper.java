package com.jkqj.mybatis.extension.sharding.helper;


import com.jkqj.mybatis.extension.annotations.AutoFillBackstage;
import com.jkqj.mybatis.extension.annotations.IgnoreLog;
import com.jkqj.mybatis.extension.annotations.Selective;
import com.jkqj.mybatis.extension.sharding.annotations.TableSharding;
import com.jkqj.mybatis.extension.sharding.annotations.TableShardingKey;
import com.jkqj.mybatis.extension.sharding.policy.NoneShardingTablePolicy;
import com.jkqj.mybatis.extension.sharding.policy.ShardingTablePolicy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.binding.MapperMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分表操作的工具类 <br>
 *
 * <p>
 * 匹配生效注解、生成分表策略实例，自动根据TableSharding.entityClass匹配参数（用于确定分表策略）
 * </p>
 *
 * @author rolandhe
 */
@Slf4j
public class ShardingTableHelper {

    /**
     * key is mapperInterfaceName TableSharding 配置
     * <p>
     * 存储每一个Mapper接口的
     */
    private static final Map<String, ShardingConfigHolder> mapperTableShardingConfig = new ConcurrentHashMap<>();

    /**
     * key is mapperStmtId, it's style is (interface class name).(method name)
     * <p>
     * 存储每个方法的注解配置
     */
    private static final Map<String, MethodConfig> methodTableShardingConfig = new ConcurrentHashMap<>();

    /**
     * key is ShardingTablePolicy 实现类的名称
     * <p>
     * 存储每个策略的实例
     */
    private static final Map<String, ShardingTablePolicy> policyInstanceCache = new ConcurrentHashMap<>();

    /**
     * key is mapperStmtId, it's style is (interface class name).(method name)
     * <p>
     * 存储每个方法上配置TableShardingKey的参数的index
     */
    private static final Map<String, Integer> matchedMethodCache = new ConcurrentHashMap<>();

    public static final Integer INVALID_MATCHED_METHOD_INDEX = Integer.MIN_VALUE;
    public static final String PARAM_PREFIX_MYBATIS = "param";
    public static final String FIRST_PARAM_NAME_MYBATIS = PARAM_PREFIX_MYBATIS + "1";

    private ShardingTableHelper() {
    }

    /**
     * 存储分表配置的信息，用于缓存
     */
    private static class ShardingConfigHolder {
        /**
         * 表示无效的配置信息，用于缓存时，如果一个Mapper没有分表信息，在缓存中会记录对应的value是NONE， null则分不清楚没有缓存还是没有配置。
         */
        static final ShardingConfigHolder NONE = new ShardingConfigHolder();

        TableSharding tableSharding;

    }

    /**
     * 方法上的注解配置
     */
    public static class MethodConfig {
        public static final MethodConfig NONE = new MethodConfig();

        private TableShardingKeyInfo tableShardingKeyInfo;
        private Selective selective;
        private IgnoreLog ignoreLog;

        private AutoFillBackstage autoFillBackstage;


        public TableShardingKeyInfo getTableShardingKeyInfo() {
            return tableShardingKeyInfo;
        }

        public Selective getSelective() {
            return selective;
        }

        public IgnoreLog getIgnoreLog() {
            return ignoreLog;
        }

        public AutoFillBackstage getAutoFillBackstage(){
            return autoFillBackstage;
        }
    }


    /**
     * 方法上的TableShardingKey信息
     */
    public static class TableShardingKeyInfo {
        /**
         * TableShardingKey在第几个参数上，从 0 开始
         */
        private int parameterIndex;

        private TableShardingKey tableShardingKey;

        public int getParameterIndex() {
            return parameterIndex;
        }

        public TableShardingKey getShardingKey() {
            return tableShardingKey;
        }
    }

    /**
     * 最终用户分表的信息
     */
    public static class TableNameShardingToolbox {
        public static final TableNameShardingToolbox NONE = new TableNameShardingToolbox(null, null);

        /**
         * 分表策略
         */
        public final ShardingTablePolicy shardingTablePolicy;
        /**
         * 分表键的值
         */
        public final Object shardingKeyValue;


        public TableNameShardingToolbox(ShardingTablePolicy shardingTablePolicy, Object shardingKeyValue) {
            this.shardingTablePolicy = shardingTablePolicy;
            this.shardingKeyValue = shardingKeyValue;
        }
    }

    /**
     * 通过mybatis的MapperStatement.getId()来获取对应Mapper上的TableSharding注解
     *
     * @param mapperStmtId MapperStatement.id的格式是 Mapper (interface class name).(method name)
     * @return
     */
    public static TableSharding findMapperShardingConfig(String mapperStmtId) {
        String mapperInterfaceName = parseMapperInterfaceFromMapperStatementId(mapperStmtId)[0];
        ShardingConfigHolder shardingConfigHolder = mapperTableShardingConfig.get(mapperInterfaceName);
        if (shardingConfigHolder != null) {
            if (ShardingConfigHolder.NONE == shardingConfigHolder) {
                return null;
            }
            return shardingConfigHolder.tableSharding;
        }
        try {
            Class clazz = Class.forName(mapperInterfaceName);
            TableSharding tableSharding = (TableSharding) clazz.getAnnotation(TableSharding.class);
            if (tableSharding == null) {
                mapperTableShardingConfig.putIfAbsent(mapperInterfaceName, ShardingConfigHolder.NONE);
                return null;
            }
            ShardingConfigHolder holder = new ShardingConfigHolder();
            holder.tableSharding = tableSharding;
            mapperTableShardingConfig.putIfAbsent(mapperInterfaceName, holder);
            return tableSharding;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过mybatis的MapperStatement.getId()来获取对应方法对应的TableShardingKey注解
     *
     * @param mappedStmtId MapperStatement.id的格式是 Mapper (interface class name).(method name)
     * @return
     * @Parm needShardingKeyDetect 是否需要探测参数上的TableShardingKey注解
     */
    public static MethodConfig findMethodConfig(String mappedStmtId, boolean needShardingKeyDetect) {
        MethodConfig methodConfig = methodTableShardingConfig.get(mappedStmtId);
        if (methodConfig != null) {
            if (MethodConfig.NONE == methodConfig) {
                return null;
            }
            return methodConfig;
        }

        methodConfig = findShardingKeyParamFromClass(mappedStmtId, needShardingKeyDetect);
        if (methodConfig == null) {
            methodTableShardingConfig.putIfAbsent(mappedStmtId, MethodConfig.NONE);
            return null;
        }
        methodTableShardingConfig.putIfAbsent(mappedStmtId, methodConfig);
        return methodConfig;
    }

    /**
     * 根据分表策略类获取对应的实例
     *
     * @param clazz
     * @return
     */
    public static ShardingTablePolicy getShardingPolicyInstance(Class<? extends ShardingTablePolicy> clazz) {
        String clazzName = clazz.getName();
        ShardingTablePolicy instance = policyInstanceCache.get(clazzName);
        if (instance != null) {
            return instance;
        }
        try {
            instance = clazz.getConstructor().newInstance();
            policyInstanceCache.putIfAbsent(clazzName, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据TableSharding.entityClass匹配参数的类型，如果匹配到用来分表。
     *
     * @param mapperStmtId
     * @param entityClass
     * @return 匹配到的参数的index
     */
    public static int matchTableShardingParamIndex(String mapperStmtId, Class entityClass) {
        Integer v = matchedMethodCache.get(mapperStmtId);
        if (v != null) {
            return v;
        }
        Method method = findMethodByMappedStmtId(mapperStmtId).method;
        Class[] paramTypeArray = method.getParameterTypes();
        if (paramTypeArray == null || paramTypeArray.length == 0) {
            matchedMethodCache.putIfAbsent(mapperStmtId, INVALID_MATCHED_METHOD_INDEX);
            return INVALID_MATCHED_METHOD_INDEX;
        }

        int index = 0;
        for (Class paramType : paramTypeArray) {
            if (paramType.equals(entityClass)) {
                matchedMethodCache.putIfAbsent(mapperStmtId, index);
                return index;
            }
            index++;
        }
        matchedMethodCache.putIfAbsent(mapperStmtId, INVALID_MATCHED_METHOD_INDEX);
        return INVALID_MATCHED_METHOD_INDEX;
    }


    /**
     * 通过反射从entityClass对象读取分区键的值
     *
     * @param entityValue
     * @param fieldName
     * @return
     */
    public static Object readShardingKeyValue(Object entityValue, String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            return entityValue;
        }
        try {
            return FieldUtils.readField(entityValue, fieldName, true);
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 或者根据TableShardingKey或者根据TableSharding信息读取分表信息，用于分表
     *
     * @param paramMap
     * @param tableShardingKeyInfo
     * @param tableSharding
     * @param mstatId
     * @return
     */
    public static TableNameShardingToolbox buildTableNameShardingToolbox(final MapperMethod.ParamMap paramMap, TableShardingKeyInfo tableShardingKeyInfo, TableSharding tableSharding, String mstatId) {
        if (tableShardingKeyInfo != null) {
            Class shardingPolicyClass = tableShardingKeyInfo.getShardingKey().shardingPolicyClass();
            if (shardingPolicyClass.equals(NoneShardingTablePolicy.class)) {
                if (tableSharding == null) {
                    throw new RuntimeException("invalid sharding policy for:" + mstatId);
                }
                shardingPolicyClass = tableSharding.shardingPolicyClass();
            }
            ShardingTablePolicy shardingTablePolicy = ShardingTableHelper.getShardingPolicyInstance(shardingPolicyClass);
            Object shardKeyValue = paramMap.get(PARAM_PREFIX_MYBATIS + (tableShardingKeyInfo.getParameterIndex() + 1));
            return new TableNameShardingToolbox(shardingTablePolicy, shardKeyValue);
        }
        int index = ShardingTableHelper.matchTableShardingParamIndex(mstatId, tableSharding.entityClass());
        if (index == INVALID_MATCHED_METHOD_INDEX) {
            return TableNameShardingToolbox.NONE;
        }

        Object entityValue = paramMap.get(PARAM_PREFIX_MYBATIS + (index + 1));
        ShardingTablePolicy shardingTablePolicy = ShardingTableHelper.getShardingPolicyInstance(tableSharding.shardingPolicyClass());
        Object shardKeyValue = ShardingTableHelper.readShardingKeyValue(entityValue, tableSharding.shardingKeyNameOfEntity());
        return new TableNameShardingToolbox(shardingTablePolicy, shardKeyValue);
    }

    private static MethodConfig findShardingKeyParamFromClass(String mappedStmtId, boolean needShardingKeyDetect) {
        MethodInfoGroup group = findMethodByMappedStmtId(mappedStmtId);
        Method targetMethod = group.method;

        MethodConfig methodConfig = new MethodConfig();
        methodConfig.selective = targetMethod.getAnnotation(Selective.class);
        methodConfig.ignoreLog = targetMethod.getAnnotation(IgnoreLog.class);
        methodConfig.autoFillBackstage = getAutoFillBackstageFor(group.clazz);
        if (!needShardingKeyDetect) {
            return methodConfig;
        }

        Parameter[] parameterArray = targetMethod.getParameters();
        List<TableShardingKey> tableShardingKeyList = new ArrayList<>();
        int firstIndex = -1;
        int index = 0;
        for (Parameter parameter : parameterArray) {
            TableShardingKey tableShardingKey = parameter.getAnnotation(TableShardingKey.class);
            if (tableShardingKey != null) {
                if (firstIndex == -1) {
                    firstIndex = index;
                }
                tableShardingKeyList.add(tableShardingKey);
            }
            index++;
        }

        if (tableShardingKeyList.size() == 0) {
            return methodConfig;
        }
        if (tableShardingKeyList.size() > 1) {
            log.warn("{} find {} ShardingKeyParam, use the first.", mappedStmtId, tableShardingKeyList.size());
        }
        methodConfig.tableShardingKeyInfo = new TableShardingKeyInfo();

        methodConfig.tableShardingKeyInfo.parameterIndex = firstIndex;
        methodConfig.tableShardingKeyInfo.tableShardingKey = tableShardingKeyList.get(0);

        return methodConfig;
    }

    private static AutoFillBackstage getAutoFillBackstageFor(Class<?> clazz) {
        while (clazz != null && clazz != Object.class) {
            AutoFillBackstage autoFillBackstage = clazz.getAnnotation(AutoFillBackstage.class);
            if(autoFillBackstage != null) {
                return autoFillBackstage;
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    private static class MethodInfoGroup {
        private final Method method;
        private final Class<?> clazz;

        private MethodInfoGroup(Method method, Class<?> clazz) {
            this.method = method;
            this.clazz = clazz;
        }
    }
    private static MethodInfoGroup findMethodByMappedStmtId(String msId) {
        String[] methodInfo = parseMapperInterfaceFromMapperStatementId(msId);
        Class clazz = null;
        try {
            clazz = Class.forName(methodInfo[0]);
        } catch (ClassNotFoundException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        String methodName = methodInfo[1];
        Method[] methodArray = clazz.getMethods();
        Method targetMethod = null;
        for (Method method : methodArray) {
            if (method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }
        if (targetMethod == null) {
            throw new RuntimeException("can't find " + msId);
        }
        return new MethodInfoGroup(targetMethod, clazz);
    }


    private static String[] parseMapperInterfaceFromMapperStatementId(String msId) {
        String[] result = new String[2];
        int pos = msId.lastIndexOf(".");
        result[0] = msId.substring(0, pos);
        result[1] = msId.substring(pos + 1);
        return result;
    }
}
