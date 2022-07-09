package org.pjj.educenter.controller;

import com.google.gson.Gson;
import org.pjj.commonutils.JwtUtils;
import org.pjj.commonutils.R;
import org.pjj.educenter.entity.UcenterMember;
import org.pjj.educenter.service.impl.UcenterMemberServiceImpl;
import org.pjj.educenter.utils.ConstantWXUtils;
import org.pjj.educenter.utils.HttpClientUtils;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author PengJiaJun
 * @Date 2022/3/24 14:53
 */
@Controller //注意 没有使用 @RestController
@RequestMapping("/api/ucenter/wx")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class WxApiController {

    @Autowired
    private UcenterMemberServiceImpl ucenterMemberService;

    /**
     * 第二步
     *
     * 微信登录后 跳转至接口(http://localhost:8160/api/ucenter/wx/callback) 也就是本接口 (跳转的同时会返回一个 临时票据code 相当于一个临时token)
     *
     * 根据临时票据code 去请求微信提供的固定接口 https://api.weixin.qq.com/sns/oauth2/access_token?appid=... 同时将app_id, code.等传递过去
     *
     * 返回一个 json字符串 其中关键的有 access_token(相当于通行证token, 第三方拿着他就相当于拿着微信用户的账户密码) 与 openid(相当于微信用户id)
     * 返回的json字符串 {"access_token":"55_vTayEbsFi_KjwKCh1psoJkn7glhGVExhFeWxuS44BpQT-iOX9pK4pxuZcKszB2l_qklNCuxo2OYzpOC2OMYRUSBITixBvebkVcb_oo4ia90","expires_in":7200,"refresh_token":"55_EcYAdy7T-bT0_OTTLf1OOsgbShX57tcq1dNtJxKoQwLHXRqtv8k4WYm7ipCJs1VbdnCdIiLjWQcQJcsNs9bfJis69G6lZU22SYfPV0QjnqA","openid":"o3_SC5zFYknhbdThq0Qhc6ER5Bys","scope":"snsapi_login","unionid":"oWgGz1JWUVRwNtkXmZ6OkhDeSbO8"}
     *
     * 拿着 access_token 和 openid 去请求微信提供的固定地址 https://api.weixin.qq.com/sns/userinfo?access_token... 获取用户信息
     * 返回的json字符串 {"openid":"o3_SC5zFYknhbdThq0Qhc6ER5Bys","nickname":"Lucky","sex":0,"language":"","city":"","province":"","country":"","headimgurl":"https:\/\/thirdwx.qlogo.cn\/mmopen\/vi_32\/ulK7b35mj3SyJSqOSvdibic66ScOtv2PArjoBAxySIJ9GibnIIxegw1SwGzkXpGKzsHiclIfhoftq7Sl0x89yibicURA\/132","privilege":[],"unionid":"oWgGz1JWUVRwNtkXmZ6OkhDeSbO8"}
     *
     * @return
     */
    @GetMapping("/callback")
    public String callback(@RequestParam String code, @RequestParam String state) {
        // 临时票据 code (登录后 微信返回的)
        // 拿着 code 去请求微信的固定地址 获取 access_token(相当于通行证token) 与 openid(相当于微信用户id)
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        // 拼接参数: id, 密钥 和 临时票据code
        String accessTokenUrl = String.format(
                baseAccessTokenUrl,
                ConstantWXUtils.WX_OPEN_APP_ID,
                ConstantWXUtils.WX_OPEN_APP_SECRET,
                code
        );

        try {
            // 根据拼接好的地址, 请请求, 然后获取 access_token 与 openid
            // accessTokenInfo是一个json字符串 {"access_token":"55_vTayEbsFi_KjwKCh1psoJkn7glhGVExhFeWxuS44BpQT-iOX9pK4pxuZcKszB2l_qklNCuxo2OYzpOC2OMYRUSBITixBvebkVcb_oo4ia90","expires_in":7200,"refresh_token":"55_EcYAdy7T-bT0_OTTLf1OOsgbShX57tcq1dNtJxKoQwLHXRqtv8k4WYm7ipCJs1VbdnCdIiLjWQcQJcsNs9bfJis69G6lZU22SYfPV0QjnqA","openid":"o3_SC5zFYknhbdThq0Qhc6ER5Bys","scope":"snsapi_login","unionid":"oWgGz1JWUVRwNtkXmZ6OkhDeSbO8"}
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            //使用 Gson 将accessTokenInfo转为map集合 (方便获取access_token 与 openid)
            Gson gson = new Gson();
            HashMap accessTokenMap = gson.fromJson(accessTokenInfo, HashMap.class);
            String accessToken = (String) accessTokenMap.get("access_token");
            String openId = (String) accessTokenMap.get("openid");

            //判断数据库表里面是否存在相同微信信息(根据open_id) 如有则说明之前注册过, 如没有则说明没有注册过
            UcenterMember member = ucenterMemberService.getOpenIdMember(openId);
            if(member == null) {
                // 如果member == null 则说明该微信用户之前没有注册过, 所以 先获取微信用户信息然后再注册

                //拿着得到的 access_token 与 open_id 再去请求微信提供的固定地址, 获取登录微信用户的信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                //拼接参数 access_token 与 open_id
                String userInfoUrl = String.format(
                        baseUserInfoUrl,
                        accessToken,
                        openId);

                //发送请求 获取微信用户信息 (返回的json字符串 {"openid":"...","nickname":"Lucky","sex":0,"language":"",...})
                String userInfo = HttpClientUtils.get(userInfoUrl);

                //使用 Gson 将userInfo转为map集合 (方便获取用户信息)
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname");//昵称
                String headimgurl = (String) userInfoMap.get("headimgurl");//头像
                Integer sex = (int) Math.round(((Double) userInfoMap.get("sex")));//性别

                member = new UcenterMember();
                member.setOpenid(openId);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                member.setSex(sex);

                ucenterMemberService.save(member);
            }

            //微信登录(注册)完成后, 跳转回首页面需要显示以登录(显示用户名与头像等信息)
            //所以这里需要一个 登录的过程 (使用jwt生成token并返回给前端) (前端再根据token获取用户信息)
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());//member注册后会回显id, 所以不用担心id为空

            return "redirect:http://localhost:3000?token=" + jwtToken;
        } catch (Exception e) {
            e.printStackTrace();
            new GuliException(20001, "error");
        }

        return "redirect:http://localhost:3000";
    }


    /**
     * 第一步
     *
     * 拿着 微信开发者平台申请的 app_id 与 app_secret 以及 redirect_url (详情可见application.properties)
     *
     * 去请求 微信提供的固定接口 https://open.weixin.qq.com/connect/qrconnect?appid=?... 同时将app_id, app_secret等传递过去
     *
     * 用户使用微信登录之后, 跳转至我们指定的redirect_url 也就是重定向地址 (跳转的同时会返回一个 临时票据code 相当于一个临时token)
     * @return
     */
    @GetMapping("/login")
    public String getWxQrCode() {
        // 不建议这种写法, 参数多了之后不好管理
//        String wxUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=" + ConstantWXUtils.WX_OPEN_APP_ID  + "&response_type=code";

//        固定请求地址, 后面拼接 参数  %s表示占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        String redirect_uri = "";
        try {
            // 对redirect_uri进行URLEncoder编码 (微信要求的 传过去的redirect_uri必须使用 URLEncoder编码后传过去)
            redirect_uri = URLEncoder.encode(ConstantWXUtils.WX_OPEN_REDIRECT_URL, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 替换上面 baseUrl 中的占位符 %s 生成新的字符串
        String wxUrl = String.format(
                baseUrl,
                ConstantWXUtils.WX_OPEN_APP_ID,
                redirect_uri,
                "pjj"
        );


        //重定向到 微信登录二维码页面
        return "redirect:" + wxUrl;
    }

}
