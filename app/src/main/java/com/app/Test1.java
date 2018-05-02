package com.app;

import android.app.Activity;

/**
 * Created by magic.yang on 17/3/20.
 */

public class Test1 extends Test0{

    public Test1() {
    }

    protected String protectedTest(Activity activity) {
        return "0protectedTest";
    }

    private String privateTest(Activity activity) {
        return "privateTest";
    }

    public static String publicStaticTest() {
        return "publicStaticTest";
    }

    protected static String protectedStaticTest() {
        return "protectedStaticTest";
    }

    private static String privateStaticTest() {
        return "privateStaticTest";
    }

    public final String finalTest(Activity activity) {
        return "finalTest";
    }

    public String callPrivate(Activity activity) {
        return privateTest(activity);
    }

    public static String callPrivateStatic() {
        return privateStaticTest();
    }


    String innner() {
        return "you hei wo ke";
    }


    public String publicTest() {
        return "publicTest";
    }
}
