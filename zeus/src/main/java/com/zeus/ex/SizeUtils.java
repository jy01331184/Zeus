package com.zeus.ex;

import java.util.Arrays;

/**
 * Created by magic.yang on 17/5/20.
 */

public class SizeUtils {

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
            return 0;
        }

        return (int) temp1;

    }


}
