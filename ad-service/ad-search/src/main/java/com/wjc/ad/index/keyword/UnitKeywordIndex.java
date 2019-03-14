package com.wjc.ad.index.keyword;

import com.wjc.ad.index.IndexAware;
import com.wjc.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Component
public class UnitKeywordIndex implements IndexAware<String, Set<Long>> {

    private static Map<String, Set<Long>> keywordUnitMap;
    private static Map<Long, Set<String>> unitKeywordMap;

    static {
        keywordUnitMap = new ConcurrentHashMap<>();
        unitKeywordMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if(StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }
        Set<Long> result = keywordUnitMap.get(key);
        if(result == null){
            return Collections.emptySet();
        }
        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("unitKeywordMap before add: {}", unitKeywordMap);
        Set<Long> unitIdSet = CommonUtils.getOrCreate(key, keywordUnitMap, ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);
        for (Long unitId : value) {
            Set<String> keywordSet = CommonUtils.getOrCreate(unitId, unitKeywordMap, ConcurrentSkipListSet::new);
            keywordSet.add(key);
        }
        log.info("unitKeywordMap after add: {}", unitKeywordMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("不能更新！");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("unitKeywordMap before delete: {}", unitKeywordMap);
        Set<Long> unitIds = CommonUtils.getOrCreate(key, keywordUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);
        for (Long unitId : value) {
            Set<String> keywordSet = CommonUtils.getOrCreate(unitId, unitKeywordMap, ConcurrentSkipListSet::new);
            keywordSet.remove(key);
        }
        log.info("unitKeywordMap after delete: {}", unitKeywordMap);
    }

    public boolean match(Long unitId, List<String> keywords){
        if(unitKeywordMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitKeywordMap.get(unitId))){
            Set<String> unitKeywords = unitKeywordMap.get(unitId);
            return CollectionUtils.isSubCollection(keywords, unitKeywords);
        }
        return false;
    }
}
