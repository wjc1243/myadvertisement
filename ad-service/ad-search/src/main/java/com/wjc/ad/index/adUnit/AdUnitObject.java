package com.wjc.ad.index.adUnit;

import com.wjc.ad.index.adPlan.AdPlanObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitObject {
    private Long unitId;
    private Integer unitStatus;
    private Integer positionType;
    private Long planId;

    private AdPlanObject adPlanObject;

    void update(AdUnitObject newObject){
        if(null != newObject.getUnitId()){
            this.unitId = newObject.getUnitId();
        }
        if(null != newObject.getUnitStatus()){
            this.unitStatus = newObject.getUnitStatus();
        }
        if(null != newObject.getPositionType()){
            this.positionType = newObject.getPositionType();
        }
        if(null != newObject.getPlanId()){
            this.planId = newObject.getPlanId();
        }
        if(null != newObject.getAdPlanObject()){
            this.adPlanObject = newObject.getAdPlanObject();
        }
    }

    private static boolean isKaiPing(int positionType){
        //"&" 运算符如果大于0则匹配
        return (positionType & AdUnitConstants.POSITION_TYPE.KAIPING) > 0;
    }
    private static boolean isTiePian(int positionType){
        //"&" 运算符如果大于0则匹配
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN) > 0;
    }
    private static boolean isTiePianMiddle(int positionType){
        //"&" 运算符如果大于0则匹配
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_MIDDLE) > 0;
    }
    private static boolean isTiePianPause(int positionType){
        //"&" 运算符如果大于0则匹配
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_PAUSE) > 0;
    }
    private static boolean isTiePianPost(int positionType){
        //"&" 运算符如果大于0则匹配
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_POST) > 0;
    }

    public static boolean isAdSlotTypeOK(int adSlotType, int positionType){
        switch (adSlotType){
            case AdUnitConstants.POSITION_TYPE.KAIPING:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN:
                return isTiePian(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_MIDDLE:
                return isTiePianMiddle(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_PAUSE:
                return isTiePianPause(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_POST:
                return isTiePianPost(positionType);
            default:
                return false;
        }
    }
}
