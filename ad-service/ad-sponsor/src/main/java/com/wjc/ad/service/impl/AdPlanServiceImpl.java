package com.wjc.ad.service.impl;

import com.wjc.ad.constant.CommonStatus;
import com.wjc.ad.constant.Constants;
import com.wjc.ad.dao.AdPlanRepository;
import com.wjc.ad.dao.AdUserRepository;
import com.wjc.ad.entity.AdPlan;
import com.wjc.ad.entity.AdUser;
import com.wjc.ad.exception.AdException;
import com.wjc.ad.service.IAdPlanService;
import com.wjc.ad.utils.CommonUtils;
import com.wjc.ad.vo.AdPlanGetRequest;
import com.wjc.ad.vo.AdPlanRequest;
import com.wjc.ad.vo.AdPlanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AdPlanServiceImpl implements IAdPlanService {

    private final AdUserRepository userRepository;
    private final AdPlanRepository planRepository;

    @Autowired
    public AdPlanServiceImpl(AdUserRepository userRepository, AdPlanRepository planRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    @Override
    public AdPlan get() {
        return planRepository.findByIdAndUserId(1L, 1L);
    }

    @Override
    @Transactional
    public AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException {
        if(!request.createValidate()){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        Optional<AdUser> aduser = userRepository.findById(request.getUserId());
        if(!aduser.isPresent()){
            throw new AdException(Constants.ErrorMsg.CAN_NOT_FIND_RECORD);
        }
        AdPlan oldPlan = planRepository.findByUserIdAndPlanName(request.getUserId(), request.getPlanName());
        if(oldPlan != null){
            throw new AdException(Constants.ErrorMsg.SAME_NAME_PLAN_ERROR);
        }
        AdPlan newAddPlan = planRepository.save(new AdPlan(request.getUserId(), request.getPlanName(),
                CommonUtils.parseStringDate(request.getStartDate()), CommonUtils.parseStringDate(request.getEndDate())));
        return new AdPlanResponse(newAddPlan.getId(), newAddPlan.getPlanName());
    }

    @Override
    public List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException {
        if(!request.validate()){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        return planRepository.findAllByIdInAndUserId(request.getIds(), request.getUserId());
    }

    @Override
    @Transactional
    public AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException {
        if(!request.updateValidate()){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        AdPlan plan = planRepository.findByIdAndUserId(request.getId(), request.getUserId());
        if(plan == null){
            throw new AdException(Constants.ErrorMsg.CAN_NOT_FIND_RECORD);
        }
        if(request.getPlanName() != null){
            plan.setPlanName(request.getPlanName());
        }
        if(request.getStartDate() != null){
            plan.setStartDate(CommonUtils.parseStringDate(request.getStartDate()));
        }
        if(request.getEndDate() != null){
            plan.setEndDate(CommonUtils.parseStringDate(request.getEndDate()));
        }
        plan.setUpdateTime(new Date());
        plan = planRepository.save(plan);

        return new AdPlanResponse(plan.getId(), plan.getPlanName());
    }

    @Override
    @Transactional
    public void deleteAdPlan(AdPlanRequest request) throws AdException {
        if(!request.deledeValidate()){
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        AdPlan plan = planRepository.findByIdAndUserId(request.getId(), request.getUserId());
        if(plan == null){
            throw new AdException(Constants.ErrorMsg.CAN_NOT_FIND_RECORD);
        }
        plan.setPlanStatus(CommonStatus.INVALID.getStatus());
        plan.setUpdateTime(new Date());
        planRepository.save(plan);
    }
}
