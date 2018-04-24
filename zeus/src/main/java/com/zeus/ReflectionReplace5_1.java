package com.zeus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by magic.yang on 17/5/15.
 * cover 5.1
 * Method.java  ->  ArtMethod.java  没有  entryPointFromInterpreter 与 entryPointFromQuickCompiledCode
 * 参照6.0+ patch
 */

public class ReflectionReplace5_1 implements IReflectionReplace {

    static Field artMethodField;
    static Map<Class, Set<Object>> PATCHS = new HashMap<>();
    static Map<String, List<Object>> CACHE = new HashMap<>();

    static {
        try {
            Class absMethodClass = Class.forName("java.lang.reflect.AbstractMethod");
            artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replace(Method src, Method dest) throws Exception {
        //static 需要提前初始化
        UnsafeProxy.ensureClassInitialized(src.getDeclaringClass());
        UnsafeProxy.ensureClassInitialized(dest.getDeclaringClass());

        Object o1 = artMethodField.get(src);
        Object o2 = artMethodField.get(dest);

        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);
        List<Object> cache = CACHE.get(key);
        if (cache == null) {
            cache = new ArrayList<>();
            CACHE.put(key, cache);
        }

        replaceReal(cache, o1, o2);
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        Object o1 = artMethodField.get(src);
        Object o2 = artMethodField.get(dest);
        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);
        List<Object> cache = CACHE.get(key);
        if (cache == null) {
            cache = new ArrayList<>();
            CACHE.put(key, cache);
        }
        replaceReal(cache, o1, o2);
    }

    @Override
    public void recover(Class clazz) throws Exception {
        Set<Object> patchs = PATCHS.remove(clazz);
        if (patchs != null) {
            for (Object patch : patchs) {
                String key = ReflectionUtils.getKey(patch);
                List<Object> cache = CACHE.remove(key);
                if (cache != null) {
                    Object src = cache.remove(0);

                    int methodSize = MethodSizeUtils.methodSize();
                    int methodIndexOffsetIndex = MethodSizeUtils.methodIndexOffset() / 4;
                    int declaringClassOffsetIndex = MethodSizeUtils.declaringClassOffset() / 4;
                    // index 0 is declaring_class, declaring_class need not replace.
                    for (int i = 0, size = methodSize / 4; i < size; i++) {
                        if (i != methodIndexOffsetIndex) {
                            int value = (int) cache.remove(0);
                            UnsafeProxy.putIntVolatile(src, i * 4, value);
                        }
                    }
                    System.out.println("recover:" + key);
                } else {
                    System.err.println("no recover for key:" + key);
                }
            }
        }
    }

    protected void replaceReal(List<Object> cache, Object src, Object dest) throws Exception {
        int methodSize = MethodSizeUtils.methodSize();
        //methodIndex need not replace,becase the process of finding method in vtable
        int methodIndexOffsetIndex = MethodSizeUtils.methodIndexOffset() / 4;
        int declaringClassOffsetIndex = MethodSizeUtils.declaringClassOffset() / 4;
        int superClassOffset = MethodSizeUtils.superClassOffset() / 4;

        cache.add(src);
        //why 1? index 0 is declaring_class, declaring_class need not replace.
        for (int i = 0, size = methodSize / 4; i < size; i++) {
            if (i != methodIndexOffsetIndex) {
                if (i == declaringClassOffsetIndex && superClassOffset != Constants.INVALID_SIZE) {
                    int destClsAddr = UnsafeProxy.getIntVolatile(dest, i * 4);
                    UnsafeProxy.putIntVolatile(destClsAddr + superClassOffset * 4, 0);  //update super_class_ in class.h
                }
                int value = UnsafeProxy.getIntVolatile(dest, i * 4);
                int origin = UnsafeProxy.getIntVolatile(src, i * 4);
                cache.add(origin);
                UnsafeProxy.putIntVolatile(src, i * 4, value);
            }
        }
    }


}
