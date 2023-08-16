package com.jkqj.common.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import com.jkqj.common.exception.ExecuteRuleException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 规则工具类（https://www.yuque.com/boyan-avfmj/aviatorscript/guhmrc）
 *
 * @author cb
 * @date 2019-12-05
 */
@Slf4j
public final class RuleUtils {

    /** 规则引擎：规则标识前缀 */
    public static final String RULE_PREFIX = "`#`";

    /** 规则引擎：规则标识后缀 */
    public static final String RULE_SUFFIX = "`@`";

    /** 规则引擎：正则 */
    public static final Pattern RULE_PATTERN = Pattern.compile("(" + RULE_PREFIX + "(.*?)" + RULE_SUFFIX + ")");

    static {
        try {
            AviatorEvaluator.addFunction(new JsonFunction());
        } catch (Throwable e) {
            log.error("register json function to aviator error", e);
        }

        try {
            AviatorEvaluator.addFunction(new IncludeAnyFunction());
        } catch (Throwable e) {
            log.error("register include any function to aviator error", e);
        }

        try {
            AviatorEvaluator.addFunction(new IncludeAllFunction());
        } catch (Throwable e) {
            log.error("register include all function to aviator error", e);
        }
    }

    private RuleUtils() {
    }

    /**
     * 生成规则包装
     *
     * @param rule 规则
     * @return 规则包装
     */
    public static String generateRuleWrap(String rule) {
        return RULE_PREFIX + rule + RULE_SUFFIX;
    }

    /**
     * 替换规则包装
     *
     * @param content  内容
     * @param function 替换方法
     * @return 替换后的内容
     */
    public static String replaceRuleWrap(String content, Function<String, String> function) {
        StringBuffer buffer = new StringBuffer();

        Matcher matcher = RULE_PATTERN.matcher(content);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, function.apply(matcher.group(2)));
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    /**
     * 执行规则引擎
     *
     * @param params 参数
     * @param rule   规则
     * @return 结果
     */
    public static Object execute(Map<String, Object> params, String rule) throws ExecuteRuleException {
        try {
            Expression expression = AviatorEvaluator.compile(rule, true);

            return expression.execute(params);
        } catch (Throwable e) {
            throw new ExecuteRuleException(params, rule, e);
        }
    }

    /**
     * 执行规则引擎
     *
     * @param rule   规则
     * @param params 参数
     * @return 结果
     */
    public static Object execute(String rule, Map<String, Object> params) {

        try {
            return RuleUtils.execute(params, rule);
        } catch (ExecuteRuleException e) {
            log.error("execute rule error", e);
        }

        return null;
    }

    /**
     * 执行规则引擎返回 String 型结果
     *
     * @param rule   规则
     * @param params 参数
     * @return 执行结果
     */
    public static String executeString(String rule, Map<String, Object> params) {
        Object result = RuleUtils.execute(rule, params);

        return result == null ? null : String.valueOf(result);
    }

    /**
     * 执行规则引擎返回 Boolean 型结果
     *
     * @param rule   规则
     * @param params 参数
     * @return 执行结果
     */
    public static Boolean executeBoolean(Map<String, Object> params, String rule) {
        String result = RuleUtils.executeString(rule, params);

        return result == null ? null : Boolean.valueOf(result);
    }

    /**
     * 校验
     *
     * @param rule   规则
     * @param params 参数
     * @return 是否通过
     * @throws ExecuteRuleException 规则执行异常
     */
    public static boolean check(String rule, Map<String, Object> params) throws ExecuteRuleException {
        Object result = RuleUtils.execute(params, rule);

        return result == null ? false : Boolean.valueOf(String.valueOf(result));
    }

