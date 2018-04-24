package com.zeus;

import android.os.Build;

import com.zeus.core.IReflectionReplace;
import com.zeus.core.ReflectionReplace5_0;
import com.zeus.core.ReflectionReplace5_1;
import com.zeus.core.ReflectionReplace6_0;
import com.zeus.core.ReflectionReplace8_0;
import com.zeus.core.ReflectionReplaceDalvik4_0;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by magic.yang on 17/5/15.
 */

public class ZeusProxy {

    private IReflectionReplace proxy;

    private ZeusProxy() {
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 21) {
            final String vmVersion = System.getProperty("java.vm.version");
            boolean isArt = vmVersion != null && vmVersion.startsWith("2");
            if(!isArt){
                proxy = new ReflectionReplaceDalvik4_0();
            } else {
                proxy = new ReflectionReplace5_0();
            }
        } else if (Build.VERSION.SDK_INT == 21) {
            proxy = new ReflectionReplace5_0();
        } else if (Build.VERSION.SDK_INT == 22) {
            proxy = new ReflectionReplace5_1();
        } else if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT<26) {
            proxy = new ReflectionReplace6_0();
        } else {
            proxy = new ReflectionReplace8_0();
        }
    }

    public static ZeusProxy instance() {
        return Holder.instance;
    }

    public void replace(Method src, Method dest) throws Exception {
        checkMethod(src, dest);
        proxy.replace(src, dest);
    }

    public void replace(Constructor src, Constructor dest) throws Exception {
        checkConstructor(src, dest);
        proxy.replace(src, dest);
    }

    public void recover(Class cls) throws Exception {
        proxy.recover(cls);
    }

    private static class Holder {
        static ZeusProxy instance = new ZeusProxy();
    }

    private void checkMethod(Method src, Method dest) {
        if (src == null || dest == null)
            throw new IllegalArgumentException();
        if (src.getReturnType() != dest.getReturnType()) {
            throw new RuntimeException("返回类型必须一致");
        }

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            throw new RuntimeException("异常类型必须一致");
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            throw new RuntimeException("参数类型必须一致");
        }

        if (!Modifier.isStatic(src.getModifiers()) == Modifier.isStatic(dest.getModifiers())) {
            throw new RuntimeException("必须都为static或都不为static");
        }

    }

    private void checkConstructor(Constructor src, Constructor dest) {
        if (src == null || dest == null)
            throw new IllegalArgumentException();

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            throw new RuntimeException("异常类型必须一致");
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            throw new RuntimeException("参数类型必须一致");
        }

    }

    private boolean checkClasses(Class[] srcClasses, Class[] destClasses) {
        if (srcClasses == null && destClasses == null) {
            return true;
        }
        if (srcClasses == null || destClasses == null) {
            return false;
        }
        if (srcClasses.length != destClasses.length) {
            return false;
        }

        for (int i = 0, size = srcClasses.length; i < size; i++) {
            if (srcClasses[i] != destClasses[i]) {
                return false;
            }
        }
        return true;
    }

}
