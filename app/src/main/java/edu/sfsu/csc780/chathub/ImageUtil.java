package edu.sfsu.csc780.chathub;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by bees on 10/4/17.
 */

public class ImageUtil {

    public static final double MAX_LINEAR_DIMENSION = 500.0;
    static String IMAGE_FILE_PREFIX = "chathub-";

    public static Bitmap scaleImage(Bitmap bitmap){
        int originalHeight = bitmap.getHeight();
        int originalWidth = bitmap.getWidth();
        double scaleFactor = MAX_LINEAR_DIMENSION / (double) (originalHeight + originalWidth);

        if (scaleFactor < 1.0){
            int targetWidth = (int) Math.round(originalWidth*scaleFactor);
            int targetHeight = (int) Math.round(originalHeight*scaleFactor);
            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        }
        else{
            return bitmap;
        }
    }

    public static Uri savePhotoImage(Bitmap imageBitmap){
        File photoFile = null;
        try{
            photoFile= createImageFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        if (photoFile == null){
            Log.d(TAG, "Error creating media file");
            return null;
        }

        try{
            FileOutputStream fos = new FileOutputStream(photoFile);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        }

        catch (FileNotFoundException e){
            Log.d(TAG, "File not found: " + e.getMessage());
        }

        catch (IOException e){
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        return Uri.fromFile(photoFile);
    }

    public static File createImageFile() throws IOException {
        //Create Image File Name
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String imageFileNamePrefix= IMAGE_FILE_PREFIX + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileNamePrefix,
                ".jpg",
                storageDir
        );

        return  imageFile;
    }

    public static Bitmap getBitmapForUri(Uri imageUri){
        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return bitmap;
    }



}
