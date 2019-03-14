package com.wjc.ad.index.adUnit;

import com.wjc.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdUnitIndex implements IndexAware<Long, AdUnitObject> {

    private static Map<Long, AdUnitObject> objectMap;
    static {
        objectMap = new ConcurrentHashMap<>();
    }

    public Set<Long> match(Integer positionType){
        Set<Long> adUnitIds = new HashSet<>();
        objectMap.forEach((k, v) -> {
            if(AdUnitObject.isAdSlotTypeOK(positionType, v.getPositionType())){
                adUnitIds.add(k);
            }
        });
        return adUnitIds;
    }

    public List<AdUnitObject> fetch(Collection<Long> adunitIds){
        if(CollectionUtils.isEmpty(adunitIds)){
            return Collections.emptyList();
        }
        List<AdUnitObject> result = new ArrayList<>();

        adunitIds.forEach((u) -> {
            AdUnitObject object = get(u);
            if(null == object){
                log.error("AdUnitObject not found: {}", u);
                return;
            }
            result.add(object);
        });
        return result;
    }

    @Override
    public AdUnitObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdUnitObject value) {
        log.info("before add: {}", objectMap);
        objectMap.put(key, value);
        log.info("after add: {}", objectMap);
    }

    @Override
    public void update(Long key, AdUnitObject value) {
        log.info("before update: {}", objectMap);
        AdUnitObject oldObject = objectMap.get(key);
        if(null == oldObject){
            objectMap.put(key, value);
        }else{
            oldObject.update(value);
        }
        log.info("after update: {}", objectMap);
    }

    @Override
    public void delete(Long key, AdUnitObject value) {
        log.info("before delete: {}", objectMap);
        objectMap.remove(key);
        log.info("after delete: {}", objectMap);
    }
}
