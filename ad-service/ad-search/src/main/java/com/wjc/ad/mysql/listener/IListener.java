package com.wjc.ad.mysql.listener;

import com.wjc.ad.mysql.dto.BinlogRowData;

public interface IListener {

    void register();

    void onEvent(BinlogRowData eventData);
}
