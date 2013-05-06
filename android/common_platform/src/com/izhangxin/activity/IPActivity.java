package com.izhangxin.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.cocos2dx.lib.Cocos2dxActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.izhangxin.platform.AndroidPlatformInterface;

public class IPActivity extends Cocos2dxActivity{

    public static final int NONE = 0;  
    public static final int PHOTOHRAPH = 1;// 拍照  
    public static final int PHOTOZOOM = 2; // 缩放  
    public static final int PHOTORESOULT = 3;// 结果  
    public static final String IMAGE_UNSPECIFIED = "image/*";
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   	     	 
        if (resultCode == NONE)
            return;  
        // 拍照  
    	if (requestCode == PHOTOHRAPH) {
    		File picture = new File(AndroidPlatformInterface.strFilePath);
    		if(picture != null && picture.exists())
    			startPhotoZoom(Uri.fromFile(picture));
    	}  

       // 读取相册缩放图片  
    	else if (requestCode == PHOTOZOOM) {  
    		startPhotoZoom(data.getData());
       }  
        // 处理结果  
       else if (requestCode == PHOTORESOULT) {  
       	Bundle extras = data.getExtras();  
           if (extras != null) {
               Bitmap photo = extras.getParcelable("data");
               saveFile(photo);
               AndroidPlatformInterface.initHeadFaceDidFinish(AndroidPlatformInterface.strFilePath);
           }
       }
      
       super.onActivityResult(requestCode, resultCode, data);
   }

   private void startPhotoZoom(Uri uri) {
       Intent intent = new Intent("com.android.camera.action.CROP");    
       intent.setDataAndType(uri, IMAGE_UNSPECIFIED);    
       intent.putExtra("crop", "true");
       // aspectX aspectY 是宽高的比例
       intent.putExtra("aspectX", 1);
       intent.putExtra("aspectY", 1);
       // outputX outputY 是裁剪图片宽高
       intent.putExtra("outputX", 100);
       intent.putExtra("outputY", 100);
       intent.putExtra("return-data", true);
       startActivityForResult(intent, PHOTORESOULT);
   }
   
   public void saveFile(Bitmap bm) {
	   
	   if(AndroidPlatformInterface.strFilePath == null)
		   AndroidPlatformInterface.strFilePath = AndroidPlatformInterface.strDirPath + String.valueOf(System.currentTimeMillis()) + ".png";
	   
       File myCaptureFile = new File(AndroidPlatformInterface.strFilePath);
       if(myCaptureFile.exists())
       {
    	   myCaptureFile.delete();
    	   try {
    		   myCaptureFile.createNewFile();
    	   } catch (IOException e1) {
			// TODO Auto-generated catch block
    		   e1.printStackTrace();
    	   }
    	   
    	   BufferedOutputStream bos = null;
    	   try {
               bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
               bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
               bos.flush();
               bos.close();
    	   } catch (Exception e) {
    		   e.printStackTrace();
    	   } finally {
    		   try {
    			   bos.close();
    		   } catch (IOException e) {
				
				e.printStackTrace();
    		   }
    	   }
       }
   }
}
