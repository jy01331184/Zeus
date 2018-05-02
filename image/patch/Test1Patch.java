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
public class Test1Patch {

    @Patch("com.app.Test1:public com.app.Test1()")
    public Test1Patch() {
        System.out.println("patch2 unpro constructor:");
    }

    @Patch("com.app.Test1:public java.lang.String com.app.Test1.publicTest()")
    public String publicTest() {
        return "patch publicTest";
    }

    @Patch("com.app.Test1:protected java.lang.String com.app.Test1.protectedTest(android.app.Activity)")
    protected String protectedTest(Activity activity) {
        return "patch protectedTest";
    }

    @Patch("com.app.Test1:private java.lang.String com.app.Test1.privateTest(android.app.Activity)")
    private String privateTest(Activity activity) {

        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        Object _this = this;

        return "patch privateTest " + _this.toString() + "-" + activity;
    }

    @Patch("com.app.Test1:public static java.lang.String com.app.Test1.publicStaticTest()")
    public static String publicStaticTest() {
        return "patch publicStaticTest";
    }

    @Patch("com.app.Test1:protected static java.lang.String com.app.Test1.protectedStaticTest()")
    protected static String protectedStaticTest() {
        return "patch protectedStaticTest";
    }

    @Patch("com.app.Test1:private static java.lang.String com.app.Test1.privateStaticTest()")
    private static String privateStaticTest() {
        return "patch privateStaticTest";
    }

    @Patch("com.app.Test1:public final java.lang.String com.app.Test1.finalTest(android.app.Activity)")
    public final String finalTest(Activity activity) {
        return "patch finalTest";
    }
}
