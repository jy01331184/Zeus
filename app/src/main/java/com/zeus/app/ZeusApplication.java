package com.zeus.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.zeus.ReflectionReplaceProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by jingchaoqinjc on 17/3/20.
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
            Method method1,method2;
            method1 = Test1.class.getDeclaredMethod("publicTest",new Class[]{});
            method2 = Test2.class.getDeclaredMethod("publicTest",new Class[]{});
            ReflectionReplaceProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("protectedTest",new Class[]{Activity.class});
            method2 = Test2.class.getDeclaredMethod("protectedTest",new Class[]{Activity.class});
            ReflectionReplaceProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("privateTest",new Class[]{Activity.class});
            method2 = Test2.class.getDeclaredMethod("privateTest",new Class[]{Activity.class});
            ReflectionReplaceProxy.instance().replace(method1, method2);
//
            method1 = Test1.class.getDeclaredMethod("finalTest",new Class[]{Activity.class});
            method2 = Test2.class.getDeclaredMethod("finalTest",new Class[]{Activity.class});
            ReflectionReplaceProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("publicStaticTest",new Class[]{});
            method2 = Test2.class.getDeclaredMethod("publicStaticTest",new Class[]{});
            ReflectionReplaceProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("protectedStaticTest",new Class[]{});
            method2 = Test2.class.getDeclaredMethod("protectedStaticTest",new Class[]{});
            ReflectionReplaceProxy.instance().replace(method1, method2);

            method1 = Test1.class.getDeclaredMethod("privateStaticTest",new Class[]{});
            method2 = Test2.class.getDeclaredMethod("privateStaticTest",new Class[]{});
            ReflectionReplaceProxy.instance().replace(method1, method2);

            Constructor constructor1 = Test1.class.getDeclaredConstructors()[0];
            Constructor constructor2 = Test2.class.getDeclaredConstructors()[0];

            ReflectionReplaceProxy.instance().replace(constructor1,constructor2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
