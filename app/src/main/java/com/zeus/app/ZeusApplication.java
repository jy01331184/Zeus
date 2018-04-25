package com.zeus.app;

import android.app.Application;
import android.content.Context;

import com.zeus.ZeusManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by magic.yang on 17/3/20.
 */

public class ZeusApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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

            ZeusManager.getInstance().install(this,patchFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
