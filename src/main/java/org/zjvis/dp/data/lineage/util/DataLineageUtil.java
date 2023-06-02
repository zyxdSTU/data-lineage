package org.zjvis.dp.data.lineage.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouyu
 * @create 2023-04-25 11:06
 */
public class DataLineageUtil {
    /**
     * 对边里面的任务id进行去重
     * @param keyExtractor
     * @return
     * @param <T>
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 判断是不是 'ab' "a, b"
     * @param name
     * @return
     */
    public static boolean isString(String name) {
        if(StringUtils.isEmpty(name)) {
            return false;
        }

        char startChar = name.charAt(0);
        char endChar = name.charAt(name.length() - 1);

        if(startChar == '"' && endChar == '"') {
            return true;
        }

        if(startChar == '\'' && endChar == '\'') {
            return true;
        }

        return false;
    }
}
