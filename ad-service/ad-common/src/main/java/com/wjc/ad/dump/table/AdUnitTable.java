package com.wjc.ad.dump.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitTable {
    private Long unitId;
    private Integer unitStatus;
    private Integer positionType;
    private Long planId;
}
