package com.wjc.ad.index.district;

import com.wjc.ad.index.IndexAware;
import com.wjc.ad.search.vo.feature.DistrictFeature;
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
import java.util.stream.Collectors;

@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {

    //<province-city, adUnitIdSet>
    private static Map<String, Set<Long>> districtUnitMap;
    //<unitId, province-citySet>
    private static Map<Long, Set<String>> unitDistrictMap;

    static {
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if(StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }
        Set<Long> result = districtUnitMap.get(key);
        if(result == null){
            return Collections.emptySet();
        }
        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("unitDistrictMap before add: {}", unitDistrictMap);
        Set<Long> unitIdSet = CommonUtils.getOrCreate(key, districtUnitMap, ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);
        for (Long unitId : value) {
            Set<String> districtSet = CommonUtils.getOrCreate(unitId, unitDistrictMap, ConcurrentSkipListSet::new);
            districtSet.add(key);
        }
        log.info("unitDistrictMap after add: {}", unitDistrictMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("不能更新！");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("unitDistrictMap before delete: {}", unitDistrictMap);
        Set<Long> unitIds = CommonUtils.getOrCreate(key, districtUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);
        for (Long unitId : value) {
            Set<String> districtSet = CommonUtils.getOrCreate(unitId, unitDistrictMap, ConcurrentSkipListSet::new);
            districtSet.remove(key);
        }
        log.info("unitDistrictMap after delete: {}", unitDistrictMap);
    }

    public boolean match(Long unitId, List<DistrictFeature.ProvinceAndCity> districts){
        if(unitDistrictMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitDistrictMap.get(unitId))){
            Set<String> unitDistricts = unitDistrictMap.get(unitId);

            List<String> targetDistrict = districts.stream()
                    .map((d) -> CommonUtils.stringConcat(d.getProvince(), d.getCity()))
                    .collect(Collectors.toList());
            return CollectionUtils.isSubCollection(targetDistrict, unitDistricts);
        }
        return false;
    }
}
