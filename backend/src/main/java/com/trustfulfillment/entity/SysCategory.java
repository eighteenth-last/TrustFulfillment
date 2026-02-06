package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 业务分类实体
 */
@TableName("sys_category")
public class SysCategory {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer parentId;       // 父级ID，0表示一级分类
    private String name;            // 分类名称
    private Integer level;          // 分类级别：1一级，2二级，3三级
    private String icon;            // 图标
    private Integer sortOrder;      // 排序
    private Integer status;         // 状态：0禁用，1启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 子分类列表（非数据库字段）
    @TableField(exist = false)
    private List<SysCategory> children;

    // ============ Getters and Setters ============

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public List<SysCategory> getChildren() { return children; }
    public void setChildren(List<SysCategory> children) { this.children = children; }
}
