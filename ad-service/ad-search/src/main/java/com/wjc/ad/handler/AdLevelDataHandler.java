package com.wjc.ad.handler;

import com.alibaba.fastjson.JSON;
import com.wjc.ad.dump.table.*;
import com.wjc.ad.index.DataTable;
import com.wjc.ad.index.IndexAware;
import com.wjc.ad.index.adPlan.AdPlanIndex;
import com.wjc.ad.index.adPlan.AdPlanObject;
import com.wjc.ad.index.adUnit.AdUnitIndex;
import com.wjc.ad.index.adUnit.AdUnitObject;
import com.wjc.ad.index.creative.CreativeIndex;
import com.wjc.ad.index.creative.CreativeObject;
import com.wjc.ad.index.creativeunit.CreativeUnitIndex;
import com.wjc.ad.index.creativeunit.CreativeUnitObject;
import com.wjc.ad.index.district.UnitDistrictIndex;
import com.wjc.ad.index.it.UnitItIndex;
import com.wjc.ad.index.keyword.UnitKeywordIndex;
import com.wjc.ad.utils.CommonUtils;
import com.wjc.ad.mysql.constant.OpType;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class AdLevelDataHandler {

    public static void handleLevel2(AdPlanTable planTable, OpType type){
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(), planTable.getUserId(), planTable.getPlanStatus(), planTable.getStartDate(), planTable.getEndDate()
        );
        handleBinlogEvent(DataTable.of(AdPlanIndex.class), planObject.getPlanId(), planObject, type);
    }

    public static void handleLevel2(AdCreativeTable creativeTable, OpType type){
        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(), creativeTable.getName(), creativeTable.getType(), creativeTable.getMaterialType(),
                creativeTable.getHeight(), creativeTable.getWidth(), creativeTable.getAuditStatus(), creativeTable.getAdUrl()
        );
        handleBinlogEvent(DataTable.of(CreativeIndex.class), creativeObject.getAdId(), creativeObject, type);
    }

    public static void handleLevel3(AdUnitTable unitTable, OpType type){
        AdPlanObject adPlanObject = DataTable.of(AdPlanIndex.class).get(unitTable.getPlanId());
        if(null == adPlanObject){
            log.error("handleLevel3 Found AdPlanObject error: {}", unitTable.getPlanId());
            return;
        }
        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(), unitTable.getUnitStatus(), unitTable.getPositionType(), unitTable.getPlanId(), adPlanObject
        );
        handleBinlogEvent(DataTable.of(AdUnitIndex.class), unitTable.getUnitId(), unitObject, type);
    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("creativeUnitIndex not support update");
        }
        AdUnitObject unitObject = DataTable.of(AdUnitIndex.class).get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = DataTable.of(CreativeIndex.class).get(creativeUnitTable.getAdId());
        if(unitObject == null || creativeObject == null){
            log.error("AdCreativeUnitTable index error: {}", JSON.toJSONString(creativeUnitTable));
            return;
        }
        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(creativeUnitTable.getAdId(), creativeUnitTable.getUnitId());
        handleBinlogEvent(DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(creativeUnitObject.getAdId().toString(), creativeUnitObject.getUnitId().toString()),
                creativeUnitObject, type);
    }

    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("AdUnitDistrictIndex not support update");
            return;
        }
        AdUnitObject unitObject = DataTable.of(AdUnitIndex.class).get(unitDistrictTable.getUnitId());
        if(unitObject == null){
            log.error("AdUnitDistrictTable index error: {}", unitDistrictTable.getUnitId());
            return;
        }
        String key = CommonUtils.stringConcat(unitDistrictTable.getProvince(), unitDistrictTable.getCity());
        Set<Long> value = new HashSet<>(Collections.singleton(unitDistrictTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), key, value, type);
    }

    public static void handleLevel4(AdUnitItTable unitItTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("AdUnitItIndex not support update");
            return;
        }
        AdUnitObject unitObject = DataTable.of(AdUnitIndex.class).get(unitItTable.getUnitId());
        if(unitObject == null){
            log.error("AdUnitItTable index error: {}", unitItTable.getUnitId());
            return;
        }
        Set<Long> value = new HashSet<>(Collections.singleton(unitItTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitItIndex.class), unitItTable.getItTag(), value, type);
    }

    public static void handleLevel4(AdUnitKeywordTable unitKeywordTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("AdUnitKeywordIndex not support update");
            return;
        }
        AdUnitObject unitObject = DataTable.of(AdUnitIndex.class).get(unitKeywordTable.getUnitId());
        if(unitObject == null){
            log.error("AdUnitKeywordTable index error: {}", unitKeywordTable.getUnitId());
            return;
        }
        Set<Long> value = new HashSet<>(Collections.singleton(unitKeywordTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitKeywordIndex.class), unitKeywordTable.getKeyword(), value, type);
    }

    private static <K, V> void handleBinlogEvent(IndexAware<K, V> index, K key, V value, OpType type){
        switch (type){
            case ADD: index.add(key, value); break;
            case UPDATE: index.update(key, value); break;
            case DELETE: index.delete(key, value); break;
            default: break;
        }
    }
}
