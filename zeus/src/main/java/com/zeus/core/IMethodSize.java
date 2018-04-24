package com.zeus.core;

/**
 * Created by magic.yang on 17/5/16.
 */

public interface IMethodSize {

    int methodSize() throws Exception;

    int methodIndexOffset() throws Exception;

    int declaringClassOffset() throws Exception;

    int superClassOffset() throws Exception;
}
