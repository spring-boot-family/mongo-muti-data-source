package com.xinyan.mongo.utils;

import com.alibaba.fastjson.JSON;
import com.xinyan.mongo.common.InnerException;
import com.xinyan.mongo.common.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Slf4j
public class ObjectUtil {
    /**
     * 校验对象数据大小
     *
     * @param target 目标对象
     * @param size   比对大小
     * @return 目标对象大小 < 比对大小 返回true,其他返回false;
     */
    public static boolean compareObjectSize(Object target, int size) {
        if (target == null) {
            throw new InnerException(StatusCode.UNKNOWN);
        }
        try {
            String dataStr = JSON.toJSONString(target);
            int dataLength = dataStr.getBytes("UTF-8").length;
            log.info("校验对象数据大小dataLength:{}, {}mb", dataLength, dataLength / (1024 * 1024));
            if (dataLength < size) {
                return true;
            }
        } catch (Exception e) {
            log.error("校验对象数据大小异常e:{}", e);
        }
        return false;
    }
    /**
     * 校验对象数据大小是否超过16MB
     *
     * @param target 目标对象
     * @return 目标对象大小 < 比对大小 返回true,其他返回false;
     */
    public static boolean isSixteenMB(Object target) {
        if (target == null) {
            throw new InnerException(StatusCode.UNKNOWN);
        }
        try {
            String dataStr = JSON.toJSONString(target);
            int dataLength = dataStr.getBytes("UTF-8").length;
            log.info("校验对象数据大小dataLength:{}, {}mb", dataLength, dataLength / (1024 * 1024));
            if (dataLength > Constans.MONGO_LIMIT_SIZE) {
                return true;
            }
        } catch (Exception e) {
            log.error("校验对象数据大小异常e:{}", e);
        }
        return false;
    }
    /**
     * 将对象转换成 InputStream
     *
     * @param object
     * @return
     */
    public static InputStream objectToInputStream(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(object);
            oos.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            log.error("对象转inputStream 异常e:{}", e);
            throw new InnerException(StatusCode.UNKNOWN);
        }
    }
    /**
     * 将InputStream 转换成 Object
     *
     * @param is
     * @return
     */
    public static Object inputStreamToObject(InputStream is) {
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        } catch (Exception e) {
            log.error("对象转inputStream 异常e:{}", e);
            throw new InnerException(StatusCode.UNKNOWN);
        }
    }
    /**
     * 序列化为bytes
     *
     * @param obj
     * @return
     */
    public static byte[] toBytes(Serializable obj) {
        return SerializationUtils.serialize(obj);
    }
    /**
     * 反序列化为对象
     *
     * @param bytes
     * @return
     */
    public static Object toObj(byte[] bytes) {
        return SerializationUtils.deserialize(bytes);
    }
    /**
     * 将对象转换成 byte[]
     *
     * @param object
     * @return
     */
    public static byte[] objectToBytes(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(object);
            out.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("对象转byte[]异常e:{}", e);
            throw new InnerException(StatusCode.UNKNOWN);
        }
    }
}

