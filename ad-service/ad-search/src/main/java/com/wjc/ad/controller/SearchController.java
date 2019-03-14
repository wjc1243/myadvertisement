package com.wjc.ad.controller;

import com.alibaba.fastjson.JSON;
import com.wjc.ad.annotation.IgnoreResponseAdvice;
import com.wjc.ad.client.SponsorClient;
import com.wjc.ad.client.vo.AdPlan;
import com.wjc.ad.client.vo.AdPlanGetRequest;
import com.wjc.ad.search.ISearch;
import com.wjc.ad.search.vo.SearchRequest;
import com.wjc.ad.search.vo.SearchResponse;
import com.wjc.ad.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
public class SearchController {

    private final ISearch search;
    private final RestTemplate restTemplate;
    private final SponsorClient sponsorClient;

    @Autowired
    public SearchController(RestTemplate restTemplate, SponsorClient sponsorClient, ISearch search) {
        this.restTemplate = restTemplate;
        this.sponsorClient = sponsorClient;
        this.search = search;
    }

    @PostMapping("/fetchAds")
    public SearchResponse fetchAds(@RequestBody SearchRequest request){
        log.info("ad-search: fetchAds -> {}", JSON.toJSONString(request));
        return search.fetchAds(request);
    }

    @SuppressWarnings("all")
    @IgnoreResponseAdvice
    @PostMapping("/getAdPlansByRibbon")
    public CommonResponse<List<AdPlan>> getAdPlansByRibbon(@RequestBody AdPlanGetRequest request){
        log.info("ad-search: getAdPlansByRibbon -> {}", JSON.toJSONString(request));
        return restTemplate.postForEntity("http://ad-sponsor/ad-sponsor/get/adPlan", request, CommonResponse.class).getBody();
    }

    @IgnoreResponseAdvice
    @PostMapping("/getAdPlans")
    public CommonResponse<List<AdPlan>> getAdPlans(@RequestBody AdPlanGetRequest request){
        log.info("ad-search: getAdPlans -> {}", JSON.toJSONString(request));
        return sponsorClient.getAdPlans(request);
    }

}
