package lapras.orb_android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static lapras.orb_android.CVandCGViewBase.RGBA;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("orbAndroid");
    }
    private static final String TAG = "MainActivity";
    private CVandCGViewBase cvgSurfaceView;
    private BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private Mat cframe;

    private int viewportWidth;
    private int viewportHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSurfaceView();
        cframe = new Mat(640,480, CvType.CV_8UC4);
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
        cvgSurfaceView.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
        if (cvgSurfaceView != null)
            cvgSurfaceView.disableView();

        cvgSurfaceView.onPause();
    }
    public void onDestroy() {
        super.onDestroy();
        if (cvgSurfaceView != null)
            cvgSurfaceView.disableView();
    }

    private void setupSurfaceView(){
        cvgSurfaceView = (CVandCGViewBase) findViewById(R.id.camera_view);

//        cvgSurfaceView.enableFpsMeter();
        cvgSurfaceView.setCameraIndex(CVandCGJavaCamera2View.CAMERA_ID_ANY);
        cvgSurfaceView.setCvCameraViewListener(new cvgCamListener());
        cvgSurfaceView.disableView();
        cvgSurfaceView.setVisibility(SurfaceView.VISIBLE);

        cvgSurfaceView.setPreserveEGLContextOnPause(true);

        cvgSurfaceView.setEGLContextClientVersion(2);
        cvgSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        cvgSurfaceView.setRenderer(new MainActivity.Renderer(this));
        cvgSurfaceView.setZOrderOnTop(true);
        cvgSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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
        public Renderer(Context context){
            this.context = context;
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try{
                backgroundRenderer.createOnGlThread(this.context);
            }catch (IOException e){
                Log.e(TAG, "onSurfaceCreated: Fail to create background renderer" );
            }

//            GLES30.glClearColor(1.0f,.0f,.0f,1.0f);
//            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//            GLES30.glEnable(GLES30.GL_CULL_FACE);
//            JniInterface.JNIonGlSurfaceCreated(calvr_dest);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0,0,width,height);
            viewportWidth = width;
            viewportHeight = height;

        }

        @Override
        public void onDrawFrame(GL10 gl) {
//            glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
//
//            int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
//            backgroundRenderer.draw(cframe,
//                                    cframe.cols(),
//                                    cframe.rows(),
//                    (float)viewportWidth / viewportHeight,
//                                    displayRotation);

//            Log.e(TAG, "!!!frame!!!!!" );
            // Synchronized to avoid racing onDestroy.
//            synchronized (this) {
//                if (controllerAddr == 0) {
//                    return;
//                }
//                if (viewportChanged) {
//                    int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
//                    JniInterface.JNIonViewChanged(displayRotation, viewportWidth, viewportHeight);
//
//                    viewportChanged = false;
//                }
//                JniInterface.JNIdrawFrame();
//                updateFPS(JniInterface.JNIgetFPS());
//            }
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
            return (mPreviewFormat == RGBA)? inputFrame.rgba() : inputFrame.gray();
        }
    }
}
