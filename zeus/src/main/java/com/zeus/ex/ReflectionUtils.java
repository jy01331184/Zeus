package com.zeus.ex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by magic.yang on 17/5/20.
 */

public class ReflectionUtils {
    public static String getKey(Object object) {
        return "zeus:" + System.identityHashCode(object);
    }


    public static void checkMethod(Method src, Method dest) throws ZeusException {
        if (src == null || dest == null)
            throw new IllegalArgumentException();
        if (src.getReturnType() != dest.getReturnType()) {
            throw new ZeusException("method return type not equals!");
        }

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            throw new ZeusException("method exceptions not equals! " + src + " && " + dest);
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            throw new ZeusException("method parameters not equals!" + src + " && " + dest);
        }

        if (!Modifier.isStatic(src.getModifiers()) == Modifier.isStatic(dest.getModifiers())) {
            throw new ZeusException("method modifiers not equals!" + src + " && " + dest);
        }

    }

    public static void checkConstructor(Constructor src, Constructor dest) throws ZeusException {
        if (src == null || dest == null)
            throw new IllegalArgumentException();

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            throw new ZeusException("constructor exceptions not equals! " + src + " && " + dest);
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            throw new ZeusException("constructor parameters not equals!" + src + " && " + dest);
        }

    }

    private static boolean checkClasses(Class[] srcClasses, Class[] destClasses) {
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
