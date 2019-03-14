package com.wjc.ad.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

public class BinlogServiceTest {
    public static void main(String[] args) throws Exception {
        BinaryLogClient client = new BinaryLogClient("123.56.16.37", 3306, "admin", "123qwe");
        client.setBinlogFilename("mysql-bin.000002");

        client.registerEventListener(event -> {
            EventData data = event.getData();
            if(data instanceof WriteRowsEventData){
                System.out.println(event.getHeader().getEventType());
                System.out.println(data.toString());
            }else if(data instanceof UpdateRowsEventData){
                System.out.println(data.toString());
            }else if(data instanceof DeleteRowsEventData){
                System.out.println(data.toString());
            }
        });
        client.connect();
    }
}
