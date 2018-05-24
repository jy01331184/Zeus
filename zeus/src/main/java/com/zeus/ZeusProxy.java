package com.zeus;

import android.os.Build;

import com.zeus.core.fix.IZeusCompatFix;
import com.zeus.core.fix.ZeusCompatFix5_0;
import com.zeus.core.fix.ZeusCompatFix5_1;
import com.zeus.core.fix.ZeusCompatFix6_0;
import com.zeus.core.fix.ZeusCompatFix8_0;
import com.zeus.core.fix.ZeusCompatFixDalvik4_0;
import com.zeus.ex.ReflectionUtils;
import com.zeus.ex.ZeusException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by magic.yang on 17/5/15.
 */

class ZeusProxy {

    private IZeusCompatFix proxy;

    private ZeusProxy() {
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 21) {
            final String vmVersion = System.getProperty("java.vm.version");
            boolean isArt = vmVersion != null && vmVersion.startsWith("2");
            if (!isArt) {
                proxy = new ZeusCompatFixDalvik4_0();
            } else {
                proxy = new ZeusCompatFix5_0();
            }
        } else if (Build.VERSION.SDK_INT == 21) {
            proxy = new ZeusCompatFix5_0();
        } else if (Build.VERSION.SDK_INT == 22) {
            proxy = new ZeusCompatFix5_1();
        } else if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 26) {
            proxy = new ZeusCompatFix6_0();
        } else {
            proxy = new ZeusCompatFix8_0();
        }
    }

    static ZeusProxy instance() {
        return Holder.instance;
    }

    public void replace(Method src, Method dest) throws ZeusException {
        ReflectionUtils.checkMethod(src, dest);
        try {
            proxy.replace(src, dest);
        } catch (Exception e) {
            ZeusException zeusException = new ZeusException("ZeusProxy replace method fail", e);
            throw zeusException;
        }
    }

    public void replace(Constructor src, Constructor dest) throws ZeusException {
        ReflectionUtils.checkConstructor(src, dest);
        try {
            proxy.replace(src, dest);
        } catch (Exception e) {
            ZeusException zeusException = new ZeusException("ZeusProxy replace constructor fail", e);
            throw zeusException;
        }
    }

    public void recover(Class cls) throws ZeusException {
        try {
            proxy.recover(cls);
        } catch (Exception e) {
            ZeusException zeusException = new ZeusException("ZeusProxy recover fail :" + cls, e);
            throw zeusException;
        }
    }

    private static class Holder {
        static ZeusProxy instance = new ZeusProxy();
    }


}
