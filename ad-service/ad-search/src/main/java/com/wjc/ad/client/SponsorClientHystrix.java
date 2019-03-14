package com.wjc.ad.client;

import com.wjc.ad.client.vo.AdPlan;
import com.wjc.ad.client.vo.AdPlanGetRequest;
import com.wjc.ad.vo.CommonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SponsorClientHystrix implements SponsorClient {
    @Override
    public CommonResponse<List<AdPlan>> getAdPlans(AdPlanGetRequest request) {
        return new CommonResponse<>(-1, "ad-sponsor error");
    }
}
