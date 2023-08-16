package com.jkqj.dubbo.generic;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * dubbo泛化调用结果会携带类名信息，需要递归清除，清除工具
 *
 * @author rolandhe
 *
 */
public class ResultPure {
    private ResultPure() {
    }

    public static void pureResult(Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Map) {
            Map map = (Map) value;
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getKey() instanceof String && entry.getKey().equals("class")) {
                    iterator.remove();
                    continue;
                }
                pureResult(entry.getValue());
            }
        }
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            for (Object line : collection) {
                pureResult(line);
            }
        }
    }
}
