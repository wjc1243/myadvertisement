package com.wjc.ad.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class CommonUtils {

    /**
     * 如果穿传进来的map不存在key， 则通过factory返回一个新生成的V对象
     * @param key
     * @param map
     * @param factory
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V getOrCreate(K key, Map<K, V> map, Supplier<V> factory){
        return map.computeIfAbsent(key, (e) -> factory.get());
    }

    public static String stringConcat(String... args){
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append("-");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    //Wed Mar 13 23:53:08 CST 2019
    public static Date parseStringDate(String dateString){
        try {
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            return DateUtils.addHours(dateFormat.parse(dateString), -8);
        }catch (ParseException e){
            log.error("parse stringdate error: {}", dateString);
            return null;
        }
    }
}
