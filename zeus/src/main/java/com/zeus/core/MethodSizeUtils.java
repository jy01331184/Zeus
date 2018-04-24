package com.zeus.core;

import android.os.Build;

/**
 * Created by magic.yang on 17/5/16.
 */

public class MethodSizeUtils {

    private static int size = Constants.INVALID_SIZE;
    private static IMethodSize methodSize = null;
    private static int methodIndexOffset = Constants.INVALID_SIZE;
    private static int declaringClassOffset = Constants.INVALID_SIZE;
    private static int superClassOffset = Constants.INVALID_SIZE;

    static {
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 21) {
        } else if (Build.VERSION.SDK_INT == 21) {
        } else if (Build.VERSION.SDK_INT == 22) {
            methodSize = new MethodSize5_1();
        } else if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 26) {
            methodSize = new MethodSize6_0();
        } else {
            methodSize = new MethodSize8_0();
        }
    }

    public static int superClassOffset() throws Exception {
        if (superClassOffset == Constants.INVALID_SIZE) {
            superClassOffset = methodSize.superClassOffset();
        }
        if (superClassOffset == Constants.INVALID_SIZE) {
            throw new RuntimeException();
        }
        return superClassOffset;
    }

    public static int methodSize() throws Exception {
        if (size == Constants.INVALID_SIZE) {
            size = methodSize.methodSize();
        }
        if (size == Constants.INVALID_SIZE) {
            throw new RuntimeException();
        }
        return size;
    }

    public static int methodIndexOffset() throws Exception {
        if (methodIndexOffset == Constants.INVALID_SIZE) {
            methodIndexOffset = methodSize.methodIndexOffset();
        }

        if (methodIndexOffset == Constants.INVALID_SIZE) {
            throw new RuntimeException();
        }

        return methodIndexOffset;
    }

    public static int declaringClassOffset() throws Exception {
        if (declaringClassOffset == Constants.INVALID_SIZE) {
            declaringClassOffset = methodSize.declaringClassOffset();
        }

        if (declaringClassOffset == Constants.INVALID_SIZE) {
            throw new RuntimeException();
        }

        return declaringClassOffset;
    }

}
