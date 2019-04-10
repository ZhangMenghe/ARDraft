#ifndef ARDRAFT_JNI_INTERFACE_H
#define ARDRAFT_JNI_INTERFACE_H

#include <jni.h>

#define JNI_METHOD(returnType, funcName)\
    JNIEXPORT returnType JNICALL        \
        Java_lapras_orb_1android_JniInterface_##funcName

extern "C"{
    JNI_METHOD(jlong, JNIcreateController)(JNIEnv*, jclass, jobject);

    JNI_METHOD(void, JNIdrawFrame)(JNIEnv*, jclass, jlong);

    JNIEnv * GetJniEnv();
}
#endif