package com.wjc.ad.service;

import com.wjc.ad.exception.AdException;
import com.wjc.ad.vo.AdCreativeRequest;
import com.wjc.ad.vo.AdCreativeResponse;

public interface ICreativeService {

    AdCreativeResponse createCreative(AdCreativeRequest request) throws AdException;
}
