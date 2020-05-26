package com.lvhaifeng.mybatis.utils;

/**
 * @Description string 工具类
 * @Author haifeng.lv
 * @Date 2019/12/18 16:31
 */
public class StringUtils {
    public static boolean isNotEmpty(Object object) {
        if (object != null && !object.equals("") && !object.equals("null")) {
            return (true);
        }
        return (false);
    }
}