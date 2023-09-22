package com.hquyyp.protocol;

import lombok.ToString;

import java.util.HashMap;

@ToString(callSuper = true)
public class HitEntity extends ProtoEntity {

    public int shootee;
    public int shooter;
    public boolean isUpdate;
    public int position;
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
