package com.wjc.ad.constant;

import lombok.Getter;

@Getter
public enum CreativeMetrialType {
    JPG(1, "jpg"),
    BMP(2, "bmp"),

    MP4(3, "mp4"),
    AVI(4, "avi"),

    TXT(5, "txt");

    private Integer status;
    private String desc;

    CreativeMetrialType(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
