package com.izhangxin.bdengine;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AndroidGameInterface
{
	private static Context context;
	
	private static float batteryLevel = -1;
	
	private static LocationListener locationListener;

	private static Location location;
	    
	public static void setContext(Context context)
	{
		AndroidGameInterface.context = context;
	}
	
	public static String getMACAddress() 
	{  
		// XXX: 当wifi关闭时，不能取得wifi的mac，3g的情况没有测试，但是如果3g关闭的话，是不是也取得不了3g的mac呢？所以这里直接返回imei
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }  
	
	public static void openURL(String addr)
	{
		Uri uri = Uri.parse(addr);  
	    context.startActivity(new Intent(Intent.ACTION_VIEW,uri));  
	}
	
	public static int getReachability()  
    {    
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);      
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        
        Log.d("NetworkInfo====>", "NetworkInfo:" + (networkInfo == null));
        
        if (networkInfo == null || !networkInfo.isAvailable())
        {
        	networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	if (networkInfo == null)
        	{
        		return 0;
            }
        	else
            {
            	 Log.d("NetworkInfo===>", "networkInfo.isAvailable():" + networkInfo.isAvailable());
            	if(!networkInfo.isAvailable())
            		return 0;
            	else
            		return 1;
            }
        }
        
        if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
        	return 1;
        }
        
        return 2;
    }   
	
    public static float getBatteryLevel()
    {
    	if(batteryLevel < -0.5f)
    	{
    		//初始化电池监听
    		IntentFilter intentFilter = new IntentFilter();
    		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    		batteryLevel = 0;
    		context.registerReceiver(new BroadcastReceiver() 
    			{
    				@Override
    				public void onReceive(Context context, Intent intent)
    				{
    					String action = intent.getAction();
    					if (action.equals(Intent.ACTION_BATTERY_CHANGED)) 
    					{        
    						int level = intent.getIntExtra("level", 0);//电量
    						int scale = intent.getIntExtra("scale", 0);//最大电量
    						Log.d("test", level+" --- " + scale);
    						if(scale != 0)
    						{
    							batteryLevel = ((float)level) / scale;
    							if(batteryLevel < 0 )
    							{
    								batteryLevel = 0;
    							}else if(batteryLevel > 1)
    							{
    								batteryLevel = 1;
    							}
    						}else
    						{
    							batteryLevel = 0;
    						}
    					}
    				}
    			} , intentFilter);
    	}
    	
    	return batteryLevel;
    }
    
    public static String getDeviceName()
    {
    	return Build.MODEL;
    }
    
    public static void sendSMS(String strNumber, String strMessage)
    {
        Uri uri = Uri.parse("smsto:");          
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);          
        it.putExtra("sms_body", strMessage);          
        context.startActivity(it);   
    }
    
    public static String[] getPhoneInformation()
    {
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	
    	String deviceid = tm.getDeviceId();
    	String tel = tm.getLine1Number();
    	String imei = tm.getSimSerialNumber();
    	String imsi = tm.getSubscriberId();
    	
    	String[] strInfos = new String[4];
    	strInfos[0] = deviceid;
    	strInfos[1] = tel;
    	strInfos[2] = imei;
    	strInfos[3] = imsi;
    	System.out.println("==========================================================>tel:" + tm.getNeighboringCellInfo() + "imsi:" + imsi);
    	Log.d("jni", "deviceid:" + deviceid + " tel:" + tel + " imei:" + imei + " imsi:" + imsi);
    	
    	return strInfos; 	
    }
    
    public static int isAppExist(String strPacketName)
    {
		PackageInfo pinfo = null;
		try 
		{
			pinfo = context.getPackageManager().getPackageInfo(strPacketName, 0);
			if(pinfo != null)
			{
				Log.d("install", "packet:" + pinfo.packageName + ", versionName:" + pinfo.versionName + ", version:" + pinfo.versionCode);
				return pinfo.versionCode;	
			}
			//Log.d("install",  pinfo + "a");
		} catch (NameNotFoundException e) 
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//Log.d("install", e.getMessage()+pinfo);
			return 0;
		}
		return 0;
	}
	
	public static boolean openApp(int nBDOPID, String strTicket, String strPacketName, String strAccount, String strPassword, String strDomain, 
		int nGameID, String strLoginAddr, int nLoginPort, String strServerAddr, int nServerPort, int nChannel)
	{	
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(strPacketName);
		Bundle bundle = new Bundle();
		
		bundle.putInt("bdo_pid", nBDOPID);
		bundle.putString("ticket", strTicket);
		bundle.putString("account", strAccount);
		bundle.putString("password", strPassword);
		bundle.putString("domain", strDomain);
		bundle.putInt("gameid", nGameID);
		bundle.putString("login_addr", strLoginAddr);
		bundle.putInt("login_port", nLoginPort);
		bundle.putString("server_addr", strServerAddr);
		bundle.putInt("server_port", nServerPort);
		bundle.putInt("channle", nChannel);
		
		intent.putExtras(bundle);
		context.startActivity(intent);
		return true;
	}
	
	protected static boolean installApp(String strPacketName)
	{
		Intent intent = new Intent();
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		Log.d("install", Uri.parse(strPacketName).toString());
		intent.setDataAndType(Uri.parse(strPacketName), "application/vnd.android.package-archive");
		
		context.startActivity(intent);
		return true;
	}	
	
	public static void stopUpdatingLocation() 
	{ 
		((Activity)context).runOnUiThread(new Runnable()
		{
			public void run()
			{
				if(locationListener  != null)
				{
					LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);    
					locationManager.removeUpdates(locationListener);
					locationListener = null;
				};
			}
		});
	}
	
	public synchronized static float getUpdatingLocationLatitude() 
	{ 
		if(location!=null)
		{
			return (float) location.getLatitude();
		}else
		{
			return -1;
		}
	}
	
	public synchronized static float getUpdatingLocationLongitude() 
	{ 
		if(location!=null)
		{
			return (float) location.getLongitude();
		}else
		{
			return -1;
		}
	}
	
	public static void startUpdatingLocation() 
	{   
		((Activity)context).runOnUiThread(new Runnable()
		{
			public void run()
			{
				if(getLocation()  == null)
				{
					LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);    
					String provider = LocationManager.NETWORK_PROVIDER;
					setLocation( locationManager.getLastKnownLocation(provider));
					try
					{
						locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() 
						{   
							@Override
							public void onLocationChanged(Location location) 
							{   
								setLocation(location);   
							}   

							@Override
							public void onProviderDisabled(String provider)
							{
								Log.d(AndroidGameInterface.class.getName(), "onProviderDisabled"  +provider);
							}

							@Override
							public void onProviderEnabled(String provider)
							{
								Log.d(AndroidGameInterface.class.getName(), "onProviderEnabled" +provider);
							}   

							@Override
							public void onStatusChanged(String provider, int status, Bundle extras)
							{
								Log.d(AndroidGameInterface.class.getName(), "onStatusChanged"  +provider +" "+  status +" "+ extras);
							}   
						});
					}catch(Exception e)
					{
						Log.d("setLocation","NETWORK_PROVIDER gps not exist");
					}  
				}
			}
		});
	}
	
	public synchronized static Location getLocation() 
	{
		return location;
	}

	public synchronized static void setLocation(Location newLoc) 
	{
		//if(location != null)
		Log.d(AndroidGameInterface.class.getName(), "setLocation : " + newLoc);
		if(newLoc != null)
		{
			location = newLoc;
		}
	}
	
	
	public static String[] getPackageVersionInfo()
	{
		PackageInfo packInfo = null;
		String packageName = "";
		
		String[] strInfos = new String[4];
		
		packageName = context.getPackageName();
		
		PackageManager manager = context.getPackageManager();
		try {
			packInfo = manager.getPackageInfo(packageName, 0);
			
			strInfos[0] = packInfo.versionName;
			strInfos[1] = String.valueOf(packInfo.versionCode);
			strInfos[2] = packageName;
			
		} catch (NameNotFoundException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
			return strInfos;
		}
		
		return strInfos;
	}
}