package com.zeus.core.size;

/**
 * Created by magic.yang on 17/5/16.
 */

public interface IZeusMethodStruct {

    int methodSize() throws Exception;

    int methodIndexOffset() throws Exception;

    int declaringClassOffset() throws Exception;

    int superClassOffset() throws Exception;
}
