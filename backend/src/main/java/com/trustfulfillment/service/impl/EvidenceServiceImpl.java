package com.trustfulfillment.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.entity.TfEvidence;
import com.trustfulfillment.mapper.TfEvidenceMapper;
import com.trustfulfillment.service.EvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 存证服务实现
 */
@Service
public class EvidenceServiceImpl implements EvidenceService {

    @Autowired
    private TfEvidenceMapper evidenceMapper;

    @Override
    public List<TfEvidence> getOrderEvidence(Long orderId) {
        LambdaQueryWrapper<TfEvidence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfEvidence::getOrderId, orderId)
                .orderByDesc(TfEvidence::getCreateTime);
        return evidenceMapper.selectList(wrapper);
    }

    @Override
    public List<TfEvidence> getStageEvidence(Long stageId) {
        LambdaQueryWrapper<TfEvidence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfEvidence::getStageId, stageId)
                .orderByDesc(TfEvidence::getCreateTime);
        return evidenceMapper.selectList(wrapper);
    }

    @Override
    public TfEvidence createEvidence(TfEvidence evidence) {
        // 如果没有设置文件哈希，自动生成
        if (evidence.getFileHash() == null && evidence.getContent() != null) {
            String hash = DigestUtil.sha256Hex(evidence.getContent() + System.currentTimeMillis());
            evidence.setFileHash(hash);
        }
        
        // 模拟区块链上链（实际项目中需要对接真实区块链）
        if (evidence.getBlockHeight() == null) {
            // 模拟区块高度
            evidence.setBlockHeight(System.currentTimeMillis() / 1000);
            evidence.setChainTime(LocalDateTime.now());
        }
        
        evidence.setCreateTime(LocalDateTime.now());
        evidenceMapper.insert(evidence);
        return evidence;
    }

    @Override
    public TfEvidence getById(Long id) {
        return evidenceMapper.selectById(id);
    }
}
