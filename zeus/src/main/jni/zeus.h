//
// Created by tianyang on 18/4/26.
//
#include "jni.h"
#include <android/log.h>
#define  LOG_TAG    "System.out"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#ifdef __cplusplus
extern "C" {
#endif
#ifndef JANDFIX_MASTER_ZEUS_H
#define JANDFIX_MASTER_ZEUS_H
JNIEXPORT jlong JNICALL Java_com_zeus_ex_UnsafeProxy_getAddr(JNIEnv *, jclass, jobject);

#endif //JANDFIX_MASTER_ZEUS_H

#ifdef __cplusplus
}
#endif
