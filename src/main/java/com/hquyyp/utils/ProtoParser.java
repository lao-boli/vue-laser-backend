package com.hquyyp.utils;

import com.hquyyp.protocol.HitEntity;
import com.hquyyp.protocol.PingEntity;
import com.hquyyp.protocol.ProtoEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * <p>
 * protocol parser
 * <p>
 *
 * @date 2023/9/20 16:56
 */
@Slf4j
public class ProtoParser {

    public static final int PING_DATA_LENGTH = 15;
    /**
     * old hit data length, maybe unused, now it's just for compatibility.
     *
     * @deprecated
     */
    public static final int HIT_DATA_LENGTH_1 = 6;
    /**
     * current hit data length
     */
    public static final int HIT_DATA_LENGTH = 7;

    public static ProtoEntity decode(byte[] bytes) {
        int[] data = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            data[i] = bytes[i] & 0xff;
        }
        log.info("received byte array: " + Arrays.toString(bytes));
        log.info("convert to int array: " + Arrays.toString(data));
        //位置报送
        if (data.length == PING_DATA_LENGTH) {
            return decodePing(data);
        }
        //hit数据
        if (data.length == HIT_DATA_LENGTH_1 || data.length == HIT_DATA_LENGTH) {
            return decodeHit(data);
        }
        return null;
    }

    /**
     * <table cellspacing=5>
     * <tr>
     *     <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td>
     * </tr>
     * <tr>
     *     <td>击中位置代号</td><td>是否是需要更新的数据</td><td>开枪士兵编号高位</td><td>开枪士兵编号低位</td><td>受击士兵编号高位</td><td>受击士兵编号低位</td><td>消息类型</td>
     * </tr>
     * </table>
     *
     * @param data 转换为正数的裸数据
     * @return 射击数据实体对象
     */
    public static HitEntity decodeHit(int[] data) {
        if (data.length == HIT_DATA_LENGTH_1) {
            int type = data[5];
            int shootee = toUint16(data[3], data[4]);
            int shooter = toUint16(data[1], data[2]);
            int position = data[0];
            return new HitEntity(type, shootee, shooter, true, position);
        }
        // 带state的hit数据
        if (data.length == HIT_DATA_LENGTH) {
            int type = data[6];
            int shootee = toUint16(data[4], data[5]);
            int shooter = toUint16(data[2], data[3]);
            int isUpdate = data[1];
            int position = data[0];
            return new HitEntity(type, shootee, shooter, isUpdate, position);
        }
        return null;
    }

    /**
     * <table cellspacing=5>
     * <tr>
     *     <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td>
     * </tr>
     * <tr>
     *     <td>冗余位</td><td>剩余子弹</td><td>头部被击中次数</td><td>背甲被击中次数</td><td>经度整数位高位</td><td>经度整数位低位</td><td>经度小数位高位</td><td>经度小数位低位</td><td>纬度整数位高位</td><td>纬度整数位低位</td><td>纬度小数位高位</td><td>纬度小数位低位</td><td>士兵编号高位</td><td>士兵编号低位</td><td>消息类型</td>
     * </tr>
     * </table>
     *
     * @param data 转换为正数的裸数据
     * @return 位置报送数据实体对象
     */
    public static PingEntity decodePing(int[] data) {
        int type = data[14];
        int equipment = toUint16(data[12], data[13]);
        double lat = toUint16(data[8], data[9]) / 100.0
                + toUint16(data[10], data[11]) / 1000000.0;
        double lng = toUint16(data[4], data[5]) / 100.0
                + toUint16(data[6], data[7]) / 1000000.0;
        int backHit = data[3];
        int headHit = data[2];
        int leftAmmo = data[1];
        return new PingEntity(type, lat, lng, equipment, backHit, headHit, leftAmmo);
    }

    public static int toUint16(int high, int low) {
        return (high << 8) + low;
    }

}
