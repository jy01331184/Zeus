package com.zeus;

import android.content.Context;

import com.zeus.ex.ZeusException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * Created by tianyang on 18/4/24.
 */
public class ZeusManager {

    private static ZeusManager instance;

    private boolean isPatched = false;
    private Set<Class> patchedClass = new HashSet<>();

    public static ZeusManager getInstance() {
        if (instance == null) {
            instance = new ZeusManager();
        }

        return instance;
    }

    public synchronized void install(Context context, File patchFile) throws ZeusException {

        try {
            isPatched = true;
            DexFile dexFile = DexFile.loadDex(patchFile.getAbsolutePath(), new File(context.getFilesDir(), "patch.opt").getAbsolutePath(), 0);
            ClassLoader parent = context.getClassLoader();
            Enumeration<String> patchClasseNames = dexFile.entries();

            while (patchClasseNames.hasMoreElements()) {

                String patchClassName = patchClasseNames.nextElement();
                System.out.println("patch:"+patchClassName);
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
                                patchedClass.add(targetClass);
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
                                patchedClass.add(targetClass);
                                break;
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            isPatched = false;
            ZeusException zeusException = new ZeusException("ZeusManager install fail");
            zeusException.initCause(e);
            throw zeusException;
        }
    }

    public synchronized void recover() throws ZeusException {
        ZeusException zeusException = new ZeusException("ZeusManager recover fail");
        boolean throwException = false;
        if (isPatched) {
            for (Class cls : patchedClass) {
                try {
                    ZeusProxy.instance().recover(cls);
                } catch (ZeusException e) {
                    throwException = true;
                    e.printStackTrace();
                }
            }

            if (throwException) {
                throw zeusException;
            }
        }
    }
}
