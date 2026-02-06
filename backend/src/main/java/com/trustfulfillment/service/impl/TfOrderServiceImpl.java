package com.trustfulfillment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trustfulfillment.entity.SysCategory;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfCommission;
import com.trustfulfillment.entity.TfContract;
import com.trustfulfillment.entity.TfEvidence;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfOrderStage;
import com.trustfulfillment.entity.TfTransaction;
import com.trustfulfillment.mapper.TfEvidenceMapper;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.mapper.TfOrderStageMapper;
import com.trustfulfillment.mapper.TfTransactionMapper;
import com.trustfulfillment.service.CommissionService;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.SysUserService;
import com.trustfulfillment.service.TfOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务实现
 * 
 * 新版交付流程:
 * 用户发布(0) → 商家接单(8) → 签合同 → 托管首付款(1→2) → 商家交付(3) → 用户验收 → 质保中(9) → 释放质保款(4)
 * 
 * 订单状态流转:
 * 0-待接单 → 8-待签合同 → 1-待托管 → 2-进行中 → 3-待验收 → 9-质保中 → 4-已完成
 *                                                      ↘ 6-纠纷中
 *         ↘ 5-已取消
 * 
 * 状态说明:
 * 0: 待接单 - 用户已发布，等待商家接单
 * 8: 待签合同 - 商家已接单，需拟定合同并双方签署
 * 1: 待托管 - 合同已签署，等待用户托管首付款
 * 2: 进行中 - 用户已托管首付款，商家执行中
 * 3: 待验收 - 商家已提交最终交付，等待用户验收
 * 4: 已完成 - 项目完成（含质保款已释放）
 * 5: 已取消 - 订单已取消
 * 6: 纠纷中 - 存在争议，需平台介入
 * 9: 质保中 - 验收通过，首付款+尾款已释放，质保款等待质保期结束
 * 
 * 付款阶段类型:
 * 1: 首付款 - 合同签署后托管
 * 2: 尾款 - 验收通过后释放
 * 3: 质保款 - 质保期结束后释放
 */
@Service
public class TfOrderServiceImpl extends ServiceImpl<TfOrderMapper, TfOrder> implements TfOrderService {

    @Autowired
    private TfOrderStageMapper stageMapper;

    @Autowired
    private TfTransactionMapper transactionMapper;

    @Autowired
    private TfEvidenceMapper evidenceMapper;

    @Autowired
    private SysUserService userService;

    @Autowired
    private ConfigService configService;
    
    @Autowired
    private com.trustfulfillment.mapper.SysCategoryMapper categoryMapper;
    
    @Autowired
    @Lazy
    private CommissionService commissionService;
    
    @Autowired
    @Lazy
    private com.trustfulfillment.service.RiskService riskService;
    
    @Autowired
    @Lazy
    private com.trustfulfillment.service.ContractService contractService;

