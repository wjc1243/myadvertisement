package com.wjc.ad.search;

import com.wjc.ad.search.vo.SearchRequest;
import com.wjc.ad.search.vo.SearchResponse;

public interface ISearch {

    SearchResponse fetchAds(SearchRequest request);
}
