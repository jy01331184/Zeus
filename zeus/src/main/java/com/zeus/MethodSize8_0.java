package com.zeus;

import com.zeus.ex.MethodSizeCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by jingchaoqinjc on 17/5/16.
 */

public class MethodSize8_0 implements IMethodSize {

    private static int methodSize = Constants.INVALID_SIZE;
    private static int methodIndexOffset = Constants.INVALID_SIZE;
    private static int declaringClassOffset = Constants.INVALID_SIZE;
    private static int superClassOffset = Constants.INVALID_SIZE;

    static {
        try {
            Class absMethodClass = Class.forName("java.lang.reflect.Executable");
            Field artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);

            //init size
            Method method1 = MethodSizeCase.class.getDeclaredMethod("method1");
            Method method2 = MethodSizeCase.class.getDeclaredMethod("method2");
            Method method3 = MethodSizeCase.class.getDeclaredMethod("method3");
            Method method4 = MethodSizeCase.class.getDeclaredMethod("method4");

            long method1Addr = ((Long) artMethodField.get(method1)).longValue();
            long method2Addr = ((Long) artMethodField.get(method2)).longValue();
            long method3Addr = ((Long) artMethodField.get(method3)).longValue();
            long method4Addr = ((Long) artMethodField.get(method4)).longValue();

            methodSize = AnthyphairesisUtils.size(method1Addr, method2Addr, method3Addr, method4Addr);

            int constructNumber = MethodSizeCase.class.getDeclaredConstructors().length;

            //init methodIndexOffset
            for (int i = 0, size = methodSize / 4; i < size; i++) {
                int value1 = UnsafeProxy.getIntVolatile(method1Addr + i * 4);
                int value2 = UnsafeProxy.getIntVolatile(method2Addr + i * 4);
                int value3 = UnsafeProxy.getIntVolatile(method3Addr + i * 4);
                int value4 = UnsafeProxy.getIntVolatile(method4Addr + i * 4);
                if (value1 == constructNumber && value2 == value1 + 1
                        && value3 == value1 + 2
                        && value4 == value1 + 3) {
                    methodIndexOffset = i * 4;
                    break;
                }
            }

            long declaringClassAddr = UnsafeProxy.getObjectAddress(MethodSizeCase.class);
            //init declaringClassOffset
            for (int i = 0, size = methodSize / 4; i < size; i++) {
                int value1 = UnsafeProxy.getIntVolatile(method1Addr + i * 4);
                if (value1 == declaringClassAddr) {
                    declaringClassOffset = i * 4;
                    break;
                }
            }

            long classAddr = UnsafeProxy.getObjectAddress(MethodSizeCase.class);

            long objectClassAddr = UnsafeProxy.getObjectAddress(Object.class);

            for (int i = 0; i < 20; i++) {
                int val = UnsafeProxy.getIntVolatile(classAddr+i*4);
                if(val == objectClassAddr){
                    superClassOffset = i * 4;
                    break;
                }
            }

            System.out.println("MethodSize8_0:init methodSize:"+methodSize+" declaringClassOffset:"+declaringClassOffset+","+"methodIndexOffset:"+methodIndexOffset+","+"superClassOffset:"+superClassOffset);


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

    @Override
    public int superClassOffset() throws Exception {
        return superClassOffset;
    }

}
