#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <opencv2/imgcodecs.hpp>
#include "jni_interface.h"
#include "allController.h"

namespace {
    //maintain a reference to VM
    static JavaVM *g_vm = nullptr;

    jobject main_object;
    //global environment
    jlong nativeAppAddr = 0;

    inline jlong controllerPtr(allController * native_controller){
        return reinterpret_cast<intptr_t>(native_controller);
    }
    inline allController * controllerNative(jlong ptr){
        return reinterpret_cast<allController *>(ptr);
    }
}

//JNI Library function: call when native lib is load(System.loadLibrary)
jint JNI_OnLoad(JavaVM *vm, void *) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

/*Native Application methods*/
JNI_METHOD(jlong, JNIcreateController)
(JNIEnv* env, jclass, jobject asset_manager){

    AAssetManager * cpp_asset_manager = AAssetManager_fromJava(env, asset_manager);
    nativeAppAddr =  controllerPtr(new allController(cpp_asset_manager));
    return nativeAppAddr;
}

JNI_METHOD(void, JNIdrawFrame)
(JNIEnv*, jclass, jlong matAddr){
    controllerNative(nativeAppAddr)->onDrawFrame((cv::Mat*)matAddr);
}

JNIEnv * GetJniEnv(){
    JNIEnv *env;
    jint result = g_vm->AttachCurrentThread(&env, nullptr);
    return result == JNI_OK ? env : nullptr;
}