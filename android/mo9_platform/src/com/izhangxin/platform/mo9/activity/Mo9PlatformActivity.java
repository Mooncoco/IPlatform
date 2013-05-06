package com.izhangxin.platform.mo9.activity;

import org.cocos2dx.lib.Cocos2dxActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.izhangxin.platform.mo9.Mo9PlatformInterface;

public class Mo9PlatformActivity extends Cocos2dxActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Mo9PlatformInterface.setContext(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
//		Toast.makeText(this, "RESULT reqCode:"+requestCode+",resCode:"+resultCode, Toast.LENGTH_LONG).show();
		if(requestCode==100 && resultCode==10)
		{
			//Toast.makeText(this, "支付成，请下发用户购买商品.", Toast.LENGTH_LONG).show();
			
			Mo9PlatformInterface.onMo9PurchaseSuccess();
		}
		else
			Mo9PlatformInterface.onMo9PurchaseFail("");
	}
}
