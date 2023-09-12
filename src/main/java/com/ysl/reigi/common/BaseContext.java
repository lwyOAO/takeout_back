package com.ysl.reigi.common;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/9 14:06
 */
//封装 BaseContext工具类  设置LoginCheckFilter的doFilter方法 设置当前用户的ID
//    然后在 MyMetaObjectHandler 使用 Thread 方法 获取用户ID（MyMetaObjectHandler 不能使用 res获取session的值）
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    /**
     * @description: 获取登录用户ID
     * @author: YSL
     * @time: 2023/2/9 14:06
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    /**
     * @description: 获取登录用户ID
     * @author: YSL
     * @time: 2023/2/9 14:06
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
