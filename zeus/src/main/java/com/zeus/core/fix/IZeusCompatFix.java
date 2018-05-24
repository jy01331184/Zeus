package com.zeus.core.fix;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by magic.yang on 17/5/15.
 */

public interface IZeusCompatFix {

    void replace(Method src, Method dest) throws Exception;

    void replace(Constructor src, Constructor dest) throws Exception;

    void recover(Class cls) throws Exception;

}
