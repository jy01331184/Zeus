package com.zeus;

/**
 * Created by jingchaoqinjc on 17/5/20.
 */

//欧几里德公因子算法
public class AnthyphairesisUtils {

    public static int anthyphairesis(int a, int b) {
        if (a <= 0 || b <= 0) {
            throw new IllegalArgumentException();
        }
        if (a < b) {
            return anthyphairesis(b, a);
        }
        if (a % b == 0) {
            return b;
        }
        return anthyphairesis(b, a % b);
    }
}
