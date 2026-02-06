package com.trustfulfillment.service;

import com.trustfulfillment.entity.TfContract;

import java.util.Map;

/**
 * 合同服务接口
 */
public interface ContractService {

    /**
     * 创建或更新合同（商家操作）
     */
    TfContract createOrUpdateContract(Long orderId, Map<String, Object> contractData, Long merchantId);

    /**
     * 提交合同（商家完成编辑，等待甲方签署）
     */
    boolean submitContract(Long contractId, Long merchantId);

    /**
     * 签署合同
     * @param contractId 合同ID
     * @param signature 签名图片(Base64)
     * @param userId 签署人ID
     */
    boolean signContract(Long contractId, String signature, Long userId);

    /**
     * 获取合同详情
     */
    TfContract getContractDetail(Long contractId);

    /**
     * 根据订单获取合同
     */
    TfContract getContractByOrder(Long orderId);

    /**
     * 获取用户的合同列表（作为甲方或乙方）
     */
    java.util.List<TfContract> getContractListByUser(Long userId);
}
