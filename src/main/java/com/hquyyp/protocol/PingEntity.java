package com.hquyyp.protocol;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
public class PingEntity extends ProtoEntity {

    public double lat;
    public double lng;
    public int number;
    public int backHit;
    public int headHit;
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
