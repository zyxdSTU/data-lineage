package org.zjvis.dp.data.lineage.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.zjvis.dp.data.lineage.exception.DataLineageException;

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


    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = Lists.newArrayList();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add(clazz.cast(o));
            }
            return result;
        } else {
            throw  new DataLineageException("cast type failed, pls check");
        }
    }
}
