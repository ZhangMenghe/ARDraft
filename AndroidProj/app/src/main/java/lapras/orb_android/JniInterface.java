package lapras.orb_android;

import android.content.res.AssetManager;

public class JniInterface {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("orbAndroid");
    }
    private static final String TAG = "JniInterfaceCalVR";
    static AssetManager assetManager;

    public static native long JNIcreateController(AssetManager asset_manager);

    public static native void JNIsetupResource(String calvr_path);

    public static native void JNIregisterJavaObject(Object obj, String obj_name);

//    public static native void JNIonGlSurfaceCreated(String calvr_path);
//
//    public static native void JNIonViewChanged(int rot, int width, int height);
//
    public static native void JNIdrawFrame(long frameAddr);
//
//    public static native void JNIonSingleTouchDown(int type, float x, float y);
//
//    public static native void JNIonSingleTouchUp(int type, float x, float y);
//
//    public static native void JNIonDoubleTouch(int type, float x, float y);
//
//    public static native void JNIonTouchMove(int type, float x, float y);
//
//    public static native void JNIonResume(Context context, Activity activity);
//
//    public static native void JNIonPause();
//
//    public static native void JNIonDestroy();

}
