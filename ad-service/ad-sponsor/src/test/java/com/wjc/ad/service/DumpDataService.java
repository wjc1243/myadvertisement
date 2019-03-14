package com.wjc.ad.service;

import com.alibaba.fastjson.JSON;
import com.wjc.ad.Application;
import com.wjc.ad.constant.CommonStatus;
import com.wjc.ad.dao.AdCreativeRepository;
import com.wjc.ad.dao.AdPlanRepository;
import com.wjc.ad.dao.AdUnitRepository;
import com.wjc.ad.dao.unit_condition.AdCreativeUnitItRepository;
import com.wjc.ad.dao.unit_condition.AdUnitDistrictRepository;
import com.wjc.ad.dao.unit_condition.AdUnitItRepository;
import com.wjc.ad.dao.unit_condition.AdUnitKeywordRepository;
import com.wjc.ad.dump.DConstant;
import com.wjc.ad.dump.table.*;
import com.wjc.ad.entity.AdCreative;
import com.wjc.ad.entity.AdPlan;
import com.wjc.ad.entity.AdUnit;
import com.wjc.ad.entity.unit_condition.AdCreativeUnit;
import com.wjc.ad.entity.unit_condition.AdUnitDistrict;
import com.wjc.ad.entity.unit_condition.AdUnitIt;
import com.wjc.ad.entity.unit_condition.AdUnitKeyword;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DumpDataService {
    @Autowired
    private AdPlanRepository planRepository;
    @Autowired
    private AdUnitRepository unitRepository;
    @Autowired
    private AdCreativeRepository creativeRepository;
    @Autowired
    private AdCreativeUnitItRepository creativeUnitItRepository;
    @Autowired
    private AdUnitKeywordRepository keywordRepository;
    @Autowired
    private AdUnitItRepository itRepository;
    @Autowired
    private AdUnitDistrictRepository districtRepository;

    /**
     * 将所有数据存储到文件
     */
    @Test
    public void dumpAdTableData(){
        dumpAdPlanTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_PLAN));
        dumpAdUnitTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT));
        dumpAdCreativeTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE));
        dumpAdCreativeUnitTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT));
        dumpAdUnitDistrictTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTRICT));
        dumpAdUnitItTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));
        dumpAdUnitKeywordTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));
    }

    private void dumpAdPlanTable(String fileName){
        List<AdPlan> adPlans = planRepository.findAllByPlanStatus(CommonStatus.VALID.getStatus());
        if(CollectionUtils.isEmpty(adPlans)){
            return;
        }
        List<AdPlanTable> planTables = new ArrayList<>();
        adPlans.forEach((e) -> planTables.add(
                new AdPlanTable(e.getId(), e.getUserId(), e.getPlanStatus(), e.getStartDate(), e.getEndDate())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdPlanTable planTable : planTables) {
                writer.write(JSON.toJSONString(planTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdPlanTable error");
        }
    }

    private void dumpAdUnitTable(String fileName){
        List<AdUnit> adUnits = unitRepository.findAllByUnitStatus(CommonStatus.VALID.getStatus());
        if(CollectionUtils.isEmpty(adUnits)){
            return;
        }
        List<AdUnitTable> unitTables = new ArrayList<>();
        adUnits.forEach((e) -> unitTables.add(
                new AdUnitTable(e.getId(), e.getUnitStatus(), e.getPositionType(), e.getPlanId())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitTable unitTable: unitTables) {
                writer.write(JSON.toJSONString(unitTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdUnitTable error");
        }
    }

    private void dumpAdCreativeTable(String fileName){
        List<AdCreative> creatives = creativeRepository.findAll();
        if(CollectionUtils.isEmpty(creatives)){
            return;
        }
        List<AdCreativeTable> creativeTables = new ArrayList<>();
        creatives.forEach((e) -> creativeTables.add(
                new AdCreativeTable(e.getId(), e.getName(), e.getType(), e.getMaterialType(),
                        e.getHeight(), e.getWidth(), e.getAuditStatus(), e.getUrl())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdCreativeTable creativeTable: creativeTables) {
                writer.write(JSON.toJSONString(creativeTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdCreativeTable error");
        }
    }

    private void dumpAdCreativeUnitTable(String fileName){
        List<AdCreativeUnit> creativeUnits = creativeUnitItRepository.findAll();
        if(CollectionUtils.isEmpty(creativeUnits)){
            return;
        }
        List<AdCreativeUnitTable> creativeUnitTables = new ArrayList<>();
        creativeUnits.forEach((e) -> creativeUnitTables.add(
                new AdCreativeUnitTable(e.getCreativeId(), e.getUnitId())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdCreativeUnitTable creativeUnitTable: creativeUnitTables) {
                writer.write(JSON.toJSONString(creativeUnitTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdCreativeUnitTable error");
        }
    }

    private void dumpAdUnitDistrictTable(String fileName){
        List<AdUnitDistrict> unitDistricts = districtRepository.findAll();
        if(CollectionUtils.isEmpty(unitDistricts)){
            return;
        }
        List<AdUnitDistrictTable> unitDistrictTables = new ArrayList<>();
        unitDistricts.forEach((e) -> unitDistrictTables.add(
                new AdUnitDistrictTable(e.getUnitId(), e.getProvince(), e.getCity())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitDistrictTable unitDistrictTable: unitDistrictTables) {
                writer.write(JSON.toJSONString(unitDistrictTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdUnitDistrictTable error");
        }
    }

    private void dumpAdUnitItTable(String fileName){
        List<AdUnitIt> unitIts = itRepository.findAll();
        if(CollectionUtils.isEmpty(unitIts)){
            return;
        }
        List<AdUnitItTable> unitItTables = new ArrayList<>();
        unitIts.forEach((e) -> unitItTables.add(
                new AdUnitItTable(e.getUnitId(), e.getItTag())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitItTable unitItTable: unitItTables) {
                writer.write(JSON.toJSONString(unitItTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdUnitItTable error");
        }
    }

    private void dumpAdUnitKeywordTable(String fileName){
        List<AdUnitKeyword> unitKeywords = keywordRepository.findAll();
        if(CollectionUtils.isEmpty(unitKeywords)){
            return;
        }
        List<AdUnitKeywordTable> unitKeywordTables = new ArrayList<>();
        unitKeywords.forEach((e) -> unitKeywordTables.add(
                new AdUnitKeywordTable(e.getUnitId(), e.getKeyword())
        ));
        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitKeywordTable unitKeywordTable: unitKeywordTables) {
                writer.write(JSON.toJSONString(unitKeywordTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            log.error("dumpAdUnitKeywordTable error");
        }
    }
}
