package com.zeus.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.zeus.ZeusProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by magic.yang on 17/3/20.
 */

public class ZeusApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//
//        try {
//
//            Class cls = Method.class;
//
//            while(cls != null && cls != Object.class){
//                System.out.println("class :"+cls.getName());
//
//                Field[] fs = cls.getDeclaredFields();
//                System.out.println("fields:");
//                for (Field field : fs) {
//                    System.out.println("\t"+field.getType().getName()+"->"+field.getName());
//                }
//                System.out.println("methods:");
//                Method[] ms = cls.getDeclaredMethods();
//                for (Method method : ms) {
//                    System.out.println("\t"+method.getReturnType().getName()+"->"+method.getName());
//                }
//                cls = cls.getSuperclass();
//            }
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            File patchFile = new File(getFilesDir(), "patch.apk");
            if (!patchFile.exists()) {
                InputStream is = getResources().getAssets().open("patch.apk");

                byte[] bs = new byte[is.available()];
                is.read(bs);
                FileOutputStream fileOutputStream = new FileOutputStream(patchFile);
                fileOutputStream.write(bs);
                fileOutputStream.close();
                is.close();
            }

            DexClassLoader dexClassLoader = new DexClassLoader(patchFile.getAbsolutePath(),getFilesDir().getAbsolutePath(),null,getClassLoader());

            //PathClassLoader pathClassLoader = new PathClassLoader(patchFile.getAbsolutePath(),getClassLoader());

            Class<?> cls = dexClassLoader.loadClass("com.zeus.app.Patch");

            Method method1, method2;


            method1 = Test1.class.getDeclaredMethod("publicTest", new Class[]{});
//            method2 = Test2.class.getDeclaredMethod("publicTest", new Class[]{});

            method2 = cls.getDeclaredMethod("publicTest", new Class[]{});

            ZeusProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("protectedTest", new Class[]{Activity.class});
            method2 = cls.getDeclaredMethod("protectedTest", new Class[]{Activity.class});
            ZeusProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("privateTest", new Class[]{Activity.class});
            method2 = cls.getDeclaredMethod("privateTest", new Class[]{Activity.class});
            ZeusProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("finalTest", new Class[]{Activity.class});
            method2 = cls.getDeclaredMethod("finalTest", new Class[]{Activity.class});
            ZeusProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("publicStaticTest", new Class[]{});
            method2 = cls.getDeclaredMethod("publicStaticTest", new Class[]{});
            ZeusProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("protectedStaticTest", new Class[]{});
            method2 = cls.getDeclaredMethod("protectedStaticTest", new Class[]{});
            ZeusProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("privateStaticTest", new Class[]{});
            method2 = cls.getDeclaredMethod("privateStaticTest", new Class[]{});
            ZeusProxy.instance().replace(method1, method2);


            Constructor constructor1 = Test1.class.getDeclaredConstructors()[0];
            Constructor constructor2 = cls.getDeclaredConstructors()[0];

            ZeusProxy.instance().replace(constructor1, constructor2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
