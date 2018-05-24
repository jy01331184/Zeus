//
// Created by tianyang on 18/4/26.
//
#include "zeus.h"

JNIEXPORT jlong JNICALL Java_com_zeus_ex_UnsafeProxy_getAddr(JNIEnv *env, jclass cls, jobject method){
    jmethodID mid = env->FromReflectedMethod(method);


    int * addr = (int *) mid;
    int value = *addr;
    LOGD("addr %p %p %d",mid,addr,value);

    return value;
}
