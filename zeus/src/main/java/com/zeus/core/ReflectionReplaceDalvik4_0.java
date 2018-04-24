package com.zeus.core;

import com.zeus.ex.ReflectionUtils;
import com.zeus.ex.UnsafeProxy;
import com.zeus.ex.MethodSizeCase;

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

public class ReflectionReplaceDalvik4_0 implements IReflectionReplace {

    private final static int DIRECT_METHOD_OFFSET = 25;
    private final static int VIRTUAL_METHOD_OFFSET = 27;
    private final static int METHOD_SIZE_BYTE = 56;

    static Field methodSlotField;
    static Field constructSlotField;
    static int methodSize = METHOD_SIZE_BYTE;
    static int directMethodOffset = DIRECT_METHOD_OFFSET;
    static int virtualMethodOffset = VIRTUAL_METHOD_OFFSET;
    private static int superClassOffset = Constants.INVALID_SIZE;
    private static int declaringClassOffset = Constants.INVALID_SIZE;

    static Map<Class, Set<Object>> PATCHS = new HashMap<>();
    static Map<String, List<Long>> CACHE = new HashMap<>();


    static {
        try {
            methodSlotField = Method.class.getDeclaredField("slot");
            methodSlotField.setAccessible(true);

            constructSlotField = Constructor.class.getDeclaredField("slot");
            constructSlotField.setAccessible(true);

            int directMethodAddr = UnsafeProxy.getIntVolatile(MethodSizeCase.class, DIRECT_METHOD_OFFSET * 4);
            int declaringClassAddr = (int) UnsafeProxy.getObjectAddress(MethodSizeCase.class);
            int count = 0;
            int elementCount = 0;
            //通过扫面内存来确认Method的结构体大小
            for (int i = 0; i < 100; i++) {
                int value = UnsafeProxy.getIntVolatile(directMethodAddr + i * 4);
                if (value == declaringClassAddr) {
                    count++;
                }
                if (count == 2) {
                    break;
                }
                if (count == 1) {
                    elementCount++;
                }
            }

            methodSize = 4 * elementCount;

//            long objectClassAddr = UnsafeProxy.getObjectAddress(Object.class);
//
//            for (int i = 0; i < 20; i++) {
//                int val = UnsafeProxy.getIntVolatile(declaringClassAddr + i * 4);
//                if (val == objectClassAddr) {
//                    superClassOffset = i * 4;
//                    break;
//                }
//            }

            for (int i = DIRECT_METHOD_OFFSET-10; i <= VIRTUAL_METHOD_OFFSET+10; i++) {
                int val = UnsafeProxy.getIntVolatile(declaringClassAddr + i * 4);
                if(val == 5){
                    directMethodOffset = i+1;
                } else if(val == 2){
                    virtualMethodOffset = i+1;
                }
            }

            System.out.println("ReflectionReplaceDalvik4_0:init methodSize:"+methodSize+" declaringClassOffset:" + declaringClassOffset + "," + "superClassOffset:" + superClassOffset+",directMethodOffset:"+directMethodOffset+",virtualMethodOffset:"+virtualMethodOffset);

        } catch (Exception e) {
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
            virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, directMethodOffset * 4);
            virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, directMethodOffset * 4);
        } else {
            virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, virtualMethodOffset * 4);
            virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, virtualMethodOffset * 4);
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

        replaceReal(cache, virtualMethodSrcAddr + slotSrc * methodSize, virtualMethodDestAddr + slotDest * methodSize);
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
        long virtualMethodSrcAddr = UnsafeProxy.getIntVolatile(classSrc, directMethodOffset * 4);
        long virtualMethodDestAddr = UnsafeProxy.getIntVolatile(classDest, directMethodOffset * 4);

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

        replaceReal(cache, virtualMethodSrcAddr + slotSrc * methodSize, virtualMethodDestAddr + slotDest * methodSize);
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

                    int declaringClassOffsetIndex = declaringClassOffset / 4;
                    for (int i = 0, size = methodSize / 4; i < size; i++) {
//                        if (i != declaringClassOffsetIndex) {
                        int value = cache.remove(0).intValue();
                        UnsafeProxy.putIntVolatile(src + i * 4, value);
//                        }
                    }
                }
                System.out.println("recover:" + key);
            }
        }
    }

    protected void replaceReal(List<Long> cache, long src, long dest) throws Exception {
        //why 1? index 0 is declaring_class, declaring_class need not replace.

        cache.add(src);
        for (int i = 0, size = methodSize / 4; i < size; i++) {
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
