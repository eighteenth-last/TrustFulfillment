package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfBankCard;
import com.trustfulfillment.entity.TfTransaction;
import com.trustfulfillment.mapper.TfBankCardMapper;
import com.trustfulfillment.mapper.TfTransactionMapper;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private TfTransactionMapper transactionMapper;

    @Autowired
    private TfBankCardMapper bankCardMapper;

    @Autowired
    private ConfigService configService;

    /**
     * 获取钱包信息
     */
    @GetMapping("/info")
    public Result<?> getWalletInfo() {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("balance", user.getBalance());
        info.put("frozenAmount", user.getFrozenAmount());

        // 计算总收入和总支出
        LambdaQueryWrapper<TfTransaction> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(TfTransaction::getUserId, userId)
                .in(TfTransaction::getType, "recharge", "release") // 充值、释放收款
                .eq(TfTransaction::getStatus, 1);

        LambdaQueryWrapper<TfTransaction> expenseWrapper = new LambdaQueryWrapper<>();
        expenseWrapper.eq(TfTransaction::getUserId, userId)
                .in(TfTransaction::getType, "withdraw", "deposit") // 提现、支付托管
                .eq(TfTransaction::getStatus, 1);

        BigDecimal totalIncome = transactionMapper.selectList(incomeWrapper).stream()
                .map(TfTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionMapper.selectList(expenseWrapper).stream()
                .map(TfTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        info.put("totalIncome", totalIncome);
        info.put("totalExpense", totalExpense);

        return Result.success(info);
    }

    /**
     * 获取交易记录
     */
    @GetMapping("/transactions")
    public Result<?> getTransactions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "type", required = false) String type) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<TfTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfTransaction::getUserId, userId);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(TfTransaction::getType, type);
        }
        
        wrapper.orderByDesc(TfTransaction::getCreateTime);

        IPage<TfTransaction> transactions = transactionMapper.selectPage(
                new Page<>(page, size), wrapper);

        return Result.success(transactions);
    }

    /**
     * 充值（模拟）
     */
    @PostMapping("/recharge")
    public Result<?> recharge(@RequestBody Map<String, Object> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("充值金额必须大于0");
        }

        try {
            // 更新余额
            userService.updateBalance(userId, amount, true);
            
            // 获取更新后的余额
            SysUser user = userService.getById(userId);

            // 创建交易记录
            TfTransaction trans = new TfTransaction();
            trans.setUserId(userId);
            trans.setType("recharge");
            trans.setAmount(amount);
            trans.setBalanceAfter(user.getBalance());
            trans.setTitle("账户充值");
            trans.setStatus(1);
            transactionMapper.insert(trans);

            return Result.success("充值成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提现申请
     */
    @PostMapping("/withdraw")
    public Result<?> withdraw(@RequestBody Map<String, Object> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("提现金额必须大于0");
        }

        // 检查最低提现金额
        BigDecimal minAmount = configService.getWithdrawMinAmount();
        if (amount.compareTo(minAmount) < 0) {
            return Result.error("最低提现金额为 ¥" + minAmount);
        }

        // 计算手续费
        BigDecimal feeRate = configService.getWithdrawFeeRate();
        BigDecimal fee = amount.multiply(feeRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal totalDeduct = amount.add(fee); // 实际扣除 = 提现金额 + 手续费

        SysUser user = userService.getById(userId);
        if (user.getBalance().compareTo(totalDeduct) < 0) {
            return Result.error("余额不足（含手续费 ¥" + fee + "）");
        }

        try {
            // 扣除余额（含手续费）
            userService.updateBalance(userId, totalDeduct, false);
            
            // 获取更新后的余额
            user = userService.getById(userId);

            // 创建交易记录
            TfTransaction trans = new TfTransaction();
            trans.setUserId(userId);
            trans.setType("withdraw");
            trans.setAmount(amount);
            trans.setBalanceAfter(user.getBalance());
            trans.setTitle("账户提现");
            trans.setRemark("提现金额: ¥" + amount + "，手续费: ¥" + fee + "（费率: " + feeRate + "%）");
            trans.setStatus(0); // 处理中
            transactionMapper.insert(trans);

            return Result.success("提现申请已提交，提现金额 ¥" + amount + "，手续费 ¥" + fee + "，预计1-3个工作日到账", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取银行卡列表
     */
    @GetMapping("/bank-cards")
    public Result<?> getBankCards() {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        LambdaQueryWrapper<TfBankCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfBankCard::getUserId, userId)
               .eq(TfBankCard::getStatus, 1)
               .orderByDesc(TfBankCard::getIsDefault)
               .orderByDesc(TfBankCard::getCreateTime);
        
        List<TfBankCard> cards = bankCardMapper.selectList(wrapper);
        return Result.success(cards);
    }

    /**
     * 添加银行卡
     */
    @PostMapping("/bank-cards")
    public Result<?> addBankCard(@RequestBody Map<String, Object> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        String holderName = (String) params.get("holderName");
        String bankName = (String) params.get("bankName");
        String cardNo = (String) params.get("cardNo");
        Boolean isDefault = params.get("isDefault") != null && (Boolean) params.get("isDefault");
        
        if (holderName == null || bankName == null || cardNo == null) {
            return Result.error("请填写完整的银行卡信息");
        }
        
        // 检查卡号是否已存在
        LambdaQueryWrapper<TfBankCard> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(TfBankCard::getUserId, userId)
                    .eq(TfBankCard::getCardNo, cardNo)
                    .eq(TfBankCard::getStatus, 1);
        if (bankCardMapper.selectCount(checkWrapper) > 0) {
            return Result.error("该银行卡已添加");
        }
        
        // 如果设为默认，先取消其他默认卡
        if (isDefault) {
            TfBankCard updateCard = new TfBankCard();
            updateCard.setIsDefault(0);
            LambdaQueryWrapper<TfBankCard> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(TfBankCard::getUserId, userId);
            bankCardMapper.update(updateCard, updateWrapper);
        }
        
        TfBankCard card = new TfBankCard();
        card.setUserId(userId);
        card.setHolderName(holderName);
        card.setBankName(bankName);
        card.setCardNo(cardNo);
        card.setCardType(1); // 默认借记卡
        card.setIsDefault(isDefault ? 1 : 0);
        card.setStatus(1);
        
        bankCardMapper.insert(card);
        
        return Result.success("银行卡添加成功", null);
    }

    /**
     * 删除银行卡
     */
    @DeleteMapping("/bank-cards/{cardId}")
    public Result<?> deleteBankCard(@PathVariable("cardId") Long cardId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 检查银行卡是否属于当前用户
        TfBankCard card = bankCardMapper.selectById(cardId);
        if (card == null || !card.getUserId().equals(userId)) {
            return Result.error("银行卡不存在");
        }
        
        // 软删除
        card.setStatus(0);
        bankCardMapper.updateById(card);
        
        return Result.success("删除成功", null);
    }

    /**
     * 设置默认银行卡
     */
    @PutMapping("/bank-cards/{cardId}/default")
    public Result<?> setDefaultCard(@PathVariable("cardId") Long cardId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 检查银行卡是否属于当前用户
        TfBankCard card = bankCardMapper.selectById(cardId);
        if (card == null || !card.getUserId().equals(userId) || card.getStatus() != 1) {
            return Result.error("银行卡不存在");
        }
        
        // 取消其他默认卡
        TfBankCard updateCard = new TfBankCard();
        updateCard.setIsDefault(0);
        LambdaQueryWrapper<TfBankCard> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(TfBankCard::getUserId, userId);
        bankCardMapper.update(updateCard, updateWrapper);
        
        // 设置当前卡为默认
        card.setIsDefault(1);
        bankCardMapper.updateById(card);
        
        return Result.success("设置成功", null);
    }
}
