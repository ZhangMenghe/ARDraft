package lapras.orb_android;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class fileUtils {
    final static String TAG = "File Utils";
    public static void copyFromAsset(AssetManager assetManager, String src_path, String target_path)
            throws IOException {
        String assets[] = assetManager.list(src_path);
        if(assets.length == 0){
            copyAssetFile(assetManager, src_path, target_path);
        }else{
            File dir = new File(target_path);
            if(!dir.exists())
                dir.mkdir();
            for(int i=0; i<assets.length; i++){
                String nsrc = src_path + "/" + assets[i];
                String ntarget = target_path + "/" + assets[i];
                copyFromAsset(assetManager, nsrc, ntarget);
            }
        }
    }
    public static void copyAssetFile(AssetManager assetManager, String src_name, String dest_name){
        File model = new File(dest_name);
        try {
            if(!model.exists()){
                InputStream is = assetManager.open(src_name);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                FileOutputStream os = new FileOutputStream(model);
                os.write(buffer);
            }
        } catch (Exception e) {
            Log.e(TAG, "Fail to copyAssetFile ");
        }
    }
    public static void writeToDevice(String file_name){

    }
}
