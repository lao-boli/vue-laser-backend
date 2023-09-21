package com.hquyyp.protocol;

import lombok.ToString;

import java.util.Arrays;

/**
 * <p>
 * protocol entity father
 * <p>
 *
 * @date 2023/9/21 14:34
 */
@ToString
public class ProtoEntity {

    public int type;

    public ProtoEntity(int type) {
        this.type = type;
    }

    public int[] getIntArr() {
        return new int[]{type};
    }

}

