package com.wjc.ad.mysql.dto;

import com.wjc.ad.mysql.constant.OpType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Data
public class ParseTemplate {

    private String database;
    private Map<String, TableTemplate> tableTemplateMap = new HashMap<>();

    public static ParseTemplate parse(Template _template){
        ParseTemplate template = new ParseTemplate();
        template.setDatabase(_template.getDatabase());

        for (JsonTable jsonTable : _template.getTableList()) {
            String name = jsonTable.getTableName();
            Integer level = jsonTable.getLevel();

            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setTableName(name);
            tableTemplate.setLevel(level.toString());

            template.tableTemplateMap.put(name, tableTemplate);

            //遍历操作类型对应的列
            Map<OpType, List<String>> opTypeFieldSetMap = tableTemplate.getOpTypeFieldSetMap();
            for (JsonTable.Column column : jsonTable.getInsert()) {
                getAndCreateIfNeed(OpType.ADD, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }
            for (JsonTable.Column column : jsonTable.getUpdate()) {
                getAndCreateIfNeed(OpType.UPDATE, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }
            for (JsonTable.Column column : jsonTable.getDelete()) {
                getAndCreateIfNeed(OpType.DELETE, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }
        }
        return template;
    }

    private static <T, R> R getAndCreateIfNeed(T key, Map<T, R> map, Supplier<R> factory){
        return map.computeIfAbsent(key, (e) -> factory.get());
    }
}
