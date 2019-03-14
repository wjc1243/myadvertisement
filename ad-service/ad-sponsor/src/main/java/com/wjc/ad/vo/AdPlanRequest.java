package com.wjc.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanRequest {

    private Long id;
    private Long userId;
    private String planName;
    private String startDate;
    private String endDate;

    public boolean createValidate(){
        return userId != null
                && StringUtils.isNotEmpty(planName)
                && StringUtils.isNotEmpty(startDate)
                && StringUtils.isNotEmpty(endDate);
    }
    public boolean updateValidate(){
        return userId != null && id != null;
    }

    public boolean deledeValidate(){
        return id != null && userId != null;
    }

}
