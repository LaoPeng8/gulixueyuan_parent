package org.pjj.acl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.acl.entity.Permission;
import org.pjj.acl.entity.RolePermission;
import org.pjj.acl.entity.User;
import org.pjj.acl.helper.MemuHelper;
import org.pjj.acl.helper.PermissionHelper;
import org.pjj.acl.mapper.PermissionMapper;
import org.pjj.acl.service.PermissionService;
import org.pjj.acl.service.RolePermissionService;
import org.pjj.acl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private UserService userService;
    
    //获取全部菜单
    @Override
    public List<Permission> queryAllMenu() {

        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<Permission> permissionList = baseMapper.selectList(wrapper);

        List<Permission> result = bulid(permissionList);

        return result;
    }

    //根据角色获取菜单
    @Override
    public List<Permission> selectAllMenu(String roleId) {
        //查询权限表 查询出所有权限(所有菜单)
        List<Permission> allPermissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)"));

        //根据角色id 获取角色权限 (一个角色对应多个权限)
        List<RolePermission> rolePermissionList = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("role_id",roleId));

        //转换给角色id与角色权限对应Map对象
//        List<String> permissionIdList = rolePermissionList.stream().map(e -> e.getPermissionId()).collect(Collectors.toList());
//        allPermissionList.forEach(permission -> {
//            if(permissionIdList.contains(permission.getId())) {
//                permission.setSelect(true);
//            } else {
//                permission.setSelect(false);
//            }
//        });

        // 遍历全部权限 与 角色权限, 如果全部权限中的某给权限id == 角色权限的权限id
        // , 即表示该权限属于该角色, 即设置 permission.setSelect(true); 标记该权限, 方便后面build()方法中构建 树形菜单
        for (int i = 0; i < allPermissionList.size(); i++) {
            Permission permission = allPermissionList.get(i);
            for (int m = 0; m < rolePermissionList.size(); m++) {
                RolePermission rolePermission = rolePermissionList.get(m);
                if(rolePermission.getPermissionId().equals(permission.getId())) {
                    permission.setSelect(true);
                }
            }
        }

        List<Permission> permissionList = bulid(allPermissionList);
        return permissionList;
    }

    //给角色分配权限
    @Override
    public void saveRolePermissionRealtionShip(String roleId, String[] permissionIds) {

        rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));

  

        List<RolePermission> rolePermissionList = new ArrayList<>();
        for(String permissionId : permissionIds) {
            if(StringUtils.isEmpty(permissionId)) continue;
      
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionList.add(rolePermission);
        }
        rolePermissionService.saveBatch(rolePermissionList);
    }

    //递归删除菜单
    @Override
    public void removeChildById(String id) {
        List<String> idList = new ArrayList<>();
        this.selectChildListById(id, idList);

        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    //根据用户id获取用户菜单
    @Override
    public List<String> selectPermissionValueByUserId(String id) {

        List<String> selectPermissionValueList = null;
        if(this.isSysAdmin(id)) {
            //如果是系统管理员，获取所有权限
            selectPermissionValueList = baseMapper.selectAllPermissionValue();
        } else {
            selectPermissionValueList = baseMapper.selectPermissionValueByUserId(id);
        }
        return selectPermissionValueList;
    }

    @Override
    public List<JSONObject> selectPermissionByUserId(String userId) {
        List<Permission> selectPermissionList = null;
        if(this.isSysAdmin(userId)) {
            //如果是超级管理员，获取所有菜单
            selectPermissionList = baseMapper.selectList(null);
        } else {
            selectPermissionList = baseMapper.selectPermissionByUserId(userId);
        }

        List<Permission> permissionList = PermissionHelper.bulid(selectPermissionList);
        List<JSONObject> result = MemuHelper.bulid(permissionList);
        return result;
    }

    /**
     * 判断用户是否系统管理员
     * @param userId
     * @return
     */
    private boolean isSysAdmin(String userId) {
        User user = userService.getById(userId);

        if(null != user && "admin".equals(user.getUsername())) {
            return true;
        }
        return false;
    }

    /**
     *	递归获取子节点
     * @param id
     * @param idList
     */
    private void selectChildListById(String id, List<String> idList) {
        List<Permission> childList = baseMapper.selectList(new QueryWrapper<Permission>().eq("pid", id).select("id"));
        childList.stream().forEach(item -> {
            idList.add(item.getId());
            this.selectChildListById(item.getId(), idList);
        });
    }

    /**
     * 使用递归方法建菜单
     * @param treeNodes 所有权限集合
     * @return
     */
    private static List<Permission> bulid(List<Permission> treeNodes) {
        List<Permission> trees = new ArrayList<>();//最终返回的树形结构的数据

        for (Permission treeNode : treeNodes) {
            if ("0".equals(treeNode.getPid())) {//找到主菜单 (pid = 0 即为主菜单)
                treeNode.setLevel(1);//设置主菜单层级为1
                trees.add(findChildren(treeNode, treeNodes));//进行递归调用
            }
        }
        return trees;
    }

    /*
     * 递归查找子节点
     * @param treeNode 权限(菜单)
     * @param treeNodes 全部权限(菜单)
     * @return
     */
    private static Permission findChildren(Permission treeNode, List<Permission> treeNodes) {
        treeNode.setChildren(new ArrayList<Permission>());//初始化 子菜单集合

        // 遍历所有权限
        for (Permission it : treeNodes) {

            // 遍历所有菜单, 比较id 与 pid 是否相同 (找到 当前菜单下的子菜单 id为当前菜单(treeNode)的id, pid为遍历的所有菜单)
            if(treeNode.getId().equals(it.getPid())) {
                int level = treeNode.getLevel() + 1;
                it.setLevel(level);

                //把查询出来的子菜单放到父菜单中
                treeNode.getChildren().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }


    //========================递归查询所有菜单================================================
    //获取全部菜单 (自己写的)
    @Override
    public List<Permission> queryAllMenuGuli() {
        //查询菜单表所有数据
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<Permission> permissions = baseMapper.selectList(wrapper);//查出所有数据(查出所有权限菜单)

        //把查询出来的所有数据 按照要求封装为 树形结构(一级权限 用户管理(二级权限 查询 删除...), 一级权限 讲师管理(二级权限 查询 删除...))
        List<Permission> resultList = bulidPermission(permissions);

        return resultList;
    }

    //把返回所有菜单的list集合进行封装的方法 (自己写的)
    public static List<Permission> bulidPermission(List<Permission> permissionList) {

        //创建list集合, 用于数据最终封装
        List<Permission> finalNode = new ArrayList<>();

        //把所有菜单list集合遍历, 找到顶层菜单 pid = 0 的菜单, 设置level=1
        for(Permission permission : permissionList) {
            if(permission.getPid().equals("0")) {//得到顶层菜单
                permission.setLevel(1);//设置层级为1
                //根据顶层菜单, 向下继续查询子菜单(找到pid等于顶层菜单的id的菜单), 封装到finalNode中
                finalNode.add(selectChildren(permission, permissionList));//传入顶层菜单, 与所有菜单
            }
        }

        return finalNode;
    }

    //找出 将当前菜单的子菜单 并放入当前菜单的子菜单属性中 (递归操作, 还是非常秒的, 明白了又不完全明白)
    private static Permission selectChildren(Permission permissionNode, List<Permission> permissionList) {
        //因为需要向 一层菜单里面放二级菜单, 二级菜单里面放三级菜单
        permissionNode.setChildren(new ArrayList<>());//初始化 子菜单集合 防止空指针

        //遍历所有菜单, 比较id 与 pid 是否相同 (找到 当前菜单下的子菜单 id为当前菜单(permissionNode)的id, pid为遍历的所有菜单)
        for(Permission permission : permissionList) {
            //判断 id 与 pid是否相同
            if(permissionNode.getId().equals(permission.getPid())) {
                int level = permissionNode.getLevel() + 1;//当前菜单层级 + 1, 就是当前菜单的子菜单的层级
                permission.setLevel(level);

                //把查询出来的子菜单放到父菜单中
                permissionNode.getChildren().add(selectChildren(permission, permissionList));
            }
        }

        return permissionNode;
    }

    //============递归删除菜单==================================
    @Override
    public void removeChildByIdGuli(String id) {
        //存放所有查询出来的菜单id 以及 子菜单id
        List<String> idList = new ArrayList<>();

        //查询出所有菜单数据, 方便遍历
        List<Permission> allList = baseMapper.selectList(null);

        //递归找出所有需要删除的子菜单id, 并放入待删除的List集合中 (只查找出了当前菜单的子菜单, 并没有将当前菜单id放入idList)
        selectPermissionChildById(id, idList, allList);
        idList.add(id);//将当前菜单id 封装入 待删除List集合

        baseMapper.deleteBatchIds(idList);//根据 idList 中存放的id 删除菜单
    }

    //2 根据当前菜单id，查询菜单里面子菜单id，封装到list集合 (最后将list集合中的id对应的数据全部删除 完成递归删除菜单)
    private void selectPermissionChildById(String id, List<String> idList, List<Permission> allList) {
        //根据当前菜单id 查找出 所有为该菜单的子菜单(pid = 当前菜单id的菜单 就是当前菜单的子菜单)
        for(Permission temp : allList) {
            if(temp.getPid().equals(id)) {
                idList.add(temp.getId());//将找出来的子菜单id 放入 待删除的List集合

                //递归查询
                selectPermissionChildById(temp.getId(), idList, allList);
            }
        }

    }

    /**
     * 给角色分配菜单
     * @param roleId 需要分配菜单的 角色id
     * @param permissionIds 菜单id (数组形式) 需要将哪些菜单 分配给 角色
     */
    @Override
    public void saveRolePermissionRealtionShipGuli(String roleId, String[] permissionIds) {
        //创建list集合, 用于封装 insert 的数据
        List<RolePermission> rolePermissionsList = new ArrayList<>();

        //遍历所有菜单数组(权限数组)
        for(String permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();//创建角色权限关联表对象
            rolePermission.setRoleId(roleId);//向角色权限关联表对象中传入 角色id
            rolePermission.setPermissionId(permissionId);//向角色权限关联表对象中传入 权限id (菜单id)

            //将封装好的 角色权限关联表对象 放入list集合, 最终将list中的对象批量插入到 角色 与 权限 关联表中
            rolePermissionsList.add(rolePermission);
        }

        //最终的向 角色 与 权限 关联表中添加记录
        rolePermissionService.saveBatch(rolePermissionsList);
    }
}
