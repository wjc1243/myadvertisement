package com.wjc.ad.constant;

import lombok.Getter;

@Getter
public enum CreativeType {
    IMAGE(1, "图片"),
    VIDEO(2, "视频"),
    TEXT(3, "文本");

    private Integer status;
    private String desc;

    CreativeType(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
