package org.pjj.order.service.impl;

import org.pjj.commonutils.R;
import org.pjj.order.client.EduCourseClient;
import org.pjj.order.client.UcenterMemberClient;
import org.pjj.order.entity.Order;
import org.pjj.order.mapper.OrderMapper;
import org.pjj.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.order.utils.OrderNoUtil;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-30
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private UcenterMemberClient ucenterMemberClient;

    @Autowired
    private EduCourseClient eduCourseClient;

    //创建订单返回订单号 (传入课程id 与 用户id)
    @Override
    public String createOrder(String courseId, String memberId) {
        Order order = new Order();//订单对象

        //通过远程调用获取用户信息 (根据用户id)
        if(StringUtils.isEmpty(memberId)) {
            throw new GuliException(20001, "userId is null");
        }
        R memberR = ucenterMemberClient.getMemberInfoById(memberId);
        if(memberR.getCode() != 20000) {
            throw new GuliException(20001, "feign error");
        }
        Map<String, Object> memberInfo = (Map<String, Object>) memberR.getData().get("userInfo");//用户信息
        //将远程调用查出来的用户信息放入订单对象
        order.setMemberId((String) memberInfo.get("id"));
        order.setNickname((String) memberInfo.get("nickname"));
        order.setMobile((String) memberInfo.get("mobile"));
        order.setMail((String) memberInfo.get("mail"));


        //通过远程调用获取课程信息 (根据课程id)
        if(StringUtils.isEmpty(courseId)) {
            throw new GuliException(20001, "courseId is null");
        }
        R courseR = eduCourseClient.getPublishCourseInfo(courseId);
        if(courseR.getCode() != 20000) {
            throw new GuliException(20001, "feign error");
        }
        Map<String, Object> courseInfo = (Map<String, Object>) courseR.getData().get("coursePublish");//课程信息
        //将远程调用查出来的课程信息放入订单对象
        order.setCourseId((String) courseInfo.get("id"));
        order.setCourseTitle((String) courseInfo.get("title"));
        order.setCourseCover((String) courseInfo.get("cover"));
        order.setTeacherName((String) courseInfo.get("teacherName"));


        //设置订单信息
        String orderNo = OrderNoUtil.getOrderNo();//获取订单号
        order.setOrderNo(orderNo);//设置订单号
        order.setTotalFee(BigDecimal.valueOf((Double) courseInfo.get("price")));//设置订单价格(根据课程价格设置订单价格)
        order.setStatus(0);  //设置订单状态 0为未支付, 1为已支付 (当前是生成订单, 肯定是没有付款的, 所以默认为 0)
        order.setPayType(1); //设置支付类型 1为微信, 2为支付宝 (目前只使用微信支付, 就写死为1了)


        baseMapper.insert(order);//插入操作

        return orderNo;
    }
}
