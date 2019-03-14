package com.wjc.ad.index.creativeunit;

import com.wjc.ad.index.IndexAware;
import com.wjc.ad.index.adUnit.AdUnitObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Component
public class CreativeUnitIndex implements IndexAware<String, CreativeUnitObject> {

    //<adId-unitId, CreativeUnitObject>
    private static Map<String, CreativeUnitObject> objectMap;
    //<adId, unitIdSet>
    private static Map<Long, Set<Long>> creativeUnitMap;
    //<unitId, adIdSet>
    private static Map<Long, Set<Long>> unitCreativeMap;

    static {
        objectMap = new ConcurrentHashMap<>();
        creativeUnitMap = new ConcurrentHashMap<>();
        unitCreativeMap = new ConcurrentHashMap<>();
    }

    @Override
    public CreativeUnitObject get(String key) {
        return objectMap.get(key);
    }

    @Override
    public void add(String key, CreativeUnitObject value) {
        log.info("before add: {}", objectMap);
        objectMap.put(key, value);
        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if(CollectionUtils.isEmpty(unitSet)){
            unitSet = new ConcurrentSkipListSet<>();
            creativeUnitMap.put(value.getAdId(), unitSet);
        }
        unitSet.add(value.getUnitId());
        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if(CollectionUtils.isEmpty(creativeSet)){
            creativeSet = new ConcurrentSkipListSet<>();
            unitCreativeMap.put(value.getUnitId(), creativeSet);
        }
        creativeSet.add(value.getAdId());
        log.info("after add: {}", objectMap);
    }

    @Override
    public void update(String key, CreativeUnitObject value) {
        log.error("CreativeUnitIndex Not Support Update");
    }

    @Override
    public void delete(String key, CreativeUnitObject value) {
        log.info("before delete: {}", objectMap);
        objectMap.remove(key);
        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if(!CollectionUtils.isEmpty(unitSet)){
            unitSet.remove(value.getUnitId());
        }
        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if(!CollectionUtils.isEmpty(creativeSet)){
            creativeSet.remove(value.getAdId());
        }
        log.info("after delete: {}", objectMap);
    }

    public List<Long> selectIds(List<AdUnitObject> unitObjects){
        if(org.apache.commons.collections4.CollectionUtils.isEmpty(unitObjects)){
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        for (AdUnitObject unitObject : unitObjects) {
            Set<Long> adIds = unitCreativeMap.get(unitObject.getUnitId());
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(adIds)){
                result.addAll(adIds);
            }
        }
        return result;
    }
}
