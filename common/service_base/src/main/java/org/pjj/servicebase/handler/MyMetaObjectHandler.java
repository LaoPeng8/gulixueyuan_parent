package org.pjj.servicebase.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis-plus 自动填充的内容
 * @author PengJiaJun
 * @Date 2022/2/15 10:45
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时 自动填充 gmtCreate 、 gmtModified 为当前时间
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("gmtCreate", new Date(), metaObject);
        this.setFieldValByName("gmtModified", new Date(), metaObject);
    }

    /**
     * 修改时 自动填充 gmtModified 为当前时间
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtModified", new Date(), metaObject);
    }
}
