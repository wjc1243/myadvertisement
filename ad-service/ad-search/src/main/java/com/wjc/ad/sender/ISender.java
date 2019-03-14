package com.wjc.ad.sender;

import com.wjc.ad.mysql.dto.MySqlRowData;

public interface ISender {

    void sender(MySqlRowData rowData);
}
