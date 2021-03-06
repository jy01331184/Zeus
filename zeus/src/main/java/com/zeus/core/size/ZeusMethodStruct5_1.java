package com.zeus.core.size;

import com.zeus.ex.MethodSizeCase;
import com.zeus.ex.SizeUtils;
import com.zeus.ex.UnsafeProxy;
import com.zeus.ex.ZeusException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by magic.yang on 17/5/16.
 */

public class ZeusMethodStruct5_1 implements IZeusMethodStruct {

    private int methodSize;
    private int methodIndexOffset;
    private int declaringClassOffset;
    private int superClassOffset;


    public ZeusMethodStruct5_1() {
        try {
            Field artMethodField;
            Class absMethodClass = Class.forName("java.lang.reflect.AbstractMethod");
            artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);

            //init size
            Method method1 = MethodSizeCase.class.getDeclaredMethod("method1");
            Method method2 = MethodSizeCase.class.getDeclaredMethod("method2");
            Method method3 = MethodSizeCase.class.getDeclaredMethod("method3");
            Method method4 = MethodSizeCase.class.getDeclaredMethod("method4");

            Object object1 = artMethodField.get(method1);
            Object object2 = artMethodField.get(method2);
            Object object3 = artMethodField.get(method3);
            Object object4 = artMethodField.get(method4);

            long method1Addr = UnsafeProxy.getObjectAddress(object1);
            long method2Addr = UnsafeProxy.getObjectAddress(object2);
            long method3Addr = UnsafeProxy.getObjectAddress(object3);
            long method4Addr = UnsafeProxy.getObjectAddress(object4);


            methodSize = SizeUtils.size(method1Addr, method2Addr, method3Addr, method4Addr);

            //init methodIndexOffset declaringClassOffset
            Class artMethodClass = object1.getClass();
            Field methodIndexField = artMethodClass.getDeclaredField("methodIndex");
            Field declaringClassField = artMethodClass.getDeclaredField("declaringClass");

            declaringClassOffset = (int) UnsafeProxy.objectFieldOffset(declaringClassField);
            methodIndexOffset = (int) UnsafeProxy.objectFieldOffset(methodIndexField);

            long classAddr = UnsafeProxy.getObjectAddress(MethodSizeCase.class);

            long objectClassAddr = UnsafeProxy.getObjectAddress(Object.class);

            for (int i = 0; i < 20; i++) {
                int val = UnsafeProxy.getIntVolatile(classAddr + i * 4);
                if (val == objectClassAddr) {
                    superClassOffset = i * 4;
                    break;
                }
            }

            System.out.println("ZeusMethodStruct5_1:init declaringClassOffset:" + declaringClassOffset + "," + "methodIndexOffset:" + methodIndexOffset + "," + "superClassOffset:" + superClassOffset);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int methodSize() throws Exception {
        if (methodSize <= 0) {
            throw new ZeusException("ZeusMethodStruct5_1 methodSize not correct:" + methodSize);
        }
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

    @Override
    public int superClassOffset() throws Exception {
        return superClassOffset;
    }


}
