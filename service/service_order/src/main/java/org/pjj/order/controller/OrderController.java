package org.pjj.order.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.JwtUtils;
import org.pjj.commonutils.R;
import org.pjj.order.entity.Order;
import org.pjj.order.service.OrderService;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * 记录
 * 前端报错 Access to XMLHttpRequest at 'http://localhost:9001/eduorder/order/createOrder/1500666777116016642' from origin 'http://localhost:3000' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
 * 刚开始没想到是跨域, 因为 我发现已经访问到了后端方法 方法中报错 用户id为空, 然后我就打断点排查错误, 发现是token没有传过来 导致根据token查不出用户id
 * 然后我发现其他请求的请求头会正常的带上token, 我就奇了怪了, 后来听到老师说跨域才想起来原因, 是没有加跨域的注解 @CrossOrigin ..........
 *
 * @author PengJiaJun
 * @since 2022-03-30
 */
@RestController
@RequestMapping("/eduorder/order")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 生成订单
     * @param courseId
     * @param request
     * @return
     */
    @PostMapping("/createOrder/{courseId}")
    public R saveOrder(@PathVariable String courseId, HttpServletRequest request) {

        //创建订单返回订单号 (传入课程id 与 用户id(用户id通过传入的token获取))
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        if(StringUtils.isEmpty(memberId)) {
            return R.error().message("登录异常");
        }

        //查询 该用户是否已经购买过了该课程, 如果查出来数据, 说明已经购买过了, 则不会再次生成订单, 如果没有数据说明没有购买过则正常生成订单
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        wrapper.eq("course_id", courseId);

        Order one = orderService.getOne(wrapper);
        if(one != null){//如果不为null 则说明 该用户购买过该课程(不知道是否购买成功)
            //判断是否购买成功
            if(one.getStatus() == 1) {//用户已经购买过该课程, 则不会再次生成订单
                return R.error().message("您已经购买过本课程, 请不要重复购买");
            }

            //有订单记录, 但是没有购买成功 one.getStatus() != 1  也就是 为0
            //则将订单的订单编号直接返回给他, 不生成新的订单了.
            return R.ok().data("orderNo", one.getOrderNo());
        }

        //创建订单并返回订单编号
        String orderNo = orderService.createOrder(courseId, memberId);

        return R.ok().data("orderNo", orderNo);
    }

    /**
     * 根据订单号查询订单
     * @param orderNo
     * @return
     */
    @GetMapping("/getOrderInfo/{orderNo}")
    public R getOrderInfo(@PathVariable String orderNo) {
        if(StringUtils.isEmpty(orderNo)) {
            return R.error().message("orderNo is null");
        }

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);

        Order order = orderService.getOne(wrapper);

        return R.ok().data("order", order);
    }


    /**
     * 根据 课程id 以及token中的 用户id 判断, 该用户是否购买过该课程, 如购买过 则返回true 反之false
     * @param courseId
     * @param request
     * @return
     */
    @GetMapping("/isPayCourse/{courseId}")
    public R isPayCourse(@PathVariable String courseId, HttpServletRequest request) {

        //创建订单返回订单号 (传入课程id 与 用户id(用户id通过传入的token获取))
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
//        if(StringUtils.isEmpty(memberId)) {
//            return R.error().message("登录异常");
//        }
//        这个if不注掉的话 前端一直报错这个, 虽然其他都是正常显示 请求返回也是200 数据也有, 其他都是挺正常的, 不过就是控制台报错
//        也不报具体的错, 就是请求头 请求地址 socket信息啥的, 而且这个if中的信息也出现了, 我注掉了就好了,
//        但是我从network中看请求该接口的 请求 也是正常返回的 {"success":true,"code":20000,"message":"成功","data":{"isPayCourse":true}}
//        就感觉挺奇怪的...
//         ERROR status: 200,   statusText: '',   headers: {} ... data: { success: false, code: 20001,message: '登录异常',data: {} ...

        //查询订单表 查询该用户是否已经购买过了该课程, 如果查出来数据, 说明已经购买过了
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        wrapper.eq("course_id", courseId);
        wrapper.eq("status", 1);//支付状态 1 表示支付成功

        Order one = orderService.getOne(wrapper);
        if(one != null){
            //查出来记录, 说明已经购买过了, 返回true
            return R.ok().data("isPayCourse", true);
        }

        //one == null, 说明没有购买过, 返回false
        return R.ok().data("isPayCourse", false);
    }

}

