package com.zeus;

import java.util.Arrays;

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



    public static int size(long a, long b, long c,long d) {

        long[] arr = new long[4];
        arr[0] = a;
        arr[1] = b;
        arr[2] = c;
        arr[3] = d;
        Arrays.sort(arr);

        long temp1 = arr[1] - arr[0];

        long temp2 = arr[3] - arr[2];

        if(temp1 != temp2){
            return Constants.INVALID_SIZE;
        }

        return (int) temp1;

    }


}
