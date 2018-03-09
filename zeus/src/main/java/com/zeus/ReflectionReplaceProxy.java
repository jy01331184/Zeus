package com.zeus;

import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class ReflectionReplaceProxy {

    private IReflectionReplace realReplace;

    private ReflectionReplaceProxy() {
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 21) {
            final String vmVersion = System.getProperty("java.vm.version");
            boolean isArt = vmVersion != null && vmVersion.startsWith("2");
            if(!isArt){
                realReplace = new ReflectionReplaceDalvik4_0();
            } else {
                realReplace = new ReflectionReplaceArt4_0();
            }
        } else if (Build.VERSION.SDK_INT == 21) {
            realReplace = new ReflectionReplace5_0();
        } else if (Build.VERSION.SDK_INT == 22) {
            realReplace = new ReflectionReplace5_1();
        } else if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT<26) {
            realReplace = new ReflectionReplace6_0();
        } else {
            realReplace = new ReflectionReplace8_0();
        }
    }

    public static ReflectionReplaceProxy instance() {
        return Holder.instance;
    }

    public void replace(Method src, Method dest) throws Exception {
        checkMethod(src, dest);
        realReplace.replace(src, dest);
    }

    public void replace(Constructor src, Constructor dest) throws Exception {
        checkConstructor(src, dest);
        realReplace.replace(src, dest);
    }

    public void recover(Class cls) throws Exception {
        realReplace.recover(cls);
    }

    private static class Holder {
        static ReflectionReplaceProxy instance = new ReflectionReplaceProxy();
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
