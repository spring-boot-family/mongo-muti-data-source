package com.xinyan.mongo.utils;

/**
 * 静态常量
 * @author weimin_ruan
 * @date 2019/9/23
 */
public class Constans {
    /**
     * mongo 16M 数据大小, 因与mongo判断大小有一定误差, 所以改小一点为10M
     */
    public final static int MONGO_LIMIT_SIZE = 16 * 1024 * 1024;
    /**
     * 测试环境 100kb
     */
    public final static int TEST_MONGO_LIMIT_SIZE = 1 * 9300;
    /**
     * 中文正则表达式
     */
    public final static String chineseReg = "[\u4e00-\u9fa5]";
    public final static String phoneReg = "\\d{8,11}";
}
