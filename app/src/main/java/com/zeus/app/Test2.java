package com.zeus.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by magic.yang on 17/3/20.
 */

public class Test2 {

    public Test2() {
        System.out.println("patched111 constructor");
    }



    protected String protectedTest(Activity activity) {
        return "patch protectedTest";
    }

    private String privateTest(Activity activity) {

        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        Object _this = this;

        Test1 test1 = (Test1) _this;

        return "patch privateTest "+test1.innner();
    }

    public static String publicStaticTest() {
        return "patch publicStaticTest";
    }

    protected static String protectedStaticTest() {
        return "patch protectedStaticTest";
    }

    private static String privateStaticTest() {
        return "patch privateStaticTest";
    }

    public final String finalTest(Activity activity) {
        return "patch finalTest";
    }

    public String publicTest() {
        return "patch publicTest";
    }
//
//    public void aaa(){};
//
//    public void zzz(){};
}
