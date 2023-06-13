package com.github.walkvoid.worknote;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/4/21
 * @desc
 */
public class Float {

    @Test
    public void test1() {
        int i = java.lang.Float.floatToIntBits(0.6f);
        System.out.println(i);//1058642330 00111111 00011001 10011001 10011010
        float f = java.lang.Float.intBitsToFloat(1058642330);
        System.out.println(f);//0.6

    }

    @Test
    public void test2() {
        BigDecimal bigDecimal = new BigDecimal("7.35");
        BigDecimal bigDecimal2 = new BigDecimal("0.32");
        System.out.println(bigDecimal.multiply(bigDecimal2).setScale(2, BigDecimal.ROUND_HALF_UP));


    }
}
