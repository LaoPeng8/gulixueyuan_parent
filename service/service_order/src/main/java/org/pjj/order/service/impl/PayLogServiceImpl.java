package org.pjj.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import org.pjj.order.entity.Order;
import org.pjj.order.entity.PayLog;
import org.pjj.order.mapper.PayLogMapper;
import org.pjj.order.service.OrderService;
import org.pjj.order.service.PayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.order.utils.ConstantWXUtils;
import org.pjj.order.utils.HttpClient;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author PengJiaJun
 * @since 2022-03-30
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Autowired
    private OrderService orderService;

    /**
     * 生成微信支付二维码接口
     * @param orderNo 订单号
     * @return
     */
    @Override
    public Map<String, Object> createNative(String orderNo) {

        //1. 根据订单号查询订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        Order order = orderService.getOne(wrapper);
        if(order == null) {
            throw new GuliException(20001, "订单信息异常");
        }


        //2. 使用map设置生成二维码需要参数 (map的key 是固定的)
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("appid", ConstantWXUtils.WX_APP_ID);//appid
        paramsMap.put("mch_id", ConstantWXUtils.WX_PARTNER);//商户号
        paramsMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        paramsMap.put("body", order.getCourseTitle());//生成二维码名字, 这里使用课程名称 (有点类似商品名称的意思)
        paramsMap.put("out_trade_no", orderNo);//二维码唯一标识, 此处使用订单号
        paramsMap.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue() + "");//商品价格, 此处就是查出来的订单的价格(BigDecimal转为String后存入map)
        paramsMap.put("spbill_create_ip", "127.0.0.1");// 进行支付操作的地址, 目前是本地进行所以是127..., 如实际项目可能写域名如: www.baidu.com
        paramsMap.put("notify_url", ConstantWXUtils.WX_NOTIFYURL);//支付后的回调地址 (支付成功后会跳转至该路径)
        paramsMap.put("trade_type", "NATIVE");//支付的类型, NATIVE: 根据价格生成的二维码


        //3. 使用httpclient发送请求 (传递参数需要是xml格式的) (微信支付提供的固定的请求地址)
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        try {
            //设置xml格式的参数 (传入map 与 商户Key)
            client.setXmlParam(WXPayUtil.generateSignedXml(paramsMap, ConstantWXUtils.WX_PARTNERKEY));
            client.setHttps(true);//请求地址为https需要该设置

            client.post();//发送post请求
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001, "生成二维码失败");
        }


        //4. 得到发送请求返回结果
        try {
            String contentXML = client.getContent();//返回结果是 xml 格式

            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXML);//将 xml 格式转换为map集合, 把map集合返回

            //resultMap中只有二维码的信息, 如二维码地址啥的, 信息并不全, 付款页面除了二维码还需要订单编号等其他信息
            //所以还需要将将 resultMap 进一步封装为我们想要的数据
            Map<String, Object> map = new HashMap<>();
            map.put("out_trade_no", orderNo);//订单编号
            map.put("course_id", order.getCourseId());//课程id
            map.put("total_fee", order.getTotalFee() + "");//课程价格(支付价格)
            map.put("result_code", resultMap.get("result_code"));//返回二维码操作状态码
            map.put("code_url", resultMap.get("code_url"));//二维码地址

            return map;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new GuliException(20001, "生成二维码失败");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001, "生成二维码失败");
        }

    }

    /**
     * 根据订单号 查询 订单支付状态
     *
     * @param orderNo
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {

        //1. 封装参数
        Map<String, String> map = new HashMap<>();
        map.put("appid", ConstantWXUtils.WX_APP_ID);//appid
        map.put("mch_id", ConstantWXUtils.WX_PARTNER);//商户号
        map.put("out_trade_no", orderNo);//订单编号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        //2. 发送httpclient
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        client.setHttps(true);//请求地址为https需要该设置
        try {
            //设置xml格式的参数 (传入map 与 商户Key)
            client.setXmlParam(WXPayUtil.generateSignedXml(map, ConstantWXUtils.WX_PARTNERKEY));

            //发送post请求
            client.post();

            //将 xml 格式转换为map集合, 把map集合返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(client.getContent());

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 添加记录到支付表, 更新订单表订单_状态字段
     *
     * @param map
     */
    @Override
    public void updateOrderStatus(Map<String, String> map) {
        //从map中获取订单号 (out_trade_no中取订单编号, 是因为之前生成微信支付二维码时out_trade_no就是传入的订单编号, 也或许微信支付规定的就是out_trade_no中就是存放订单编号的)
        String orderNo = map.get("out_trade_no");

        //根据订单号查询订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        Order order = orderService.getOne(wrapper);
        if(order == null) {
            throw new GuliException(20001, "订单信息异常");
        }


        //更新订单表 订单状态
        if(order.getStatus().intValue() == 1) {
            //如果订单状态已经是 1, 也就是已支付, 则不做任何修改
            return;
        }

        //将刚才查出来的 order订单对象 的顶订单状体修改为 1(已支付), 然后update
        order.setStatus(1);
        orderService.updateById(order);


        //像支付表中添加支付记录
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);//订单编号
        payLog.setPayTime(new Date());//支付完成时间
        payLog.setPayType(1);//支付类型 1 表示微信
        payLog.setTotalFee(order.getTotalFee());//支付金额
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));//订单流水号
        payLog.setAttr(JSONObject.toJSONString(map));//其他属性

        //添加支付记录
        baseMapper.insert(payLog);

    }
}