    /**
     * 创建订单（用户发布需求）
     * 新流程：
     * 1. 用户填写需求信息，选择付款方案（首付款+尾款+质保款）
     * 2. 订单直接发布为"待接单"状态(0)，商家可见
     * 3. 商家接单后进入"待签合同"状态(8)
     * 4. 双方签署合同后进入"待托管"状态(1)
     * 5. 用户托管首付款后进入"进行中"状态(2)
     * 6. 商家交付 → 用户验收 → 释放首付款+尾款
     * 7. 质保期结束后 → 释放质保款 → 完成
     */
    @Override
    @Transactional
    public TfOrder createOrder(TfOrder order, List<TfOrderStage> stages) {
        // 计算总金额
        BigDecimal total = stages.stream()
                .map(TfOrderStage::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        
        // 生成订单号: TF + 雪花ID
        order.setOrderNo("TF" + IdUtil.getSnowflakeNextIdStr());
        order.setStatus(0); // 待接单（直接发布，商家可见）
        order.setDepositAmount(BigDecimal.ZERO); // 未托管
        order.setReleasedAmount(BigDecimal.ZERO);
        
        // 保存订单
        save(order);

        // 保存订单阶段，自动识别阶段类型
        int sort = 1;
        int totalStages = stages.size();
        for (int i = 0; i < stages.size(); i++) {
            TfOrderStage stage = stages.get(i);
            stage.setOrderId(order.getId());
            stage.setStatus(TfOrderStage.STATUS_PENDING); // 未开始
            stage.setSort(sort++);
            
            // 自动设置阶段类型：第一个是首付款，最后一个是质保款，中间是尾款
            if (i == 0) {
                stage.setStageType(TfOrderStage.TYPE_DEPOSIT);
            } else if (i == totalStages - 1 && totalStages > 1) {
                // 最后一个阶段，如果名称包含"质保"则为质保款，否则为尾款
                String name = stage.getStageName() != null ? stage.getStageName() : "";
                if (name.contains("质保") || name.contains("保修")) {
                    stage.setStageType(TfOrderStage.TYPE_WARRANTY);
                } else {
                    stage.setStageType(TfOrderStage.TYPE_FINAL);
                }
            } else {
                stage.setStageType(TfOrderStage.TYPE_FINAL);
            }
            
            stageMapper.insert(stage);
        }
        
        // ============ 风控检测：大额交易 ============
        try {
            riskService.checkLargeTransaction(order.getBuyerId(), order.getId(), total);
        } catch (Exception e) {
            // 风控检测失败不影响订单创建
            e.printStackTrace();
        }

        return order;
    }

    /**
     * 分页查询订单
     */
    @Override
    public IPage<TfOrder> pageOrders(Long userId, String role, Integer status, int page, int size) {
        LambdaQueryWrapper<TfOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 根据角色查询: 用户查买家订单，商家查卖家订单
        if ("user".equals(role)) {
            wrapper.eq(TfOrder::getBuyerId, userId);
        } else if ("merchant".equals(role)) {
            // 商家可以看到自己接的订单，也可以看到待接单的订单
            if (status != null && status == 0) {
                // 查看任务大厅（待接单订单）
                wrapper.eq(TfOrder::getStatus, 0);
                // 排除自己发布的订单（商家不能接自己发布的任务）
                wrapper.ne(TfOrder::getBuyerId, userId);
            } else {
                // 商家的"我的订单"显示所有已接单的订单
                // 包括：待签合同(8)、待托管(1)、进行中(2)、待验收(3)、质保中(9)、已完成(4)、纠纷中(6)
                wrapper.eq(TfOrder::getSellerId, userId);
                // 排除已取消的订单(5)和待接单的订单(0)
                wrapper.ne(TfOrder::getStatus, 5);
                wrapper.ne(TfOrder::getStatus, 0);
            }
        }
        
        // 状态筛选（商家端额外筛选时，需要确保在 status >= 2 的基础上）
        if (status != null && !(role.equals("merchant") && status == 0)) {
            wrapper.eq(TfOrder::getStatus, status);
        }
        
        wrapper.orderByDesc(TfOrder::getCreateTime);
        
        IPage<TfOrder> result = page(new Page<>(page, size), wrapper);
        
        // 填充买家/卖家名称和阶段信息
        result.getRecords().forEach(this::fillOrderInfo);
        
        return result;
    }

    /**
     * 获取订单详情（包含阶段信息和合同信息）
     */
    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 填充订单信息
        fillOrderInfo(order);
        
        // 获取阶段列表
        List<TfOrderStage> stages = stageMapper.selectList(
            new LambdaQueryWrapper<TfOrderStage>()
                .eq(TfOrderStage::getOrderId, orderId)
                .orderByAsc(TfOrderStage::getSort)
        );
        
        // 获取合同信息（如果存在）
        TfContract contract = contractService.getContractByOrder(orderId);
        
        result.put("order", order);
        result.put("stages", stages);
        result.put("contract", contract);
        
        return result;
    }

    /**
     * 接单（商家接受任务）
     * 新流程：用户发布时已完成托管，商家接单后直接进入"进行中"状态
     */
    @Override
    @Transactional
    public boolean acceptOrder(Long orderId, Long sellerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单已被接单或状态异常");
        }
        if (order.getBuyerId().equals(sellerId)) {
            throw new RuntimeException("不能接自己发布的订单");
        }
        
        order.setSellerId(sellerId);
        order.setStatus(8); // 进入"待签合同"状态，需要商家拟定合同，双方签署后才能托管付款
        order.setUpdateTime(LocalDateTime.now());
        
        // 注意：不再立即开始阶段，需等合同签署、用户托管付款后才开始
        
