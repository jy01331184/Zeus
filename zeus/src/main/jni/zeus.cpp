//
// Created by tianyang on 18/4/26.
//
#include "zeus.h"

JNIEXPORT jlong JNICALL Java_com_zeus_ex_UnsafeProxy_getAddr(JNIEnv *env, jclass cls, jobject method){
    jmethodID mid = env->FromReflectedMethod(method);

    LOGD("addr %p",mid);
    long * addr = (long *) mid;
    long value = *addr;
    return value;
}
