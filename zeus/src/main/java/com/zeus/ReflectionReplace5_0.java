package com.zeus;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by jingchaoqinjc on 17/5/15.
 */

public class ReflectionReplace5_0 implements IReflectionReplace {

    static Field artMethodField;

    static Map<Class, Set<Object>> PATCHS = new HashMap<>();
    static Map<String, Map<String, Object>> CACHE = new HashMap<>();

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
        if (Modifier.isStatic(src.getModifiers())) {
            UnsafeProxy.ensureClassInitialized(src.getDeclaringClass());
            UnsafeProxy.ensureClassInitialized(dest.getDeclaringClass());
        }

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

        replaceReal(cache, o1, o2);
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {
        Object o1 = artMethodField.get(src);
        Object o2 = artMethodField.get(dest);

        String key = src.getDeclaringClass().getName() + ":" + src.toString();
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

        replaceReal(cache, o1, o2);
    }

    @Override
    public void recover(Class clazz) throws Exception {
        Set<Object> patchs = PATCHS.remove(clazz);
        if (patchs != null) {
            for (Object patch : patchs) {
                if (patch instanceof Method) {
                    recoverMethod((Method) patch);
                } else if (patch instanceof Constructor) {
                    recoverConstructor((Constructor) patch);
                }
            }
        }
    }

    private void recoverMethod(Method method) throws Exception {
        String key = ReflectionUtils.getKey(method);
        Map<String, Object> cache = CACHE.remove(key);
        Object origin = artMethodField.get(method);
        if (cache != null) {
            Class c = origin.getClass();
            while (c != Object.class) {
                for (Field f : c.getDeclaredFields()) {
                    f.setAccessible(true);
                    if (!f.getName().equals("declaringClass") && !f.getName().equals("methodIndex")) {
                        Object value = cache.get(f.getName());
                        if (value != null) {
                            f.set(origin, value);
                        }
                    }
                }
                c = c.getSuperclass();
            }
            System.out.println("recover:"+key);
        }
    }

    private void recoverConstructor(Constructor method) throws Exception {
        String key = ReflectionUtils.getKey(method);
        Map<String, Object> cache = CACHE.remove(key);
        Object origin = artMethodField.get(method);
        if (cache != null) {
            Class c = origin.getClass();
            while (c != Object.class) {
                for (Field f : c.getDeclaredFields()) {
                    f.setAccessible(true);
                    if (!f.getName().equals("declaringClass") && !f.getName().equals("methodIndex")) {
                        Object value = cache.get(f.getName());
                        if (value != null) {
                            f.set(origin, value);
                        }
                    }
                }
                c = c.getSuperclass();
            }
            System.out.println("recover:"+key);
        }
    }

    protected void replaceReal(Map<String, Object> cache, Object src, Object dest) throws Exception {
        Class c = src.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                if (!f.getName().equals("declaringClass") && !f.getName().equals("methodIndex")) {
                    Object value = f.get(src);
                    f.set(src, f.get(dest));
                    cache.put(f.getName(), value);
                }
            }
            c = c.getSuperclass();
        }
    }
}
