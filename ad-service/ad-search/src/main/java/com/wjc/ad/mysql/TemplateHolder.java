package com.wjc.ad.mysql;

import com.alibaba.fastjson.JSON;
import com.wjc.ad.mysql.constant.OpType;
import com.wjc.ad.mysql.dto.ParseTemplate;
import com.wjc.ad.mysql.dto.TableTemplate;
import com.wjc.ad.mysql.dto.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TemplateHolder {

    private ParseTemplate template;
    private final JdbcTemplate jdbcTemplate;

    private String SQL_SCHEMA = "select table_schema, table_name, column_name, ordinal_position " +
            "from information_schema.columns where table_schema = ? and table_name = ?";

    @Autowired
    public TemplateHolder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void  init(){
        loadJson("template.json");
    }

    public TableTemplate getTable (String tableName){
        return template.getTableTemplateMap().get(tableName);
    }

    private void loadJson(String path){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream inStream = cl.getResourceAsStream(path);

        try {
            Template template = JSON.parseObject(inStream, Charset.defaultCharset(), Template.class);
            this.template = ParseTemplate.parse(template);
            loadMeta();
        }catch (IOException e){
            log.error(e.getMessage());
            throw new RuntimeException("fail to parse json file");
        }
    }

    private void loadMeta(){
        for (Map.Entry<String, TableTemplate> entry : template.getTableTemplateMap().entrySet()) {
            TableTemplate table = entry.getValue();
            List<String> insertFields = table.getOpTypeFieldSetMap().get(OpType.ADD);
            List<String> updateFields = table.getOpTypeFieldSetMap().get(OpType.UPDATE);
            List<String> deleteFields = table.getOpTypeFieldSetMap().get(OpType.DELETE);

            jdbcTemplate.query(SQL_SCHEMA, new Object[]{template.getDatabase(), table.getTableName()
            },(rs, i) -> {
                int pos = rs.getInt("ordinal_position");
                String colName = rs.getString("column_name");

                if((null != updateFields && updateFields.contains(colName))
                        || (null != insertFields && insertFields.contains(colName))
                        || (null != deleteFields && deleteFields.contains(colName))){
                    table.getPosMap().put(pos - 1, colName);
                }
                return null;
            });
        }
    }
}
