package org.pjj.acl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pjj.acl.entity.Permission;
import org.pjj.acl.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author PengJiaJun
 * @Date 2022/4/9 9:39
 */
@RunWith(SpringRunner.class) //不加该注解 bean 注不进来 会报空指针
@SpringBootTest
public class aclTest {

    @Autowired
    PermissionService permissionService;

    @Test
    public void test01() {

        System.out.println(permissionService);

        List<Permission> permissions = permissionService.selectAllMenu("1196300996034977794");
        System.out.println(permissions);
    }

}
