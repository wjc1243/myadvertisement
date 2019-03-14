package com.wjc.ad.service.impl;

import com.wjc.ad.dao.AdCreativeRepository;
import com.wjc.ad.entity.AdCreative;
import com.wjc.ad.exception.AdException;
import com.wjc.ad.service.ICreativeService;
import com.wjc.ad.vo.AdCreativeRequest;
import com.wjc.ad.vo.AdCreativeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeServiceImpl implements ICreativeService {

    private final AdCreativeRepository creativeRepository;

    @Autowired
    public CreativeServiceImpl(AdCreativeRepository creativeRepository) {
        this.creativeRepository = creativeRepository;
    }

    @Override
    public AdCreativeResponse createCreative(AdCreativeRequest request) throws AdException {
        AdCreative creative = creativeRepository.save(request.convertToEntity());
        return new AdCreativeResponse(creative.getId(), creative.getName());
    }
}
