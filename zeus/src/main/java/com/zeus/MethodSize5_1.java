package com.zeus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by jingchaoqinjc on 17/5/16.
 */

public class MethodSize5_1 implements IMethodSize {

    private static int methodSize = Constants.INVALID_SIZE;
    private static int methodIndexOffset = Constants.INVALID_SIZE;
    private static int declaringClassOffset = Constants.INVALID_SIZE;

    static {
        try {
            Field artMethodField;
            Class absMethodClass = Class.forName("java.lang.reflect.AbstractMethod");
            artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);

            //init size
            Method method1 = MethodSizeCase.class.getDeclaredMethod("method1");
            Method method2 = MethodSizeCase.class.getDeclaredMethod("method2");
            Method method3 = MethodSizeCase.class.getDeclaredMethod("method3");

            Object object1 = artMethodField.get(method1);
            Object object2 = artMethodField.get(method2);
            Object object3 = artMethodField.get(method3);

            long method1Addr = UnsafeProxy.getObjectAddress(object1);
            long method2Addr = UnsafeProxy.getObjectAddress(object2);
            long method3Addr = UnsafeProxy.getObjectAddress(object3);
            int size1 = (int) (method2Addr - method1Addr);

            if (size1 < 0) {
                size1 = -size1;
            }

            int size2 = (int) (method3Addr - method2Addr);
            if (size2 < 0) {
                size2 = -size2;
            }

            methodSize = AnthyphairesisUtils.anthyphairesis(size1, size2);

            //init methodIndexOffset declaringClassOffset
            Class artMethodClass = object1.getClass();
            Field methodIndexField = artMethodClass.getDeclaredField("methodIndex");
            Field declaringClassField = artMethodClass.getDeclaredField("declaringClass");

            declaringClassOffset = (int) UnsafeProxy.objectFieldOffset(declaringClassField);
            methodIndexOffset = (int) UnsafeProxy.objectFieldOffset(methodIndexField);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int methodSize() throws Exception {
        return methodSize;
    }

    @Override
    public int methodIndexOffset() throws Exception {
        return methodIndexOffset;
    }

    @Override
    public int declaringClassOffset() throws Exception {
        return declaringClassOffset;
    }


}
