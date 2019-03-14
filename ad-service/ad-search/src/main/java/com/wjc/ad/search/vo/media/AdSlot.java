package com.wjc.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 广告位
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSlot {

    //广告位编码
    private String adSlotCode;
    //流量类型
    private Integer positionType;
    //广告位尺寸
    private Integer width;
    private Integer height;
    //广告位物料类型图片 视频等等
    private List<Integer> type;
    //广告位出价
    private Integer minCpm;
}
