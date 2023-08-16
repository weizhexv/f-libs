package com.jkqj.common.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * 并发Lambda工具类
 *
 * @author cb
 */
public final class ParallelLambdas {

    /**
     * 将实体列表转化为一个map
     *
     * @param entities 实体列表
     * @param function key转化函数
     * @param <T>      实体类型
     * @param <K>      key类型
     * @return
     */
    public static <T, K> Map<K, T> trans2Map(Collection<T> entities, Function<T, K> function) {
        return extract2Map(entities, function, Function.identity());
    }

    /**
     * 将原map转化为一个新map
     *
     * @param map           原map
     * @param valueFunction 值转化函数
     * @param <K>           key类型
     * @param <V>           原值类型
     * @param <N>           新值类型
     * @return
     */
    public static <K, V, N> Map<K, N> trans2NewMap(Map<K, V> map, Function<V, N> valueFunction) {
        if (MapUtils.isEmpty(map)) {
            return emptyMap();
        }

        return extract2Map(map.entrySet(), Entry::getKey, entry -> valueFunction.apply(entry.getValue()));
    }

    /**
     * 将原map转化为一个新map（value为List）
     *
     * @param map           原map
     * @param valueFunction 值转化函数
     * @param <K>           key类型
     * @param <V>           原值类型
     * @param <N>           新值类型
     * @return
     */
    public static <K, V, N> Map<K, List<N>> trans2NewListMap(Map<K, List<V>> map, Function<V, N> valueFunction) {
        return trans2NewMap(map, values -> mapToList(values, valueFunction));
    }

    /**
     * 将原map转化为一个新map（value为Set）
     *
     * @param map           原map
     * @param valueFunction 值转化函数
     * @param <K>           key类型
     * @param <V>           原值类型
     * @param <N>           新值类型
     * @return
     */
    public static <K, V, N> Map<K, Set<N>> trans2NewSetMap(Map<K, Set<V>> map, Function<V, N> valueFunction) {
        return trans2NewMap(map, values -> mapToSet(values, valueFunction));
    }

    /**
     * 将实体列表转化为一个map（实体自身为key）
     *
     * @param entities      实体列表
     * @param valueFunction 值转化函数
     * @param <T>           实体类型
     * @param <V>           值类型
     * @return
     */
    public static <T, V> Map<T, V> trans2SelfMap(Collection<T> entities, Function<T, V> valueFunction) {
        return extract2Map(entities, Function.identity(), valueFunction);
    }

    /**
     * 将实体列表转化为一个map
     *
     * @param entities      实体列表
     * @param keyFunction   key转化函数
     * @param valueFunction 值转化函数
     * @param <T>           实体类型
     * @param <K>           key类型
     * @param <V>           值类型
     * @return
     */
    public static <T, K, V> Map<K, V> extract2Map(Collection<T> entities,
                                                  Function<T, K> keyFunction, Function<T, V> valueFunction) {
        if (isEmpty(entities)) {
            return emptyMap();
        }

        return stream(entities).collect(toMap(keyFunction, valueFunction, (x, y) -> x));
    }

    /**
     * 将实体列表转化为一个二元map
     *
     * @param entities     实体列表
     * @param key1Function 一级key转化函数
     * @param key1Function 二级key转化函数
     * @param <T>          实体类型
     * @param <K1>         一级key类型
     * @param <K2>         二级key类型
     * @return
     */
    public static <T, K1, K2> Map<K1, Map<K2, T>> trans2BiMap(Collection<T> entities,
                                                              Function<T, K1> key1Function,
                                                              Function<T, K2> key2Function) {
        if (isEmpty(entities)) {
            return emptyMap();
        }

        Map<K1, List<T>> k1Map = groupBy(entities, key1Function);

        return trans2NewMap(k1Map, values -> trans2Map(values, key2Function));
    }

