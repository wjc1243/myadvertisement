package com.wjc.ad.service.impl;

import com.wjc.ad.constant.Constants;
import com.wjc.ad.dao.AdCreativeRepository;
import com.wjc.ad.dao.AdPlanRepository;
import com.wjc.ad.dao.AdUnitRepository;
import com.wjc.ad.dao.unit_condition.AdCreativeUnitItRepository;
import com.wjc.ad.dao.unit_condition.AdUnitDistrictRepository;
import com.wjc.ad.dao.unit_condition.AdUnitItRepository;
import com.wjc.ad.dao.unit_condition.AdUnitKeywordRepository;
import com.wjc.ad.entity.AdPlan;
import com.wjc.ad.entity.AdUnit;
import com.wjc.ad.entity.unit_condition.AdCreativeUnit;
import com.wjc.ad.entity.unit_condition.AdUnitDistrict;
import com.wjc.ad.entity.unit_condition.AdUnitIt;
import com.wjc.ad.entity.unit_condition.AdUnitKeyword;
import com.wjc.ad.exception.AdException;
import com.wjc.ad.service.IAdUnitService;
import com.wjc.ad.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdUnitServiceImpl implements IAdUnitService {

    private final AdPlanRepository planRepository;
    private final AdUnitRepository unitRepository;
    private final AdUnitKeywordRepository keywordRepository;
    private final AdUnitItRepository itRepository;
    private final AdUnitDistrictRepository districtRepository;
    private final AdCreativeRepository creativeRepository;
    private final AdCreativeUnitItRepository creativeUnitItRepository;

    @Autowired
    public AdUnitServiceImpl(AdPlanRepository planRepository, AdUnitRepository unitRepository, AdUnitKeywordRepository keywordRepository,
                             AdUnitItRepository itRepository, AdUnitDistrictRepository districtRepository, AdCreativeRepository creativeRepository,
                             AdCreativeUnitItRepository creativeUnitItRepository) {
        this.planRepository = planRepository;
        this.unitRepository = unitRepository;
        this.keywordRepository = keywordRepository;
        this.itRepository = itRepository;
        this.districtRepository = districtRepository;
        this.creativeRepository = creativeRepository;
        this.creativeUnitItRepository = creativeUnitItRepository;
    }

    @Override
    public AdUnitResponse createUnit(AdUnitRequest request) throws AdException {
        if(!request.createValidate()){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        Optional<AdPlan> oldPlan = planRepository.findById(request.getPlanId());
        if(!oldPlan.isPresent()){
            throw new  AdException(Constants.ErrorMsg.CAN_NOT_FIND_RECORD);
        }
        AdUnit oldUnit = unitRepository.findByPlanIdAndUnitName(request.getPlanId(), request.getUnitName());
        if(oldUnit != null){
            throw new AdException(Constants.ErrorMsg.SAME_NAME_UNIT_ERROR);
        }
        AdUnit newUnit = unitRepository.save(new AdUnit(request.getPlanId(), request.getUnitName(), request.getPositionType(), request.getBudget()));
        return new AdUnitResponse(newUnit.getId(), newUnit.getUnitName());
    }

    @Override
    public AdUnitKeywordResponse createUnitKeyword(AdUnitKeywordRequest request) throws AdException {
        List<Long> unitIds = request.getUnitKeywords().stream()
                .map(AdUnitKeywordRequest.unitKeyword::getUnitId)
                .collect(Collectors.toList());
        if(!isRelatedUnitExist(unitIds)){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        List<Long> ids = Collections.emptyList();
        List<AdUnitKeyword> unitKeywords = new ArrayList<>();
        if(!CollectionUtils.isEmpty(request.getUnitKeywords())){
            request.getUnitKeywords().forEach((e) -> unitKeywords.add(new AdUnitKeyword(
                    e.getUnitId(), e.getKeyword()
            )));
            ids = keywordRepository.saveAll(unitKeywords).stream()
                    .map(AdUnitKeyword::getId)
                    .collect(Collectors.toList());
        }
        return new AdUnitKeywordResponse(ids);
    }

    @Override
    public AdUnitItResponse createUnitIt(AdUnitItRequest request) throws AdException {
        List<Long> unitIds = request.getUnitIts().stream()
                .map(AdUnitItRequest.unitIt::getUnitId)
                .collect(Collectors.toList());
        if(!isRelatedUnitExist(unitIds)){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        List<AdUnitIt> unitIts = new ArrayList<>();
        request.getUnitIts().forEach((e) -> unitIts.add(
                new AdUnitIt(e.getUnitId(), e.getItTag())
        ));
        List<Long> ids = itRepository.saveAll(unitIts).stream()
                .map(AdUnitIt::getId)
                .collect(Collectors.toList());
        return new AdUnitItResponse(ids);
    }

    @Override
    public AdUnitDistrictResponse createUnitDistrict(AdUnitDistrictRequest request) throws AdException {
        List<Long> unitIds = request.getUnitDistricts().stream()
                .map(AdUnitDistrictRequest.unitDistrict::getUnitId)
                .collect(Collectors.toList());
        if(!isRelatedUnitExist(unitIds)){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        List<AdUnitDistrict> unitDistricts = new ArrayList<>();
        request.getUnitDistricts().forEach((e) -> unitDistricts.add(
                new AdUnitDistrict(e.getUnitId(), e.getProvince(), e.getCity())
        ));
        List<Long> ids = districtRepository.saveAll(unitDistricts).stream()
                .map(AdUnitDistrict::getId)
                .collect(Collectors.toList());
        return new AdUnitDistrictResponse(ids);
    }

    @Override
    public CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request) throws AdException {
        List<Long> unitIds = request.getUnitItems().stream()
                .map(CreativeUnitRequest.creativeUnitItem::getUnitId)
                .collect(Collectors.toList());
        List<Long> creativeIds = request.getUnitItems().stream()
                .map(CreativeUnitRequest.creativeUnitItem::getCreativeId)
                .collect(Collectors.toList());
        if(isRelatedUnitExist(unitIds) || isRelatedCreativeExist(creativeIds)){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        List<AdCreativeUnit> creativeUnits = new ArrayList<>();
        request.getUnitItems().forEach((e) -> creativeUnits.add(
                new AdCreativeUnit(e.getCreativeId(), e.getUnitId())
        ));
        List<Long> ids = creativeUnitItRepository.saveAll(creativeUnits).stream()
                .map(AdCreativeUnit::getId)
                .collect(Collectors.toList());
        return new CreativeUnitResponse(ids);
    }

    private boolean isRelatedUnitExist(List<Long> unitIds){
        if(CollectionUtils.isEmpty(unitIds)){
            return false;
        }
        return unitRepository.findAllById(unitIds).size() == new HashSet<>(unitIds).size();
    }

    private boolean isRelatedCreativeExist(List<Long> creativeIds){
        if(CollectionUtils.isEmpty(creativeIds)){
            return false;
        }
        return creativeRepository.findAllById(creativeIds).size() == new HashSet<>(creativeIds).size();
    }
}
