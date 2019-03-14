package com.wjc.ad.index.it;

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
public class UnitItIndex implements IndexAware<String, Set<Long>> {

    //<itTag, adUnitIdSet>
    private static Map<String, Set<Long>> itUnitMap;
    //<unitId, itTagSet>
    private static Map<Long, Set<String>> unitItMap;

    static {
        itUnitMap = new ConcurrentHashMap<>();
        unitItMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if(StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }
        Set<Long> result = itUnitMap.get(key);
        if(result == null){
            return Collections.emptySet();
        }
        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("unitItMap before add: {}", unitItMap);
        Set<Long> unitIdSet = CommonUtils.getOrCreate(key, itUnitMap, ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);
        for (Long unitId : value) {
            Set<String> itSet = CommonUtils.getOrCreate(unitId, unitItMap, ConcurrentSkipListSet::new);
            itSet.add(key);
        }
        log.info("unitItMap after add: {}", unitItMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("不能更新！");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("unitItMap before delete: {}", unitItMap);
        Set<Long> unitIds = CommonUtils.getOrCreate(key, itUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);
        for (Long unitId : value) {
            Set<String> itSet = CommonUtils.getOrCreate(unitId, unitItMap, ConcurrentSkipListSet::new);
            itSet.remove(key);
        }
        log.info("unitItMap after delete: {}", unitItMap);
    }

    public boolean match(Long unitId, List<String> its){
        if(unitItMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitItMap.get(unitId))){
            Set<String> unitits = unitItMap.get(unitId);
            return CollectionUtils.isSubCollection(its, unitits);
        }
        return false;
    }
}