    /**
     * 将实体列表中的每个对象提取出相应的key与value(List)组成一个map
     *
     * @param entities      实体列表
     * @param keyFunction   key转化函数
     * @param valueFunction 值转化函数
     * @param <T>           实体类型
     * @param <K>           key类型
     * @param <V>           值类型
     * @return
     */
    public static <T, K, V> Map<K, List<V>> extract2KeyListMap(Collection<T> entities,
                                                               Function<T, K> keyFunction,
                                                               Function<T, V> valueFunction) {
        if (isEmpty(entities)) {
            return emptyMap();
        }

        return stream(entities).collect(groupingBy(keyFunction, mapping(valueFunction, toList())));
    }

    /**
     * 将实体列表中的每个对象提取出相应的key与value(Set)组成一个map
     *
     * @param entities      实体列表
     * @param keyFunction   key转化函数
     * @param valueFunction 值转化函数
     * @param <T>           实体类型
     * @param <K>           key类型
     * @param <V>           值类型
     * @return
     */
    public static <T, K, V> Map<K, Set<V>> extract2KeySetMap(Collection<T> entities,
                                                             Function<T, K> keyFunction, Function<T, V> valueFunction) {
        if (isEmpty(entities)) {
            return emptyMap();
        }

        return stream(entities).collect(groupingBy(keyFunction, mapping(valueFunction, toSet())));
    }

