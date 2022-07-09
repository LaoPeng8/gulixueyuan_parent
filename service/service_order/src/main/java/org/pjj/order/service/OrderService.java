package org.pjj.order.service;

import org.pjj.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-30
 */
public interface OrderService extends IService<Order> {

    //创建订单返回订单号 (传入课程id 与 用户id)
    String createOrder(String courseId, String memberId);
}
