package com.ysl.reigi.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @description: 自定义元数据对象处理器
 * @author: YSL
 * @time: 2023/2/9 13:16
 */

// 属性自动填充公共字段
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * @description: 插入操作自动填充
     * @author: YSL
     * @time: 2023/2/9 13:16
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充 [inset]");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
    /**
     * @description: 更新操作自动填充
     * @author: YSL
     * @time: 2023/2/9 13:16
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充 [update]");
        log.info(metaObject.toString());


//            使用Thread 里面的方法获取线程的id值 通过同一个线程为一个存储空间 在LoginCheckFilter使用线程set用户ID 从使用Thread的get方法取到
        long id = Thread.currentThread().getId();
        log.info("线程id为 {}",id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