        return updateById(order);
    }

    /**
     * 托管资金（用户托管全部款项）
     * 新流程：
     * 1. 合同签署后，用户托管全部款项（首付款+尾款+质保款）
     * 2. 所有阶段状态变为"已托管"
     * 3. 订单状态从1（待托管）变为2（进行中）
     * 4. 验收通过后释放首付款+尾款，质保款等待质保期结束后释放
     */
    @Override
    @Transactional
    public boolean depositFunds(Long orderId, Long buyerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态异常，只有待托管状态才能进行支付");
        }
        
        // 获取所有阶段
        List<TfOrderStage> stages = getOrderStages(orderId);
        if (stages.isEmpty()) {
            throw new RuntimeException("订单阶段信息异常");
        }
        
        // 计算全部托管金额
        BigDecimal totalAmount = order.getTotalAmount();
        
        // 检查单笔支付上限
        BigDecimal maxPayment = configService.getMaxSinglePayment();
        if (totalAmount.compareTo(maxPayment) > 0) {
            throw new RuntimeException("单笔支付金额超过上限 ¥" + maxPayment);
        }
        
        // 检查用户余额
        SysUser buyer = userService.getById(buyerId);
        if (buyer.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("余额不足，请先充值。当前余额: " + buyer.getBalance() + "，需支付: " + totalAmount);
        }
        
        // 托管全部款项（从余额转入冻结金额）
        userService.updateFrozenAmount(buyerId, totalAmount, true);
        
        // 获取更新后的余额
        buyer = userService.getById(buyerId);
        
        // 生成存证哈希
        String depositHash = IdUtil.fastSimpleUUID();
        
        // 创建交易记录
        TfTransaction trans = new TfTransaction();
        trans.setUserId(buyerId);
        trans.setOrderId(orderId);
        trans.setType("deposit");
        trans.setAmount(totalAmount);
        trans.setBalanceAfter(buyer.getBalance());
        trans.setTitle("项目款项托管 - " + order.getTitle());
        trans.setRemark("订单号: " + order.getOrderNo() + " | 含首付款+尾款+质保款");
        trans.setStatus(1);
        trans.setEvidenceHash(depositHash);
        transactionMapper.insert(trans);
        
        // 写入资金托管存证记录
        TfEvidence depositEvidence = new TfEvidence();
        depositEvidence.setOrderId(orderId);
        depositEvidence.setType(TfEvidence.TYPE_DEPOSIT);
        depositEvidence.setTitle("项目款项托管 - " + order.getTitle());
        depositEvidence.setContent("托管金额: ¥" + totalAmount + "，订单号: " + order.getOrderNo());
        depositEvidence.setFileHash(depositHash);
        depositEvidence.setChainTime(LocalDateTime.now());
        depositEvidence.setOperatorId(buyerId);
        evidenceMapper.insert(depositEvidence);
        
        // 更新所有阶段状态为已托管
        for (TfOrderStage stage : stages) {
            stage.setStatus(TfOrderStage.STATUS_ESCROWED); // 已托管
            stageMapper.updateById(stage);
        }
        
        // 更新订单状态：从待托管(1)变为进行中(2)
        order.setDepositAmount(totalAmount);
        order.setStatus(2); // 进行中
        order.setStartTime(LocalDateTime.now());
        
        return updateById(order);
    }

    /**
     * 开始阶段（商家开始执行某阶段）
     */
    @Override
    @Transactional
    public boolean startStage(Long orderId, Long stageId, Long sellerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单不在进行中状态");
        }
        
        TfOrderStage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getOrderId().equals(orderId)) {
            throw new RuntimeException("阶段不存在");
        }
        if (stage.getStatus() != 0) {
            throw new RuntimeException("该阶段已开始或已完成");
        }
        
        // 检查前置阶段是否已完成
        List<TfOrderStage> allStages = getOrderStages(orderId);
        for (TfOrderStage s : allStages) {
            if (s.getSort() < stage.getSort() && s.getStatus() != 3) {
                throw new RuntimeException("请先完成前置阶段: " + s.getStageName());
            }
        }
        
        stage.setStatus(1); // 进行中
        return stageMapper.updateById(stage) > 0;
    }

    /**
     * 提交交付（商家提交最终交付物）
     * 正确流程：
     * 1. 首付款在合同签署后已自动释放，不需要提交交付
     * 2. 商家对尾款阶段提交交付（最终成果）
     * 3. 用户验收后释放尾款，如有质保款则进入质保状态
     */
    @Override
    @Transactional
    public boolean submitDelivery(Long orderId, Long stageId, String evidenceUrl, String deliveryDesc, Long sellerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单不在进行中状态，当前状态: " + order.getStatus());
        }
        
        TfOrderStage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getOrderId().equals(orderId)) {
            throw new RuntimeException("阶段不存在");
        }
        
        // 检查阶段类型：首付款阶段不需要提交交付（已在合同签署后自动释放）
        Integer stageType = stage.getStageType() != null ? stage.getStageType() : TfOrderStage.TYPE_FINAL;
        if (stageType == TfOrderStage.TYPE_DEPOSIT) {
            throw new RuntimeException("首付款阶段已在合同签署后自动释放，请提交尾款阶段的交付");
        }
        
        // 状态1是"已托管"，表示款项已托管，商家可以提交交付
        if (stage.getStatus() != TfOrderStage.STATUS_ESCROWED) {
            throw new RuntimeException("该阶段款项未托管或已提交交付，当前阶段状态: " + stage.getStatus());
        }
        
        // 更新阶段信息
        stage.setEvidenceUrl(evidenceUrl);
        stage.setDeliveryDesc(deliveryDesc);
        stage.setDeliveryTime(LocalDateTime.now());
        stage.setStatus(TfOrderStage.STATUS_DELIVERY); // 待验收
        
        // 生成存证哈希（模拟区块链存证）
        String evidenceHash = IdUtil.fastSimpleUUID();
        stage.setEvidenceHash(evidenceHash);
        
        int stageUpdated = stageMapper.updateById(stage);
        if (stageUpdated == 0) {
            throw new RuntimeException("更新阶段状态失败");
        }
        
        // 写入存证记录表
        TfEvidence evidence = new TfEvidence();
        evidence.setOrderId(orderId);
        evidence.setStageId(stageId);
        evidence.setType(TfEvidence.TYPE_DELIVERY); // 交付提交
        evidence.setTitle("项目交付 - " + stage.getStageName());
        evidence.setContent(deliveryDesc);
        evidence.setFileUrl(evidenceUrl);
        evidence.setFileHash(evidenceHash);
        evidence.setChainTime(LocalDateTime.now()); // 模拟上链时间
        evidence.setOperatorId(sellerId);
        evidenceMapper.insert(evidence);
        
        // 更新订单状态为待验收
        order.setStatus(3); // 待验收
        order.setUpdateTime(LocalDateTime.now());
        
        boolean orderUpdated = updateById(order);
        if (!orderUpdated) {
            throw new RuntimeException("更新订单状态失败");
        }
        
        return true;
    }

    /**
     * 拒绝验收（用户验收不通过）
     */
    @Override
    @Transactional
    public boolean rejectDelivery(Long orderId, Long stageId, Long buyerId, String reason) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        TfOrderStage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getOrderId().equals(orderId)) {
            throw new RuntimeException("阶段不存在");
        }
        if (stage.getStatus() != 2) {
            throw new RuntimeException("该阶段不在待验收状态");
        }
        
        // 拒绝验收，阶段回到进行中
        stage.setStatus(1); // 退回进行中
        stage.setDeliveryDesc(stage.getDeliveryDesc() + "\n【验收不通过】" + reason);
        stageMapper.updateById(stage);
        
        // 订单回到进行中状态
        order.setStatus(2);
        
        return updateById(order);
    }

    /**
     * 获取阶段详情
     */
    @Override
    public TfOrderStage getStageDetail(Long stageId) {
        return stageMapper.selectById(stageId);
    }

    /**
     * 验收确认（用户验收项目交付）
     * 正确流程：
     * 1. 首付款已在合同签署时自动释放给商家
     * 2. 商家提交尾款阶段的交付（最终成果）
     * 3. 用户验收通过后：
     *    - 释放尾款给商家（只释放状态为"已托管"的非质保款阶段）
     *    - 质保款进入质保状态（等待质保期结束）
     * 4. 如果有质保款，订单进入"质保中"状态(9)
     * 5. 质保期结束后，用户确认释放质保款，订单完成
     */
    @Override
    @Transactional
    public boolean acceptDelivery(Long orderId, Long stageId, Long buyerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        TfOrderStage stage = stageMapper.selectById(stageId);
        if (stage == null || !stage.getOrderId().equals(orderId)) {
            throw new RuntimeException("阶段不存在");
        }
        if (stage.getStatus() != TfOrderStage.STATUS_DELIVERY) {
            throw new RuntimeException("该阶段不在待验收状态");
        }
        
        // 验收通过，更新当前阶段状态
        stage.setStatus(TfOrderStage.STATUS_COMPLETED);
        stage.setAcceptTime(LocalDateTime.now());
        stage.setReleaseTime(LocalDateTime.now());
        stageMapper.updateById(stage);
        
        // 写入验收存证记录
        TfEvidence acceptEvidence = new TfEvidence();
        acceptEvidence.setOrderId(orderId);
        acceptEvidence.setStageId(stageId);
        acceptEvidence.setType(TfEvidence.TYPE_ACCEPT);
        acceptEvidence.setTitle("项目验收确认 - " + order.getTitle());
        acceptEvidence.setContent("项目验收通过，尾款阶段: " + stage.getStageName() + "，金额: ¥" + stage.getAmount());
        acceptEvidence.setFileUrl(stage.getEvidenceUrl());
        acceptEvidence.setFileHash(IdUtil.fastSimpleUUID());
        acceptEvidence.setChainTime(LocalDateTime.now());
        acceptEvidence.setOperatorId(buyerId);
        evidenceMapper.insert(acceptEvidence);
        
        // 获取所有阶段
        List<TfOrderStage> allStages = getOrderStages(orderId);
        
        // 计算需要释放的尾款金额（只释放状态为"已托管"的非质保款阶段）
        // 注意：首付款已在合同签署时释放，状态为STATUS_COMPLETED，不会重复释放
        BigDecimal releaseAmount = BigDecimal.ZERO;
        TfOrderStage warrantyStage = null;
        
        for (TfOrderStage s : allStages) {
            Integer sType = s.getStageType() != null ? s.getStageType() : TfOrderStage.TYPE_FINAL;
            
            if (sType == TfOrderStage.TYPE_WARRANTY) {
                // 质保款，进入质保状态
                if (s.getStatus() == TfOrderStage.STATUS_ESCROWED) {
                    warrantyStage = s;
                    s.setStatus(TfOrderStage.STATUS_WARRANTY);
                    // 设置质保期结束时间（从配置读取，默认15天）
                    int warrantyDays = configService.getInt("warranty_days", 15);
                    s.setWarrantyEndTime(LocalDateTime.now().plusDays(warrantyDays));
                    stageMapper.updateById(s);
                }
            } else if (sType == TfOrderStage.TYPE_FINAL) {
                // 尾款阶段：只有状态为"已托管"或"待验收变成已完成"的才释放
                // 首付款阶段已经是STATUS_COMPLETED，不会进入这个分支
                if (s.getStatus() == TfOrderStage.STATUS_ESCROWED) {
                    // 还有已托管但未提交交付的尾款阶段，设为已结算
                    s.setStatus(TfOrderStage.STATUS_COMPLETED);
                    s.setReleaseTime(LocalDateTime.now());
                    stageMapper.updateById(s);
                    releaseAmount = releaseAmount.add(s.getAmount());
                } else if (s.getId().equals(stageId)) {
                    // 当前验收的阶段，计入释放金额
                    releaseAmount = releaseAmount.add(s.getAmount());
                }
            }
            // TYPE_DEPOSIT（首付款）不处理，因为已在合同签署时释放
        }
        
        // 释放尾款给商家（扣除平台提成）
        if (releaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            String evidenceHash = IdUtil.fastSimpleUUID();
            
            // 计算并扣除平台提成
            TfCommission commission = commissionService.calculateAndDeductCommission(
                    order, releaseAmount, stageId, 2, "尾款验收结算提成");
            BigDecimal actualAmount = commission.getActualAmount(); // 商家实际到账金额
            
            // 解冻买家对应金额（全额解冻）
            userService.updateFrozenAmount(buyerId, releaseAmount, false);
            // 增加卖家余额（扣除提成后的金额）
            userService.updateBalance(order.getSellerId(), actualAmount, true);
            
            // 更新订单已释放金额（记录原始释放金额）
            BigDecimal currentReleased = order.getReleasedAmount() != null ? order.getReleasedAmount() : BigDecimal.ZERO;
            order.setReleasedAmount(currentReleased.add(releaseAmount));
            
            // 获取更新后的余额
            SysUser seller = userService.getById(order.getSellerId());
            
            // 创建卖家收款交易记录（记录实际到账金额）
            TfTransaction sellerTrans = new TfTransaction();
            sellerTrans.setUserId(order.getSellerId());
            sellerTrans.setOrderId(orderId);
            sellerTrans.setType("release");
            sellerTrans.setAmount(actualAmount);
            sellerTrans.setBalanceAfter(seller.getBalance());
            sellerTrans.setTitle("尾款验收结算 - " + order.getTitle());
            sellerTrans.setRemark("订单号: " + order.getOrderNo() + " | 原金额: ¥" + releaseAmount + 
                    ", 提成(" + commission.getCommissionRate() + "%): ¥" + commission.getCommissionAmount() + 
                    (warrantyStage != null ? " | 尾款结算，质保款待释放" : " | 尾款结算"));
            sellerTrans.setStatus(1);
            sellerTrans.setEvidenceHash(evidenceHash);
            transactionMapper.insert(sellerTrans);
        }
        
        // 更新订单状态
        if (warrantyStage != null) {
            // 有质保款，订单进入质保中状态
            order.setStatus(9); // 9: 质保中
        } else {
            // 没有质保款，订单直接完成
            order.setStatus(4);
            order.setCompletedTime(LocalDateTime.now());
        }
        
        return updateById(order);
    }
    
    /**
     * 释放质保款
     * 质保期结束后，用户确认释放质保款给商家
     */
    @Override
    @Transactional
    public boolean releaseWarranty(Long orderId, Long buyerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 9) {
            throw new RuntimeException("订单不在质保状态");
        }
        
        // 找到质保款阶段
        List<TfOrderStage> allStages = getOrderStages(orderId);
        TfOrderStage warrantyStage = null;
        for (TfOrderStage s : allStages) {
            Integer sType = s.getStageType() != null ? s.getStageType() : 0;
            if (sType == TfOrderStage.TYPE_WARRANTY && s.getStatus() == TfOrderStage.STATUS_WARRANTY) {
                warrantyStage = s;
                break;
            }
        }
        
        if (warrantyStage == null) {
            throw new RuntimeException("没有待释放的质保款");
        }
        
        // 检查质保期是否已结束（允许提前释放，但给出提示）
        boolean earlyRelease = false;
        if (warrantyStage.getWarrantyEndTime() != null && 
            LocalDateTime.now().isBefore(warrantyStage.getWarrantyEndTime())) {
            earlyRelease = true;
        }
        
        BigDecimal warrantyAmount = warrantyStage.getAmount();
        String evidenceHash = IdUtil.fastSimpleUUID();
        
        // 计算并扣除平台提成
        TfCommission commission = commissionService.calculateAndDeductCommission(
                order, warrantyAmount, warrantyStage.getId(), 3, "质保款释放提成");
        BigDecimal actualAmount = commission.getActualAmount(); // 商家实际到账金额
        
        // 释放质保款（扣除提成后）
        userService.updateFrozenAmount(buyerId, warrantyAmount, false);
        userService.updateBalance(order.getSellerId(), actualAmount, true);
        
        // 更新阶段状态
        warrantyStage.setStatus(TfOrderStage.STATUS_WARRANTY_RELEASED);
        warrantyStage.setReleaseTime(LocalDateTime.now());
        stageMapper.updateById(warrantyStage);
        
        // 更新订单已释放金额（记录原始释放金额）
        BigDecimal currentReleased = order.getReleasedAmount() != null ? order.getReleasedAmount() : BigDecimal.ZERO;
        order.setReleasedAmount(currentReleased.add(warrantyAmount));
        
        // 获取更新后的余额
        SysUser seller = userService.getById(order.getSellerId());
        
        // 创建交易记录（记录实际到账金额）
        TfTransaction sellerTrans = new TfTransaction();
        sellerTrans.setUserId(order.getSellerId());
        sellerTrans.setOrderId(orderId);
        sellerTrans.setType("release");
        sellerTrans.setAmount(actualAmount);
        sellerTrans.setBalanceAfter(seller.getBalance());
        sellerTrans.setTitle("质保款释放 - " + order.getTitle());
        sellerTrans.setRemark("订单号: " + order.getOrderNo() + " | 原金额: ¥" + warrantyAmount + 
                ", 提成(" + commission.getCommissionRate() + "%): ¥" + commission.getCommissionAmount() + 
                (earlyRelease ? " | 提前释放质保款" : " | 质保期结束"));
        sellerTrans.setStatus(1);
        sellerTrans.setEvidenceHash(evidenceHash);
        transactionMapper.insert(sellerTrans);
        
        // 写入存证记录
        TfEvidence warrantyEvidence = new TfEvidence();
        warrantyEvidence.setOrderId(orderId);
        warrantyEvidence.setStageId(warrantyStage.getId());
        warrantyEvidence.setType(TfEvidence.TYPE_ACCEPT);
        warrantyEvidence.setTitle("质保款释放 - " + order.getTitle());
        warrantyEvidence.setContent("质保款 ¥" + warrantyAmount + " 已释放给商家");
        warrantyEvidence.setFileHash(evidenceHash);
        warrantyEvidence.setChainTime(LocalDateTime.now());
        warrantyEvidence.setOperatorId(buyerId);
        evidenceMapper.insert(warrantyEvidence);
        
        // 订单完成
        order.setStatus(4);
        order.setCompletedTime(LocalDateTime.now());
        
        return updateById(order);
    }
    
    /**
     * 支付下一阶段款项
     */
    @Override
    @Transactional
    public boolean payNextStage(Long orderId, Long buyerId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 7) {
            throw new RuntimeException("订单状态异常，不需要支付下一阶段");
        }
        
        // 找到下一个待开始的阶段
        List<TfOrderStage> allStages = getOrderStages(orderId);
        TfOrderStage nextStage = null;
        for (TfOrderStage s : allStages) {
            if (s.getStatus() == 0) {
                nextStage = s;
                break;
            }
        }
        
        if (nextStage == null) {
            throw new RuntimeException("没有待支付的阶段");
        }
        
        BigDecimal amount = nextStage.getAmount();
        
        // 检查用户余额
        SysUser buyer = userService.getById(buyerId);
        if (buyer.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，请先充值。当前余额: " + buyer.getBalance() + "，需支付: " + amount);
        }
        
        // 托管下一阶段款项（从余额转入冻结金额）
        // 注意：updateFrozenAmount(true) 内部已经会从余额扣除并增加冻结金额，不需要额外调用 updateBalance
        userService.updateFrozenAmount(buyerId, amount, true);
        
        // 更新订单托管金额
        order.setDepositAmount(order.getDepositAmount().add(amount));
        
        // 获取更新后的余额
        buyer = userService.getById(buyerId);
        
        // 创建交易记录
        TfTransaction trans = new TfTransaction();
        trans.setUserId(buyerId);
        trans.setOrderId(orderId);
        trans.setStageId(nextStage.getId());
        trans.setType("deposit");
        trans.setAmount(amount);
        trans.setBalanceAfter(buyer.getBalance());
        trans.setTitle("阶段托管 - " + nextStage.getStageName());
        trans.setRemark("订单号: " + order.getOrderNo());
        trans.setStatus(1);
        trans.setEvidenceHash(IdUtil.fastSimpleUUID());
        transactionMapper.insert(trans);
        
        // 开始下一阶段
        nextStage.setStatus(1); // 进行中
        stageMapper.updateById(nextStage);
        
        // 订单回到进行中状态
        order.setStatus(2);
        
        return updateById(order);
    }

    /**
     * 取消订单
     * - 状态1（待支付/待托管）：直接取消，无需退款
     * - 状态0（待接单/已发布）：取消并退回已托管的第一阶段款项
     */
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        TfOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有买家可以取消订单
        boolean isBuyer = order.getBuyerId().equals(userId);
        if (!isBuyer) {
            throw new RuntimeException("只有发布者可以取消订单");
        }
        
        // 只有待支付(1)和待接单(0)状态可以取消
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new RuntimeException("订单已被接单，无法取消。如有问题请申请仲裁");
        }
        
        order.setStatus(5); // 已取消
        
        // 如果是待接单状态(0)，退回已托管的第一阶段款项
        if (order.getDepositAmount() != null && order.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal refundAmount = order.getDepositAmount();
            userService.updateFrozenAmount(order.getBuyerId(), refundAmount, false); // 解冻
            userService.updateBalance(order.getBuyerId(), refundAmount, true); // 退回余额
            
            // 获取更新后的余额
            SysUser buyer = userService.getById(order.getBuyerId());
            
            // 创建退款交易记录
            TfTransaction trans = new TfTransaction();
            trans.setUserId(order.getBuyerId());
            trans.setOrderId(orderId);
            trans.setType("refund");
            trans.setAmount(refundAmount);
            trans.setBalanceAfter(buyer.getBalance());
            trans.setTitle("取消订单退款 - " + order.getTitle());
            trans.setRemark("订单号: " + order.getOrderNo());
            trans.setStatus(1);
            trans.setEvidenceHash(IdUtil.fastSimpleUUID());
            transactionMapper.insert(trans);
        }
        
        return updateById(order);
    }

    /**
     * 获取用户订单统计数据
     */
    @Override
    public Map<String, Object> getUserStats(Long userId, String role) {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询用户所有订单
        LambdaQueryWrapper<TfOrder> wrapper = new LambdaQueryWrapper<>();
        if ("user".equals(role)) {
            wrapper.eq(TfOrder::getBuyerId, userId);
        } else {
            // 商家只统计已开始执行的订单（status >= 2）
            wrapper.eq(TfOrder::getSellerId, userId);
            wrapper.ge(TfOrder::getStatus, 2);
        }
        
        List<TfOrder> orders = list(wrapper);
        
        // 统计各状态数量
        int total = orders.size();
        long needPay = orders.stream().filter(o -> o.getStatus() == 1).count(); // 待支付
        long waiting = orders.stream().filter(o -> o.getStatus() == 0).count(); // 待接单
        long inProgress = orders.stream().filter(o -> o.getStatus() == 2).count(); // 进行中
        long pending = orders.stream().filter(o -> o.getStatus() == 3).count(); // 待验收
        long completed = orders.stream().filter(o -> o.getStatus() == 4).count(); // 已完成
        long cancelled = orders.stream().filter(o -> o.getStatus() == 5).count();
        long disputes = orders.stream().filter(o -> o.getStatus() == 6).count();
        
        // 计算托管中的资金（待接单、进行中、待验收的订单）
        BigDecimal frozenAmount = orders.stream()
                .filter(o -> o.getStatus() >= 0 && o.getStatus() <= 3 && o.getStatus() != 1)
                .map(o -> {
                    BigDecimal deposit = o.getDepositAmount() != null ? o.getDepositAmount() : BigDecimal.ZERO;
                    BigDecimal released = o.getReleasedAmount() != null ? o.getReleasedAmount() : BigDecimal.ZERO;
                    return deposit.subtract(released); // 托管金额减去已释放金额
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算本月收入（商家）
        BigDecimal monthIncome = BigDecimal.ZERO;
        int monthOrders = 0;
        if ("merchant".equals(role)) {
            LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            for (TfOrder order : orders) {
                if (order.getCompletedTime() != null && order.getCompletedTime().isAfter(monthStart)) {
                    monthIncome = monthIncome.add(order.getReleasedAmount() != null ? order.getReleasedAmount() : BigDecimal.ZERO);
                    monthOrders++;
                }
            }
        }
        
        // 获取用户信息
        SysUser user = userService.getById(userId);
        BigDecimal balance = user != null && user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        Integer creditScore = user != null && user.getCreditScore() != null ? user.getCreditScore() : 600;
        
        // 计算好评率（完成订单中的好评比例，暂时假设都是好评）
        double goodRate = completed > 0 ? 100.0 : 0.0;
        
        // 返回统计结果
        stats.put("total", total);
        stats.put("needPay", needPay);
        stats.put("waiting", waiting);
        stats.put("inProgress", inProgress);
        stats.put("pending", pending);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("disputes", disputes);
        stats.put("frozenAmount", frozenAmount);
        stats.put("balance", balance);
        stats.put("monthIncome", monthIncome);
        stats.put("monthOrders", monthOrders);
        stats.put("creditScore", creditScore);
        stats.put("goodRate", goodRate);
        
        return stats;
    }
    
    // ============ 私有方法 ============
    
    /**
     * 填充订单信息（买家/卖家名称、分类名称、阶段统计）
     */
    private void fillOrderInfo(TfOrder order) {
        // 填充买家名称
        if (order.getBuyerId() != null) {
            SysUser buyer = userService.getById(order.getBuyerId());
            if (buyer != null) {
                order.setBuyerName(buyer.getNickname() != null ? buyer.getNickname() : buyer.getPhone());
            }
        }
        
        // 填充卖家名称
        if (order.getSellerId() != null) {
            SysUser seller = userService.getById(order.getSellerId());
            if (seller != null) {
                order.setSellerName(seller.getNickname() != null ? seller.getNickname() : seller.getPhone());
            }
        }
        
        // 填充分类名称
        if (order.getCategoryId() != null) {
            SysCategory category = categoryMapper.selectById(order.getCategoryId());
            if (category != null) {
                order.setCategoryName(category.getName());
            }
        }
    }
    
    /**
     * 获取订单阶段列表
     */
    private List<TfOrderStage> getOrderStages(Long orderId) {
        return stageMapper.selectList(
            new LambdaQueryWrapper<TfOrderStage>()
                .eq(TfOrderStage::getOrderId, orderId)
                .orderByAsc(TfOrderStage::getSort)
        );
    }
}
