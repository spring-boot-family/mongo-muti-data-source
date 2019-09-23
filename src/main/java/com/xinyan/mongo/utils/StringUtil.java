package com.xinyan.mongo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public class StringUtil {
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
    public static boolean containsEmpty(Object... arr) {
        for (Object str : arr) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isAllEmpty(Object... arr) {
        int count = 0;
        for (Object str : arr) {
            if (isEmpty(str)) {
                count++;
            }
        }
        return arr.length == count;
    }
    /**
     * 根据表达式跟传参，得到新字符串
     *
     * @param express 表达式
     * @param params  传参集合
     * @return
     */
    public static String getUrl(String express, String... params) {
        return String.format(express, params);
    }
    /**
     * 将对象toString 结果转换成UTF-8编码URL格式数据
     *
     * @param o
     * @return
     */
    public static String toURLString(Object o) {
        if (o == null) {
            return "";
        }
        try {
            return URLEncoder.encode(o.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    public static String rightLike(String str) {
        if (isEmpty(str)) {
            return null;
        }
        return str + "%";
    }
    public static String leftLike(String str) {
        if (isEmpty(str)) {
            return null;
        }
        return "%" + str;
    }
    public static String fullLike(String str) {
        if (isEmpty(str)) {
            return null;
        }
        return "%" + str + "%";
    }
    /**
     * 将日期 精度对象转为字符串
     */
    public static String convertStringValue(Object value) {
        if (null == value) {
            return "";
        } else if (value instanceof BigDecimal) {
            return String.valueOf(((BigDecimal) value).doubleValue());
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value);
        } else {
            return value.toString();
        }
    }
    public static String splitStrForIn(String strs) {
        return splitStrForIn(strs, null);
    }
    /**
     * <ol>
     * <li>将一个字符串<b>strs</b>以<b>split</b>分割</li>
     * <li>如果split为空,则默认是以<code>,</code>分割</li>
     * </ol>
     *
     * @param strs  要分割的字符串
     * @param split 要分割的字符
     * @return <pre>
     * 	如果strs为空
     * 	返回: ('')
     * 	否则返回: ('aa','bb')
     *         </pre>
     */
    public static String splitStrForIn(String strs, String split) {
        if (StringUtils.isBlank(strs)) {
            return "('')";
        }
        StringBuilder sb = new StringBuilder(" (");
        String ch = split;
        if (StringUtils.isBlank(split)) {
            ch = ",";
        }
        String[] splitArrs = StringUtils.split(strs, ch);
        int len = splitArrs.length;
        for (int i = 0; i < len; i++) {
            sb.append("'").append(splitArrs[i]).append("'");
            if (i != len - 1) {
                sb.append(",");
            }
        }
        sb.append(") ");
        return sb.toString();
    }
    public static String splitStrForInInt(String strs) {
        return splitStrForInInt(strs, null);
    }
    public static String splitStrForInInt(String strs, String split) {
        if (StringUtils.isBlank(strs)) {
            return "('')";
        }
        StringBuilder sb = new StringBuilder(" (");
        String ch = split;
        if (StringUtils.isBlank(split)) {
            ch = ",";
        }
        String[] splitArrs = StringUtils.split(strs, ch);
        int len = splitArrs.length;
        for (int i = 0; i < len; i++) {
            sb.append(splitArrs[i]);
            if (i != len - 1) {
                sb.append(",");
            }
        }
        sb.append(") ");
        return sb.toString();
    }
    public static String toHump(String str) {
        return toHump(str, "_");
    }
    /**
     * 转换字符串为驼峰命名
     *
     * @param str 待转换字符串
     * @param ch  单词分割符
     * @return
     */
    public static String toHump(String str, String ch) {
        String[] split = StringUtils.split(str, ch);
        StringBuilder sb = new StringBuilder(split[0]);
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                sb.append(StringUtils.capitalize(split[i]));
            }
        }
        return sb.toString();
    }
    public static String
    humpSplit(String hump) {
        return humpSplit(hump, "_");
    }
    /**
     * 驼峰命名根据分隔符拆分
     *
     * @param hump  待拆分字符串
     * @param split 分隔符
     * @return
     */
    public static String humpSplit(String hump, String split) {
        char[] chars = hump.toCharArray();
        StringBuilder result = new StringBuilder();
        for (Character ch : chars) {
            if ('A' <= ch && ch <= 'Z') {
                result.append(split).append(ch.toString().toLowerCase());
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
    /**
     * 获取json对象对应key值
     *
     * @param json
     * @param key
     * @return
     */
    public static String getValueByKey(String json, String key) {
        JSONObject obj = JSON.parseObject(json);
        return obj.getString(key);
    }
    /**
     * 将一个"1,2,3,4,5,6,7,8"类型的数据转换成 List<Integer>
     *
     * @param str 需要转换的字符串
     * @return List<Integer>
     */
    public static List<Integer> str2ListInt(String str) {
        return Arrays.stream(StringUtils.splitByWholeSeparator(str, ",")).map(Integer::valueOf).collect(Collectors.toList());
    }
    /**
     * 生成分类redis key
     *
     * @param keys
     * @return
     */
    public static String redisKey(String... keys) {
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            if (isEmpty(key)) {
                throw new RuntimeException("key值不能为空");
            }
            builder.append(key).append(":");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
    /**
     * 判断在Values数组，是否包含key的值
     *
     * @param key
     * @param values
     * @return
     */
    public static boolean valuesContainsKey(String key, String... values) {
        if (containsEmpty(key, values)) {
            return false;
        }
        for (String value : values) {
            if (key.equals(value)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 去除DataMongoObject中的_class
     *
     * @param data
     * @return
     */
    public static String toJsonWithReplaceClass(Object data) {
        return replaceClass(JsonUtil.toString(data));
    }
    /**
     * 去除DataMongoObject中的_class
     *
     * @param data
     * @return
     */
    public static String replaceClass(String data) {
        return data.replaceAll(",\"_class\":\".*?\"\\}", "\\}").replaceAll(",\"_class\":\".*?\",", ",").replaceAll("\\{\"_class\":\".*?\",", "\\{");
    }
}
