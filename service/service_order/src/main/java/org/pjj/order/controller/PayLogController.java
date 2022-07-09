package org.pjj.order.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.R;
import org.pjj.order.client.EduCourseClient;
import org.pjj.order.entity.Order;
import org.pjj.order.service.OrderService;
import org.pjj.order.service.PayLogService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-30
 */
@RestController
@RequestMapping("/eduorder/paylog")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class PayLogController {

    @Autowired
    private PayLogService payLogService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EduCourseClient eduCourseClient;

    /**
     * 生成微信支付二维码接口
     * @param orderNo 订单号
     * @return
     */
    @GetMapping("/createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo) {
        if(StringUtils.isEmpty(orderNo)) {
            return R.error().message("orderNo is null");
        }

        //返回信息, 包含二维码地址, 还有其他信息
        Map<String, Object> map = payLogService.createNative(orderNo);

        return R.ok().data(map);
    }

    /**
     * 根据订单号 查询 订单支付状态
     * @param orderNo
     * @return
     */
    @GetMapping("/queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        if(StringUtils.isEmpty(orderNo)) {
            return R.error().message("orderNo is null");
        }

        Map<String, String> map = payLogService.queryPayStatus(orderNo);
        if(map == null) {
            R.error().message("订单异常");
        }

        //如果返回的map里面不是空, 通过map获取订单状态
        if (map.get("trade_state").equals("SUCCESS")) {
            //支付成功
            //添加记录到支付表, 更新订单表订单_状态字段
            payLogService.updateOrderStatus(map);

            //根据订单编号查询出课程id, 然后将该课程的购买人数 +1
            QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
            orderWrapper.eq("order_no", orderNo);
            Order one = orderService.getOne(orderWrapper);
            String courseId = one.getCourseId();//课程id

            eduCourseClient.updateCourseBuyCount(courseId);//课程购买人数 +1

            return R.ok();
        }

        return R.error().message("支付中...");
    }


}

