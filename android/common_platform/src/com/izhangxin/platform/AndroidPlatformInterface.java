package com.izhangxin.platform;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.izhangxin.activity.IPActivity;

public class AndroidPlatformInterface
{
	private static Context mContext;
	public static String strFilePath = null;
	public static String strDirPath = null;
	
	public static void setContext(Context context) {mContext = context;}
	
	public static void initHeadFaceWithPath(String path)
	{
		Log.i("AndroidPlatformInterface", "initHeadFaceWithPath: "+path);
		
		strDirPath = path;
		
		((Activity)mContext).runOnUiThread(new Runnable()
		{
			public void run()
			{
			    if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
			    {
			        // 摄像头存在 
					Builder dialog = new AlertDialog.Builder(mContext);
					dialog.setTitle(R.string.init_photo_title);				
					dialog.setItems(R.array.init_photo_items, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.i("onClick", "dialog: "+dialog.toString()+" which: "+which);
							
							switch(which)
							{
							case 0://camera
							{
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								strFilePath = strDirPath + String.valueOf(System.currentTimeMillis())+".png";
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(strFilePath)));
				                ((Activity)mContext).startActivityForResult(intent, IPActivity.PHOTOHRAPH);

								break;
							}
							case 1://gallery
							{
								Intent intent = new Intent(Intent.ACTION_PICK, null);
				                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IPActivity.IMAGE_UNSPECIFIED);    
				                ((Activity)mContext).startActivityForResult(intent, IPActivity.PHOTOZOOM);
								break;
							}
							}
							
						}
					});
					
					dialog.show();

			    }
			    else
			    { 
			        // 摄像头不存在
					Intent intent = new Intent(Intent.ACTION_PICK, null);    
	                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IPActivity.IMAGE_UNSPECIFIED);    
	                ((Activity)mContext).startActivityForResult(intent, IPActivity.PHOTOZOOM);
			    }
			}
		});
	}
	
    
   public native static void initHeadFaceDidFinish(String path);
    
}