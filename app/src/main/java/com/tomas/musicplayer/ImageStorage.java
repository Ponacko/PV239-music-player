package com.tomas.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Golombiatko on 6/3/2017.
 */

public class ImageStorage {
    public static String save(Bitmap bitmap, String filename) {
        if (bitmap == null) {
            Log.d("bitmap is", "null");
        }
        String stored = null;
        File sdcard = Environment.getExternalStorageDirectory();

        File folder = new File(sdcard.getAbsoluteFile(), ".images");//the dot makes this directory hidden to the user
        folder.mkdir();
        Log.d("filename", filename);

        File file = new File(folder.getAbsoluteFile(), filename + ".jpg");
        if (file.exists()) {
            return stored;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            Log.d("compression", "bitmap not yet saved");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Log.d("compressed", "bitmap saved");
            out.flush();
            out.close();
            stored = "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stored;
    }

    public static File getImage(String imagename) {
        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/.images/"+imagename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaImage;
    }
    public static boolean checkifImageExists(String imagename)
    {
        Bitmap b = null;
        File file = ImageStorage.getImage("/"+imagename+".jpg");
        String path = file.getAbsolutePath();

        if (path != null)
            b = BitmapFactory.decodeFile(path);

        if(b == null ||  b.equals("")) {
            Log.d("Exists? ", "no");
            return false;
        }
        return true;
    }
}
