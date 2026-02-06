package com.trustfulfillment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.SysBank;
import com.trustfulfillment.mapper.SysBankMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    private SysBankMapper bankMapper;

    /**
     * 获取银行列表（用于下拉选择）
     */
    @GetMapping("/options")
    public Result<List<Map<String, String>>> getBankOptions() {
        LambdaQueryWrapper<SysBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysBank::getStatus, 1)
               .orderByAsc(SysBank::getSortOrder);
        List<SysBank> banks = bankMapper.selectList(wrapper);
        
        List<Map<String, String>> options = banks.stream().map(bank -> {
            Map<String, String> option = new HashMap<>();
            option.put("label", bank.getBankName());
            option.put("value", bank.getBankName());
            option.put("code", bank.getBankCode());
            if (bank.getLogoUrl() != null) {
                option.put("logo", bank.getLogoUrl());
            }
            return option;
        }).collect(Collectors.toList());
        
        return Result.success(options);
    }

    /**
     * 获取所有银行信息（管理端）
     */
    @GetMapping
    public Result<List<SysBank>> getAllBanks() {
        LambdaQueryWrapper<SysBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysBank::getSortOrder);
        return Result.success(bankMapper.selectList(wrapper));
    }
}
