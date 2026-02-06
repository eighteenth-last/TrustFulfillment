package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.*;
import com.trustfulfillment.mapper.*;
import com.trustfulfillment.entity.TfServiceMessage;
import com.trustfulfillment.entity.TfContract;
import com.trustfulfillment.entity.SysBank;
import com.trustfulfillment.entity.SysNotification;
import com.trustfulfillment.entity.SysCategory;
import com.trustfulfillment.entity.SysConfig;
import com.trustfulfillment.mapper.SysConfigMapper;
import com.trustfulfillment.service.CommissionService;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.SmsService;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private TfOrderMapper orderMapper;

    @Autowired
    private TfTransactionMapper transactionMapper;

    @Autowired
    private TfDisputeMapper disputeMapper;

    @Autowired
    private TfMerchantMapper merchantMapper;

    @Autowired
    private TfMerchantApplyMapper merchantApplyMapper;

    @Autowired
    private TfServiceMessageMapper serviceMessageMapper;

    @Autowired
    private TfContractMapper contractMapper;

    @Autowired
    private SysBankMapper bankMapper;

    @Autowired
    private SysNotificationMapper notificationMapper;

    @Autowired
    private SysCategoryMapper categoryMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private TfCommissionMapper commissionMapper;

    @Autowired
    private SmsService smsService;

    /**
     * 管理员登录 (使用手机号+密码)
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params, jakarta.servlet.http.HttpServletRequest request) {
        String phone = params.get("phone");
        String password = params.get("password");
        String ip = getClientIp(request);

        try {
            SysUser user = userService.getByPhone(phone);
            if (user == null) {
                return Result.error("用户不存在");
            }

            if (!"admin".equals(user.getRole())) {
                return Result.error("非管理员账号");
            }

            // 使用密码登录，传递IP
            com.trustfulfillment.dto.LoginResultDTO result = userService.loginByPhonePassword(
                    phone, password, ip, null, "admin");

            Map<String, Object> data = new HashMap<>();
            data.put("token", result.getToken());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("phone", user.getPhone());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("role", user.getRole());
            data.put("userInfo", userInfo);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        StpUtil.logout();
        return Result.success("登出成功", null);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<?> getAdminInfo() {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("phone", user.getPhone());
        result.put("nickname", user.getNickname());
        result.put("username", user.getNickname());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        return Result.success(result);
    }

    /**
     * 更新管理员个人信息
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Map<String, String> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 更新允许修改的字段
        String nickname = params.get("nickname");
        String phone = params.get("phone");
        String email = params.get("email");

        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        if (phone != null && !phone.isEmpty()) {
            SysUser existUser = userService.getByPhone(phone);
            if (existUser != null && !existUser.getId().equals(userId)) {
                return Result.error("该手机号已被其他用户使用");
            }
            user.setPhone(phone);
        }
        if (email != null) {
            user.setEmail(email);
        }

        boolean success = userService.updateById(user);
        if (success) {
            return Result.success("个人信息更新成功", null);
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 修改管理员密码
     */
    @PostMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String confirmPassword = params.get("confirmPassword");

        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.error("请输入原密码");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error("请输入新密码");
        }
        if (!newPassword.equals(confirmPassword)) {
            return Result.error("两次输入的密码不一致");
        }

        try {
            Long userId = StpUtil.getLoginIdAsLong();
            userService.setPassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/dashboard/stats")
    public Result<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 信托托管总额
            BigDecimal totalTrust = orderMapper.selectTotalDepositAmount();
            stats.put("totalTrust", totalTrust != null ? totalTrust : BigDecimal.ZERO);

            // 活跃商户数（有isMerchant=1的用户）
            Long activeMerchants = userService.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getIsMerchant, 1));
            stats.put("activeMerchants", activeMerchants != null ? activeMerchants : 0L);

            // 争议订单率
            Long totalOrders = orderMapper.selectCount(null);
            Long disputeOrders = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 6));
            totalOrders = totalOrders != null ? totalOrders : 0L;
            disputeOrders = disputeOrders != null ? disputeOrders : 0L;
            double disputeRate = totalOrders > 0 ? (disputeOrders * 100.0 / totalOrders) : 0;
            stats.put("disputeRate", Math.round(disputeRate * 10.0) / 10.0);

            // 安全运行天数 - 从第一笔订单创建时间开始计算
            TfOrder firstOrder = orderMapper.selectOne(new LambdaQueryWrapper<TfOrder>()
                    .orderByAsc(TfOrder::getCreateTime)
                    .last("LIMIT 1"));
            long runningDays = 0;
            if (firstOrder != null && firstOrder.getCreateTime() != null) {
                runningDays = java.time.temporal.ChronoUnit.DAYS.between(
                        firstOrder.getCreateTime().toLocalDate(), LocalDate.now()) + 1;
            }
            stats.put("runningDays", runningDays);

            // 今日统计
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            Long todayOrders = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                    .ge(TfOrder::getCreateTime, todayStart));
            stats.put("todayOrders", todayOrders != null ? todayOrders : 0L);

            Long todayUsers = userService.countNewUsersToday();
            stats.put("todayUsers", todayUsers != null ? todayUsers : 0L);

            BigDecimal todayInflow = transactionMapper.selectTodayInflow();
            stats.put("todayInflow", todayInflow != null ? todayInflow : BigDecimal.ZERO);

            // 待处理纠纷 = 待处理(0) + 处理中(1)，即所有未完结的纠纷
            Long pendingDisputes = disputeMapper.selectCount(new LambdaQueryWrapper<TfDispute>()
                    .in(TfDispute::getStatus, 0, 1));
            stats.put("pendingDisputes", pendingDisputes != null ? pendingDisputes : 0L);

            // ========== 资金趋势图表数据 - 最近12个月 ==========
            List<Map<String, Object>> chartData = new ArrayList<>();
            for (int i = 11; i >= 0; i--) {
                LocalDate month = LocalDate.now().minusMonths(i).withDayOfMonth(1);
                Map<String, Object> item = new HashMap<>();
                item.put("month", month.getMonthValue() + "月");
                BigDecimal amount = transactionMapper.selectMonthAmount(month.getYear(), month.getMonthValue());
                item.put("amount", amount != null ? amount : BigDecimal.ZERO);
                chartData.add(item);
            }
            stats.put("chartData", chartData);

            // ========== 订单状态分布 ==========
            List<Map<String, Object>> orderStatusData = new ArrayList<>();
            // 进行中 (status=2)
            Long inProgress = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 2));
            orderStatusData.add(createPieData("进行中", inProgress, "#00AFE1"));
            // 已完成 (status=4)
            Long completed = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 4));
            orderStatusData.add(createPieData("已完成", completed, "#10B981"));
            // 待接单 (status=0)
            Long pending = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 0));
            orderStatusData.add(createPieData("待接单", pending, "#F59E0B"));
            // 待验收 (status=3)
            Long toAccept = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 3));
            orderStatusData.add(createPieData("待验收", toAccept, "#8B5CF6"));
            // 纠纷中 (status=6)
            Long dispute = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 6));
            orderStatusData.add(createPieData("纠纷中", dispute, "#EF4444"));
            // 其他 (status=1,5,7,8,9)
            Long other = orderMapper
                    .selectCount(new LambdaQueryWrapper<TfOrder>().in(TfOrder::getStatus, 1, 5, 7, 8, 9));
            orderStatusData.add(createPieData("其他", other, "#9CA3AF"));
            stats.put("orderStatusData", orderStatusData);

            // ========== 用户增长趋势 - 最近6个月 ==========
            List<Long> userGrowthData = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
                LocalDate monthEnd = monthStart.plusMonths(1);
                Long count = userService.count(new LambdaQueryWrapper<SysUser>()
                        .ge(SysUser::getCreateTime, monthStart.atStartOfDay())
                        .lt(SysUser::getCreateTime, monthEnd.atStartOfDay()));
                userGrowthData.add(count != null ? count : 0L);
            }
            stats.put("userGrowthData", userGrowthData);

            // ========== 商户业绩排行 - Top5 ==========
            List<Map<String, Object>> merchantRankData = new ArrayList<>();
            // 按已完成订单的总金额排名
            List<TfOrder> completedOrders = orderMapper.selectList(new LambdaQueryWrapper<TfOrder>()
                    .eq(TfOrder::getStatus, 4)
                    .isNotNull(TfOrder::getSellerId));
            // 按卖家ID分组统计
            Map<Long, BigDecimal> sellerAmounts = new HashMap<>();
            for (TfOrder order : completedOrders) {
                if (order.getSellerId() != null && order.getTotalAmount() != null) {
                    sellerAmounts.merge(order.getSellerId(), order.getTotalAmount(), BigDecimal::add);
                }
            }
            // 排序取前5
            sellerAmounts.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(5)
                    .forEach(entry -> {
                        SysUser seller = userService.getById(entry.getKey());
                        Map<String, Object> item = new HashMap<>();
                        item.put("name", seller != null && seller.getNickname() != null ? seller.getNickname()
                                : "商户" + entry.getKey());
                        item.put("value", entry.getValue().longValue());
                        merchantRankData.add(item);
                    });
            // 如果不足5个，补充空数据
            while (merchantRankData.size() < 5) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", "暂无数据");
                item.put("value", 0L);
                merchantRankData.add(item);
            }
            stats.put("merchantRankData", merchantRankData);

            // ========== 交易金额分布 ==========
            List<Map<String, Object>> amountDistData = new ArrayList<>();
            // 0-1k
            Long range1 = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                    .lt(TfOrder::getTotalAmount, 1000));
            amountDistData.add(createDistData("0-1k", range1));
            // 1k-5k
            Long range2 = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                    .ge(TfOrder::getTotalAmount, 1000).lt(TfOrder::getTotalAmount, 5000));
            amountDistData.add(createDistData("1k-5k", range2));
            // 5k-1w
            Long range3 = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                    .ge(TfOrder::getTotalAmount, 5000).lt(TfOrder::getTotalAmount, 10000));
            amountDistData.add(createDistData("5k-1w", range3));
            // 1w-5w
            Long range4 = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                    .ge(TfOrder::getTotalAmount, 10000).lt(TfOrder::getTotalAmount, 50000));
            amountDistData.add(createDistData("1w-5w", range4));
            // 5w以上
            Long range5 = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                    .ge(TfOrder::getTotalAmount, 50000));
            amountDistData.add(createDistData("5w以上", range5));
            stats.put("amountDistData", amountDistData);

            // ========== 平台收入统计 - 最近6个月 ==========
            List<Map<String, Object>> incomeData = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
                LocalDate monthEnd = monthStart.plusMonths(1);
                Map<String, Object> item = new HashMap<>();
                item.put("month", monthStart.getMonthValue() + "月");
                // 平台提成收入 - 从 tf_commission 表查询
                BigDecimal platformFee = commissionMapper.selectTotalCommissionByDateRange(
                        monthStart.atStartOfDay(), monthEnd.atStartOfDay());
                item.put("platformFee", platformFee != null ? platformFee.abs() : BigDecimal.ZERO);
                // 提现手续费 - type=2 且金额为负（扣除的手续费）
                BigDecimal withdrawFee = transactionMapper.selectWithdrawFeeByDateRange(
                        monthStart.atStartOfDay(), monthEnd.atStartOfDay());
                item.put("withdrawFee", withdrawFee != null ? withdrawFee.abs() : BigDecimal.ZERO);
                incomeData.add(item);
            }
            stats.put("incomeData", incomeData);

            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    // 辅助方法：创建饼图数据
    private Map<String, Object> createPieData(String name, Long value, String color) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", value != null ? value : 0L);
        Map<String, String> itemStyle = new HashMap<>();
        itemStyle.put("color", color);
        data.put("itemStyle", itemStyle);
        return data;
    }

    // 辅助方法：创建分布数据
    private Map<String, Object> createDistData(String range, Long count) {
        Map<String, Object> data = new HashMap<>();
        data.put("range", range);
        data.put("count", count != null ? count : 0L);
        return data;
    }

    /**
     * 获取待处理任务
     */
    @GetMapping("/dashboard/pending-tasks")
    public Result<?> getPendingTasks() {
        try {
            List<Map<String, Object>> tasks = new ArrayList<>();

            // 待审核订单
            List<TfOrder> pendingOrders = orderMapper.selectList(new LambdaQueryWrapper<TfOrder>()
                    .eq(TfOrder::getStatus, 0)
                    .orderByDesc(TfOrder::getCreateTime)
                    .last("LIMIT 2"));
            if (pendingOrders != null) {
                for (TfOrder order : pendingOrders) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("id", order.getId());
                    task.put("type", "任务审核");
                    task.put("title", order.getTitle());
                    task.put("description", order.getDescription());
                    task.put("time", order.getCreateTime());
                    task.put("action", "审核通过");
                    tasks.add(task);
                }
            }

            // 待审核商家
            List<SysUser> pendingMerchants = userService.listPendingMerchants(2);
            if (pendingMerchants != null) {
                for (SysUser merchant : pendingMerchants) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("id", merchant.getId());
                    task.put("type", "商家入驻");
                    task.put("title", merchant.getNickname());
                    task.put("description", "申请入驻平台，资质待审核");
                    task.put("time", merchant.getCreateTime());
                    task.put("action", "审核资质");
                    tasks.add(task);
                }
            }

            // 待处理纠纷
            List<TfDispute> pendingDisputes = disputeMapper.selectList(new LambdaQueryWrapper<TfDispute>()
                    .eq(TfDispute::getStatus, 0)
                    .orderByDesc(TfDispute::getCreateTime)
                    .last("LIMIT 2"));
            if (pendingDisputes != null) {
                for (TfDispute dispute : pendingDisputes) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("id", dispute.getId());
                    task.put("type", "纠纷处理");
                    task.put("title", "订单争议");
                    task.put("description", dispute.getReason());
                    task.put("time", dispute.getCreateTime());
                    task.put("action", "介入调解");
                    tasks.add(task);
                }
            }

            return Result.success(tasks);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取待处理任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单列表（管理端）
     */
    @GetMapping("/orders")
    public Result<?> getOrderList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword) {

        LambdaQueryWrapper<TfOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TfOrder::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(TfOrder::getTitle, keyword)
                    .or().like(TfOrder::getOrderNo, keyword));
        }
        wrapper.orderByDesc(TfOrder::getCreateTime);

        IPage<TfOrder> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充用户名
        for (TfOrder order : pageResult.getRecords()) {
            SysUser buyer = userService.getById(order.getBuyerId());
            if (buyer != null) {
                order.setBuyerName(buyer.getNickname());
            }
            if (order.getSellerId() != null) {
                // 从商户表获取商家店铺名称
                TfMerchant merchant = merchantMapper.selectOne(
                        new LambdaQueryWrapper<TfMerchant>().eq(TfMerchant::getUserId, order.getSellerId()));
                if (merchant != null) {
                    order.setSellerName(merchant.getShopName());
                } else {
                    // 兼容旧数据，回退到用户昵称
                    SysUser seller = userService.getById(order.getSellerId());
                    if (seller != null) {
                        order.setSellerName(seller.getNickname());
                    }
                }
            }
        }

        return Result.success(pageResult);
    }

    /**
     * 审核订单
     */
    @PostMapping("/orders/{id}/audit")
    public Result<?> auditOrder(@PathVariable("id") Long id, @RequestBody Map<String, Object> params) {
        Boolean approved = (Boolean) params.get("approved");
        String reason = (String) params.get("reason");

        TfOrder order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (approved) {
            order.setStatus(1); // 待托管
        } else {
            order.setStatus(5); // 已取消
        }
        orderMapper.updateById(order);

        return Result.success(approved ? "审核通过" : "已驳回", null);
    }

    /**
     * 获取商户列表（从商户表）
     */
    @GetMapping("/merchants")
    public Result<?> getMerchantList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword) {

        LambdaQueryWrapper<TfMerchant> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TfMerchant::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(TfMerchant::getShopName, keyword)
                    .or().like(TfMerchant::getMerchantNo, keyword)
                    .or().like(TfMerchant::getCompanyName, keyword));
        }
        wrapper.orderByDesc(TfMerchant::getCreateTime);

        IPage<TfMerchant> pageResult = merchantMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充用户信息
        for (TfMerchant merchant : pageResult.getRecords()) {
            SysUser user = userService.getById(merchant.getUserId());
            if (user != null) {
                merchant.setUserName(user.getNickname());
            }
        }

        return Result.success(pageResult);
    }

    /**
     * 获取商户申请列表
     */
    @GetMapping("/merchant-applies")
    public Result<?> getMerchantApplyList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status) {

        LambdaQueryWrapper<TfMerchantApply> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TfMerchantApply::getStatus, status);
        }
        wrapper.orderByDesc(TfMerchantApply::getCreateTime);

        IPage<TfMerchantApply> pageResult = merchantApplyMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充用户信息
        for (TfMerchantApply apply : pageResult.getRecords()) {
            SysUser user = userService.getById(apply.getUserId());
            if (user != null) {
                apply.setUserName(user.getNickname());
                apply.setUserPhone(user.getPhone());
            }
        }

        return Result.success(pageResult);
    }

    /**
     * 审核商户申请
     */
    @PostMapping("/merchants/{id}/audit")
    public Result<?> auditMerchant(@PathVariable("id") Long id, @RequestBody Map<String, Object> params) {
        Boolean approved = (Boolean) params.get("approved");
        String reason = (String) params.get("reason");

        TfMerchantApply apply = merchantApplyMapper.selectById(id);
        if (apply == null) {
            return Result.error("申请不存在");
        }

        if (apply.getStatus() != 0) {
            return Result.error("该申请已处理");
        }

        Long auditUserId = StpUtil.getLoginIdAsLong();

        if (approved) {
            // 通过审核 - 创建商户记录
            TfMerchant merchant = new TfMerchant();
            merchant.setUserId(apply.getUserId());
            merchant.setMerchantNo("M" + System.currentTimeMillis());
            merchant.setMerchantType(apply.getMerchantType());
            merchant.setShopName(apply.getShopName());
            merchant.setCompanyName(apply.getCompanyName());
            merchant.setLegalPerson(apply.getLegalPerson());
            merchant.setLegalIdCard(apply.getLegalIdCard());
            merchant.setLicenseNo(apply.getLicenseNo());
            merchant.setLicenseImg(apply.getLicenseImg());
            merchant.setIdCardFront(apply.getIdCardFront());
            merchant.setIdCardBack(apply.getIdCardBack());
            merchant.setOtherDocs(apply.getOtherDocs());
            merchant.setContactPhone(apply.getContactPhone());
            merchant.setContactEmail(apply.getContactEmail());
            merchant.setBusinessAddress(apply.getBusinessAddress());
            merchant.setBusinessScope(apply.getBusinessScope());
            merchant.setBusinessCategories(apply.getBusinessCategories());
            // 根据商户类型设置提成比例
            merchant.setCommissionRate(apply.getMerchantType() == 1 ? new BigDecimal("8.00") : new BigDecimal("5.00"));
            merchant.setStatus(1);
            merchant.setVerifyTime(LocalDateTime.now());
            merchantMapper.insert(merchant);

            // 更新用户为商户
            SysUser user = userService.getById(apply.getUserId());
            user.setIsMerchant(1);
            user.setMerchantId(merchant.getId());
            userService.updateById(user);

            // 更新申请状态
            apply.setStatus(1);
            apply.setAuditUserId(auditUserId);
            apply.setAuditTime(LocalDateTime.now());
            apply.setAuditRemark(reason);
            merchantApplyMapper.updateById(apply);

            return Result.success("审核通过，商户已创建", null);
        } else {
            // 拒绝申请
            apply.setStatus(2);
            apply.setAuditUserId(auditUserId);
            apply.setAuditTime(LocalDateTime.now());
            apply.setAuditRemark(reason);
            merchantApplyMapper.updateById(apply);

            return Result.success("已拒绝申请", null);
        }
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<?> getUserList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {

        IPage<SysUser> pageResult = userService.pageUsers(page, size, keyword);
        return Result.success(pageResult);
    }

    /**
     * 搜索用户（用于下拉选择）
     */
    @GetMapping("/users/search")
    public Result<?> searchUsers(@RequestParam(value = "keyword", required = false) String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(SysUser::getRole, "admin"); // 排除管理员

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword)
                    .or().like(SysUser::getRealName, keyword));
        }

        wrapper.select(SysUser::getId, SysUser::getNickname, SysUser::getPhone, SysUser::getRealName)
                .last("LIMIT 20"); // 限制返回数量

        List<SysUser> users = userService.list(wrapper);

        // 构建简化的返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysUser user : users) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", user.getId());
            item.put("nickname", user.getNickname());
            item.put("phone", user.getPhone());
            item.put("realName", user.getRealName());
            // 显示名称：优先昵称，其次实名，最后手机号
            String displayName = user.getNickname();
            if (displayName == null || displayName.isEmpty()) {
                displayName = user.getRealName();
            }
            if (displayName == null || displayName.isEmpty()) {
                displayName = user.getPhone();
            }
            item.put("displayName", displayName);
            result.add(item);
        }

        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{id}")
    public Result<?> getUserDetail(@PathVariable("id") Long id) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("phone", user.getPhone());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("gender", user.getGender());
        result.put("role", user.getRole());
        result.put("isMerchant", user.getIsMerchant() != null && user.getIsMerchant() == 1);
        result.put("merchantId", user.getMerchantId());
        result.put("email", user.getEmail());
        result.put("balance", user.getBalance());
        result.put("frozenAmount", user.getFrozenAmount());
        result.put("creditScore", user.getCreditScore());
        result.put("status", user.getStatus());
        result.put("verified", user.getVerified() != null && user.getVerified() == 1);
        result.put("realName", user.getRealName());
        result.put("idCard", user.getIdCard());
        result.put("lastLoginTime", user.getLastLoginTime());
        result.put("lastLoginIp", user.getLastLoginIp());
        result.put("registerSource", user.getRegisterSource());
        result.put("createTime", user.getCreateTime());
        result.put("updateTime", user.getUpdateTime());

        return Result.success(result);
    }

    /**
     * 更新用户状态
     */
    @PostMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable("id") Long id, @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");

        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setStatus(status);
        userService.updateById(user);

        return Result.success("操作成功", null);
    }

    /**
     * 获取纠纷列表
     */
    @GetMapping("/disputes")
    public Result<?> getDisputeList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status) {

        LambdaQueryWrapper<TfDispute> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TfDispute::getStatus, status);
        }
        wrapper.orderByDesc(TfDispute::getCreateTime);

        IPage<TfDispute> pageResult = disputeMapper.selectPage(new Page<>(page, size), wrapper);

        // 构建包含用户信息的结果列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (TfDispute dispute : pageResult.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", dispute.getId());
            item.put("orderId", dispute.getOrderId());
            item.put("status", dispute.getStatus());
            item.put("reason", dispute.getReason());
            item.put("evidenceUrls", dispute.getEvidenceUrls());
            item.put("createTime", dispute.getCreateTime());
            item.put("result", dispute.getResult());
            item.put("resultDesc", dispute.getResultDesc());

            // 获取订单和用户信息
            TfOrder order = orderMapper.selectById(dispute.getOrderId());
            if (order != null) {
                item.put("orderNo", order.getOrderNo());
                item.put("amount", order.getTotalAmount());

                // 获取甲方（买家）信息
                SysUser buyer = userService.getById(order.getBuyerId());
                item.put("buyerName", buyer != null ? buyer.getNickname() : "未知用户");

                // 获取乙方（卖家）信息 - 从商户表获取店铺名称
                if (order.getSellerId() != null) {
                    TfMerchant sellerMerchant = merchantMapper.selectOne(
                            new LambdaQueryWrapper<TfMerchant>().eq(TfMerchant::getUserId, order.getSellerId()));
                    if (sellerMerchant != null) {
                        item.put("sellerName", sellerMerchant.getShopName());
                    } else {
                        SysUser seller = userService.getById(order.getSellerId());
                        item.put("sellerName", seller != null ? seller.getNickname() : "未知用户");
                    }
                } else {
                    item.put("sellerName", "未接单");
                }
            } else {
                item.put("orderNo", "");
                item.put("amount", 0);
                item.put("buyerName", "未知");
                item.put("sellerName", "未知");
            }

            resultList.add(item);
        }

        // 构建分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", resultList);
        result.put("total", pageResult.getTotal());
        result.put("size", pageResult.getSize());
        result.put("current", pageResult.getCurrent());
        result.put("pages", pageResult.getPages());

        return Result.success(result);
    }

    /**
     * 处理纠纷
     */
    @PostMapping("/disputes/{id}/resolve")
    @Transactional
    public Result<?> resolveDispute(@PathVariable("id") Long id, @RequestBody Map<String, Object> params) {
        String result = (String) params.get("result");
        String reason = (String) params.get("reason");

        TfDispute dispute = disputeMapper.selectById(id);
        if (dispute == null) {
            return Result.error("纠纷不存在");
        }

        // 人工介入调解
        if ("mediate".equals(result)) {
            dispute.setStatus(1); // 处理中
            dispute.setResultDesc(reason);
            disputeMapper.updateById(dispute);
            return Result.success("已介入调解", null);
        }

        // 获取关联订单
        TfOrder order = orderMapper.selectById(dispute.getOrderId());
        if (order == null) {
            return Result.error("关联订单不存在");
        }

        // 裁决
        dispute.setStatus(2); // 已裁决
        // result: "buyer_win" -> 1, "seller_win" -> 2, "both" -> 3
        if ("buyer_win".equals(result)) {
            dispute.setResult(1);
            // 甲方胜诉：退款给买家，订单取消
            BigDecimal refundAmount = order.getDepositAmount().subtract(
                    order.getReleasedAmount() != null ? order.getReleasedAmount() : BigDecimal.ZERO);
            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 解冻并退款
                userService.updateFrozenAmount(order.getBuyerId(), refundAmount, false);
            }
            order.setStatus(5); // 已取消
        } else if ("seller_win".equals(result)) {
            dispute.setResult(2);
            // 乙方胜诉：释放剩余款项给卖家，订单完成
            BigDecimal releaseAmount = order.getDepositAmount().subtract(
                    order.getReleasedAmount() != null ? order.getReleasedAmount() : BigDecimal.ZERO);
            if (releaseAmount.compareTo(BigDecimal.ZERO) > 0) {
                userService.updateFrozenAmount(order.getBuyerId(), releaseAmount, false);
                userService.updateBalance(order.getSellerId(), releaseAmount, true);
                order.setReleasedAmount(order.getDepositAmount());
            }
            order.setStatus(4); // 已完成
            order.setCompletedTime(LocalDateTime.now());
        } else if ("both".equals(result)) {
            dispute.setResult(3);
            // 协商解决：恢复订单到进行中状态
            order.setStatus(2); // 进行中
        } else {
            return Result.error("无效的裁决结果");
        }

        dispute.setResultDesc(reason);
        dispute.setResolvedTime(LocalDateTime.now());
        disputeMapper.updateById(dispute);

        // 更新订单状态
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return Result.success("纠纷已处理，订单状态已更新", null);
    }

    /**
     * 获取信托资金统计
     */
    @GetMapping("/trust/stats")
    public Result<?> getTrustStats() {
        Map<String, Object> stats = new HashMap<>();

        BigDecimal totalDeposit = orderMapper.selectTotalDepositAmount();
        stats.put("totalDeposit", totalDeposit != null ? totalDeposit : BigDecimal.ZERO);

        BigDecimal totalReleased = orderMapper.selectTotalReleasedAmount();
        stats.put("totalReleased", totalReleased != null ? totalReleased : BigDecimal.ZERO);

        BigDecimal frozen = totalDeposit != null && totalReleased != null ? totalDeposit.subtract(totalReleased)
                : BigDecimal.ZERO;
        stats.put("frozenAmount", frozen);

        // 最近交易
        List<TfTransaction> recentTx = transactionMapper.selectList(
                new LambdaQueryWrapper<TfTransaction>()
                        .orderByDesc(TfTransaction::getCreateTime)
                        .last("LIMIT 10"));
        stats.put("recentTransactions", recentTx);

        return Result.success(stats);
    }

    @Autowired
    private com.trustfulfillment.service.RiskService riskService;

    @Autowired
    private TfRiskEventMapper riskEventMapper;

    /**
     * 获取风险告警
     */
    @GetMapping("/risk/alerts")
    public Result<?> getRiskAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();

        // 从风险事件表获取待处理的告警
        List<TfRiskEvent> riskEvents = riskService.getRiskAlerts(10);
        for (TfRiskEvent event : riskEvents) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", event.getId());
            alert.put("type", event.getEventType());
            alert.put("level", event.getRiskLevel());
            alert.put("icon", getAlertIcon(event.getEventType()));
            alert.put("title", event.getTitle());
            alert.put("description", event.getDescription());
            alert.put("time", event.getCreateTime());
            alert.put("userId", event.getUserId());
            alert.put("orderId", event.getOrderId());
            alert.put("amount", event.getAmount());
            alerts.add(alert);
        }

        // 同时检查大额纠纷订单（阈值10万）
        List<TfOrder> highRiskOrders = orderMapper.selectList(
                new LambdaQueryWrapper<TfOrder>()
                        .eq(TfOrder::getStatus, 6)
                        .gt(TfOrder::getTotalAmount, new BigDecimal("100000")));
        for (TfOrder order : highRiskOrders) {
            // 检查是否已有对应的风险事件
            Long existCount = riskEventMapper.selectCount(new LambdaQueryWrapper<TfRiskEvent>()
                    .eq(TfRiskEvent::getOrderId, order.getId())
                    .eq(TfRiskEvent::getEventType, TfRiskEvent.TYPE_DISPUTE));
            if (existCount == 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("id", "order_" + order.getId());
                alert.put("type", "high_value_dispute");
                alert.put("level", "high");
                alert.put("icon", "fas fa-exclamation-triangle");
                alert.put("title", "大额订单争议");
                alert.put("description", "订单 " + order.getOrderNo() + " 涉及金额: ¥" + order.getTotalAmount());
                alert.put("time", order.getUpdateTime());
                alert.put("orderId", order.getId());
                alert.put("amount", order.getTotalAmount());
                alerts.add(alert);
            }
        }

        // 检查长时间未处理的纠纷
        List<TfDispute> oldDisputes = disputeMapper.selectList(
                new LambdaQueryWrapper<TfDispute>()
                        .eq(TfDispute::getStatus, 0)
                        .lt(TfDispute::getCreateTime, LocalDateTime.now().minusDays(3)));
        for (TfDispute dispute : oldDisputes) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "dispute_" + dispute.getId());
            alert.put("type", "overdue_dispute");
            alert.put("level", "medium");
            alert.put("icon", "fas fa-clock");
            alert.put("title", "纠纷处理超时");
            alert.put("description", "纠纷已等待 "
                    + java.time.temporal.ChronoUnit.DAYS.between(dispute.getCreateTime().toLocalDate(), LocalDate.now())
                    + " 天");
            alert.put("time", dispute.getCreateTime());
            alert.put("disputeId", dispute.getId());
            alerts.add(alert);
        }

        return Result.success(alerts);
    }

    // 根据事件类型获取图标
    private String getAlertIcon(String eventType) {
        Map<String, String> iconMap = new HashMap<>();
        iconMap.put("large_transaction", "fas fa-money-bill-wave");
        iconMap.put("frequent_operation", "fas fa-clock");
        iconMap.put("abnormal_login", "fas fa-user-shield");
        iconMap.put("dispute", "fas fa-exclamation-triangle");
        iconMap.put("account_anomaly", "fas fa-user-times");
        iconMap.put("ip_risk", "fas fa-globe");
        iconMap.put("fraud_suspicion", "fas fa-robot");
        return iconMap.getOrDefault(eventType, "fas fa-exclamation-triangle");
    }

    /**
     * 获取风控统计数据
     */
    @GetMapping("/risk/stats")
    public Result<?> getRiskStats() {
        return Result.success(riskService.getRiskStats());
    }

    /**
     * 获取风控趋势数据
     */
    @GetMapping("/risk/trend")
    public Result<?> getRiskTrend() {
        return Result.success(riskService.getRiskTrend(7));
    }

    /**
     * 获取风控规则配置
     */
    @GetMapping("/risk/rules")
    public Result<?> getRiskRules() {
        return Result.success(riskService.getRiskRules());
    }

    /**
     * 保存风控规则配置
     */
    @PostMapping("/risk/rules")
    public Result<?> saveRiskRules(@RequestBody Map<String, Boolean> rules) {
        riskService.saveRiskRules(rules);
        return Result.success("保存成功", null);
    }

    /**
     * 获取风险事件列表
     */
    @GetMapping("/risk/events")
    public Result<?> getRiskEventList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "riskLevel", required = false) String riskLevel,
            @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(riskService.getRiskEventList(page, size, eventType, riskLevel, status));
    }

    /**
     * 处理风险事件
     */
    @PostMapping("/risk/events/{id}/process")
    public Result<?> processRiskEvent(
            @PathVariable("id") Long eventId,
            @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");
        String remark = (String) params.get("remark");
        Long processedBy = StpUtil.getLoginIdAsLong();

        if (riskService.processRiskEvent(eventId, status, remark, processedBy)) {
            return Result.success("处理成功", null);
        }
        return Result.error("处理失败");
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/users/stats")
    public Result<?> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总用户数（排除管理员）
        Long total = userService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .ne(SysUser::getRole, "admin"));
        stats.put("total", total);

        // 今日新增
        Long todayNew = userService.countNewUsersToday();
        stats.put("todayNew", todayNew);

        // 活跃用户 (有订单的不同用户数)
        Long activeBuyers = orderMapper.countDistinctBuyers();
        stats.put("active", activeBuyers != null ? activeBuyers : 0L);

        // 已禁用（排除管理员）
        Long disabled = userService
                .count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 0)
                        .ne(SysUser::getRole, "admin"));
        stats.put("disabled", disabled);

        return Result.success(stats);
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/orders/stats")
    public Result<?> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", orderMapper.selectCount(null));
        stats.put("pending", orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 0)));
        stats.put("inProgress", orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 2)));
        stats.put("completed", orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 4)));
        stats.put("disputed", orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>().eq(TfOrder::getStatus, 6)));

        return Result.success(stats);
    }

    /**
     * 获取纠纷统计数据
     */
    @GetMapping("/disputes/stats")
    public Result<?> getDisputeStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", disputeMapper.selectCount(null));
        stats.put("pending",
                disputeMapper.selectCount(new LambdaQueryWrapper<TfDispute>().eq(TfDispute::getStatus, 0)));
        stats.put("processing",
                disputeMapper.selectCount(new LambdaQueryWrapper<TfDispute>().eq(TfDispute::getStatus, 1)));
        stats.put("resolved",
                disputeMapper.selectCount(new LambdaQueryWrapper<TfDispute>().eq(TfDispute::getStatus, 2)));

        return Result.success(stats);
    }

    /**
     * 获取纠纷详情（含买卖双方信息）
     */
    @GetMapping("/disputes/{id}")
    public Result<?> getDisputeDetail(@PathVariable("id") Long id) {
        TfDispute dispute = disputeMapper.selectById(id);
        if (dispute == null) {
            return Result.error("纠纷不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", dispute.getId());
        result.put("status", dispute.getStatus());
        result.put("reason", dispute.getReason());
        result.put("evidenceUrls", dispute.getEvidenceUrls());
        result.put("createTime", dispute.getCreateTime());

        // 获取订单信息
        TfOrder order = orderMapper.selectById(dispute.getOrderId());
        if (order != null) {
            result.put("orderNo", order.getOrderNo());
            result.put("orderTitle", order.getTitle());
            result.put("amount", order.getTotalAmount());

            // 获取买方信息
            SysUser buyer = userService.getById(order.getBuyerId());
            if (buyer != null) {
                result.put("buyerName", buyer.getNickname());
            }

            // 获取卖方信息 - 从商户表获取店铺名称
            if (order.getSellerId() != null) {
                TfMerchant sellerMerchant = merchantMapper.selectOne(
                        new LambdaQueryWrapper<TfMerchant>().eq(TfMerchant::getUserId, order.getSellerId()));
                if (sellerMerchant != null) {
                    result.put("sellerName", sellerMerchant.getShopName());
                } else {
                    SysUser seller = userService.getById(order.getSellerId());
                    if (seller != null) {
                        result.put("sellerName", seller.getNickname());
                    }
                }
            }
        }

        return Result.success(result);
    }

    /**
     * 获取信托交易列表
     */
    @GetMapping("/trust/transactions")
    public Result<?> getTrustTransactions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "type", required = false) String type) {

        LambdaQueryWrapper<TfTransaction> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(TfTransaction::getType, type);
        }
        wrapper.eq(TfTransaction::getStatus, 1);
        wrapper.orderByDesc(TfTransaction::getCreateTime);

        IPage<TfTransaction> pageResult = transactionMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充用户信息
        for (TfTransaction tx : pageResult.getRecords()) {
            if (tx.getUserId() != null) {
                SysUser user = userService.getById(tx.getUserId());
                if (user != null) {
                    tx.setTitle(tx.getTitle() + " - " + user.getNickname());
                }
            }
        }

        return Result.success(pageResult);
    }

    /**
     * 获取信托监控统计
     */
    @GetMapping("/trust/monitor")
    public Result<?> getTrustMonitor() {
        Map<String, Object> stats = new HashMap<>();

        // 当前资金总池
        BigDecimal totalDeposit = orderMapper.selectTotalDepositAmount();
        BigDecimal totalReleased = orderMapper.selectTotalReleasedAmount();
        BigDecimal totalPool = totalDeposit != null ? totalDeposit : BigDecimal.ZERO;
        stats.put("totalPool", totalPool);

        // 今日流入
        BigDecimal todayInflow = transactionMapper.selectTodayInflow();
        stats.put("todayInflow", todayInflow != null ? todayInflow : BigDecimal.ZERO);

        // 待清算
        BigDecimal pendingSettle = totalDeposit != null && totalReleased != null ? totalDeposit.subtract(totalReleased)
                : BigDecimal.ZERO;
        stats.put("pendingSettle", pendingSettle);

        // 待清算笔数
        Long pendingCount = orderMapper.selectCount(new LambdaQueryWrapper<TfOrder>()
                .in(TfOrder::getStatus, 2, 3));
        stats.put("pendingCount", pendingCount);

        // 资金分布
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("frozen", totalPool);
        distribution.put("pending", pendingSettle);
        distribution.put("available", totalReleased != null ? totalReleased : BigDecimal.ZERO);
        stats.put("distribution", distribution);

        // 最近交易
        List<TfTransaction> recentTx = transactionMapper.selectList(
                new LambdaQueryWrapper<TfTransaction>()
                        .eq(TfTransaction::getStatus, 1)
                        .orderByDesc(TfTransaction::getCreateTime)
                        .last("LIMIT 10"));

        List<Map<String, Object>> transactions = new ArrayList<>();
        for (TfTransaction tx : recentTx) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", tx.getId());
            item.put("type", tx.getType().equals("deposit") || tx.getType().equals("recharge") ? "in" : "out");
            item.put("title", tx.getTitle());
            item.put("detail", tx.getRemark());
            item.put("amount", tx.getAmount());
            item.put("time", tx.getCreateTime());
            transactions.add(item);
        }
        stats.put("transactions", transactions);

        return Result.success(stats);
    }

    /**
     * 获取商家统计数据
     */
    @GetMapping("/merchants/stats")
    public Result<?> getMerchantStats() {
        Map<String, Object> stats = new HashMap<>();

        // 全部商户
        Long total = merchantMapper.selectCount(null);
        stats.put("total", total != null ? total : 0L);

        // 待审核（商户申请）
        Long pending = merchantApplyMapper.selectCount(
                new LambdaQueryWrapper<TfMerchantApply>().eq(TfMerchantApply::getStatus, 0));
        stats.put("pending", pending != null ? pending : 0L);

        // 已认证（正常状态的商户）
        Long verified = merchantMapper.selectCount(
                new LambdaQueryWrapper<TfMerchant>().eq(TfMerchant::getStatus, 1));
        stats.put("verified", verified != null ? verified : 0L);

        // 已禁用
        Long disabled = merchantMapper.selectCount(
                new LambdaQueryWrapper<TfMerchant>().eq(TfMerchant::getStatus, 2));
        stats.put("disabled", disabled != null ? disabled : 0L);

        return Result.success(stats);
    }

    /**
     * 获取系统设置
     */
    @GetMapping("/settings")
    public Result<?> getSettings() {
        Map<String, Object> settings = new HashMap<>();

        // 基本设置
        settings.put("platformName", configService.getString("platform_name", "臻托 TrustFulfillment"));
        settings.put("servicePhone", configService.getString("service_phone", "400-888-8888"));
        settings.put("serviceEmail", configService.getString("service_email", "support@trustfulfillment.com"));

        // 信托设置
        settings.put("serviceFeeRate", configService.getDecimal("platform_fee_rate", new java.math.BigDecimal("3.5")));
        settings.put("minTrustAmount", configService.getDecimal("min_trust_amount", new java.math.BigDecimal("100")));
        settings.put("acceptTimeout", configService.getInt("accept_timeout", 48));
        settings.put("withdrawFeeRate", configService.getDecimal("withdraw_fee_rate", new java.math.BigDecimal("0.1")));
        settings.put("maxSinglePayment",
                configService.getDecimal("max_single_payment", new java.math.BigDecimal("200000")));
        settings.put("withdrawMinAmount",
                configService.getDecimal("withdraw_min_amount", new java.math.BigDecimal("100")));

        // 安全设置
        settings.put("force2FA", configService.getBoolean("force_2fa", false));
        settings.put("loginFailLock", configService.getInt("login_fail_lock", 5));
        settings.put("sessionTimeout", configService.getInt("session_timeout", 30));

        // 通知设置
        settings.put("emailNotify", configService.getBoolean("email_notify", true));
        settings.put("smsNotify", configService.getBoolean("sms_notify", true));
        settings.put("disputeAlert", configService.getBoolean("dispute_alert", true));

        return Result.success(settings);
    }

    /**
     * 保存系统设置
     */
    @PostMapping("/settings")
    public Result<?> saveSettings(@RequestBody Map<String, Object> settings) {
        // 基本设置
        if (settings.containsKey("platformName")) {
            configService.set("platform_name", String.valueOf(settings.get("platformName")), "平台名称");
        }
        if (settings.containsKey("servicePhone")) {
            configService.set("service_phone", String.valueOf(settings.get("servicePhone")), "客服电话");
        }
        if (settings.containsKey("serviceEmail")) {
            configService.set("service_email", String.valueOf(settings.get("serviceEmail")), "客服邮箱");
        }

        // 信托设置
        if (settings.containsKey("serviceFeeRate")) {
            configService.set("platform_fee_rate", String.valueOf(settings.get("serviceFeeRate")), "平台服务费率(%)");
        }
        if (settings.containsKey("minTrustAmount")) {
            configService.set("min_trust_amount", String.valueOf(settings.get("minTrustAmount")), "最低托管金额(元)");
        }
        if (settings.containsKey("acceptTimeout")) {
            configService.set("accept_timeout", String.valueOf(settings.get("acceptTimeout")), "验收超时时间(小时)");
        }
        if (settings.containsKey("withdrawFeeRate")) {
            configService.set("withdraw_fee_rate", String.valueOf(settings.get("withdrawFeeRate")), "提现手续费率(%)");
        }
        if (settings.containsKey("maxSinglePayment")) {
            configService.set("max_single_payment", String.valueOf(settings.get("maxSinglePayment")), "单笔支付上限(元)");
        }
        if (settings.containsKey("withdrawMinAmount")) {
            configService.set("withdraw_min_amount", String.valueOf(settings.get("withdrawMinAmount")), "最低提现金额(元)");
        }

        // 安全设置
        if (settings.containsKey("force2FA")) {
            configService.set("force_2fa", String.valueOf(settings.get("force2FA")), "强制双因素认证");
        }
        if (settings.containsKey("loginFailLock")) {
            configService.set("login_fail_lock", String.valueOf(settings.get("loginFailLock")), "登录失败锁定次数");
        }
        if (settings.containsKey("sessionTimeout")) {
            configService.set("session_timeout", String.valueOf(settings.get("sessionTimeout")), "会话超时时间(分钟)");
        }

        // 通知设置
        if (settings.containsKey("emailNotify")) {
            configService.set("email_notify", String.valueOf(settings.get("emailNotify")), "邮件通知");
        }
        if (settings.containsKey("smsNotify")) {
            configService.set("sms_notify", String.valueOf(settings.get("smsNotify")), "短信通知");
        }
        if (settings.containsKey("disputeAlert")) {
            configService.set("dispute_alert", String.valueOf(settings.get("disputeAlert")), "纠纷告警");
        }

        return Result.success("设置已保存", null);
    }

    /**
     * 禁用/启用商户
     */
    @PostMapping("/merchants/{id}/status")
    public Result<?> updateMerchantStatus(@PathVariable("id") Long id, @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");

        TfMerchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return Result.error("商户不存在");
        }

        merchant.setStatus(status);
        merchantMapper.updateById(merchant);

        return Result.success(status == 1 ? "已启用" : "已禁用", null);
    }

    // ================ 客服消息 ================

    /**
     * 获取会话列表（按用户分组）
     */
    @GetMapping("/service/conversations")
    public Result<?> getServiceConversations() {
        // 获取所有有消息的用户ID（按最新消息时间排序）
        List<Map<String, Object>> conversations = new ArrayList<>();

        // 查询所有不同的用户ID
        List<TfServiceMessage> allMessages = serviceMessageMapper.selectList(
                new LambdaQueryWrapper<TfServiceMessage>()
                        .orderByDesc(TfServiceMessage::getCreateTime));

        // 按用户分组
        Map<Long, List<TfServiceMessage>> userMessages = new LinkedHashMap<>();
        for (TfServiceMessage msg : allMessages) {
            userMessages.computeIfAbsent(msg.getUserId(), k -> new ArrayList<>()).add(msg);
        }

        // 构建会话列表
        for (Map.Entry<Long, List<TfServiceMessage>> entry : userMessages.entrySet()) {
            Long userId = entry.getKey();
            List<TfServiceMessage> msgs = entry.getValue();

            SysUser user = userService.getById(userId);
            if (user == null)
                continue;

            // 计算未读消息数（用户发送的未读消息）
            long unreadCount = msgs.stream()
                    .filter(m -> m.getSenderRole() == 1 && m.getIsRead() == 0)
                    .count();

            // 获取最后一条消息
            TfServiceMessage lastMsg = msgs.get(0);

            Map<String, Object> conv = new HashMap<>();
            conv.put("userId", userId);
            conv.put("phone", user.getPhone());
            conv.put("nickname", user.getNickname());
            conv.put("avatar", user.getAvatar());
            conv.put("unreadCount", unreadCount);
            conv.put("lastMessage", lastMsg.getContent());
            conv.put("lastMessageTime", lastMsg.getCreateTime());

            conversations.add(conv);
        }

        return Result.success(conversations);
    }

    /**
     * 获取与某用户的聊天记录
     */
    @GetMapping("/service/messages/{userId}")
    public Result<?> getServiceMessages(@PathVariable("userId") Long userId) {
        List<TfServiceMessage> messages = serviceMessageMapper.selectList(
                new LambdaQueryWrapper<TfServiceMessage>()
                        .eq(TfServiceMessage::getUserId, userId)
                        .orderByAsc(TfServiceMessage::getCreateTime));

        // 填充发送者信息
        for (TfServiceMessage msg : messages) {
            SysUser sender = userService.getById(msg.getSenderId());
            if (sender != null) {
                msg.setSenderName(sender.getNickname());
                msg.setSenderAvatar(sender.getAvatar());
            }
        }

        return Result.success(messages);
    }

    /**
     * 发送客服消息
     */
    @PostMapping("/service/messages")
    public Result<?> sendServiceMessage(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String content = (String) params.get("content");
        Integer type = 1;
        if (params.get("type") != null) {
            type = Integer.valueOf(params.get("type").toString());
        }

        Long adminId = StpUtil.getLoginIdAsLong();

        TfServiceMessage message = new TfServiceMessage();
        message.setUserId(userId);
        message.setSenderId(adminId);
        message.setSenderRole(2); // 管理员
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());

        serviceMessageMapper.insert(message);

        // 填充发送者信息
        SysUser admin = userService.getById(adminId);
        if (admin != null) {
            message.setSenderName(admin.getNickname());
            message.setSenderAvatar(admin.getAvatar());
        }

        return Result.success(message);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/service/messages/{userId}/read")
    public Result<?> markMessagesRead(@PathVariable("userId") Long userId) {
        // 将该用户发送的所有未读消息标记为已读
        TfServiceMessage update = new TfServiceMessage();
        update.setIsRead(1);

        serviceMessageMapper.update(update, new LambdaQueryWrapper<TfServiceMessage>()
                .eq(TfServiceMessage::getUserId, userId)
                .eq(TfServiceMessage::getSenderRole, 1) // 用户发送的消息
                .eq(TfServiceMessage::getIsRead, 0));

        return Result.success("已标记已读", null);
    }

    /**
     * 获取未读消息总数
     */
    @GetMapping("/service/unread-count")
    public Result<?> getUnreadMessageCount() {
        Long count = serviceMessageMapper.selectCount(
                new LambdaQueryWrapper<TfServiceMessage>()
                        .eq(TfServiceMessage::getSenderRole, 1) // 用户发送的消息
                        .eq(TfServiceMessage::getIsRead, 0));

        return Result.success(count);
    }

    // ================ 合同管理 ================

    /**
     * 获取合同列表
     */
    @GetMapping("/contracts")
    public Result<?> getContractList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword) {

        LambdaQueryWrapper<TfContract> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TfContract::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(TfContract::getContractNo, keyword)
                    .or().like(TfContract::getTitle, keyword));
        }
        wrapper.orderByDesc(TfContract::getCreateTime);

        IPage<TfContract> pageResult = contractMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(pageResult);
    }

    /**
     * 获取合同统计
     */
    @GetMapping("/contracts/stats")
    public Result<?> getContractStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", contractMapper.selectCount(null));
        stats.put("pending",
                contractMapper.selectCount(new LambdaQueryWrapper<TfContract>().in(TfContract::getStatus, 1, 2)));
        stats.put("active",
                contractMapper.selectCount(new LambdaQueryWrapper<TfContract>().eq(TfContract::getStatus, 3)));
        stats.put("cancelled",
                contractMapper.selectCount(new LambdaQueryWrapper<TfContract>().eq(TfContract::getStatus, 4)));
        return Result.success(stats);
    }

    // ================ 银行信息管理 ================

    /**
     * 获取银行列表
     */
    @GetMapping("/banks")
    public Result<?> getBankList() {
        List<SysBank> banks = bankMapper.selectList(
                new LambdaQueryWrapper<SysBank>().orderByAsc(SysBank::getSortOrder));
        return Result.success(banks);
    }

    /**
     * 保存银行
     */
    @PostMapping("/banks")
    public Result<?> saveBank(@RequestBody SysBank bank) {
        if (bank.getId() != null) {
            bankMapper.updateById(bank);
        } else {
            bankMapper.insert(bank);
        }
        return Result.success("保存成功", null);
    }

    /**
     * 删除银行
     */
    @DeleteMapping("/banks/{id}")
    public Result<?> deleteBank(@PathVariable("id") Integer id) {
        bankMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 更新银行状态
     */
    @PostMapping("/banks/{id}/status")
    public Result<?> updateBankStatus(@PathVariable("id") Integer id, @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");
        SysBank bank = bankMapper.selectById(id);
        if (bank != null) {
            bank.setStatus(status);
            bankMapper.updateById(bank);
        }
        return Result.success("操作成功", null);
    }

    // ================ 系统通知管理 ================

    /**
     * 获取通知列表
     */
    @GetMapping("/notifications")
    public Result<?> getNotificationList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword) {

        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(SysNotification::getType, type);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysNotification::getTitle, keyword)
                    .or().like(SysNotification::getContent, keyword));
        }
        wrapper.orderByDesc(SysNotification::getCreateTime);

        IPage<SysNotification> pageResult = notificationMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充用户名
        for (SysNotification noti : pageResult.getRecords()) {
            SysUser user = userService.getById(noti.getUserId());
            if (user != null) {
                noti.setUserName(user.getNickname());
            }
        }

        return Result.success(pageResult);
    }

    /**
     * 获取通知统计
     */
    @GetMapping("/notifications/stats")
    public Result<?> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", notificationMapper.selectCount(null));

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        stats.put("todaySent", notificationMapper.selectCount(
                new LambdaQueryWrapper<SysNotification>().ge(SysNotification::getCreateTime, todayStart)));

        stats.put("read", notificationMapper.selectCount(
                new LambdaQueryWrapper<SysNotification>().eq(SysNotification::getIsRead, 1)));
        stats.put("unread", notificationMapper.selectCount(
                new LambdaQueryWrapper<SysNotification>().eq(SysNotification::getIsRead, 0)));

        return Result.success(stats);
    }

    /**
     * 发送通知
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/notifications")
    public Result<?> sendNotification(@RequestBody Map<String, Object> params) {
        String type = (String) params.get("type");
        String title = (String) params.get("title");
        String content = (String) params.get("content");

        // 支持单个userId或userIds数组
        List<Long> userIds = new ArrayList<>();
        if (params.get("userIds") != null) {
            List<?> ids = (List<?>) params.get("userIds");
            for (Object id : ids) {
                userIds.add(Long.valueOf(id.toString()));
            }
        } else if (params.get("userId") != null) {
            userIds.add(Long.valueOf(params.get("userId").toString()));
        }

        int sentCount = 0;

        if (!userIds.isEmpty()) {
            // 发送给指定用户
            for (Long userId : userIds) {
                SysNotification noti = new SysNotification();
                noti.setUserId(userId);
                noti.setType(type);
                noti.setTitle(title);
                noti.setContent(content);
                noti.setIsRead(0);
                noti.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(noti);
                sentCount++;
            }
        } else {
            // 发送给所有用户
            List<SysUser> users = userService.list(
                    new LambdaQueryWrapper<SysUser>().ne(SysUser::getRole, "admin"));
            for (SysUser user : users) {
                SysNotification noti = new SysNotification();
                noti.setUserId(user.getId());
                noti.setType(type);
                noti.setTitle(title);
                noti.setContent(content);
                noti.setIsRead(0);
                noti.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(noti);
                sentCount++;
            }
        }

        return Result.success("已发送给 " + sentCount + " 位用户", null);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/notifications/{id}")
    public Result<?> deleteNotification(@PathVariable("id") Long id) {
        notificationMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    // ================ 业务分类管理 ================

    /**
     * 获取分类树
     */
    @GetMapping("/categories/tree")
    public Result<?> getCategoryTree() {
        List<SysCategory> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<SysCategory>().orderByAsc(SysCategory::getSortOrder));

        // 构建树形结构
        List<SysCategory> tree = new ArrayList<>();
        Map<Integer, SysCategory> map = new HashMap<>();

        for (SysCategory cat : allCategories) {
            map.put(cat.getId(), cat);
            cat.setChildren(new ArrayList<>());
        }

        for (SysCategory cat : allCategories) {
            if (cat.getParentId() == 0) {
                tree.add(cat);
            } else {
                SysCategory parent = map.get(cat.getParentId());
                if (parent != null) {
                    parent.getChildren().add(cat);
                }
            }
        }

        return Result.success(tree);
    }

    /**
     * 保存分类
     */
    @PostMapping("/categories")
    public Result<?> saveCategory(@RequestBody SysCategory category) {
        if (category.getId() != null) {
            categoryMapper.updateById(category);
        } else {
            categoryMapper.insert(category);
        }
        return Result.success("保存成功", null);
    }

    /**
     * 删除分类（级联删除子分类）
     */
    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable("id") Integer id) {
        // 删除子分类
        categoryMapper.delete(new LambdaQueryWrapper<SysCategory>().eq(SysCategory::getParentId, id));
        // 删除当前分类
        categoryMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 更新分类状态
     */
    @PostMapping("/categories/{id}/status")
    public Result<?> updateCategoryStatus(@PathVariable("id") Integer id, @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");
        SysCategory category = categoryMapper.selectById(id);
        if (category != null) {
            category.setStatus(status);
            categoryMapper.updateById(category);
        }
        return Result.success("操作成功", null);
    }

    // ============ 提成管理 API ============

    /**
     * 获取提成记录列表
     */
    @GetMapping("/commissions")
    public Result<?> getCommissionList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "orderId", required = false) Long orderId) {

        IPage<TfCommission> pageResult = commissionService.getCommissionList(page, size, merchantId, orderId);
        return Result.success(pageResult);
    }

    /**
     * 获取提成统计数据
     */
    @GetMapping("/commissions/stats")
    public Result<?> getCommissionStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总提成
        BigDecimal totalCommission = commissionService.getTotalCommission();
        stats.put("totalCommission", totalCommission);

        // 本月提成
        LocalDate now = LocalDate.now();
        BigDecimal monthCommission = commissionService.getMonthCommission(now.getYear(), now.getMonthValue());
        stats.put("monthCommission", monthCommission);

        // 今日提成
        LocalDateTime todayStart = now.atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LambdaQueryWrapper<TfCommission> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(TfCommission::getCreateTime, todayStart)
                .lt(TfCommission::getCreateTime, todayEnd);
        List<TfCommission> todayCommissions = commissionMapper.selectList(todayWrapper);
        BigDecimal todayCommission = todayCommissions.stream()
                .map(TfCommission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todayCommission", todayCommission);

        // 提成记录总数
        Long totalCount = commissionMapper.selectCount(null);
        stats.put("totalCount", totalCount);

        // 最近6个月提成趋势
        List<Map<String, Object>> trendData = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            Map<String, Object> item = new HashMap<>();
            item.put("month", month.getMonthValue() + "月");
            BigDecimal amount = commissionService.getMonthCommission(month.getYear(), month.getMonthValue());
            item.put("amount", amount);
            trendData.add(item);
        }
        stats.put("trendData", trendData);

        return Result.success(stats);
    }

    /**
     * 获取单个提成记录详情
     */
    @GetMapping("/commissions/{id}")
    public Result<?> getCommissionDetail(@PathVariable("id") Long id) {
        TfCommission commission = commissionMapper.selectById(id);
        if (commission == null) {
            return Result.error("提成记录不存在");
        }

        // 填充商户信息
        if (commission.getMerchantId() != null) {
            TfMerchant merchant = merchantMapper.selectById(commission.getMerchantId());
            if (merchant != null) {
                commission.setShopName(merchant.getShopName());
                commission.setMerchantName(
                        merchant.getCompanyName() != null ? merchant.getCompanyName() : merchant.getLegalPerson());
            }
        }
        if (commission.getMerchantUserId() != null && commission.getMerchantName() == null) {
            SysUser user = userService.getById(commission.getMerchantUserId());
            if (user != null) {
                commission.setMerchantName(user.getNickname());
            }
        }

        return Result.success(commission);
    }

    /**
     * 更新商户提成比例
     */
    @PostMapping("/merchants/{id}/commission-rate")
    public Result<?> updateMerchantCommissionRate(
            @PathVariable("id") Long merchantId,
            @RequestBody Map<String, Object> params) {

        BigDecimal commissionRate;
        Object rateObj = params.get("commissionRate");
        if (rateObj instanceof Number) {
            commissionRate = new BigDecimal(rateObj.toString());
        } else if (rateObj instanceof String) {
            commissionRate = new BigDecimal((String) rateObj);
        } else {
            return Result.error("提成比例参数无效");
        }

        // 验证提成比例范围 (0-100%)
        if (commissionRate.compareTo(BigDecimal.ZERO) < 0 ||
                commissionRate.compareTo(new BigDecimal("100")) > 0) {
            return Result.error("提成比例必须在 0-100% 之间");
        }

        TfMerchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            return Result.error("商户不存在");
        }

        merchant.setCommissionRate(commissionRate);
        merchantMapper.updateById(merchant);

        return Result.success("提成比例更新成功", null);
    }

    // ============ 短信服务配置 API ============

    /**
     * 获取短信服务配置
     */
    @GetMapping("/sms/settings")
    public Result<?> getSmsSettings() {
        Map<String, Object> settings = new HashMap<>();

        // 服务提供商
        settings.put("provider", configService.getString("sms_provider", "console"));

        // 阿里云配置
        settings.put("aliyunAccessKeyId", configService.getString("sms_aliyun_access_key_id", ""));
        settings.put("aliyunAccessKeySecret", configService.getString("sms_aliyun_access_key_secret", ""));
        settings.put("aliyunSignName", configService.getString("sms_aliyun_sign_name", "臻托"));
        settings.put("aliyunTemplateCode", configService.getString("sms_aliyun_template_code", ""));

        // 腾讯云配置
        settings.put("tencentSecretId", configService.getString("sms_tencent_secret_id", ""));
        settings.put("tencentSecretKey", configService.getString("sms_tencent_secret_key", ""));
        settings.put("tencentAppId", configService.getString("sms_tencent_app_id", ""));
        settings.put("tencentSignName", configService.getString("sms_tencent_sign_name", "臻托"));
        settings.put("tencentTemplateId", configService.getString("sms_tencent_template_id", ""));

        // 通用配置
        settings.put("codeExpireSeconds", configService.getInt("sms_code_expire_seconds", 300));
        settings.put("dayLimit", configService.getInt("sms_day_limit", 10));

        return Result.success(settings);
    }

    /**
     * 保存短信服务配置
     */
    @PostMapping("/sms/settings")
    public Result<?> saveSmsSettings(@RequestBody Map<String, Object> settings) {
        // 服务提供商
        if (settings.containsKey("provider")) {
            configService.set("sms_provider", String.valueOf(settings.get("provider")), "短信服务提供商");
        }

        // 阿里云配置
        if (settings.containsKey("aliyunAccessKeyId")) {
            configService.set("sms_aliyun_access_key_id", String.valueOf(settings.get("aliyunAccessKeyId")),
                    "阿里云AccessKeyId");
        }
        if (settings.containsKey("aliyunAccessKeySecret")) {
            configService.set("sms_aliyun_access_key_secret", String.valueOf(settings.get("aliyunAccessKeySecret")),
                    "阿里云AccessKeySecret");
        }
        if (settings.containsKey("aliyunSignName")) {
            configService.set("sms_aliyun_sign_name", String.valueOf(settings.get("aliyunSignName")), "阿里云短信签名");
        }
        if (settings.containsKey("aliyunTemplateCode")) {
            configService.set("sms_aliyun_template_code", String.valueOf(settings.get("aliyunTemplateCode")),
                    "阿里云模板代码");
        }

        // 腾讯云配置
        if (settings.containsKey("tencentSecretId")) {
            configService.set("sms_tencent_secret_id", String.valueOf(settings.get("tencentSecretId")), "腾讯云SecretId");
        }
        if (settings.containsKey("tencentSecretKey")) {
            configService.set("sms_tencent_secret_key", String.valueOf(settings.get("tencentSecretKey")),
                    "腾讯云SecretKey");
        }
        if (settings.containsKey("tencentAppId")) {
            configService.set("sms_tencent_app_id", String.valueOf(settings.get("tencentAppId")), "腾讯云AppId");
        }
        if (settings.containsKey("tencentSignName")) {
            configService.set("sms_tencent_sign_name", String.valueOf(settings.get("tencentSignName")), "腾讯云短信签名");
        }
        if (settings.containsKey("tencentTemplateId")) {
            configService.set("sms_tencent_template_id", String.valueOf(settings.get("tencentTemplateId")), "腾讯云模板ID");
        }

        // 通用配置
        if (settings.containsKey("codeExpireSeconds")) {
            configService.set("sms_code_expire_seconds", String.valueOf(settings.get("codeExpireSeconds")),
                    "验证码有效期(秒)");
        }
        if (settings.containsKey("dayLimit")) {
            configService.set("sms_day_limit", String.valueOf(settings.get("dayLimit")), "每日发送上限");
        }

        return Result.success("短信配置已保存", null);
    }

    /**
     * 发送测试短信
     */
    @PostMapping("/sms/test")
    public Result<?> sendTestSms(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");

        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }

        // 简单的手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 验证当前配置是否完整
        String provider = configService.getString("sms_provider", "console");

        if ("aliyun".equals(provider)) {
            String accessKeyId = configService.getString("sms_aliyun_access_key_id", "");
            String accessKeySecret = configService.getString("sms_aliyun_access_key_secret", "");
            String signName = configService.getString("sms_aliyun_sign_name", "");
            String templateCode = configService.getString("sms_aliyun_template_code", "");

            if (accessKeyId.isEmpty() || accessKeySecret.isEmpty()) {
                return Result.error("阿里云配置不完整：请先填写 AccessKey ID 和 AccessKey Secret，并保存配置");
            }
            if (signName.isEmpty()) {
                return Result.error("阿里云配置不完整：请先填写短信签名，并保存配置");
            }
            if (templateCode.isEmpty()) {
                return Result.error("阿里云配置不完整：请先填写模板代码，并保存配置");
            }
        } else if ("tencent".equals(provider)) {
            String secretId = configService.getString("sms_tencent_secret_id", "");
            String secretKey = configService.getString("sms_tencent_secret_key", "");
            String appId = configService.getString("sms_tencent_app_id", "");
            String signName = configService.getString("sms_tencent_sign_name", "");
            String templateId = configService.getString("sms_tencent_template_id", "");

            if (secretId.isEmpty() || secretKey.isEmpty()) {
                return Result.error("腾讯云配置不完整：请先填写 SecretId 和 SecretKey，并保存配置");
            }
            if (appId.isEmpty()) {
                return Result.error("腾讯云配置不完整：请先填写 SDK AppID，并保存配置");
            }
            if (signName.isEmpty()) {
                return Result.error("腾讯云配置不完整：请先填写短信签名，并保存配置");
            }
            if (templateId.isEmpty()) {
                return Result.error("腾讯云配置不完整：请先填写模板ID，并保存配置");
            }
        }

        try {
            // 调用短信服务发送验证码
            boolean success = smsService.sendVerifyCode(phone, 1, "127.0.0.1");

            if (success) {
                if ("console".equals(provider)) {
                    return Result.success("测试短信已发送，请查看后端控制台日志", null);
                } else {
                    return Result.success("测试短信已发送到 " + phone, null);
                }
            } else {
                return Result.error("短信发送失败");
            }
        } catch (Exception e) {
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(jakarta.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // IPv6 本地回环地址转换为 IPv4 格式
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
