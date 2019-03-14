package com.wjc.ad.index;

import com.alibaba.fastjson.JSON;
import com.wjc.ad.dump.DConstant;
import com.wjc.ad.dump.table.*;
import com.wjc.ad.handler.AdLevelDataHandler;
import com.wjc.ad.mysql.constant.OpType;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
@DependsOn("dataTable")
public class IndexFileLoader {

    @PostConstruct
    public void init(){
        List<String> adPlanStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_PLAN));
        adPlanStrings.forEach((e) -> AdLevelDataHandler.handleLevel2(JSON.parseObject(e, AdPlanTable.class), OpType.ADD));

        List<String> adCreativeStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE));
        adCreativeStrings.forEach((e) -> AdLevelDataHandler.handleLevel2(JSON.parseObject(e, AdCreativeTable.class), OpType.ADD));

        List<String> adUnitStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT));
        adUnitStrings.forEach((e) -> AdLevelDataHandler.handleLevel3(JSON.parseObject(e, AdUnitTable.class), OpType.ADD));

        List<String> adCreativeUnitStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT));
        adCreativeUnitStrings.forEach((e) -> AdLevelDataHandler.handleLevel3(JSON.parseObject(e, AdCreativeUnitTable.class), OpType.ADD));

        List<String> adUnitDistrictStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTRICT));
        adUnitDistrictStrings.forEach((e) -> AdLevelDataHandler.handleLevel4(JSON.parseObject(e, AdUnitDistrictTable.class), OpType.ADD));

        List<String> adUnitItStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));
        adUnitItStrings.forEach((e) -> AdLevelDataHandler.handleLevel4(JSON.parseObject(e, AdUnitItTable.class), OpType.ADD));

        List<String> adUnitKeywordStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));
        adUnitKeywordStrings.forEach((e) -> AdLevelDataHandler.handleLevel4(JSON.parseObject(e, AdUnitKeywordTable.class), OpType.ADD));
    }

    private List<String> loadDumpData(String fileName){
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))){
            return br.lines().collect(Collectors.toList());
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}