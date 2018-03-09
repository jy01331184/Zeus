package com.zeus.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zeus.ReflectionReplaceProxy;

/**
 * Created by jingchaoqinjc on 17/3/20.
 */

public class TestAC extends Activity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Test1 test1 = new Test1();


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

        TextView t1 = (TextView) findViewById(R.id.t1);
        t1.setText(test1.publicTest());


        findViewById(R.id.recover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    ReflectionReplaceProxy.instance().recover(Test1.class);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


}
