package com.wjc.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class App {

    //应用编码
    private String appCode;
    //应用名称
    private String appName;
    //应用包名
    private String packageName;
    //页面名称
    private String activityName;
}
