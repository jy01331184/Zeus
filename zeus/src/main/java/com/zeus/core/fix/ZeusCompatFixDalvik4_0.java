package com.zeus.core.fix;

import com.zeus.core.size.ZeusMethodStructDalvik4_0;
import com.zeus.ex.ReflectionUtils;
import com.zeus.ex.UnsafeProxy;

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
 * Created by magic.yang on 17/5/15.
 * cover 4.x
 */

public class ZeusCompatFixDalvik4_0 implements IZeusCompatFix {

    private Map<Class, Set<Object>> PATCHS = new HashMap<>();
    private Map<String, List<Long>> CACHE = new HashMap<>();

    private Field methodSlotField;
    private Field constructSlotField;

    private ZeusMethodStructDalvik4_0 methodStruct = new ZeusMethodStructDalvik4_0();

    public ZeusCompatFixDalvik4_0() {
        try {
            methodSlotField = Method.class.getDeclaredField("slot");
            methodSlotField.setAccessible(true);

            constructSlotField = Constructor.class.getDeclaredField("slot");
            constructSlotField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replace(Method src, Method dest) throws Exception {

        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);

        Class classSrc = src.getDeclaringClass();
        Class classDest = dest.getDeclaringClass();
        long virtualMethodSrcAddr;
        long virtualMethodDestAddr;

        if (isDirect(src)) {
            virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, methodStruct.getDirectMethodOffset() * 4);
            virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, methodStruct.getDirectMethodOffset() * 4);
        } else {
            virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, methodStruct.getVirtualMethodOffset() * 4);
            virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, methodStruct.getVirtualMethodOffset() * 4);
        }

        //slot is methodIndex in action
        /*
        * Convert a slot number to a method pointer.
        Method* dvmSlotToMethod(ClassObject* clazz, int slot)
        {
            if (slot < 0) {
                slot = -(slot+1);
                assert(slot < clazz->directMethodCount);
                return &clazz->directMethods[slot];
            } else {
                assert(slot < clazz->virtualMethodCount);
                return &clazz->virtualMethods[slot];
            }
        }
        */
        int slotSrc = ((Integer) methodSlotField.get(src)).intValue();
        if (slotSrc < 0) {
            slotSrc = -(slotSrc + 1);
        }
        int slotDest = ((Integer) methodSlotField.get(dest)).intValue();
        if (slotDest < 0) {
            slotDest = -(slotDest + 1);
        }

        List<Long> cache = CACHE.get(key);
        if (cache == null) {
            cache = new ArrayList<>();
            CACHE.put(key, cache);
        }

        replaceReal(cache, virtualMethodSrcAddr + slotSrc * methodStruct.methodSize(), virtualMethodDestAddr + slotDest * methodStruct.methodSize());
    }

    @Override
    public void replace(Constructor src, Constructor dest) throws Exception {

        String key = ReflectionUtils.getKey(src);
        Set<Object> patchs = PATCHS.get(src.getDeclaringClass());
        if (patchs == null) {
            patchs = new HashSet<>();
            PATCHS.put(src.getDeclaringClass(), patchs);
        }
        patchs.add(src);

        Class classSrc = src.getDeclaringClass();
        Class classDest = dest.getDeclaringClass();
        long virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, methodStruct.getDirectMethodOffset() * 4);
        long virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, methodStruct.getDirectMethodOffset() * 4);

        //slot is methodIndex in action
        /*
        * Convert a slot number to a method pointer.
        Method* dvmSlotToMethod(ClassObject* clazz, int slot)
        {
            if (slot < 0) {
                slot = -(slot+1);
                assert(slot < clazz->directMethodCount);
                return &clazz->directMethods[slot];
            } else {
                assert(slot < clazz->virtualMethodCount);
                return &clazz->virtualMethods[slot];
            }
        }
        */
        int slotSrc = ((Integer) constructSlotField.get(src)).intValue();
        if (slotSrc < 0) {
            slotSrc = -(slotSrc + 1);
        }
        int slotDest = ((Integer) constructSlotField.get(dest)).intValue();
        if (slotDest < 0) {
            slotDest = -(slotDest + 1);
        }

        List<Long> cache = CACHE.get(key);
        if (cache == null) {
            cache = new ArrayList<>();
            CACHE.put(key, cache);
        }

        replaceReal(cache, virtualMethodSrcAddr + slotSrc * methodStruct.methodIndexOffset(), virtualMethodDestAddr + slotDest * methodStruct.methodSize());
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

                    for (int i = 0, size = methodStruct.methodSize() / 4; i < size; i++) {
                        int value = cache.remove(0).intValue();
                        UnsafeProxy.putIntVolatile(src + i * 4, value);
                    }
                }
                System.out.println("recover:" + key);
            }
        }
    }

    protected void replaceReal(List<Long> cache, long src, long dest) throws Exception {
        //why 1? index 0 is declaring_class, declaring_class need not replace.

        cache.add(src);
        for (int i = 0, size = methodStruct.methodSize() / 4; i < size; i++) {
            int value = UnsafeProxy.getIntVolatile(dest + i * 4);
            int origin = UnsafeProxy.getIntVolatile(src + i * 4);
            cache.add((long) origin);
            UnsafeProxy.putIntVolatile(src + i * 4, value);
        }
    }

    private boolean isDirect(Method method) {
        int modifier = method.getModifiers();
        return Modifier.isStatic(modifier)
                || Modifier.isPrivate(modifier);
    }
}
