package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 存证记录实体
 * 类型: 1合同签署, 2资金托管, 3交付提交, 4验收确认, 5纠纷证据
 */
@TableName("tf_evidence")
public class TfEvidence {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;           // 关联订单ID
    private Long stageId;           // 关联阶段ID
    private Integer type;           // 类型: 1合同签署, 2资金托管, 3交付提交, 4验收确认, 5纠纷证据
    private String title;           // 存证标题
    private String content;         // 存证内容描述
    private String fileUrl;         // 文件URL
    private String fileHash;        // 文件哈希
    private Long blockHeight;       // 区块高度
    private LocalDateTime chainTime; // 上链时间
    private Long operatorId;        // 操作人ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 存证类型常量
    public static final int TYPE_CONTRACT = 1;      // 合同签署
    public static final int TYPE_DEPOSIT = 2;       // 资金托管
    public static final int TYPE_DELIVERY = 3;      // 交付提交
    public static final int TYPE_ACCEPT = 4;        // 验收确认
    public static final int TYPE_DISPUTE = 5;       // 纠纷证据

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public LocalDateTime getChainTime() {
        return chainTime;
    }

    public void setChainTime(LocalDateTime chainTime) {
        this.chainTime = chainTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
