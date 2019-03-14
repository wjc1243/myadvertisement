package com.wjc.ad.index.creative;

import com.wjc.ad.index.adPlan.AdPlanObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeObject {
    private Long adId;
    private String name;
    private Integer type;
    private Integer materialType;
    private Integer height;
    private Integer width;
    private Integer auditStatus;
    private String adUrl;

    void update(CreativeObject object){
        if(null != object.getAdId()){
            this.adId = object.getAdId();
        }
        if(null != object.getName()){
            this.name = object.getName();
        }
        if(null != object.getType()){
            this.type = object.getType();
        }
        if(null != object.getMaterialType()){
            this.materialType = object.getMaterialType();
        }
        if(null != object.getHeight()){
            this.height = object.getHeight();
        }
        if(null != object.getWidth()){
            this.width = object.getWidth();
        }
        if(null != object.getAuditStatus()){
            this.auditStatus = object.getAuditStatus();
        }
        if(null != object.getAdUrl()){
            this.adUrl = object.getAdUrl();
        }
    }
}
