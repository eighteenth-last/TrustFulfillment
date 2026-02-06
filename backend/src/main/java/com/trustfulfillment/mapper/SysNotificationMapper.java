package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.SysNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {

    /**
     * 标记用户所有通知为已读
     */
    @Update("UPDATE sys_notification SET is_read = 1, read_time = NOW() WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(Long userId);
}
