package com.app;

import android.app.Activity;

/**
 * Created by magic.yang on 17/3/20.
 */

public class Test0 {

    public Test0() {
    }

//    public void aaa(){};
//
//    public void zzz(){};


//
    protected String protectedTest(Activity activity) {
        return "su protectedTest";
    }

    private String privateTest(Activity activity) {
        return "su privateTest";
    }
//
    public static String publicStaticTest() {
        return "su publicStaticTest";
    }
////
    protected static String protectedStaticTest() {
        return "su protectedStaticTest";
    }

    private static String privateStaticTest() {
        return "su privateStaticTest";
    }
//
//    public final String finalTest(Activity activity) {
//        return "su finalTest";
//    }
//
    public String callPrivate(Activity activity) {
        return privateTest(activity);
    }
//
    public static String callPrivateStatic() {
        return privateStaticTest();
    }
//
//
    String innner() {
        return "su you hei wo ke";
    }
//
//
    public String publicTest() {
        return "su publicTest";
    }
}
