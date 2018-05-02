package com.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * Created by magic.yang on 17/3/20.
 */

public class TestAC extends Activity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Test1 test1 = new Test1();


        TextView t2 = (TextView) findViewById(R.id.t2);
        t2.setText(test1.protectedTest(this));

        TextView t3 = (TextView) findViewById(R.id.t3);
        t3.setText(test1.callPrivate(this));

        TextView t4 = (TextView) findViewById(R.id.t4);
        t4.setText(Test1.publicStaticTest());

        TextView t5 = (TextView) findViewById(R.id.t5);
        t5.setText(Test1.protectedStaticTest());

        TextView t6 = (TextView) findViewById(R.id.t6);
        t6.setText(Test1.callPrivateStatic());

        TextView t7 = (TextView) findViewById(R.id.t7);
        t7.setText(test1.finalTest(this));

        final TextView t1 = (TextView) findViewById(R.id.t1);
        t1.setText(test1.publicTest());


        findViewById(R.id.recover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //ZeusProxy.instance().recover(Test1.class);
                    test();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.reflect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method method1 = Test1.class.getDeclaredMethod("publicTest", new Class[]{});

                    System.out.println(method1.invoke(test1));

                    method1 = Test1.class.getDeclaredMethod("protectedTest", new Class[]{Activity.class});
                    method1.setAccessible(true);

                    System.out.println(method1.invoke(test1,TestAC.this));

                    method1 = Test1.class.getDeclaredMethod("privateTest", new Class[]{Activity.class});
                    method1.setAccessible(true);
                    System.out.println(method1.invoke(test1,TestAC.this));

                    method1 = Test1.class.getDeclaredMethod("finalTest", new Class[]{Activity.class});
                    method1.setAccessible(true);
                    System.out.println(method1.invoke(test1,TestAC.this));


                    method1 = Test1.class.getDeclaredMethod("publicStaticTest", new Class[]{});
                    System.out.println(method1.invoke(null));


                    method1 = Test1.class.getDeclaredMethod("protectedStaticTest", new Class[]{});
                    method1.setAccessible(true);
                    System.out.println(method1.invoke(null));

                    method1 = Test1.class.getDeclaredMethod("privateStaticTest", new Class[]{});
                    method1.setAccessible(true);
                    System.out.println(method1.invoke(null));


                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void test(){
        Test1 test11 = new Test1();
        System.out.println(test11.publicTest());
        System.out.println(test11.protectedTest(TestAC.this));
        System.out.println(test11.callPrivate(TestAC.this));
        System.out.println(test11.finalTest(TestAC.this));
        System.out.println(Test1.publicStaticTest());
        System.out.println(Test1.protectedStaticTest());
        System.out.println(Test1.callPrivateStatic());
    }
}
