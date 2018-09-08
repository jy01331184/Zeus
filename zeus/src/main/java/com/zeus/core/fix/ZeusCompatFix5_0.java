package com.zeus.core.fix;


import com.zeus.ex.ReflectionUtils;
import com.zeus.ex.UnsafeProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by magic.yang .
 * cover from 4.4-art  to 5.0
 * Method.java  ->  ArtMethod.java  有  entryPointFromInterpreter 与 entryPointFromQuickCompiledCode
 * http://androidxref.com/5.0.0_r2/xref/libcore/libart/src/main/java/java/lang/reflect/ArtMethod.java
 * http://androidxref.com/5.0.0_r2/xref/art/runtime/mirror/art_method.h
 */

public class ZeusCompatFix5_0 implements IZeusCompatFix {

    private Field artMethodField;
    private Field superClassField;

    private Map<Class, Set<Object>> PATCHS = new HashMap<>();
    private Map<String, Map<String, Object>> CACHE = new HashMap<>();

    public ZeusCompatFix5_0() {
        try {
            Class absMethodClass = Class.forName("java.lang.reflect.AbstractMethod");
            artMethodField = absMethodClass.getDeclaredField("artMethod");
            artMethodField.setAccessible(true);

            Class clsClass = Class.class;
            superClassField = clsClass.getDeclaredField("superClass");
            superClassField.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void replace(Method src, Method dest) throws Exception {
        //static 需要提前初始化
        UnsafeProxy.ensureClassInitialized(src.getDeclaringClass());
        UnsafeProxy.ensureClassInitialized(dest.getDeclaringClass());

        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);

        Object o1 = artMethodField.get(src);
        Object o2 = artMethodField.get(dest);


        Map<String, Object> cache = CACHE.get(key);
        if (cache == null) {
            cache = new HashMap<>();
            CACHE.put(key, cache);
        }

        replaceReal(cache, dest.getDeclaringClass(), o1, o2);
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
        Map<String, Object> cache = CACHE.get(key);
        if (cache == null) {
            cache = new HashMap<>();
            CACHE.put(key, cache);
        }

        replaceReal(cache, dest.getDeclaringClass(), o1, o2);
    }

    @Override
    public void recover(Class clazz) throws Exception {
        Set<Object> patchs = PATCHS.remove(clazz);
        if (patchs != null) {
            for (Object patch : patchs) {
                String key = ReflectionUtils.getKey(patch);
                Map<String, Object> cache = CACHE.remove(key);
                Object origin = artMethodField.get(patch);
                if (cache != null) {
                    Class c = origin.getClass();
                    for (Field f : c.getDeclaredFields()) {
                        f.setAccessible(true);
                        if (!f.getName().equals("methodIndex")) {
                            Object value = cache.get(f.getName());
                            if (value != null) {
                                f.set(origin, value);
                            }
                        }
                    }
                    System.out.println("recover:" + key);
                } else {
                    System.err.println("no recover for key:" + key);
                }
            }
        }
    }

    protected void replaceReal(Map<String, Object> cache, Class cls, Object src, Object dest) throws Exception {
        Class c = src.getClass();

        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            if (!f.getName().equals("methodIndex")) {
                Object value = f.get(src);
                f.set(src, f.get(dest));
                cache.put(f.getName(), value);
            }
        }

        if (superClassField != null) {
            superClassField.set(cls, null);
        }

    }
}
