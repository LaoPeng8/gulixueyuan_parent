package org.pjj.acl.service.impl;

import org.pjj.acl.entity.User;
import org.pjj.acl.service.PermissionService;
import org.pjj.acl.service.UserService;
import org.pjj.security.entity.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 自定义userDetailsService - 认证用户详情
 * </p>
 *
 * @author qy
 * @since 2019-11-08
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    /***
     * 根据账号获取用户信息
     * 根据 用户名 获取 用户信息(密码, 权限信息)
     * (TokenLoginFilter.java中 获取了前端用户传入的username 与 password 然后返回给SpringSecurity)
     * (这里 根据 username 获取数据库 中该用户真正的密码(以及其他信息, 比如该用户的权限信息) 然后交给SpringSecurity)
     * (由SpringSecurity去比对登录是否成功) (然后在TokenLoginFilter.java中执行对应的 成功方法 或 失败方法)
     * (登录成功的方法中 会将用户的权限信息 放入 redis 中, 然后在TokenAuthFilter.java中会将权限信息 交给SpringSecurity的权限上下文, 相当于授权)
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.selectByUsername(username);//根据用户名查询用户
        if(user == null) {
            throw new UsernameNotFoundException("User does not exist");//用户不存在
        }
        //与数据库交互的User中属性太多, SpringSecurity 并不需要, 所以 new 一个Security包的的user
        org.pjj.security.entity.User curUser = new org.pjj.security.entity.User();
        BeanUtils.copyProperties(user, curUser);//将user中的值 copy 到curUser

        //根据用户查询用户权限列表
        List<String> permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());

        //需要返回 SecurityUser, 该类中 登录的用户信息, 此处通过构造器传入curUser, 用户的权限信息 通过set方法传入
        SecurityUser securityUser = new SecurityUser(curUser);
        securityUser.setPermissionValueList(permissionValueList);

        return securityUser;
    }

}
