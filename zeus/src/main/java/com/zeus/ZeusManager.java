package com.zeus;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * Created by tianyang on 18/4/24.
 */
public class ZeusManager {

    private static ZeusManager instance;

    public static ZeusManager getInstance() {
        if (instance == null) {
            instance = new ZeusManager();
        }

        return instance;
    }

    public void install(Context context, File patchFile) {

        try {
            DexFile dexFile = DexFile.loadDex(patchFile.getAbsolutePath(), context.getFilesDir().getAbsolutePath(), 0);
            ClassLoader parent = context.getClassLoader();
            Enumeration<String> patchClasseNames = dexFile.entries();

            while (patchClasseNames.hasMoreElements()) {

                String patchClassName = patchClasseNames.nextElement();

                Class patchClass = dexFile.loadClass(patchClassName, parent);

                Method[] methods = patchClass.getDeclaredMethods();

                Constructor[] constructors = patchClass.getDeclaredConstructors();

                for (Method patchMethod : methods) {
                    if (patchMethod.isAnnotationPresent(Patch.class)) {

                        Patch patchAnnotation = patchMethod.getAnnotation(Patch.class);

                        String[] patchInfos = patchAnnotation.value().split(":");

                        String targetClassName = patchInfos[0];

                        String targetMethodInfo = patchInfos[1];

                        Class<?> targetClass = Class.forName(targetClassName);

                        Method[] targetMethods = targetClass.getDeclaredMethods();

                        for (Method targetMethod : targetMethods) {
                            if (targetMethod.toString().equals(targetMethodInfo)) {
                                System.out.println("find src:" + targetMethod + " - dest:" + patchMethod);
                                ZeusProxy.instance().replace(targetMethod, patchMethod);
                                break;
                            }
                        }
                    }
                }

                for (Constructor patchConstructor : constructors) {
                    if (patchConstructor.isAnnotationPresent(Patch.class)) {

                        Patch patchAnnotation = (Patch) patchConstructor.getAnnotation(Patch.class);

                        String[] patchInfos = patchAnnotation.value().split(":");

                        String targetClassName = patchInfos[0];

                        String targetMethodInfo = patchInfos[1];

                        Class<?> targetClass = Class.forName(targetClassName);

                        Constructor[] targetConstructors = targetClass.getDeclaredConstructors();

                        for (Constructor targetConstructord : targetConstructors) {
                            if (targetConstructord.toString().equals(targetMethodInfo)) {
                                System.out.println("find src:" + targetConstructord + " - dest:" + patchConstructor);
                                ZeusProxy.instance().replace(targetConstructord, patchConstructor);
                                break;
                            }
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
