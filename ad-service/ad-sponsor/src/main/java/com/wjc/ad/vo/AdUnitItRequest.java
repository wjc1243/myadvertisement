package com.wjc.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitItRequest {

    private List<unitIt> unitIts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class unitIt{
        private Long unitId;
        private String itTag;
    }
}
