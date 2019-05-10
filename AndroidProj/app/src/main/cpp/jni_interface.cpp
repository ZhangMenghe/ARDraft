#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <opencv2/imgcodecs.hpp>
#include "jni_interface.h"
#include "allController.h"
#include "AndroidHelper.h"
#include <unordered_map>
namespace {
    //maintain a reference to VM
    static JavaVM *g_vm = nullptr;

    //global environment
    jlong nativeAppAddr = 0;

    std::unordered_map<std::string, jobject > objmap;

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

JNI_METHOD(void, JNIregisterJavaObject)
        (JNIEnv* env, jobject, jobject regObj, jstring name){
    const char* cpath = env->GetStringUTFChars(name, JNI_FALSE);
    objmap[std::string(cpath)] = env->NewGlobalRef(regObj);
    env->ReleaseStringUTFChars(name, cpath);
}

/*Native Application methods*/
JNI_METHOD(jlong, JNIcreateController)
(JNIEnv* env, jclass, jobject asset_manager){

    AAssetManager * cpp_asset_manager = AAssetManager_fromJava(env, asset_manager);
    nativeAppAddr =  controllerPtr(new allController(cpp_asset_manager));
    return nativeAppAddr;
}
JNI_METHOD(void, JNIsetupResource)(JNIEnv* env, jclass, jstring calvr_path){
    const char* cpath = env->GetStringUTFChars(calvr_path, JNI_FALSE);
    setenv("CALVR_HOME", std::string(cpath));
    env->ReleaseStringUTFChars(calvr_path, cpath);
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
void CopyBackFiles(const char* path){
    JNIEnv *env = GetJniEnv();
    if(!env) {LOGE("===Failed to get environment while copying back"); return;}
    jclass helper_class = env->FindClass( "lapras/orb_android/MainActivity" );
    if(!helper_class) {LOGE("===Failed to get environment while copying back"); return;}

    if(helper_class){
        helper_class = static_cast<jclass>(env->NewGlobalRef(helper_class));
        jmethodID copy_method = env->GetMethodID(helper_class, "CopyBackFiles", "(Ljava/lang/String;)V");
        jobject obj = objmap["MainActivity"];
        if(!obj) {LOGE("====FAILED to get correct object"); return;}
        env->CallVoidMethod(obj, copy_method, env->NewStringUTF(path));
    }
}