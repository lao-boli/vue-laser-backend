package com.hquyyp.protocol;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
public class PingEntity extends ProtoEntity {

    /**
     * 经度
     */
    public double lat;
    /**
     * 纬度
     */
    public double lng;

    /**
     * 马甲编号
     */
    public int number;

    /**
     * 身体被击中次数
     */
    public int backHit;

    /**
     * 头部被击中次数
     */
    public int headHit;

    /**
     * 剩余子弹数
     */
    public int leftAmmo;

    public PingEntity(int type, double lat, double lng, int number, int backHit, int headHit, int leftAmmo) {
        super(type);
        this.lat = lat;
        this.lng = lng;
        this.number = number;
        this.backHit = backHit;
        this.headHit = headHit;
        this.leftAmmo = leftAmmo;
    }


}
