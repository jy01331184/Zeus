package com.zeus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class ReflectionReplace8_0 implements IReflectionReplace {

    static Field artMethodField;

    static Map<Class, Set<Object>> PATCHS = new HashMap<>();
    static Map<String, List<Long>> CACHE = new HashMap<>();


    static {
        try {
            Class absMethodClass = Class.forName("java.lang.reflect.Executable");
            artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replace(Method src, Method dest) throws Exception {
        //static 需要提前初始化
        if (Modifier.isStatic(src.getModifiers())) {
            UnsafeProxy.ensureClassInitialized(src.getDeclaringClass());
            UnsafeProxy.ensureClassInitialized(dest.getDeclaringClass());
        }
        long artMethodSrc = ((Long) artMethodField.get(src)).longValue();
        long artMethodDest = ((Long) artMethodField.get(dest)).longValue();

        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);
        List<Long> cache = CACHE.get(key);
        if (cache == null) {
            cache = new ArrayList<>();
            CACHE.put(key, cache);
        }
        replaceReal(cache,artMethodSrc, artMethodDest);
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        long artMethodSrc = ((Long) artMethodField.get(src)).longValue();
        long artMethodDest = ((Long) artMethodField.get(dest)).longValue();
        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);
        List<Long> cache = CACHE.get(key);
        if (cache == null) {
            cache = new ArrayList<>();
            CACHE.put(key, cache);
        }

        replaceReal(cache,artMethodSrc, artMethodDest);
    }

    @Override
    public void recover(Class clazz) throws Exception {
        Set<Object> patchs = PATCHS.remove(clazz);
        if (patchs != null) {
            for (Object patch : patchs) {
                String key = ReflectionUtils.getKey(patch);
                List<Long> cache = CACHE.remove(key);
                if (cache != null) {
                    long src = cache.remove(0);

                    int methodSize = MethodSizeUtils.methodSize();
                    int methodIndexOffsetIndex = MethodSizeUtils.methodIndexOffset() / 4;
                    int declaringClassOffsetIndex = MethodSizeUtils.declaringClassOffset() / 4;
                    // index 0 is declaring_class, declaring_class need not replace.
                    for (int i = 0, size = methodSize / 4; i < size; i++) {
                        if (i != methodIndexOffsetIndex && i != declaringClassOffsetIndex) {
                            int value = cache.remove(0).intValue();
                            UnsafeProxy.putIntVolatile(src + i * 4, value);
                        }
                    }
                }
                System.out.println("recover:"+key);
            }
        }
    }

    protected void replaceReal(List<Long> cache, long src, long dest) throws Exception {
        int methodSize = MethodSizeUtils.methodSize();
        int methodIndexOffsetIndex = MethodSizeUtils.methodIndexOffset() / 4;
        int declaringClassOffsetIndex = MethodSizeUtils.declaringClassOffset() / 4;
        cache.add(src);
        // index 0 is declaring_class, declaring_class need not replace.
        for (int i = 0, size = methodSize / 4; i < size; i++) {
            if (i != methodIndexOffsetIndex && i != declaringClassOffsetIndex) {
                int value = UnsafeProxy.getIntVolatile(dest + i * 4);
                int origin = UnsafeProxy.getIntVolatile(src + i * 4);
                cache.add((long) origin);
                UnsafeProxy.putIntVolatile(src + i * 4, value);
            }
        }
    }

}
