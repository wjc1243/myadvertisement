package com.wjc.ad.search.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.wjc.ad.index.CommonStatus;
import com.wjc.ad.index.DataTable;
import com.wjc.ad.index.adUnit.AdUnitIndex;
import com.wjc.ad.index.adUnit.AdUnitObject;
import com.wjc.ad.index.creative.CreativeIndex;
import com.wjc.ad.index.creative.CreativeObject;
import com.wjc.ad.index.creativeunit.CreativeUnitIndex;
import com.wjc.ad.index.district.UnitDistrictIndex;
import com.wjc.ad.index.it.UnitItIndex;
import com.wjc.ad.index.keyword.UnitKeywordIndex;
import com.wjc.ad.search.ISearch;
import com.wjc.ad.search.vo.SearchRequest;
import com.wjc.ad.search.vo.SearchResponse;
import com.wjc.ad.search.vo.feature.DistrictFeature;
import com.wjc.ad.search.vo.feature.FeatureRelation;
import com.wjc.ad.search.vo.feature.ItFeature;
import com.wjc.ad.search.vo.feature.KeywordFeature;
import com.wjc.ad.search.vo.media.AdSlot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SearchImpl implements ISearch {

    public SearchResponse fallback(SearchRequest request, Throwable e){
        return null;
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallback")
    public SearchResponse fetchAds(SearchRequest request) {
        //获取请求的广告位信息
        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();
        //三个feature
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        ItFeature itFeature = request.getFeatureInfo().getItFeature();
        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();

        FeatureRelation relation = request.getFeatureInfo().getRelation();

        //构造响应对象
        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlot2Ads = response.getAdSlot2Ads();
        for (AdSlot adSlot : adSlots) {
            Set<Long> targetUnitIdSet;
            //根据流量类型获取初始的AdUnit
            Set<Long> adUnitIdSet = DataTable.of(AdUnitIndex.class).match(adSlot.getPositionType());

            if(relation == FeatureRelation.AND){
                filterKeywordFeature(adUnitIdSet, keywordFeature);
                filterDistrictFeature(adUnitIdSet, districtFeature);
                filterItTagFeature(adUnitIdSet, itFeature);

                targetUnitIdSet = adUnitIdSet;
            }else{
                targetUnitIdSet = getOrRelationUnitIds(adUnitIdSet, keywordFeature, districtFeature, itFeature);
            }
            List<AdUnitObject> unitObjects = DataTable.of(AdUnitIndex.class).fetch(targetUnitIdSet);
            filterAdUnitAndPlanStatus(unitObjects, CommonStatus.VALID);

            List<Long> adIds = DataTable.of(CreativeUnitIndex.class).selectIds(unitObjects);
            List<CreativeObject> creatives = DataTable.of(CreativeIndex.class).fetch(adIds);

            //通过AdSlot实现对CreativeObject的过滤
            filterCreativeByAdSlot(creatives, adSlot.getWidth(), adSlot.getHeight(), adSlot.getType());

            adSlot2Ads.put(adSlot.getAdSlotCode(), buildCreativeResponse(creatives));
        }
        log.info("fetchAds: {}-{}", JSON.toJSONString(request), JSON.toJSONString(response));
        return response;
    }

    private Set<Long> getOrRelationUnitIds(Set<Long> adUnitIdSet, KeywordFeature keywordFeature, DistrictFeature districtFeature, ItFeature itFeature){
        if(CollectionUtils.isEmpty(adUnitIdSet)){
            return Collections.emptySet();
        }
        Set<Long> kewordUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> districtUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> itUnitIdSet = new HashSet<>(adUnitIdSet);

        filterKeywordFeature(kewordUnitIdSet, keywordFeature);
        filterDistrictFeature(districtUnitIdSet, districtFeature);
        filterItTagFeature(itUnitIdSet, itFeature);

        return new HashSet<>(CollectionUtils.union(CollectionUtils.union(kewordUnitIdSet, districtUnitIdSet), itUnitIdSet));
    }

    private void filterKeywordFeature(Collection<Long> adUnitIds, KeywordFeature keywordFeature){
        if(CollectionUtils.isEmpty(adUnitIds)){
            return;
        }
        if(CollectionUtils.isNotEmpty(keywordFeature.getKeywords())){
            CollectionUtils.filter(adUnitIds, (adUnitId) -> DataTable.of(UnitKeywordIndex.class).match(adUnitId, keywordFeature.getKeywords()));
        }
    }

    private void filterDistrictFeature(Collection<Long> adUnitIds, DistrictFeature districtFeature){
        if(CollectionUtils.isEmpty(adUnitIds)){
            return;
        }
        if(CollectionUtils.isNotEmpty(districtFeature.getDistricts())){
            CollectionUtils.filter(adUnitIds, (adUnitId) -> DataTable.of(UnitDistrictIndex.class).match(adUnitId, districtFeature.getDistricts()));
        }
    }

    private void filterItTagFeature(Collection<Long> adUnitIds, ItFeature itFeature){
        if(CollectionUtils.isEmpty(adUnitIds)){
            return;
        }
        if(CollectionUtils.isNotEmpty(itFeature.getIts())){
            CollectionUtils.filter(adUnitIds, (adUnitId) -> DataTable.of(UnitItIndex.class).match(adUnitId, itFeature.getIts()));
        }
    }

    private void filterAdUnitAndPlanStatus(List<AdUnitObject> unitObjects, CommonStatus status){
        if(CollectionUtils.isEmpty(unitObjects)){
            return;
        }
        CollectionUtils.filter(unitObjects, (u) -> u.getUnitStatus().equals(status.getStatus())
                && u.getAdPlanObject().getPlanStatus().equals(status.getStatus()));
    }

    private void filterCreativeByAdSlot(List<CreativeObject> creatives, Integer width, Integer height, List<Integer> type){
        if(CollectionUtils.isEmpty(creatives)){
            return;
        }
        CollectionUtils.filter(creatives,
                (c) -> c.getAuditStatus().equals(CommonStatus.VALID.getStatus()) && c.getWidth().equals(width)
                        && c.getHeight().equals(height) && type.contains(c.getType()));
    }

    private List<SearchResponse.Creative> buildCreativeResponse(List<CreativeObject> creativses){
        if(CollectionUtils.isEmpty(creativses)){
            return Collections.emptyList();
        }
        CreativeObject randomObject = creativses.get(Math.abs(new Random().nextInt()) % creativses.size());
        return Collections.singletonList(SearchResponse.convert(randomObject));
    }
}