    /**
     * 对实体列表进行过滤
     *
     * @param entities  实体列表
     * @param predicate 过滤函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> List<T> filterList(Collection<T> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).filter(predicate).collect(toList());
    }

    /**
     * 对实体列表进行过滤
     *
     * @param entities  实体列表
     * @param predicate 过滤函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> List<T> filterFlatMapList(Collection<? extends Collection<T>> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return emptyList();
        }
        return stream(entities).flatMap(Collection::stream).filter(predicate).collect(toList());
    }

    /**
     * 对实体列表进行过滤和转换
     *
     * @param entities  实体列表
     * @param predicate 过滤函数
     * @param function  转换函数
     * @param <T>       实体类型
     * @param <R>       转换类型
     * @return
     */
    public static <T, R> List<R> filterAndMapToList(Collection<T> entities, Predicate<T> predicate,
                                                    Function<T, R> function) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).filter(predicate).map(function).filter(Objects::nonNull).collect(toList());
    }

    /**
     * 对实体列表进行过滤和转换
     *
     * @param entities  实体列表
     * @param predicate 过滤函数
     * @param function  转换函数
     * @param <T>       实体类型
     * @param <R>       转换类型
     * @return
     */
    public static <T, R> Set<R> filterAndMapToSet(Collection<T> entities, Predicate<T> predicate,
                                                  Function<T, R> function) {
        if (isEmpty(entities)) {
            return emptySet();
        }

        return stream(entities).filter(predicate).map(function).filter(Objects::nonNull).collect(toSet());
    }

    /**
     * 对实体列表进行转换
     *
     * @param entities 实体列表
     * @param function 转换函数
     * @param <T>      实体类型
     * @param <R>      转换类型
     * @return
     */
    public static <T, R> List<R> mapToList(Collection<T> entities, Function<T, R> function) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).map(function).filter(Objects::nonNull).collect(toList());
    }

    /**
     * 对实体列表进行转换
     *
     * @param entities 实体列表
     * @param function 转换函数
     * @param <T>      实体类型
     * @param <R>      转换类型
     * @return
     */
    public static <T, R> Set<R> mapToSet(Collection<T> entities, Function<T, R> function) {
        if (isEmpty(entities)) {
            return emptySet();
        }

        return stream(entities).map(function).filter(Objects::nonNull).collect(toSet());
    }

    /**
     * 对实体列表进行转换，再拍平合并每个实体转换成的列表(List)
     *
     * @param entities 实体列表
     * @param function 转换函数
     * @param <T>      实体类型
     * @param <R>      转换类型
     * @return
     */
    public static <T, R> List<R> flatMapToList(Collection<T> entities, Function<T, Collection<R>> function) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).map(function).filter(Objects::nonNull).flatMap(Collection::stream).filter(
                Objects::nonNull).collect(toList());
    }

    /**
     * 拍平合并每个实体的列表(List)
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     * @return
     */
    public static <T> List<T> flatMapToList(Collection<? extends Collection<T>> entities) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).flatMap(Collection::stream).filter(Objects::nonNull).collect(toList());
    }

    /**
     * 对实体列表进行转换，再拍平合并每个实体转换成的列表(Set)
     *
     * @param entities 实体列表
     * @param function 转换函数
     * @param <T>      实体类型
     * @param <R>      转换类型
     * @return
     */
    public static <T, R> Set<R> flatMapToSet(Collection<T> entities, Function<T, Collection<R>> function) {
        if (isEmpty(entities)) {
            return emptySet();
        }

        return stream(entities).map(function).filter(Objects::nonNull).flatMap(Collection::stream).filter(
                Objects::nonNull).collect(toSet());
    }

    /**
     * 拍平合并每个实体的列表(Set)
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     * @return
     */
    public static <T> Set<T> flatMapToSet(Collection<? extends Collection<T>> entities) {
        if (isEmpty(entities)) {
            return emptySet();
        }

        return stream(entities).flatMap(Collection::stream).filter(Objects::nonNull).collect(toSet());
    }

    /**
     * 对实体列表进行分组
     *
     * @param entities 实体列表
     * @param function 分组函数
     * @param <T>      实体类型
     * @param <K>      key类型
     * @return
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> entities, Function<T, K> function) {
        if (isEmpty(entities)) {
            return emptyMap();
        }

        return stream(entities).collect(groupingBy(function));
    }

    /**
     * 对实体对象转换成String后符号连接
     *
     * @param entities
     * @param function
     * @param delimiter
     * @param <T>
     * @return
     */
    public static <T> String join(Collection<T> entities, Function<T, String> function, CharSequence delimiter) {
        if (isEmpty(entities)) {
            return StringUtils.EMPTY;
        }

        return stream(entities).map(function).filter(Objects::nonNull).collect(joining(delimiter));
    }

    /**
     * 对实体列表进行排序
     *
     * @param entities 实体列表
     * @param function 排序函数
     * @param <T>      实体类型
     * @param <U>      排序key类型
     * @return
     */
    public static <T, U extends Comparable<? super U>> List<T> sort(Collection<T> entities,
                                                                    Function<? super T, ? extends U> function) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).sorted(comparing(function)).collect(toList());
    }

    /**
     * 对实体列表进行排序
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     * @return
     */
    public static <T extends Comparable<? super T>> List<T> sort(Collection<T> entities) {
        return sort(entities, Function.identity());
    }

    /**
     * 对实体列表进行倒序排序
     *
     * @param entities 实体列表
     * @param function 排序函数
     * @param <T>      实体类型
     * @param <U>      排序key类型
     * @return
     */
    public static <T, U extends Comparable<? super U>> List<T> sortReversed(Collection<T> entities,
                                                                            Function<? super T, ? extends U> function) {
        if (isEmpty(entities)) {
            return emptyList();
        }

        return stream(entities).sorted(comparing(function).reversed()).collect(toList());
    }

    /**
     * 对实体列表进行倒序排序
     *
     * @param entities 实体列表
     * @param <T>      实体类型
     * @return
     */
    public static <T extends Comparable<? super T>> List<T> sortReversed(Collection<T> entities) {
        return sortReversed(entities, Function.identity());
    }

    /**
     * 获取实体列表中第一个匹配的对象
     *
     * @param entities  实体列表
     * @param predicate 判断函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> T findFirst(Collection<T> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return null;
        }

        return stream(entities).filter(predicate).findFirst().orElse(null);
    }

    /**
     * 获取实体列表中第一个匹配的对象
     *
     * @param entities  实体列表
     * @param predicate 判断函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> Optional<T> optionalFirst(Collection<T> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return null;
        }

        return stream(entities).filter(predicate).findFirst();
    }

    /**
     * 判断实体列表中是否有任意一个匹配指定逻辑
     *
     * @param entities  实体列表
     * @param predicate 判断函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> boolean anyMatch(Collection<T> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return false;
        }

        return stream(entities).anyMatch(predicate);
    }

    /**
     * 判断实体列表是否都匹配某个指定逻辑
     *
     * @param entities  实体列表
     * @param predicate 判断函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> boolean allMatch(Collection<T> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return false;
        }

        return stream(entities).allMatch(predicate);
    }

    /**
     * 判断实体列表是否都不匹配某个指定逻辑
     *
     * @param entities  实体列表
     * @param predicate 判断函数
     * @param <T>       实体类型
     * @return
     */
    public static <T> boolean noneMatch(Collection<T> entities, Predicate<T> predicate) {
        if (isEmpty(entities)) {
            return true;
        }

        return stream(entities).noneMatch(predicate);
    }

    /**
     * 实体列表转换成数组
     *
     * @param entities
     * @param function
     * @param generator
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R[] mapToArray(Collection<T> entities, Function<T, R> function, IntFunction<R[]> generator) {
        if (isEmpty(entities)) {
            return null;
        }
        return stream(entities).map(function).toArray(generator);
    }

    /**
     * 实体列表转换成数组
     *
     * @param entities
     * @param predicate
     * @param function
     * @param generator
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R[] filterAndMapToArray(Collection<T> entities, Predicate<T> predicate,
                                                 Function<T, R> function,
                                                 IntFunction<R[]> generator) {
        if (isEmpty(entities)) {
            return null;
        }
        return stream(entities).filter(predicate).map(function).toArray(generator);
    }

    /**
     * int求和
     *
     * @param entities
     * @param function
     * @param <T>
     * @return
     */
    public static <T> int sumInt(Collection<T> entities, ToIntFunction<T> function) {
        if (isEmpty(entities)) {
            return 0;
        }

        return stream(entities).mapToInt(function).filter(Objects::nonNull).sum();
    }

    /**
     * int求和
     *
     * @param entities
     * @return
     */
    public static int sumInt(Collection<Integer> entities) {
        if (isEmpty(entities)) {
            return 0;
        }

        return stream(entities).reduce(0, Integer::sum);
    }

    /**
     * long求和
     *
     * @param entities
     * @param function
     * @param <T>
     * @return
     */
    public static <T> long sumLong(Collection<T> entities, ToLongFunction<T> function) {
        if (isEmpty(entities)) {
            return 0;
        }

        return stream(entities).mapToLong(function).filter(Objects::nonNull).sum();
    }

    /**
     * long求和
     *
     * @param entities
     * @return
     */
    public static long sumLong(Collection<Long> entities) {
        if (isEmpty(entities)) {
            return 0;
        }

        return stream(entities).reduce(0L, Long::sum);
    }

    /**
     * double求和
     *
     * @param entities
     * @param function
     * @param <T>
     * @return
     */
    public static <T> double sumDouble(Collection<T> entities, ToDoubleFunction<T> function) {
        if (isEmpty(entities)) {
            return 0;
        }

        return stream(entities).mapToDouble(function).filter(Objects::nonNull).sum();
    }

    /**
     * double求和
     *
     * @param entities
     * @return
     */
    public static double sumDouble(Collection<Double> entities) {
        if (isEmpty(entities)) {
            return 0;
        }

        return stream(entities).reduce(0.0, Double::sum);
    }

    /**
     * 过滤掉空值
     *
     * @param entities
     * @param <T>
     * @return
     */
    private static <T> Stream<T> stream(Collection<T> entities) {
        return entities.parallelStream().filter(Objects::nonNull);
    }

}