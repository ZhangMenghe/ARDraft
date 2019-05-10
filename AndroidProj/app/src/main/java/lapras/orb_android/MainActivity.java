package lapras.orb_android;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lapras.orb_android.GLRender.Cube;
import lapras.orb_android.GLRender.MatrixState;

import static lapras.orb_android.CVandCGViewBase.RGBA;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CVandCGViewBase cvgSurfaceView;
    private GLSurfaceView glSurfaceView;

    private long controllerAddr;

    private BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private Mat cframe;

    private int viewportWidth;
    private int viewportHeight;


    // Resource
    final static private String calvr_folder = "calvrAssets";
    String calvr_dest = null;
    String resourceDest = null;
    boolean skipLoadingResource = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JniInterface.assetManager = getAssets();
        controllerAddr = JniInterface.JNIcreateController(JniInterface.assetManager);
        JniInterface.JNIregisterJavaObject(this, "MainActivity");
        setupSurfaceView();
        setupResource();
//        cframe = new Mat(640,480, CvType.CV_8UC4);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Request for camera permission
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        }


        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, cvLoaderCallBack);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            cvLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        glSurfaceView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (cvgSurfaceView != null)
            cvgSurfaceView.disableView();

        glSurfaceView.onPause();
    }
    public void onDestroy() {
        super.onDestroy();
        if (cvgSurfaceView != null)
            cvgSurfaceView.disableView();
    }

    private void setupSurfaceView(){
        cvgSurfaceView = (CVandCGViewBase) findViewById(R.id.camera_view);
        glSurfaceView = findViewById(R.id.glSurfaceView);

        cvgSurfaceView.enableFpsMeter();
        cvgSurfaceView.setCameraIndex(CVandCGJavaCamera2View.CAMERA_ID_ANY);
        cvgSurfaceView.setCvCameraViewListener(new cvgCamListener());
        cvgSurfaceView.disableView();
        cvgSurfaceView.setVisibility(SurfaceView.VISIBLE);

        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        glSurfaceView.setRenderer(new MainActivity.Renderer(this));
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    private void MobileDesktopFileExchange(String path_mobile, boolean to_mobile){
        if(to_mobile && skipLoadingResource){
            File destDir = new File(path_mobile);
            if(destDir.exists())
                return;
        }
        try{
            fileUtils.copyFromAsset(getAssets(), calvr_folder, calvr_dest);
        }catch (Exception e){
            Log.e(TAG, "copyFromAssets: Failed to copy from asset folder");
        }
    }
    private void setupResource(){
        resourceDest = getFilesDir().getAbsolutePath() + "/";
        calvr_dest = resourceDest + calvr_folder;
        MobileDesktopFileExchange(calvr_dest, true);
        JniInterface.JNIsetupResource(calvr_dest);
    }
    private BaseLoaderCallback cvLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cvgSurfaceView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    private class Renderer implements GLSurfaceView.Renderer {
        private Context context;
        Cube cube;
        private final float[] projectionMatrix = new float[16];
        public Renderer(Context context){
//            Log.e(TAG, "===Renderer: create!!!" );
            this.context = context;
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES30.glClearColor(0.01f,0.01f,0.01f,0.01f);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glEnable(GLES30.GL_CULL_FACE);
//
            cube = new Cube(context);
//            Log.e(TAG, "===Renderer: onSurfaceCreated!!!" );
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0,0,width,height);
            float ratio = (float) width / height;
//            MatrixState.set_projection_matrix(445f, 445f, 319.5f, 239.500000f, width, height, 0.01f, 100f);
//            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//            MatrixState.set_projection_matrix(projectionMatrix);
//            Log.e(TAG, "===Renderer: onSurfaceChanged!!!" );
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
//            Log.e(TAG, "===onDrawFrame: draw!!!!!!" );
            cube.draw();
        }

    }
    private class cvgCamListener implements CVandCGViewBase.CvCameraViewListener2{
        private int mPreviewFormat = RGBA;
        public void setFrameFormat(int format){
            mPreviewFormat = format;
        }
        public void onCameraViewStarted(int width, int height) {
        }

        public void onCameraViewStopped() {
        }
        public Mat onCameraFrame(CVandCGViewBase.CvCameraViewFrame inputFrame){
            cframe = inputFrame.rgba();
            JniInterface.JNIdrawFrame(cframe.getNativeObjAddr());
            return (mPreviewFormat == RGBA)? inputFrame.rgba() : inputFrame.gray();
        }
    }
    public void CopyBackFiles(String str){
        fileUtils.writeToDevice(str);
    }
}
