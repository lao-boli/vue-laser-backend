package com.hquyyp.protocol;

import lombok.ToString;

import java.util.HashMap;

@ToString(callSuper = true)
public class HitEntity extends ProtoEntity {

    /**
     * 受击者编号
     */
    public int shootee;
    /**
     * 射击者编号
     */
    public int shooter;
    /**
     * 是否是要更新的射击数据flag
     */
    public boolean isUpdate;

    /**
     * 击中部位编码 {@link #setHitPart(int)}
     *
     */
    public int position;

    /**
     * 转换后的击中部位,{@link #setHitPart(int)}
     */
    public String hitPart;

    public HitEntity(int type, int shootee, int shooter, int isUpdate, int position) {
        this(type, shootee, shooter, isUpdate == 1, position);
    }

    public HitEntity(int type, int shootee, int shooter, boolean isUpdate, int position) {
        super(type);
        this.shootee = shootee;
        this.shooter = shooter;
        this.isUpdate = isUpdate;
        this.position = position;
        setHitPart(position);
    }

    private void setHitPart(int position) {
        switch (position) {
            case 1:
                hitPart = "头部";
                break;
            case 2:
                hitPart = "腹部";
                break;
            case 4:
                hitPart = "左手";
                break;
            case 8:
                hitPart = "右手";
                break;
            case 16:
                hitPart = "左脚";
                break;
            case 32:
                hitPart = "右脚";
                break;
            case 64:
                hitPart = "后甲";
                break;
            case 128:
                hitPart = "前甲";
                break;
        }

    }

}
