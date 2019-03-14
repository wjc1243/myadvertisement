package com.wjc.ad.service;

import com.wjc.ad.entity.AdPlan;
import com.wjc.ad.exception.AdException;
import com.wjc.ad.vo.AdPlanGetRequest;
import com.wjc.ad.vo.AdPlanRequest;
import com.wjc.ad.vo.AdPlanResponse;

import java.util.List;

public interface IAdPlanService {
    AdPlan get();

    AdPlanResponse createAdPlan(AdPlanRequest adPlanRequest) throws AdException;

    List<AdPlan> getAdPlanByIds(AdPlanGetRequest adPlanGetRequest) throws AdException;

    AdPlanResponse updateAdPlan(AdPlanRequest adPlanRequest) throws AdException;

    void deleteAdPlan(AdPlanRequest adPlanRequest) throws AdException;
}
