package com.wjc.ad.controller;

import com.alibaba.fastjson.JSON;
import com.wjc.ad.exception.AdException;
import com.wjc.ad.service.ICreativeService;
import com.wjc.ad.vo.AdCreativeRequest;
import com.wjc.ad.vo.AdCreativeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CreativeOPController {

    private final ICreativeService creativeService;

    @Autowired
    public CreativeOPController(ICreativeService creativeService) {
        this.creativeService = creativeService;
    }

    @PostMapping("/create/creative")
    public AdCreativeResponse createCreative(@RequestBody AdCreativeRequest request) throws AdException {
        log.info("ad-sponsor: createCreative -> {}", JSON.toJSONString(request));
        return creativeService.createCreative(request);
    }
}
