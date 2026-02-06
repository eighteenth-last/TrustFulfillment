package com.trustfulfillment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.entity.*;
import com.trustfulfillment.mapper.TfContractMapper;
import com.trustfulfillment.mapper.TfEvidenceMapper;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.mapper.TfOrderStageMapper;
import com.trustfulfillment.mapper.TfTransactionMapper;
import com.trustfulfillment.service.CommissionService;
import com.trustfulfillment.service.ContractService;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 合同服务实现
 */
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private TfContractMapper contractMapper;

    @Autowired
    private TfOrderMapper orderMapper;

    @Autowired
    private TfOrderStageMapper stageMapper;

    @Autowired
    private TfEvidenceMapper evidenceMapper;

    @Autowired
    private TfTransactionMapper transactionMapper;

    @Autowired
    private SysUserService userService;
    
    @Autowired
    @Lazy
    private CommissionService commissionService;

    // 默认条款模板
    private static final String DEFAULT_BREACH_CLAUSE = 
        "1. 甲方逾期付款超过3日，需支付应付款项0.1%/日的违约金\n" +
        "2. 乙方逾期交付超过7日，甲方有权解除合同并要求退款\n" +
        "3. 任一方违约导致合同解除，违约方承担对方实际损失";

    private static final String DEFAULT_CONFIDENTIAL_CLAUSE = 
        "1. 双方对项目相关技术资料、商业信息负有保密义务\n" +
        "2. 保密期限为合同终止后2年\n" +
        "3. 违反保密义务需承担违约责任";

    private static final String DEFAULT_IP_CLAUSE = 
        "1. 乙方交付成果的知识产权归甲方所有\n" +
        "2. 乙方保证交付物不侵犯第三方知识产权\n" +
        "3. 如涉及第三方开源组件，乙方需提前说明";

    private static final String DEFAULT_DISPUTE_CLAUSE = 
        "1. 本合同争议首先通过平台调解解决\n" +
        "2. 调解不成的，任一方可向合同签订地人民法院提起诉讼";

    @Override
    @Transactional
    public TfContract createOrUpdateContract(Long orderId, Map<String, Object> contractData, Long merchantId) {
        // 验证订单
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(merchantId)) {
            throw new RuntimeException("无权操作此订单的合同");
        }
        if (order.getStatus() != 8) {
            throw new RuntimeException("订单不在待签合同状态");
        }

        // 查找现有合同或创建新合同
        TfContract contract = getContractByOrder(orderId);
        boolean isNew = (contract == null);
        
        if (isNew) {
            contract = new TfContract();
            contract.setOrderId(orderId);
            contract.setContractNo(generateContractNo());
            contract.setPartyAId(order.getBuyerId());
            contract.setPartyBId(merchantId);
            contract.setTotalAmount(order.getTotalAmount());
            contract.setStatus(TfContract.STATUS_DRAFT);
            
            // 填充甲乙双方名称
            SysUser buyer = userService.getById(order.getBuyerId());
            SysUser seller = userService.getById(merchantId);
            contract.setPartyAName(buyer != null ? (buyer.getNickname() != null ? buyer.getNickname() : buyer.getPhone()) : "甲方");
            contract.setPartyBName(seller != null ? (seller.getNickname() != null ? seller.getNickname() : seller.getPhone()) : "乙方");
            
            // 设置默认条款
            contract.setBreachClause(DEFAULT_BREACH_CLAUSE);
            contract.setConfidentialClause(DEFAULT_CONFIDENTIAL_CLAUSE);
            contract.setIpClause(DEFAULT_IP_CLAUSE);
            contract.setDisputeClause(DEFAULT_DISPUTE_CLAUSE);
        }

        // 更新合同内容
        contract.setTitle(getStringValue(contractData, "title", order.getTitle() + " - 项目合同"));
        contract.setProjectDesc(getStringValue(contractData, "projectDesc", order.getDescription()));
        contract.setTechRequirements(getStringValue(contractData, "techRequirements", order.getTechStack()));
        contract.setDeliveryStandard(getStringValue(contractData, "deliveryStandard", ""));
        
        // 交付期限
        String deadline = getStringValue(contractData, "deliveryDeadline", null);
        if (deadline != null && !deadline.isEmpty()) {
            contract.setDeliveryDeadline(LocalDate.parse(deadline));
        } else if (order.getDeliveryTime() != null) {
            contract.setDeliveryDeadline(order.getDeliveryTime().toLocalDate());
        }

        // 法律条款（如果有更新）
        if (contractData.containsKey("breachClause")) {
            contract.setBreachClause(getStringValue(contractData, "breachClause", DEFAULT_BREACH_CLAUSE));
        }
        if (contractData.containsKey("confidentialClause")) {
            contract.setConfidentialClause(getStringValue(contractData, "confidentialClause", DEFAULT_CONFIDENTIAL_CLAUSE));
        }
        if (contractData.containsKey("ipClause")) {
            contract.setIpClause(getStringValue(contractData, "ipClause", DEFAULT_IP_CLAUSE));
        }
        if (contractData.containsKey("disputeClause")) {
            contract.setDisputeClause(getStringValue(contractData, "disputeClause", DEFAULT_DISPUTE_CLAUSE));
        }

        // 生成付款条款JSON（从订单阶段）
        List<TfOrderStage> stages = getOrderStages(orderId);
        StringBuilder paymentTerms = new StringBuilder("[");
        for (int i = 0; i < stages.size(); i++) {
            TfOrderStage stage = stages.get(i);
            if (i > 0) paymentTerms.append(",");
            paymentTerms.append(String.format(
                "{\"name\":\"%s\",\"amount\":%.2f,\"percent\":%d,\"milestone\":\"%s\"}",
                stage.getStageName(),
                stage.getAmount(),
                stage.getPercent() != null ? stage.getPercent() : 0,
                stage.getMilestone() != null ? stage.getMilestone() : ""
            ));
        }
        paymentTerms.append("]");
        contract.setPaymentTerms(paymentTerms.toString());

        if (isNew) {
            contractMapper.insert(contract);
        } else {
            contractMapper.updateById(contract);
        }

        return contract;
    }

    @Override
    @Transactional
    public boolean submitContract(Long contractId, Long merchantId) {
        TfContract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        if (!contract.getPartyBId().equals(merchantId)) {
            throw new RuntimeException("无权操作此合同");
        }
        if (contract.getStatus() != TfContract.STATUS_DRAFT) {
            throw new RuntimeException("合同状态异常");
        }

        // 验证必填字段
        if (contract.getTitle() == null || contract.getTitle().isEmpty()) {
            throw new RuntimeException("请填写合同标题");
        }
        if (contract.getProjectDesc() == null || contract.getProjectDesc().isEmpty()) {
            throw new RuntimeException("请填写项目描述");
        }
        if (contract.getDeliveryStandard() == null || contract.getDeliveryStandard().isEmpty()) {
            throw new RuntimeException("请填写验收标准");
        }

        // 提交后状态变为待甲方签署
        contract.setStatus(TfContract.STATUS_WAIT_PARTY_A);
        return contractMapper.updateById(contract) > 0;
    }

    @Override
    @Transactional
    public boolean signContract(Long contractId, String signature, Long userId) {
        TfContract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        if (signature == null || signature.isEmpty()) {
            throw new RuntimeException("请提供签名");
        }

        TfOrder order = orderMapper.selectById(contract.getOrderId());
        if (order == null) {
            throw new RuntimeException("关联订单不存在");
        }

        boolean isPartyA = contract.getPartyAId().equals(userId);
        boolean isPartyB = contract.getPartyBId().equals(userId);

        if (!isPartyA && !isPartyB) {
            throw new RuntimeException("无权签署此合同");
        }

        // 甲方签署
        if (isPartyA) {
            if (contract.getStatus() != TfContract.STATUS_WAIT_PARTY_A) {
                throw new RuntimeException("当前不需要甲方签署");
            }
            contract.setPartyASignature(signature);
            contract.setPartyASignTime(LocalDateTime.now());
            
            // 甲方签署后，检查乙方是否已签
            if (contract.getPartyBSignature() != null && !contract.getPartyBSignature().isEmpty()) {
                // 双方都已签署，合同生效
                effectContract(contract, order);
            } else {
                // 等待乙方签署
                contract.setStatus(TfContract.STATUS_WAIT_PARTY_B);
            }
        }
        // 乙方签署
        else {
            if (contract.getStatus() != TfContract.STATUS_WAIT_PARTY_B && 
                contract.getStatus() != TfContract.STATUS_WAIT_PARTY_A) {
                throw new RuntimeException("当前不需要乙方签署");
            }
            contract.setPartyBSignature(signature);
            contract.setPartyBSignTime(LocalDateTime.now());
            
            // 乙方签署后，检查甲方是否已签
            if (contract.getPartyASignature() != null && !contract.getPartyASignature().isEmpty()) {
                // 双方都已签署，合同生效
                effectContract(contract, order);
            }
            // 如果甲方还没签，状态保持不变（等待甲方签署）
        }

        return contractMapper.updateById(contract) > 0;
    }

    /**
     * 合同生效处理
     * 正确流程：
     * 1. 合同生效后，用户支付全款托管给平台
     * 2. 首付款阶段自动释放给商家（无需商家提交交付）
     * 3. 尾款和质保款保持"已托管"状态，等待商家交付后释放
     */
    private void effectContract(TfContract contract, TfOrder order) {
        // 生成存证哈希
        String evidenceHash = IdUtil.fastSimpleUUID();
        contract.setEvidenceHash(evidenceHash);
        contract.setStatus(TfContract.STATUS_EFFECTIVE);
        
        // 写入合同签署存证记录
        TfEvidence contractEvidence = new TfEvidence();
        contractEvidence.setOrderId(contract.getOrderId());
        contractEvidence.setType(TfEvidence.TYPE_CONTRACT);
        contractEvidence.setTitle("合同签署 - " + contract.getTitle());
        contractEvidence.setContent("合同编号: " + contract.getContractNo() + 
                          ", 甲方: " + contract.getPartyAName() + 
                          ", 乙方: " + contract.getPartyBName() + 
                          ", 金额: ¥" + contract.getTotalAmount());
        contractEvidence.setFileHash(evidenceHash);
        contractEvidence.setChainTime(LocalDateTime.now());
        contractEvidence.setOperatorId(contract.getPartyAId());
        evidenceMapper.insert(contractEvidence);
        
        // ============ 自动托管款项 ============
        Long buyerId = order.getBuyerId();
        Long sellerId = order.getSellerId();
        BigDecimal totalAmount = order.getTotalAmount();
        
        // 检查用户余额
        SysUser buyer = userService.getById(buyerId);
        if (buyer.getBalance().compareTo(totalAmount) < 0) {
            // 余额不足，订单进入待托管状态，用户需手动托管
            order.setStatus(1); // 待托管
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
            return;
        }
        
        // 托管全部款项（从余额转入冻结金额）
        userService.updateFrozenAmount(buyerId, totalAmount, true);
        
        // 获取更新后的余额
        buyer = userService.getById(buyerId);
        
        // 生成托管存证哈希
        String depositHash = IdUtil.fastSimpleUUID();
        
        // 创建托管交易记录
        TfTransaction trans = new TfTransaction();
        trans.setUserId(buyerId);
        trans.setOrderId(order.getId());
        trans.setType("deposit");
        trans.setAmount(totalAmount);
        trans.setBalanceAfter(buyer.getBalance());
        trans.setTitle("项目款项托管 - " + order.getTitle());
        trans.setRemark("订单号: " + order.getOrderNo() + " | 合同签署自动托管");
        trans.setStatus(1);
        trans.setEvidenceHash(depositHash);
        transactionMapper.insert(trans);
        
        // 写入资金托管存证记录
        TfEvidence depositEvidence = new TfEvidence();
        depositEvidence.setOrderId(order.getId());
        depositEvidence.setType(TfEvidence.TYPE_DEPOSIT);
        depositEvidence.setTitle("项目款项托管 - " + order.getTitle());
        depositEvidence.setContent("托管金额: ¥" + totalAmount + "，订单号: " + order.getOrderNo());
        depositEvidence.setFileHash(depositHash);
        depositEvidence.setChainTime(LocalDateTime.now());
        depositEvidence.setOperatorId(buyerId);
        evidenceMapper.insert(depositEvidence);
        
        // ============ 处理各阶段状态并自动释放首付款 ============
        List<TfOrderStage> stages = getOrderStages(order.getId());
        BigDecimal depositReleaseAmount = BigDecimal.ZERO; // 首付款金额
        
        for (TfOrderStage stage : stages) {
            Integer stageType = stage.getStageType() != null ? stage.getStageType() : TfOrderStage.TYPE_FINAL;
            
            if (stageType == TfOrderStage.TYPE_DEPOSIT) {
                // 首付款阶段：自动释放给商家
                stage.setStatus(TfOrderStage.STATUS_COMPLETED); // 直接设为已结算
                stage.setReleaseTime(LocalDateTime.now());
                depositReleaseAmount = depositReleaseAmount.add(stage.getAmount());
            } else {
                // 尾款和质保款阶段：保持已托管状态，等待商家交付后释放
                stage.setStatus(TfOrderStage.STATUS_ESCROWED);
            }
            stageMapper.updateById(stage);
        }
        
        // ============ 自动释放首付款给商家（扣除平台提成） ============
        if (depositReleaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 计算并扣除平台提成
            TfCommission commission = commissionService.calculateAndDeductCommission(
                    order, depositReleaseAmount, null, 1, "首付款释放提成");
            BigDecimal actualAmount = commission.getActualAmount(); // 商家实际到账金额
            
            // 解冻买家首付款（全额解冻）
            userService.updateFrozenAmount(buyerId, depositReleaseAmount, false);
            // 增加卖家余额（扣除提成后的金额）
            userService.updateBalance(sellerId, actualAmount, true);
            
            // 获取商家更新后的余额
            SysUser seller = userService.getById(sellerId);
            
            // 创建商家收款交易记录（记录实际到账金额）
            String releaseHash = IdUtil.fastSimpleUUID();
            TfTransaction sellerTrans = new TfTransaction();
            sellerTrans.setUserId(sellerId);
            sellerTrans.setOrderId(order.getId());
            sellerTrans.setType("release");
            sellerTrans.setAmount(actualAmount);
            sellerTrans.setBalanceAfter(seller.getBalance());
            sellerTrans.setTitle("首付款到账 - " + order.getTitle());
            sellerTrans.setRemark("订单号: " + order.getOrderNo() + " | 原金额: ¥" + depositReleaseAmount + 
                    ", 提成(" + commission.getCommissionRate() + "%): ¥" + commission.getCommissionAmount() + 
                    " | 合同签署后自动释放首付款");
            sellerTrans.setStatus(1);
            sellerTrans.setEvidenceHash(releaseHash);
            transactionMapper.insert(sellerTrans);
            
            // 写入首付款释放存证记录
            TfEvidence releaseEvidence = new TfEvidence();
            releaseEvidence.setOrderId(order.getId());
            releaseEvidence.setType(TfEvidence.TYPE_ACCEPT);
            releaseEvidence.setTitle("首付款释放 - " + order.getTitle());
            releaseEvidence.setContent("首付款 ¥" + depositReleaseAmount + " 已释放给商家，扣除平台提成 ¥" + 
                    commission.getCommissionAmount() + "，实际到账 ¥" + actualAmount);
            releaseEvidence.setFileHash(releaseHash);
            releaseEvidence.setChainTime(LocalDateTime.now());
            releaseEvidence.setOperatorId(buyerId);
            evidenceMapper.insert(releaseEvidence);
            
            // 更新订单已释放金额（记录原始释放金额）
            order.setReleasedAmount(depositReleaseAmount);
        }
        
        // 更新订单状态：直接进入进行中(2)
        order.setDepositAmount(totalAmount);
        order.setStatus(2); // 进行中
        order.setStartTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public TfContract getContractDetail(Long contractId) {
        return contractMapper.selectById(contractId);
    }

    @Override
    public TfContract getContractByOrder(Long orderId) {
        LambdaQueryWrapper<TfContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfContract::getOrderId, orderId)
               .ne(TfContract::getStatus, TfContract.STATUS_INVALID)
               .orderByDesc(TfContract::getCreateTime)
               .last("LIMIT 1");
        return contractMapper.selectOne(wrapper);
    }

    /**
     * 获取订单阶段列表
     */
    private List<TfOrderStage> getOrderStages(Long orderId) {
        LambdaQueryWrapper<TfOrderStage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfOrderStage::getOrderId, orderId)
               .orderByAsc(TfOrderStage::getSort);
        return stageMapper.selectList(wrapper);
    }

    /**
     * 生成合同编号
     */
    private String generateContractNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.valueOf(System.currentTimeMillis() % 100000);
        return "HT" + dateStr + random;
    }

    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    @Override
    public List<TfContract> getContractListByUser(Long userId) {
        LambdaQueryWrapper<TfContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(TfContract::getPartyAId, userId).or().eq(TfContract::getPartyBId, userId))
               .ne(TfContract::getStatus, TfContract.STATUS_INVALID)
               .orderByDesc(TfContract::getCreateTime);
        
        List<TfContract> contracts = contractMapper.selectList(wrapper);
        
        // 填充关联信息
        for (TfContract contract : contracts) {
            // 获取订单信息
            TfOrder order = orderMapper.selectById(contract.getOrderId());
            if (order != null) {
                contract.setTitle(order.getTitle());
                contract.setTotalAmount(order.getTotalAmount());
                contract.setDeliveryDeadline(order.getDeliveryTime() != null ? order.getDeliveryTime().toLocalDate() : null);
                // 设置已结算金额（从订单的已释放金额获取）
                contract.setSettledAmount(order.getReleasedAmount() != null ? order.getReleasedAmount() : BigDecimal.ZERO);
                // 获取甲乙方名称
                SysUser buyer = userService.getById(order.getBuyerId());
                SysUser seller = userService.getById(order.getSellerId());
                contract.setBuyerName(buyer != null ? buyer.getNickname() : "未知");
                contract.setSellerName(seller != null ? seller.getNickname() : "未知");
            }
            // 获取阶段信息
            contract.setStages(getOrderStages(contract.getOrderId()));
        }
        
        return contracts;
    }
}
