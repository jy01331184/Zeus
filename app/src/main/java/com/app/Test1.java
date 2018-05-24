package com.app;

import android.app.Activity;
import android.util.Log;

/**
 * Created by magic.yang on 17/3/20.
 */

public class Test1 extends Test0{

    public Test1() {
    }

    protected String protectedTest(Activity activity) {
        Log.i("ahaha","ajajajaj");
        return "0protectedTest";
    }

    private String privateTest(Activity activity) {
        Log.i("ahaha","ajajajaj");
        return "privateTest";
    }

    public static String publicStaticTest() {
        Log.i("ahaha","ajajajaj");
        return "publicStaticTest";
    }

    protected static String protectedStaticTest() {
        Log.i("ahaha","ajajajaj");
        return "protectedStaticTest";
    }

    private static String privateStaticTest() {
        Log.i("ahaha","ajajajaj");
        return "privateStaticTest";
    }

    public final String finalTest(Activity activity) {
        Log.i("ahaha","ajajajaj");
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
        Log.i("ahaha","ajajajaj");

        return "publicTest";
    }
}
