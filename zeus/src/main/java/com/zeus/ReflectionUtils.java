package com.zeus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by jingchaoqinjc on 17/5/20.
 */

public class ReflectionUtils {

    public static boolean isMethodEqual(Method src, Method dest) {
        if (src == null || dest == null) {
            throw new IllegalArgumentException();
        }

        if (!src.getName().equals(dest.getName())) {
            return false;
        }

        if (src.getReturnType() != dest.getReturnType()) {
            return false;
        }

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            return false;
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            return false;
        }

        if (!Modifier.isStatic(src.getModifiers()) == Modifier.isStatic(dest.getModifiers())) {
            return false;
        }
        return true;
    }

    public static boolean isFieldEqual(Field src, Field dest) {
        if (src == null || dest == null) {
            throw new IllegalArgumentException();
        }

        if (!src.getName().equals(dest.getName())) {
            return false;
        }

        if (src.getType() != dest.getType()) {
            return false;
        }

        return true;
    }

    public static boolean isConstructorEqual(Constructor src, Constructor dest) {
        if (src == null || dest == null) {
            throw new IllegalArgumentException();
        }

        if (!src.getName().equals(dest.getName())) {
            return false;
        }

        if (!checkClasses(src.getExceptionTypes(), dest.getExceptionTypes())) {
            return false;
        }

        if (!checkClasses(src.getParameterTypes(), dest.getParameterTypes())) {
            return false;
        }
        return true;
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

    public static String getKey(Object object){
        if(object instanceof Method){
            Method method = (Method) object;
            return method.getDeclaringClass().getName() + ":" + method.toString();
        } else if(object instanceof Constructor){
            Constructor constructor = (Constructor) object;
            return constructor.getDeclaringClass().getName() + ":" + constructor.toString();
        }

        return null;
    }

}
