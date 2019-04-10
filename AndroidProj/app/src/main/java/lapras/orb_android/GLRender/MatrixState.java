package lapras.orb_android.GLRender;

/**
 * Created by dell on 2017/12/20.
 */
import android.opengl.Matrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

//存储系统矩阵状态的类
public class MatrixState {
    private static final float idMat[] = {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
    public static double angle=0;
    public static float x=0;
    public static float y=0;
    public static float z=0;


    public static float[] mModelMatrix = idMat;
    public static float[] model_view_matrix= idMat;
    public static float[] projection_matrix= idMat;
    public static float[] final_matrix= idMat;

    final static double scaleMat[][]={
            {1,0,0,0},
            {0,1,0,0},
            {0,0,1,0},
            {0,0,0,1}
    };

    public static void setmModelMatrix(double angle)
    {
        mModelMatrix[0]= (float) Math.cos(angle);
        mModelMatrix[1]=0;
        mModelMatrix[2]= (float) Math.sin(angle);
        mModelMatrix[3]=0;

        mModelMatrix[4]=0;
        mModelMatrix[5]=1;
        mModelMatrix[6]=0;
        mModelMatrix[7]=0;

        mModelMatrix[8]= (float) -Math.sin(angle);
        mModelMatrix[9]=0;
        mModelMatrix[10]= (float) Math.cos(angle);
        mModelMatrix[11]=0;

        mModelMatrix[12]=x;
        mModelMatrix[13]=y;
        mModelMatrix[14]=z;
        mModelMatrix[15]=1;
    }


    public static void set_model_view_matrix(RealMatrix rotation, RealMatrix translation)
    {
        final double d[][]={
                {1,0,0},
                {0,-1,0},
                {0,0,-1}
        };
        RealMatrix rx=new Array2DRowRealMatrix(d);
        rotation=rx.multiply(rotation);
        translation=rx.multiply(translation);
        double R[][]= rotation.getData();
        double T[][]=translation.getData();

        model_view_matrix[0]=(float) R[0][0];
        model_view_matrix[1]=(float) R[1][0];
        model_view_matrix[2]=(float) R[2][0];
        model_view_matrix[3]=0.0f;

        model_view_matrix[4]=(float) R[0][1];
        model_view_matrix[5]=(float) R[1][1];
        model_view_matrix[6]=(float) R[2][1];
        model_view_matrix[7]=0.0f;

        model_view_matrix[8]=(float) R[0][2];
        model_view_matrix[9]=(float) R[1][2];
        model_view_matrix[10]=(float) R[2][2];
        model_view_matrix[11]=0.0f;

        model_view_matrix[12]=(float) T[0][0];
        model_view_matrix[13]=(float) T[1][0];
        model_view_matrix[14]=(float) T[2][0];
        model_view_matrix[15]=1.0f;

    }
    public static void set_projection_matrix(float [] matrix){projection_matrix = matrix;}
    public static void set_projection_matrix(float f_x,float f_y, float c_x,float c_y, float width, float height, float near_plane, float far_plane)
    {
        projection_matrix[0] = 2*f_x/width;
        projection_matrix[1] = 0.0f;
        projection_matrix[2] = 0.0f;
        projection_matrix[3] = 0.0f;

        projection_matrix[4] = 0.0f;
        projection_matrix[5] = 2*f_y/height;
        projection_matrix[6] = 0.0f;
        projection_matrix[7] = 0.0f;

        projection_matrix[8] = 1.0f - 2*c_x/width;
        projection_matrix[9] = 2*c_y/height - 1.0f;
        projection_matrix[10] = -(far_plane + near_plane)/(far_plane - near_plane);
        projection_matrix[11] = -1.0f;

        projection_matrix[12] = 0.0f;
        projection_matrix[13] = 0.0f;
        projection_matrix[14] = -2.0f*far_plane*near_plane/(far_plane - near_plane);
        projection_matrix[15] = 0.0f;
    }

    public static float[] getFinalMatrix() {
//        final float idMat[] = {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
//        return idMat;
        Matrix.multiplyMM(final_matrix,0,model_view_matrix,0,mModelMatrix,0);
        Matrix.multiplyMM(final_matrix, 0, projection_matrix, 0, final_matrix, 0);
        return final_matrix;
    }
}