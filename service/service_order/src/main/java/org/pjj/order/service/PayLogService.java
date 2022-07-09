package org.pjj.order.service;

import org.pjj.order.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-30
 */
public interface PayLogService extends IService<PayLog> {

    /**
     * 生成微信支付二维码接口
     * @param orderNo 订单号
     * @return
     */
    Map<String, Object> createNative(String orderNo);

    /**
     * 根据订单号 查询 订单支付状态
     * @param orderNo
     * @return
     */
    Map<String, String> queryPayStatus(String orderNo);


    /**
     * 添加记录到支付表, 更新订单表订单_状态字段
     * @param map
     */
    void updateOrderStatus(Map<String, String> map);
}
