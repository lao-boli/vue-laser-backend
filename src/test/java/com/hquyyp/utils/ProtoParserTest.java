package com.hquyyp.utils;

import com.hquyyp.protocol.HitEntity;
import com.hquyyp.protocol.PingEntity;
import com.hquyyp.protocol.ProtoEntity;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>
 *
 * <p>
 *
 * @date 2023/9/21 15:26
 */
public class ProtoParserTest {

    @Test
    public void decodeTestPing() {
        PingEntity exceptPing = new PingEntity(138, 24.563346, 118.381233, 12, 0, 0, 128);
        byte[] data = new byte[]{0, -128, 0, 0, 46, 62, 4, -47, 9, -104, 13, 18, 0, 12, -118};
        ProtoEntity result = ProtoParser.decode(data);
        assertTrue(result instanceof PingEntity);
        assertEquals(exceptPing.toString(), result.toString());
    }

    @Test
    public void decodeTestHit() {
        HitEntity except = new HitEntity(91, 120, 456, true, 64);
        byte[] data = new byte[]{0x40,0x01, 0x01, (byte) 0xC8, 0x00, 0x78, 0x5B};
        ProtoEntity result = ProtoParser.decode(data);
        System.out.println(result);
        assertTrue(result instanceof HitEntity);
        assertEquals(except.toString(), result.toString());
    }

    @Test
    public void decodeHit() {
    }

    @Test
    public void decodePing() {
        PingEntity except = new PingEntity(138, 24.563346, 118.381233, 12, 0, 0, 128);
        int[] data = new int[]{0, 128, 0, 0, 46, 62, 4, 209, 9, 152, 13, 18, 0, 12, 138};
        PingEntity result = ProtoParser.decodePing(data);
        System.out.println(result);
        assertEquals(except.toString(), result.toString());
    }

}
