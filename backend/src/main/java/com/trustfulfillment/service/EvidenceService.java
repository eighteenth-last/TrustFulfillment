package com.trustfulfillment.service;

import com.trustfulfillment.entity.TfEvidence;
import java.util.List;

/**
 * 存证服务接口
 */
public interface EvidenceService {

    /**
     * 获取订单的所有存证记录
     */
    List<TfEvidence> getOrderEvidence(Long orderId);

    /**
     * 获取阶段的存证记录
     */
    List<TfEvidence> getStageEvidence(Long stageId);

    /**
     * 创建存证记录
     */
    TfEvidence createEvidence(TfEvidence evidence);

    /**
     * 根据ID获取存证详情
     */
    TfEvidence getById(Long id);
}
