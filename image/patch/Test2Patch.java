package patch;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.zeus.Keep;
import com.zeus.Patch;

/**
 * Created by tianyang on 18/4/24.
 */
@Keep
public class Test2Patch {

    @Patch("com.app.Test1:public com.app.Test1()")
    public Test2Patch() {
        System.out.println("patch2 constructor:");
    }

    @Patch("com.app.Test1:public java.lang.String com.app.Test1.e()")
    public String e() {
        return "patch publicTest";
    }

    @Patch("com.app.Test1:protected java.lang.String com.app.Test1.a(android.app.Activity)")
    protected String a(Activity activity) {
        return "patch protectedTest";
    }

    @Patch("com.app.Test1:private java.lang.String com.app.Test1.d(android.app.Activity)")
    private String d(Activity activity) {

        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        Object _this = this;

        return "patch privateTest " + _this.toString() + "-" + activity;
    }

    @Patch("com.app.Test1:public static java.lang.String com.app.Test1.f()")
    public static String f() {
        return "patch publicStaticTest";
    }

    @Patch("com.app.Test1:protected static java.lang.String com.app.Test1.g()")
    protected static String g() {
        return "patch protectedStaticTest";
    }

    @Patch("com.app.Test1:private static java.lang.String com.app.Test1.i()")
    private static String i() {
        return "patch privateStaticTest";
    }

    @Patch("com.app.Test1:public final java.lang.String com.app.Test1.c(android.app.Activity)")
    public final String c(Activity activity) {
        return "patch finalTest";
    }
}
