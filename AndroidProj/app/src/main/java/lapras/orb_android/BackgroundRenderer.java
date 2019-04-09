package lapras.orb_android;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;

import org.opencv.core.Mat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * This class renders the AR background from camera feed. It creates and hosts the texture given to
 * ARCore to be filled with the camera image.
 */
public class BackgroundRenderer {
    private static final String TAG = BackgroundRenderer.class.getSimpleName();

    // Shader names.
    private static final String VERTEX_SHADER_NAME = "shaders/screenquad.vert";
    private static final String FRAGMENT_SHADER_NAME = "shaders/screenquad.frag";

    private static final int COORDS_PER_VERTEX = 2;
    private static final int TEXCOORDS_PER_VERTEX = 2;
    private static final int FLOAT_SIZE = 4;

    private FloatBuffer quadCoords;
    private FloatBuffer quadTexCoords;

    private int quadProgram;
    private int uTexture_id;
    private int quadPositionParam;
    private int quadTexCoordParam;
    private final int[] textureObjectId = new int[1];

    private Mat cframe;
    private byte[] buffer;

    public void createOnGlThread(Context context) throws IOException {
        // Generate the background texture.
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        int textureTarget = GLES20.GL_TEXTURE_2D;
        GLES20.glGenTextures(1,textureObjectId, 0);

        GLES20.glBindTexture(textureTarget, textureObjectId[0]);

        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        int numVertices = 4;
        if (numVertices != QUAD_COORDS.length / COORDS_PER_VERTEX) {
            throw new RuntimeException("Unexpected number of vertices in BackgroundRenderer.");
        }

        ByteBuffer bbCoords = ByteBuffer.allocateDirect(QUAD_COORDS.length * FLOAT_SIZE);
        bbCoords.order(ByteOrder.nativeOrder());
        quadCoords = bbCoords.asFloatBuffer();
        quadCoords.put(QUAD_COORDS);
        quadCoords.position(0);

        ByteBuffer bbTexCoordsTransformed =
                ByteBuffer.allocateDirect(numVertices * TEXCOORDS_PER_VERTEX * FLOAT_SIZE);
        bbTexCoordsTransformed.order(ByteOrder.nativeOrder());
        quadTexCoords = bbTexCoordsTransformed.asFloatBuffer();

        int vertexShader =
                ShaderUtil.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
        int fragmentShader =
                ShaderUtil.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_NAME);

        quadProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(quadProgram, vertexShader);
        GLES20.glAttachShader(quadProgram, fragmentShader);
        GLES20.glLinkProgram(quadProgram);
        GLES20.glUseProgram(quadProgram);

        ShaderUtil.checkGLError(TAG, "Program creation");

        quadPositionParam = GLES20.glGetAttribLocation(quadProgram, "a_Position");
        quadTexCoordParam = GLES20.glGetAttribLocation(quadProgram, "a_TexCoord");
        uTexture_id = GLES20.glGetUniformLocation(quadProgram, "uTexture");

        ShaderUtil.checkGLError(TAG, "Program parameters");
    }
//    public void draw(Mat frame){
//
//    }
//    public void draw(@NonNull Frame frame) {
//        // If display rotation changed (also includes view size change), we need to re-query the uv
//        // coordinates for the screen rect, as they may have changed as well.
//        if (frame.hasDisplayGeometryChanged()) {
//            frame.transformCoordinates2d(
//                    Coordinates2d.OPENGL_NORMALIZED_DEVICE_COORDINATES,
//                    quadCoords,
//                    Coordinates2d.TEXTURE_NORMALIZED,
//                    quadTexCoords);
//        }
//
//        if (frame.getTimestamp() == 0) {
//            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
//            // drawing possible leftover data from previous sessions if the texture is reused.
//            return;
//        }
//
//        draw();
//    }


    public void draw(Mat frame,
            int imageWidth, int imageHeight, float screenAspectRatio, int cameraToDisplayRotation) {
        cframe = frame;
        // Crop the camera image to fit the screen aspect ratio.
        float imageAspectRatio = (float) imageWidth / imageHeight;
        float croppedWidth;
        float croppedHeight;
        if (screenAspectRatio < imageAspectRatio) {
            croppedWidth = imageHeight * screenAspectRatio;
            croppedHeight = imageHeight;
        } else {
            croppedWidth = imageWidth;
            croppedHeight = imageWidth / screenAspectRatio;
        }
        cameraToDisplayRotation= (cameraToDisplayRotation + 90)%360;
        float u = (imageWidth - croppedWidth) / imageWidth * 0.5f;
        float v = (imageHeight - croppedHeight) / imageHeight * 0.5f;

        float[] texCoordTransformed;
        switch (cameraToDisplayRotation) {
            case 90:
                texCoordTransformed = new float[] {1 - u, 1 - v, u, 1 - v, 1 - u, v, u, v};
                break;
            case 180:
                texCoordTransformed = new float[] {1 - u, v, 1 - u, 1 - v, u, v, u, 1 - v};
                break;
            case 270:
                texCoordTransformed = new float[] {u, v, 1 - u, v, u, 1 - v, 1 - u, 1 - v};
                break;
            case 0:
                texCoordTransformed = new float[] {u, 1 - v, u, v, 1 - u, 1 - v, 1 - u, v};
                break;
//            default:
//                throw new IllegalArgumentException("Unhandled rotation: " + cameraToDisplayRotation);
            default:
                texCoordTransformed = new float[] {u, 1 - v, u, v, 1 - u, 1 - v, 1 - u, v};
                break;
        }

        // Write image texture coordinates.
        quadTexCoords.position(0);
        quadTexCoords.put(texCoordTransformed);

        draw();
    }

    /**
     * Draws the camera background image using the currently configured {@link
     * BackgroundRenderer#quadTexCoords} image texture coordinates.
     */
    private void draw() {
        GLES20.glUseProgram(quadProgram);

        // Ensure position is rewound before use.
        quadTexCoords.position(0);

        // No need to test or write depth, the screen quad has arbitrary depth, and is expected
        // to be drawn first.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(false);

//        GLES20.glGenTextures(1, textureObjectId, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0]);

        GLES20.glUniform1i(uTexture_id, 0);
        buffer = new byte[((int)cframe.total())*cframe.channels()];
        cframe.get(0,0,buffer);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
                            0,
                            GLES20.GL_RGBA,
                            cframe.cols(),
                            cframe.rows(),
                            0,
                            GLES20.GL_RGBA,
                            GLES20.GL_UNSIGNED_BYTE,
                            ByteBuffer.wrap(buffer)
                            );

        // Set the vertex positions.
        GLES20.glVertexAttribPointer(
                quadPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadCoords);

        // Set the texture coordinates.
        GLES20.glVertexAttribPointer(
                quadTexCoordParam, TEXCOORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadTexCoords);

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(quadPositionParam);
        GLES20.glEnableVertexAttribArray(quadTexCoordParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFlush();
        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(quadPositionParam);
        GLES20.glDisableVertexAttribArray(quadTexCoordParam);

        // Restore the depth state for further drawing.
        GLES20.glDepthMask(true);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        ShaderUtil.checkGLError(TAG, "BackgroundRendererDraw");
    }

    private static final float[] QUAD_COORDS =
            new float[] {
                    -1.0f, -1.0f, -1.0f, +1.0f, +1.0f, -1.0f, +1.0f, +1.0f,
            };
}
