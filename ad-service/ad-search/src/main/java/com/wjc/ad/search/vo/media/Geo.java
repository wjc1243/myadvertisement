package com.wjc.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geo {

    //纬度
    private Float latitude;
    //经度
    private Float longitude;
    //省
    private String province;
    //市
    private String city;
}
