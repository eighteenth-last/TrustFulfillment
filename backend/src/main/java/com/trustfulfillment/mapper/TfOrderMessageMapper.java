package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.TfOrderMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 订单消息 Mapper
 */
@Mapper
public interface TfOrderMessageMapper extends BaseMapper<TfOrderMessage> {

    /**
     * 标记订单的某用户收到的消息为已读
     */
    @Update("UPDATE tf_order_message SET is_read = 1 WHERE order_id = #{orderId} AND receiver_id = #{receiverId} AND is_read = 0")
    int markAsRead(@Param("orderId") Long orderId, @Param("receiverId") Long receiverId);
}