    public static class JsonFunction extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg) {
            Object value = arg.getValue(env);
            if (value == null) {
                return AviatorNil.NIL;
            }

            if (!(value instanceof String)) {
                return AviatorRuntimeJavaType.valueOf(JsonUtils.toJson(value));
            }

            String json = String.valueOf(value).trim();
            Object result = json.startsWith("{") ? JsonUtils.toObject(json) : JsonUtils.toArray(json, Object.class);

            return AviatorRuntimeJavaType.valueOf(result);
        }

        @Override
        public String getName() {
            return "json";
        }
    }

    public static class IncludeAnyFunction extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            Object value1 = arg1.getValue(env);
            if (value1 == null) {
                return AviatorBoolean.FALSE;
            }

            Object value2 = arg2.getValue(env);
            if (value2 == null) {
                return AviatorBoolean.FALSE;
            }

            Class<?> value2Class = value2.getClass();
            if (Collection.class.isAssignableFrom(value2Class)) {
                Class<?> value1Class = value1.getClass();
                if (Collection.class.isAssignableFrom(value1Class)) {
                    return include(env, (Collection<?>) value1, (Collection<?>) value2);
                } else if (value1Class.isArray()) {
                    return include(env, value1, (Collection<?>) value2);
                }
            } else if (value2Class.isArray()) {
                Class<?> value1Class = value1.getClass();
                if (Collection.class.isAssignableFrom(value1Class)) {
                    return include(env, (Collection<?>) value1, value2);
                } else if (value1Class.isArray()) {
                    return include(env, value1, value2);
                }
            }

            return AviatorBoolean.FALSE;
        }

        @Override
        public String getName() {
            return "includeAny";
        }

        private AviatorObject include(Map<String, Object> env, Collection<?> collection1, Collection<?> collection2) {

            for (Object obj2 : collection2) {
                for (Object obj1 : collection1) {
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        return AviatorBoolean.TRUE;
                    }
                }
            }

            return AviatorBoolean.FALSE;
        }

        private AviatorObject include(Map<String, Object> env, Collection<?> collection, Object array) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                Object obj2 = Array.get(array, i);

                for (Object obj1 : collection) {
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        return AviatorBoolean.TRUE;
                    }
                }
            }

            return AviatorBoolean.FALSE;
        }

        private AviatorObject include(Map<String, Object> env, Object array, Collection<?> collection) {

            for (Object obj2 : collection) {
                int length = Array.getLength(array);
                for (int i = 0; i < length; i++) {
                    Object obj1 = Array.get(array, i);
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        return AviatorBoolean.TRUE;
                    }
                }
            }

            return AviatorBoolean.FALSE;
        }

        private AviatorObject include(Map<String, Object> env, Object array1, Object array2) {
            int length2 = Array.getLength(array2);
            for (int i = 0; i < length2; i++) {
                Object obj2 = Array.get(array2, i);

                int length1 = Array.getLength(array1);
                for (int j = 0; j < length1; j++) {
                    Object obj1 = Array.get(array1, j);
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        return AviatorBoolean.TRUE;
                    }
                }
            }

            return AviatorBoolean.FALSE;
        }
    }

    public static class IncludeAllFunction extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            Object value1 = arg1.getValue(env);
            if (value1 == null) {
                return AviatorBoolean.FALSE;
            }

            Object value2 = arg2.getValue(env);
            if (value2 == null) {
                return AviatorBoolean.FALSE;
            }

            Class<?> value2Class = value2.getClass();
            if (Collection.class.isAssignableFrom(value2Class)) {
                Class<?> value1Class = value1.getClass();
                if (Collection.class.isAssignableFrom(value1Class)) {
                    return include(env, (Collection<?>) value1, (Collection<?>) value2);
                } else if (value1Class.isArray()) {
                    return include(env, value1, (Collection<?>) value2);
                }
            } else if (value2Class.isArray()) {
                Class<?> value1Class = value1.getClass();
                if (Collection.class.isAssignableFrom(value1Class)) {
                    return include(env, (Collection<?>) value1, value2);
                } else if (value1Class.isArray()) {
                    return include(env, value1, value2);
                }
            }

            return AviatorBoolean.FALSE;
        }

        @Override
        public String getName() {
            return "includeAll";
        }

        private AviatorObject include(Map<String, Object> env, Collection<?> collection1, Collection<?> collection2) {

            for (Object obj2 : collection2) {
                boolean include = false;

                for (Object obj1 : collection1) {
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        include = true;
                        break;
                    }
                }

                if (!include) {
                    return AviatorBoolean.FALSE;
                }
            }

            return AviatorBoolean.TRUE;
        }

        private AviatorObject include(Map<String, Object> env, Collection<?> collection, Object array) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                Object obj2 = Array.get(array, i);
                boolean include = false;

                for (Object obj1 : collection) {
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        include = true;
                        break;
                    }
                }

                if (!include) {
                    return AviatorBoolean.FALSE;
                }
            }

            return AviatorBoolean.TRUE;
        }

        private AviatorObject include(Map<String, Object> env, Object array, Collection<?> collection) {

            for (Object obj2 : collection) {
                boolean include = false;

                int length = Array.getLength(array);
                for (int i = 0; i < length; i++) {
                    Object obj1 = Array.get(array, i);
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        include = true;
                        break;
                    }
                }

                if (!include) {
                    return AviatorBoolean.FALSE;
                }
            }

            return AviatorBoolean.TRUE;
        }

        private AviatorObject include(Map<String, Object> env, Object array1, Object array2) {
            int length2 = Array.getLength(array2);
            for (int i = 0; i < length2; i++) {
                Object obj2 = Array.get(array2, i);
                boolean include = false;

                int length1 = Array.getLength(array1);
                for (int j = 0; j < length1; j++) {
                    Object obj1 = Array.get(array1, j);
                    if (AviatorRuntimeJavaType.valueOf(obj1).compare(AviatorRuntimeJavaType.valueOf(obj2), env) == 0) {
                        include = true;
                        break;
                    }
                }

                if (!include) {
                    return AviatorBoolean.FALSE;
                }
            }

            return AviatorBoolean.TRUE;
        }
    }
}